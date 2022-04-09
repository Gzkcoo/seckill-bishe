package com.seckill.service;

import com.seckill.controller.OrderController;
import com.seckill.dataobject.OrderDO;
import com.seckill.error.BusinessException;
import com.seckill.service.model.OrderModel;
import com.seckill.service.model.UserModel;
import org.apache.catalina.User;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface OrderService {

    OrderModel createOrder(Integer userId, Integer productId, Integer amount,Integer seckillId,String stockLogId) throws BusinessException;

    String createPath(UserModel userModel,Integer seckillId) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    //支付接口
    OrderModel payOrder(UserModel userModel,String orderId, String accountId) throws BusinessException;

    //支付接口
    OrderModel seckillPayOrder(UserModel userModel,OrderModel orderId,Integer seckillId) throws BusinessException;

    //查询用户个人订单
    List<OrderModel> selectOrderListByUserId(Integer userId);

    //普通商品下单
    OrderModel createNormalOrder(Integer userId, Integer productId, Integer amount) throws BusinessException;

    //取消订单
    Boolean cancelOrder(OrderDO orderDO);

    //刷新订单以及回滚库存
    void rollbackOrderAndStock(String orderId) throws BusinessException;
}
