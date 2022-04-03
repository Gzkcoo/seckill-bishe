package com.seckill.service.impl;

import com.seckill.dao.*;
import com.seckill.dataobject.*;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.mq.MqProducer;
import com.seckill.service.OrderService;
import com.seckill.service.ProductService;
import com.seckill.service.SeckillService;
import com.seckill.service.UserService;
import com.seckill.service.model.OrderModel;
import com.seckill.service.model.ProductModel;
import com.seckill.service.model.SeckillModel;
import com.seckill.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;
import sun.security.provider.MD5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockDOMapper stockDOMapper;

    @Autowired
    private AccountDOMapper accountDOMapper;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private AccountFlowDOMapper accountFlowDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private ProductDOMapper productDOMapper;

    @Autowired
    private SeckillDOMapper seckillDOMapper;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Autowired
    private BankAccountDOMapper bankAccountDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer productId, Integer amount,Integer seckillId,String stockLogId) throws BusinessException {
        //1.校验下单状态，商品是否存在，用户是否合法
//        UserModel userModel = userService.getUserByIdInCache(userId);
//        if (userModel == null){
//            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
//        }
        if (amount <=0 || amount >=99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不合法");
        }

//        SeckillDO seckillDO = seckillDOMapper.selectByPrimaryKey(seckillId);
//        if (seckillId > 0 && seckillDO == null){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀活动信息不存在");
//        }

        SeckillModel seckillModel = seckillService.getSeckillByIdInCache(seckillId);

        //2.落单减库存
        boolean result = productService.decreaseStock(productId,amount);
        if (!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setAmount(amount);
        orderModel.setUserId(userId);
        orderModel.setProductId(productId);
        //orderModel.setPayTime(new Date());
        orderModel.setCreateTime(new Date());
        orderModel.setStatus(0);
        orderModel.setSeckillId(seckillId);
        if (seckillId > 0){
            orderModel.setPayMoney(seckillDOMapper.selectByPrimaryKey(seckillId).getSeckillPrice() * amount);
        }else {
            orderModel.setPayMoney(seckillModel.getProductModel().getStartAmount() * amount);
        }
        //生成交易流水号
        orderModel.setId(this.generateOrderNo());
        OrderDO orderDO = this.convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //5.设置库存流水状态成功
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if (stockLogDO == null){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        stockLogDO.setStatus(2);
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);

        //返回前端
        return orderModel;
    }

    @Override
    public String createPath(UserModel userModel, Integer seckillId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String uuid = UUID.randomUUID().toString().replace("-","");
        String uuidMD5 = EncodeByMd5(uuid);
        redisTemplate.opsForValue().set("seckillPath_seckillId_"+seckillId+"userId_"+userModel.getId(),uuidMD5);
        redisTemplate.expire("seckillPath_seckillId_"+seckillId+"userId_"+userModel.getId(),1, TimeUnit.MINUTES);
        return uuidMD5;
    }

    @Override
    @Transactional
    public OrderModel payOrder(UserModel userModel,String orderId, String accountId) throws BusinessException {

        OrderDO orderDO = orderDOMapper.selectByPrimaryKey(orderId);
        if (orderDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if (!orderDO.getUserId().equals(userModel.getId())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //判断账户余额是否满足
        Boolean flag = false;
        List<AccountDO> accountDOS = userModel.getAccountDOS();
        for (AccountDO accountDO : accountDOS){
            if (StringUtils.equals(accountId,accountDO.getId())){
                if (accountDO.getAvailableBalance() >= orderDO.getPayMoney() ){
                    accountDO.setAvailableBalance(accountDO.getAvailableBalance()-orderDO.getPayMoney());
                    accountDO.setAllExpend(orderDO.getPayMoney());
                    //更新账户余额
                    accountDOMapper.updateByPrimaryKeySelective(accountDO);
                    flag = true;
                    break;
                }else {
                    throw new BusinessException(EmBusinessError.ACCOUNT_MONEY_ENOUGH);
                }
            }
        }
        if (!flag){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //更新银行账户
        BankAccountDO bankAccountDO = bankAccountDOMapper.selectByPrimaryKey(1);
        bankAccountDO.setBalance(bankAccountDO.getBalance() + orderDO.getPayMoney());
        bankAccountDOMapper.updateByPrimaryKeySelective(bankAccountDO);

        //更新销量
        SeckillDO seckillDO = seckillDOMapper.selectByPrimaryKey(orderDO.getSeckillId());
        productService.increaseSales(seckillDO.getProductId(),orderDO.getAmount());

        //更新账户流水信息
        AccountFlowDO accountFlowDO = new AccountFlowDO();
        accountFlowDO.setAccountId(accountId);
        accountFlowDO.setId(UUID.randomUUID().toString().replace("-",""));
        accountFlowDO.setTime(new Date());
        accountFlowDO.setChangeMoney(-1 * orderDO.getPayMoney());
        accountFlowDO.setMsg("支付秒杀活动:"+seckillDO.getName());
        accountFlowDOMapper.insertSelective(accountFlowDO);

        //更新订单状态
        orderDO.setPayTime(new Date());
        orderDO.setStatus(2);
        orderDOMapper.updateByPrimaryKeySelective(orderDO);

        OrderModel orderModel = this.convertFromOrderDO(orderDO);

        redisTemplate.delete("ex_orderId_"+orderModel.getId());
        redisTemplate.delete("ex_orderId_"+orderModel.getId()+"_1");

        return orderModel;
    }

    @Override
    public List<OrderModel> selectOrderListByUserId(Integer userId) {
        List<OrderDO> orderDOS = orderDOMapper.selectByUserId(userId);
        List<OrderModel> orderModels = orderDOS.stream().map(orderDO ->{
            OrderModel orderModel = this.convertFromOrderDO(orderDO);
            return orderModel;
        }).collect(Collectors.toList());
        return orderModels;
    }

    @Override
    @Transactional
    public OrderModel createNormalOrder(Integer userId, Integer productId, Integer amount) throws BusinessException {

        ProductModel productModel = productService.getProductById(productId);
        if (productModel.getStock() <= 0){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //下单减库存
        stockDOMapper.decreaseStock(productId,amount);

        //生成订单
        OrderDO orderDO = new OrderDO();
        orderDO.setId(this.generateOrderNo());
        orderDO.setStatus(0);
        orderDO.setCreateTime(new Date());
        orderDO.setAmount(amount);
        orderDO.setProductId(productId);
        orderDO.setUserId(userId);
        orderDO.setPayMoney(productModel.getStartAmount() * amount);
        orderDOMapper.insertSelective(orderDO);

        return this.convertFromOrderDO(orderDO);
    }

    @Override
    @Transactional
    public Boolean cancelOrder(OrderDO orderDO) {

        if (orderDO.getSeckillId() > 0){
            //回补库存
            redisTemplate.delete("ex_orderId_"+orderDO.getId());
            redisTemplate.delete("ex_orderId_"+orderDO.getId()+"_1");
            redisTemplate.opsForValue().increment("seckill_product_stock_"+orderDO.getProductId(),orderDO.getAmount().intValue());
            redisTemplate.delete("seckill_product_stock_invalid_"+orderDO.getProductId());
        }
        orderDO.setStatus(1);
        orderDOMapper.updateByPrimaryKeySelective(orderDO);
        stockDOMapper.increaseStock(orderDO.getProductId(),orderDO.getAmount());

        return true;
    }

    @Override
    @Transactional
    public void rollbackOrderAndStock(String orderId) throws BusinessException {
        OrderDO orderDO = orderDOMapper.selectByPrimaryKey(orderId);
        if (orderDO == null){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        orderDO.setStatus(1);
        orderDOMapper.updateByPrimaryKeySelective(orderDO);
        stockDOMapper.increaseStock(orderDO.getProductId(),orderDO.getAmount());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderNo(){
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //中间6位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO =  sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6-sequenceStr.length();i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //最后2位为分库分表位,暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    private OrderModel convertFromOrderDO(OrderDO orderDO){
        if (orderDO == null){
            return null;
        }
        OrderModel orderModel = new OrderModel();
        BeanUtils.copyProperties(orderDO,orderModel);
        return orderModel;
    }

    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if (orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        return orderDO;
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

}
