package com.seckill.controller;

import com.seckill.controller.viewobject.SeckillVO;
import com.seckill.dataobject.ProductDO;
import com.seckill.dataobject.SeckillDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonReturnType;
import com.seckill.service.CacheService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillService;
import com.seckill.service.model.ManagerModel;
import com.seckill.service.model.ProductModel;
import com.seckill.service.model.SeckillModel;
import com.seckill.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.seckill.controller.BaseController.CONTENT_TYPE_FORMED;

@Controller("seckill")
@RequestMapping("/seckill")
@CrossOrigin
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CacheService cacheService;

    //获取秒杀活动详情
    @ApiOperation("获取秒杀活动详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "秒杀活动唯一id", required = true,
                    dataType = "int")
    })
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getSeckillById(@RequestParam(name = "id")Integer id){

        SeckillModel seckillModel = null;

        //先取本地热点缓存
        seckillModel = (SeckillModel) cacheService.getFromCommonCache("seckill_"+id);
        if (seckillModel == null){
            //根据秒杀id到redis内获取
            seckillModel = (SeckillModel) redisTemplate.opsForValue().get("seckill_"+id);
            //获取秒杀活动
            if (seckillModel  == null){
                seckillModel = seckillService.getSeckillModel(id);
                //设置sckillModel到redis内
                redisTemplate.opsForValue().set("seckill_" + id,seckillModel);
                redisTemplate.expire("seckill_"+id, 10, TimeUnit.MINUTES);
            }
            //填充本地缓存
            cacheService.setCommonCache("seckill_"+id,seckillModel);
        }

        SeckillVO seckillVO = this.convertFromModel(seckillModel);
        return CommonReturnType.create(seckillVO);
    }

    @ApiOperation("获取秒杀活动列表")
    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getSeckillList(){

        List<SeckillModel> list = seckillService.seckillModelList();
        List<SeckillVO> seckillVOList = list.stream().map(seckillModel -> {
            SeckillVO seckillVO = this.convertFromModel(seckillModel);
            return seckillVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(seckillVOList);
    }

    @ApiOperation("获取秒杀活动url的path")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动唯一id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "token", value = "已登录用户唯一识别码", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/getpath",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getPath(@RequestParam(name = "seckillId") Integer seckillId,
                                    @RequestParam(name = "token",required = false) String token) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        String str = orderService.createPath(userModel,seckillId);

        return CommonReturnType.create(str);
    }

    //发布秒杀活动
    @ApiOperation("发布秒杀活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerId", value = "管理员id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "productId", value = "产品id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "startTime", value = "秒杀开始时间", required = true,
                    dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "秒杀结束时间", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "name", value = "秒杀活动名称", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "seckillPrice", value = "秒杀价格", required = true,
                    dataType = "Double"),
            @ApiImplicitParam(name = "info", value = "秒杀活动描述", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "token", value = "已登录管理员唯一id", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/publish",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType publishSeckill(@RequestParam(name = "managerId")Integer managerId,
                                           @RequestParam(name = "productId")Integer productId,
                                           @RequestParam(name = "startTime") String startTime,
                                           @RequestParam(name = "endTime")String endTime,
                                           @RequestParam(name = "name")String name,
                                           @RequestParam(name = "seckillPrice")Double seckillPrice,
                                           @RequestParam(name = "info")String info,
                                           @RequestParam(name = "token")String token) throws BusinessException {
        //判断管理员是否登录
        ManagerModel managerModel = (ManagerModel) redisTemplate.opsForValue().get(token);
        if (managerModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        if (managerModel.getSuperManager().intValue() <= 0){
            throw new BusinessException(EmBusinessError.PERMISSION_ERROR);
        }

        SeckillDO seckillDO = new SeckillDO();
        seckillDO.setManagerId(managerId);
        seckillDO.setName(name);
        seckillDO.setProductId(productId);
        seckillDO.setSeckillPrice(seckillPrice);
        seckillDO.setInfo(info);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date std=format.parse(startTime);
            Date etd=format.parse(endTime);
            seckillDO.setStartTime(std);
            seckillDO.setEndTime(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        seckillService.publishSeckill(seckillDO);
        return CommonReturnType.create(null);
    }

    //发布秒杀活动
    @ApiOperation("发布秒杀活动------测试接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true,
                    dataType = "int"),
    })
    @RequestMapping(value = "/test/publish",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType publishSeckillTest(@RequestParam(name = "seckillId") Integer seckillId){

        SeckillModel seckillModel = seckillService.getSeckillByIdInCache(seckillId);
        ProductModel productModel = seckillModel.getProductModel();
        redisTemplate.delete("seckill_product_stock_"+productModel.getId());
        redisTemplate.opsForValue().set("seckill_product_stock_"+productModel.getId(),productModel.getStock());
        return CommonReturnType.create(null);
    }

    //秒杀活动紧急下线
    @ApiOperation("秒杀活动紧急下线")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true,
                    dataType = "int"),
    })
    @RequestMapping(value = "/getoff",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOffSeckill(@RequestParam(name = "seckillId") Integer seckillId) throws BusinessException {
        seckillService.getOffSeckillById(seckillId);
        return CommonReturnType.create(null);
    }


    private SeckillVO convertFromModel(SeckillModel seckillModel){
        if (seckillModel == null){
            return null;
        }
        SeckillVO seckillVO = new SeckillVO();
        BeanUtils.copyProperties(seckillModel,seckillVO);
        seckillVO.setStartTime(seckillModel.getStartTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        seckillVO.setEndTime(seckillModel.getEndTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        return seckillVO;
    }

}
