package com.seckill.service.impl;

import com.seckill.dao.ScreenRuleDOMapper;
import com.seckill.dataobject.ScreenRuleDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScreenServiceImpl implements ScreenService {

    @Autowired
    private ScreenRuleDOMapper screenRuleDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public boolean setScreenRule(ScreenRuleDO screenRuleDO) throws BusinessException {

        if (screenRuleDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        int result = screenRuleDOMapper.updateByPrimaryKeySelective(screenRuleDO);
        if (result <= 0){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"网络繁忙请稍后再试");
        }

        //将初筛规则设置到redis
        redisTemplate.opsForValue().set("ScreenRule",screenRuleDO);

        return true;
    }
}
