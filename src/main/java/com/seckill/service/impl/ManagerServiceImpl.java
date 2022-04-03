package com.seckill.service.impl;

import com.seckill.dao.ManagerDOMapper;
import com.seckill.dao.ManagerPermissionDOMapper;
import com.seckill.dataobject.ManagerDO;
import com.seckill.dataobject.ManagerPermissionDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.ManagerService;
import com.seckill.service.model.ManagerModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerDOMapper managerDOMapper;

    @Autowired
    private ManagerPermissionDOMapper managerPermissionDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ManagerModel getManagerById(Integer id) throws BusinessException{

        ManagerDO managerDO = managerDOMapper.selectByPrimaryKey(id);
        if (managerDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        ManagerPermissionDO managerPermissionDO = managerPermissionDOMapper.selectByPrimaryKey(id);
        if (managerPermissionDO == null){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        ManagerModel managerModel = this.convertFromDataObject(managerDO,managerPermissionDO);
        return managerModel;
    }

    @Override
    public ManagerModel validateLogin(String phone, String password) throws BusinessException {
        ManagerDO managerDO = managerDOMapper.selectByPhone(phone);
        if (managerDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if (!StringUtils.equals(password,managerDO.getPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        ManagerPermissionDO managerPermissionDO = managerPermissionDOMapper.selectByPrimaryKey(managerDO.getId());
        ManagerModel managerModel = this.convertFromDataObject(managerDO,managerPermissionDO);
        return managerModel;
    }

    @Override
    public ManagerModel getManagerByIdInCache(Integer id) throws BusinessException {
        ManagerModel managerModel = (ManagerModel) redisTemplate.opsForValue().get("manager_validate_" + id);
        if (managerModel == null){
            managerModel = this.getManagerById(id);
            redisTemplate.opsForValue().set("manager_validate_" + id,managerModel);
            redisTemplate.expire("manager_validate_" + id,10, TimeUnit.MINUTES);
        }
        return managerModel;
    }

    @Override
    @Transactional
    public ManagerModel updateManagerPermission(ManagerModel managerModel) throws BusinessException {
        ManagerPermissionDO managerPermissionDO = new ManagerPermissionDO();
        managerPermissionDO.setManagerId(managerModel.getId());
        managerPermissionDO.setSuperManager(managerModel.getSuperManager());
        managerPermissionDOMapper.updateByPrimaryKeySelective(managerPermissionDO);
        return getManagerById(managerModel.getId());
    }

    private ManagerModel convertFromDataObject(ManagerDO managerDO,ManagerPermissionDO managerPermissionDO){
        ManagerModel managerModel = new ManagerModel();
        BeanUtils.copyProperties(managerDO,managerModel);
        managerModel.setSuperManager(managerPermissionDO.getSuperManager());
        return managerModel;
    }
}
