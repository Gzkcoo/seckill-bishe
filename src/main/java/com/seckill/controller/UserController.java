package com.seckill.controller;

import com.seckill.controller.viewobject.UserVO;
import com.seckill.dao.UserDOMapper;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonReturnType;
import com.seckill.service.UserService;
import com.seckill.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api("user相关api")
@Controller("user")
@RequestMapping("/user")
@CrossOrigin//(allowCredentials="true", allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //用户登陆接口
    @ApiOperation("用户登录")
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
        UserModel userModel = userService.validateLogin(phone,this.EncodeByMd5(password));

        //修改成若用户登录验证成功后将对应的登录信息和登录凭证一起存入redis中
        //生成登录凭证token，UUID(必须唯一)
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");
        //建议token和用户登陆态之间的联系
        redisTemplate.opsForValue().set(uuidToken,userModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);  //超时时间设置为一小时

        //将登陆凭证加入到用户登陆成功的session内
//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        //下发token
        return CommonReturnType.create(uuidToken);
    }

    @ApiOperation("判断验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "otpCode", value = "验证码", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/judgeotp",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType judgeOtp(@RequestParam(name="phone")String phone,
                                     @RequestParam(name="otpCode")String otpCode) throws BusinessException {
        //验证手机号和对应的otpcode相符合}
        String inSessionOtpCode = (String) redisTemplate.opsForValue().get(phone);
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }
        redisTemplate.expire(phone,3,TimeUnit.MINUTES);
        return CommonReturnType.create(null);
        //判断验证码
    }

    //用户注册接口
    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "otpCode", value = "验证码", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="phone")String phone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="password")String password,
                                     @RequestParam(name="confirmPassword")String confirmPassword) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otpcode相符合
        String inSessionOtpCode = (String) redisTemplate.opsForValue().get(phone);
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码过期");
        }
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"两次输入密码不一致");
        }
        //用户的注册流程
        Random random = new Random();
        UserModel userModel = new UserModel();
        userModel.setPhone(phone);
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userModel.setDishonest(random.nextInt(2));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }


    //完善个人信息
    //用户注册接口
    @ApiOperation("用户信息完善")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "name", value = "真实姓名", required = false,
                    dataType = "string"),
            @ApiImplicitParam(name = "gender", value = "性别", required = false,
                    dataType = "byte"),
            @ApiImplicitParam(name = "age", value = "年龄", required = false,
                    dataType = "int"),
            @ApiImplicitParam(name = "nickName", value = "昵称", required = false,
                    dataType = "string"),
            @ApiImplicitParam(name = "workState", value = "工作状态（工作/无业/失业）", required = false,
                    dataType = "string"),
            @ApiImplicitParam(name = "idCard", value = "身份证", required = false,
                    dataType = "string")
    })
    @RequestMapping(value = "/complete",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType completeUserInfo(@RequestParam(name="phone",required = true) String phone,
                                            @RequestParam(name="name",required = false)String name,
                                             @RequestParam(name="gender",required = false)Byte gender,
                                             @RequestParam(name="age",required = false) Integer age,
                                             @RequestParam(name="nickName",required = false)String nickName,
                                             @RequestParam(name="workState",required = false)String workState,
                                             @RequestParam(name="idCard",required = false)String idCard) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {


        UserModel userModel = userService.getUserById(userDOMapper.selectByPhone(phone).getId());
        if (name != null){
            userModel.setName(java.net.URLDecoder.decode(name,"UTF-8"));
        }
        userModel.setGender(gender);
        userModel.setAge(age);
        if (nickName != null){
            userModel.setNickName(java.net.URLDecoder.decode(nickName,"UTF-8"));
        }
        if (workState != null){
            userModel.setWorkState(java.net.URLDecoder.decode(workState,"UTF-8"));
        }
        userModel.setIdCard(idCard);
        userService.completeUserInfo(userModel);

        return CommonReturnType.create(null);
    }


    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    //用户获取otp短信接口
    @ApiOperation("用户获取otp短信接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="phone")String phone){
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt =  random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        otpCode = "11111";

        //将OTP验证码同对应用户的手机号关联，使用httpsession的方式绑定他的手机号与OTPCODE
        redisTemplate.opsForValue().set(phone,otpCode);
        redisTemplate.expire(phone,1,TimeUnit.MINUTES);
        //httpServletRequest.getSession().setAttribute(phone,otpCode);

        //将OTP验证码通过短信通道发送给用户,省略
        System.out.println("phone = " + phone + " & otpCode = "+otpCode);

        return CommonReturnType.create(null);
    }


    @ApiOperation("获取用户个人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "已登录用户唯一识别码", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "token") String token) throws BusinessException {

        if (org.apache.commons.lang3.StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        //讲核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO  = convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }


}
