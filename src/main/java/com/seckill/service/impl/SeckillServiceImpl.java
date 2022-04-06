package com.seckill.service.impl;

import com.seckill.controller.viewobject.SeckillVO;
import com.seckill.dao.AnnounceDOMapper;
import com.seckill.dao.ProductDOMapper;
import com.seckill.dao.SeckillDOMapper;
import com.seckill.dataobject.AnnounceDO;
import com.seckill.dataobject.ProductDO;
import com.seckill.dataobject.SeckillDO;
import com.seckill.dataobject.StockDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.ProductService;
import com.seckill.service.SeckillService;
import com.seckill.service.UserService;
import com.seckill.service.model.ProductModel;
import com.seckill.service.model.SeckillModel;
import com.seckill.service.model.UserModel;
import org.apache.catalina.User;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillDOMapper seckillDOMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnounceDOMapper announceDOMapper;

    @Override
    public SeckillModel getSeckillModel(Integer id) {

        SeckillDO seckillDO = seckillDOMapper.selectByPrimaryKey(id);
        if (seckillDO == null){
            return null;
        }

        //秒杀Model-->DO，并判断秒杀状态
        SeckillModel seckillModel = this.convertFromSeckillDO(seckillDO);

        //获取秒杀产品信息
        ProductModel productModel = productService.getProductById(seckillModel.getProductId());
        seckillModel.setProductModel(productModel);

        //判断当前时间是否秒杀活动开始时间
        DateTime now = new DateTime();
        if (seckillModel.getStartTime().isAfterNow()){
            seckillModel.setStatus(1);
        }else if (seckillModel.getEndTime().isBeforeNow()){
            seckillModel.setStatus(3);
        }else {
            seckillModel.setStatus(2);
        }

        return seckillModel;
    }

    @Override
    public List<SeckillModel> seckillModelList() {
        List<SeckillDO> list = seckillDOMapper.seckillDOList();
        List<SeckillModel> seckillModelList = list.stream().map(seckillDO -> {
            SeckillModel seckillModel = this.convertFromSeckillDO(seckillDO);
            ProductModel productModel = productService.getProductById(seckillDO.getProductId());
            seckillModel.setProductModel(productModel);
            return seckillModel;
        }).collect(Collectors.toList());

        return seckillModelList;
    }

    @Override
    public SeckillModel getSeckillByIdInCache(Integer id) {
        SeckillModel seckillModel = (SeckillModel) redisTemplate.opsForValue().get("seckill_validate_"+id);
        if (seckillModel == null){
            seckillModel = this.getSeckillModel(id);
            redisTemplate.opsForValue().set("seckill_validate_"+id,seckillModel);
            redisTemplate.expire("seckill_validate_"+id,10,TimeUnit.MINUTES);
        }
        return seckillModel;
    }

    @Override
    @Transactional
    public void publishSeckill(SeckillDO seckillDO) {

        seckillDOMapper.insertSelective(seckillDO);
        Integer id = seckillDO.getId();
        ProductModel productModel = productService.getProductById(seckillDO.getProductId());
        //将库存同步到redis内
        redisTemplate.opsForValue().set("seckill_product_stock_"+productModel.getId(),productModel.getStock());

        //将大闸的限制数字设到redis内
        redisTemplate.opsForValue().set("seckill_door_count_"+id,productModel.getStock() * 5);
    }

    @Override
    public String generateSeckillToken(Integer seckillId,Integer productId,Integer userId) throws BusinessException {

        //判断是否库存已经售罄
        if (redisTemplate.hasKey("seckill_product_stock_invalid_"+productId)){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //判断是否已经购买过此产品
        if (redisTemplate.hasKey("seckill_"+seckillId+"_user_"+userId)){
            throw new BusinessException(EmBusinessError.USER_HAD_BOUGHT);
        }

        SeckillModel seckillModel = getSeckillByIdInCache(seckillId);
        if (seckillModel == null){
            return null;
        }

        //判断当前时间是否秒杀活动开始时间
        DateTime now = new DateTime();
        if (seckillModel.getStartTime().isAfterNow()){
            seckillModel.setStatus(1);
        }else if (seckillModel.getEndTime().isBeforeNow()){
            seckillModel.setStatus(3);
        }else {
            seckillModel.setStatus(2);
        }
        if (seckillModel.getStatus().intValue() != 2){
            return null;
        }
        //判断秒杀活动和商品id是否对应
        if (seckillModel.getProductId().intValue() != productId.intValue()){
            return null;
        }

        UserModel userModel = userService.getUserByIdInCache(userId);
        if (userModel == null){
            return null;
        }

        //获取秒杀大闸的count数量
        long result = redisTemplate.opsForValue().increment("seckill_door_count_"+seckillId,-1);
        if (result<0){
            return null;
        }

        //生成token并存如redis设置5分钟的有限期
        String token = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set("seckill_token_"+seckillId+"_userId_"+userId,token);
        redisTemplate.expire("seckill_token_"+seckillId+"_userId_"+userId,5,TimeUnit.MINUTES);
        return token;
    }

    @Override
    @Transactional
    public void getOffSeckillById(Integer seckillId) throws BusinessException {

        SeckillDO seckillDO = seckillDOMapper.selectByPrimaryKey(seckillId);
        if (seckillDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        seckillDO.setEndTime(new Date());
        seckillDOMapper.updateByPrimaryKeySelective(seckillDO);

        redisTemplate.delete("seckill_validate_id"+seckillId);
        redisTemplate.opsForValue().set("seckill_door_count_"+seckillId,0);
        redisTemplate.delete("seckill_" + seckillId);

    }

    @Override
    @Transactional
    public void subscribeSeckill(UserModel userModel, Integer seckillId) {

        AnnounceDO announceDO = new AnnounceDO();
        SeckillDO seckillDO = seckillDOMapper.selectByPrimaryKey(seckillId);
        announceDO.setPostTime(seckillDO.getStartTime());
        announceDO.setContent("您订阅的"+ seckillDO.getName() +"秒杀活动将要开始啦");
        announceDO.setUserId(userModel.getId());
        announceDOMapper.insertSelective(announceDO);

    }

    private SeckillModel convertFromSeckillDO(SeckillDO seckillDO){
        if (seckillDO == null){
            return null;
        }
        SeckillModel seckillModel = new SeckillModel();
        BeanUtils.copyProperties(seckillDO,seckillModel);
        seckillModel.setStartTime(new DateTime(seckillDO.getStartTime()));
        seckillModel.setEndTime(new DateTime(seckillDO.getEndTime()));


        return seckillModel;
    }

}
