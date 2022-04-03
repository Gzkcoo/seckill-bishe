package com.seckill.config;//package com.seckill.config;

import com.seckill.error.BusinessException;
import com.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {


    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // redis 客户端监听 Redis 16库 每个库对应不同的业务逻辑 前缀 order_timeOut_支付id
        String key = message.toString();//获取到失效的key，根据业务需要对这个key进行处理，例如删除订单，返还库存等操作
        System.out.println(key+"----key已经过期!");
        //回补库存以及订单状态更新

        if (key.length() < 12){
            return;
        }

        if ("ex_orderId_".equals(key.substring(0,11))){
            String orderId = key.substring(11);
            Integer productId = (Integer) redisTemplate.opsForValue().get(key+"_1");
            redisTemplate.delete(key+"_1");
            try {
                orderService.rollbackOrderAndStock(orderId);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            redisTemplate.opsForValue().increment("seckill_product_stock_"+productId,1);
            redisTemplate.delete("seckill_product_stock_invalid_"+productId);
        }
    }
}
