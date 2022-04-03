package com.seckill.service.impl;

import com.seckill.dao.*;
import com.seckill.dataobject.*;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.service.UserService;
import com.seckill.service.model.UserModel;
import com.seckill.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private AccountDOMapper accountDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ScreenRuleDOMapper screenRuleDOMapper;

    @Autowired
    private OverdueRecordDOMapper overdueRecordDOMapper;

    @Autowired
    private SeckillLogDOMapper seckillLogDOMapper;
    
    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null){
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);
        List<AccountDO> accountDOS = accountDOMapper.selectByUserId(id);

        return convertFromDataObject(userDO,userPasswordDO,accountDOS);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        ValidationResult result =  validator.validate(userModel);
//        if(result.isHasErrors()){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
//        }

        //实现model->dataobject方法
        UserDO userDO = convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);
        }catch(DuplicateKeyException ex){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已重复注册");
        }

        //实现创建银行卡号
        Random random = new Random();
        userModel.setId(userDO.getId());
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);
        stringBuilder.append(userModel.getPhone().substring(3));
        AccountDO accountDO = new AccountDO();
        accountDO.setId(stringBuilder.toString());
        accountDO.setUserId(userModel.getId());
        accountDO.setAvailableBalance(random.nextDouble()*50000);

        try{
            accountDOMapper.insertSelective(accountDO);
        }catch (DuplicateKeyException ex){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    @Override
    public UserModel validateLogin(String phone, String encrptPassword) throws BusinessException {
        //通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByPhone(phone);
        if(userDO == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        List<AccountDO> accountDOs = accountDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPasswordDO,accountDOs);
        //比对用户信息内加密的密码是否和传输进来的密码相匹配
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    @Override
    public UserModel getUserByIdInCache(Integer id) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get("user_validate_" + id);
        if (userModel == null){
            userModel = this.getUserById(id);
            redisTemplate.opsForValue().set("user_validate_" + id,userModel);
            redisTemplate.expire("user_validate_" + id,10, TimeUnit.MINUTES);
        }
        return userModel;
    }

    @Override
    public boolean userScreening(UserModel userModel,Integer seckillId) throws BusinessException {

        ScreenRuleDO screenRuleDO = (ScreenRuleDO) redisTemplate.opsForValue().get("ScreenRule");
        if (screenRuleDO == null){
            screenRuleDO = screenRuleDOMapper.selectByPrimaryKey(1);
        }

        SeckillLogDO seckillLogDO = new SeckillLogDO();
        seckillLogDO.setSeckillId(seckillId);
        seckillLogDO.setUserId(userModel.getId());
        seckillLogDO.setTime(new Date());
        seckillLogDO.setUserName(userModel.getName());
        //是否列入失信名单
        if (screenRuleDO.getDishonest() > 0){
            if (userModel.getDishonest() == 1){
                seckillLogDO.setStatus(1);
                seckillLogDO.setMsg("被列入失信名单");
                seckillLogDOMapper.insertSelective(seckillLogDO);
                throw new BusinessException(EmBusinessError.HONEST_NOT_FIT);
            }
        }

        //年龄筛选
        if (screenRuleDO.getMinAge() > 0){
            if (screenRuleDO.getMinAge().intValue() > userModel.getAge().intValue()){
                seckillLogDO.setStatus(1);
                seckillLogDO.setMsg("用户年龄不符合");
                seckillLogDOMapper.insertSelective(seckillLogDO);
                throw new BusinessException(EmBusinessError.AGE_NOT_FIT);
            }
        }

        //工作状态筛选
        if (screenRuleDO.getWorkState() > 0){
            if ("失业".equals(userModel.getWorkState()) || "无业".equals(userModel.getWorkState())){
                seckillLogDO.setStatus(1);
                seckillLogDO.setMsg("工作状态不符合");
                seckillLogDOMapper.insertSelective(seckillLogDO);
                throw new BusinessException(EmBusinessError.WORKSTATE_NOT_FIT);
            }
        }

        //逾期记录删选
        if (screenRuleDO.getBehindTimeNum() > 0){
            //逾期记录次数
            int count = 0;
            List<OverdueRecordDO> overdueRecordDOS = userModel.getOverdueRecordDOS();
            for (OverdueRecordDO overdueRecordDO : overdueRecordDOS){
                //有效时间段
                Date valueTime = screenRuleDO.getValueTime();
                Date recordTime = overdueRecordDO.getTime();
                if (valueTime.after(recordTime)){
                    continue;
                }
                //逾期时间
                if (screenRuleDO.getBehindTimeDay() > 0){
                    if (overdueRecordDO.getBehindTimeDay() > screenRuleDO.getBehindTimeDay()){
                        count++;
                        continue;
                    }
                }
                //逾期金额
                if (screenRuleDO.getBehindTimeMoney() > 0){
                    if (overdueRecordDO.getBehindTimeMoney() > screenRuleDO.getBehindTimeMoney()){
                        count++;
                        continue;
                    }
                }
                if (count >  screenRuleDO.getBehindTimeNum()){
                    seckillLogDO.setStatus(1);
                    seckillLogDO.setMsg("逾期次数过多");
                    seckillLogDOMapper.insertSelective(seckillLogDO);
                    throw new BusinessException(EmBusinessError.BEHINDTIME_NOT_FIT);
                }

            }
        }
        return true;
    }

    @Override
    @Transactional
    public UserModel completeUserInfo(UserModel userModel) {
        UserDO userDO = this.convertFromModel(userModel);
        userDOMapper.updateByPrimaryKeySelective(userDO);
        return this.getUserById(userModel.getId());
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO,List<AccountDO> accountDOS){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO != null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }

        if (accountDOS != null){
            userModel.setAccountDOS(accountDOS);
        }

        List<OverdueRecordDO> overdueRecordDOS = overdueRecordDOMapper.selectByUserId(userDO.getId());
        userModel.setOverdueRecordDOS(overdueRecordDOS);

        return userModel;
    }


}
