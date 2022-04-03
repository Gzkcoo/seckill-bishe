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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api("administrator相关api")
@Controller("administrator")
@RequestMapping("/administrator")
@CrossOrigin//(allowCredentials="true", allowedHeaders = "*")
public class AdministratorController extends BaseController{

    @Autowired
    private ManagerService managerService;

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


}
