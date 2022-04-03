package com.seckill.service;

import com.seckill.error.BusinessException;
import com.seckill.service.model.UserModel;

public interface UserService {

    public UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    UserModel validateLogin(String phone,String encrptPassword) throws BusinessException;

    //通过缓存获取对象
    UserModel getUserByIdInCache(Integer id);

    //用户初筛
    boolean userScreening(UserModel userModel,Integer seckillId) throws BusinessException;

    //完善用户信息
    UserModel completeUserInfo(UserModel userModel);

}
