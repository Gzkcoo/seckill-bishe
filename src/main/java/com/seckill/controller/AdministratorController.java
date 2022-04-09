package com.seckill.controller;

import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonReturnType;
import com.seckill.service.ManagerService;
import com.seckill.service.model.ManagerModel;
import com.seckill.service.model.UserModel;
import com.seckill.socket.WebSocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Api("administrator相关api")
@Controller("administrator")
@RequestMapping("/administrator")
@CrossOrigin//(allowCredentials="true", allowedHeaders = "*")
public class AdministratorController extends BaseController{

    @Autowired
    private ManagerService managerService;

    @Autowired
    private WebSocket webSocket;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("改变管理员的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerId", value = "管理员id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "superManager", value = "1为超级管理员，0为普通管理员", required = true,
                    dataType = "Byte")
    })
    @RequestMapping(value = "/manager",method = RequestMethod.POST,consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType UpdateManagerPermission(@RequestParam(name = "managerId")Integer managerId ,
                                                    @RequestParam(name = "superManager")Byte superManager) throws BusinessException {

        //系统管理员身份判断

        ManagerModel managerModel = managerService.getManagerById(managerId);
        if (managerModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        managerModel.setSuperManager(superManager);
        managerModel = managerService.updateManagerPermission(managerModel);
        return CommonReturnType.create(managerModel);
    }


    @ApiOperation("查看所有的管理员")
    @RequestMapping(value = "/manager/list",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getManagerList(@RequestParam(name = "token")String token ) throws BusinessException {

        //系统管理员身份判断

        return CommonReturnType.create(null);
    }

    @ApiOperation("查看所有的用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "已登录管理员id", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/user/list",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getUserList(@RequestParam(name = "token")String token ) throws BusinessException {

        //系统管理员身份判断

        return CommonReturnType.create(null);
    }


    @ApiOperation("群发消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "message", value = "消息内容", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/group/send",method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType groupSendMessage(@RequestParam(name = "message")String message) throws BusinessException {

        CopyOnWriteArraySet<WebSocket> webSocketSet = webSocket.getWebSocketSet();
        //系统管理员身份判断
        if( webSocketSet == null || webSocketSet.size() <= 0 ){
            System.out.println("webSocketSet 为空.....");
            CommonReturnType.create(null);
        }
        for (WebSocket w:webSocketSet){
            w.sendMessage(message);
        }
        return CommonReturnType.create(null);
    }


    @ApiOperation("发个人消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "message", value = "消息内容", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "userId", value = "消息内容", required = true,
                    dataType = "int")
    })
    @RequestMapping(value = "/person/send",method = RequestMethod.POST,consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType personSendMessage(@RequestParam(name = "message")String message,
                                              @RequestParam(name = "userId") Integer userId) throws BusinessException {

        CopyOnWriteArraySet<WebSocket> webSocketSet = webSocket.getWebSocketSet();
        //系统管理员身份判断
        if( webSocketSet == null || webSocketSet.size() <= 0 ){
            System.out.println("webSocketSet 为空.....");
            CommonReturnType.create(null);
        }
        for (WebSocket w:webSocketSet){
            UserModel userModel = (UserModel) redisTemplate.opsForValue().get(w.getToken());
            if (userModel == null){
                continue;
            }
            if (userId.intValue() == userModel.getId().intValue()){
                w.sendMessage(message);
                break;
            }
        }
        return CommonReturnType.create(null);
    }


}
