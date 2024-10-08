package com.seckill.service;

import com.seckill.controller.viewobject.SeckillLogVO;
import com.seckill.dataobject.SeckillDO;
import com.seckill.dataobject.SeckillLogDO;
import com.seckill.error.BusinessException;
import com.seckill.service.model.SeckillModel;
import com.seckill.service.model.UserModel;

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

    //订阅秒杀提醒
    void subscribeSeckill(UserModel userModel, Integer seckillId);

    //成功下单秒杀活动日志
    List<SeckillLogVO> getSeckillLogSuccess(Integer seckillId);


}
