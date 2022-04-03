package com.seckill.controller;

import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonReturnType;
import com.seckill.service.ManagerService;
import com.seckill.service.model.ManagerModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api("manager相关api")
@Controller("manager")
@RequestMapping("/manager")
@CrossOrigin//(allowCredentials="true", allowedHeaders = "*")
public class ManagerController extends BaseController{

    @Autowired
    private ManagerService managerService;

    @Autowired
    private RedisTemplate redisTemplate;

    //管理员登陆接口
    @ApiOperation("管理员登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="phone")String phone,
                                  @RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(phone)||
                StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登陆服务,用来校验用户登陆是否合法
        ManagerModel managerModel = managerService.validateLogin(phone,password);

        //修改成若用户登录验证成功后将对应的登录信息和登录凭证一起存入redis中
        //生成登录凭证token，UUID(必须唯一)
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");
        //建议token和用户登陆态之间的联系
        redisTemplate.opsForValue().set(uuidToken,managerModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);  //超时时间设置为一小时

        //下发token
        return CommonReturnType.create(uuidToken);
    }

    @ApiOperation("获取管理员个人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "已登录管理员唯一id", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getManager(@RequestParam(name = "token") String token) throws BusinessException {

        if (org.apache.commons.lang3.StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        ManagerModel managerModel = (ManagerModel) redisTemplate.opsForValue().get(token);
        if (managerModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        //讲核心领域模型用户对象转化为可供UI使用的viewobject
        //UserVO userVO  = convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(managerModel);
    }


}
