package com.seckill.service;

import com.seckill.dataobject.ScreenRuleDO;
import com.seckill.error.BusinessException;

public interface ScreenService {

    //设置初筛规则
    boolean setScreenRule(ScreenRuleDO screenRuleDO) throws BusinessException;

}
