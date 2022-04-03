package com.seckill.service;

import com.seckill.dataobject.SeckillDO;
import com.seckill.error.BusinessException;
import com.seckill.service.model.SeckillModel;

import java.util.List;

public interface SeckillService {


    SeckillModel getSeckillModel(Integer id);

    List<SeckillModel> seckillModelList();

    //product及seckill缓存模型
    SeckillModel getSeckillByIdInCache(Integer id);

    //活动发布
    void publishSeckill(SeckillDO seckillDO);
    
    //生成秒杀用的令牌
    String generateSeckillToken(Integer seckillId,Integer productId,Integer userId) throws BusinessException;

    //紧急下线
    void getOffSeckillById(Integer id) throws BusinessException;


}
