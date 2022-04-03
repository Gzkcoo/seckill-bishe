package com.seckill.service;

import com.seckill.error.BusinessException;
import com.seckill.service.model.ManagerModel;

public interface ManagerService {
    ManagerModel getManagerById(Integer id) throws BusinessException;

    ManagerModel validateLogin(String phone,String password) throws BusinessException;

    ManagerModel getManagerByIdInCache(Integer id) throws BusinessException;

    ManagerModel updateManagerPermission(ManagerModel managerModel) throws BusinessException;
}
