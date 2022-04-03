package com.seckill.controller;

import com.seckill.dao.ScreenRuleDOMapper;
import com.seckill.dataobject.ScreenRuleDO;
import com.seckill.error.BusinessException;
import com.seckill.response.CommonReturnType;
import com.seckill.service.ScreenService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller("screen")
@RequestMapping("/screen")
@CrossOrigin
public class ScreenController extends BaseController{

    @Autowired
    private ScreenService screenService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ScreenRuleDOMapper screenRuleDOMapper;

    //设置初筛规则
    @ApiOperation(value = "设置初筛规则",notes = "参数为-1时表示不限制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "behindTimeDay", value = "一次记录逾期天数，-1为无限制", required = false,defaultValue = "-1",
                    dataType = "int"),
            @ApiImplicitParam(name = "behindTimeMoney", value = "一次记录逾期最大金额，-1为无限制", required = false,defaultValue = "-1",
                    dataType = "Double"),
            @ApiImplicitParam(name = "behindTimeNum", value = "满足条件的逾期最大次数，-1为无限制", required = false,defaultValue = "-1",
                    dataType = "int"),
            @ApiImplicitParam(name = "valueTime", value = "此年后的统计", required = true,
                    dataType = "String"),
            @ApiImplicitParam(name = "minAge", value = "最小年龄，-1为无限制", required = false,defaultValue = "-1",
                    dataType = "int"),
            @ApiImplicitParam(name = "dishonest", value = "-1为无限制，1为限制失信", required = false,defaultValue = "-1",
                    dataType = "int"),
            @ApiImplicitParam(name = "workState", value = "-1为无限制，0为工作，1为无/失业", required = false,defaultValue = "-1",
                    dataType = "int")
    })
    @RequestMapping(value = "/set",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType setScreenRule(@RequestParam(name = "behindTimeDay",required = false,defaultValue = "-1")Integer behindTimeDay ,
                                          @RequestParam(name = "behindTimeMoney",required = false,defaultValue = "-1")Double behindTimeMoney ,
                                          @RequestParam(name = "behindTimeNum",required = false,defaultValue = "-1")Integer behindTimeNum ,
                                          @RequestParam(name = "valueTime",required = false)String valueTime ,
                                          @RequestParam(name = "minAge",required = false,defaultValue = "-1")Integer minAge ,
                                          @RequestParam(name = "dishonest",required = false,defaultValue = "-1")Integer dishonest ,
                                          @RequestParam(name = "workState",required = false,defaultValue = "-1")Integer workState ) throws BusinessException {

        //判断管理员是否登录

        ScreenRuleDO screenRuleDO = new ScreenRuleDO();
        screenRuleDO.setId(1);
        screenRuleDO.setBehindTimeDay(behindTimeDay);
        screenRuleDO.setBehindTimeMoney(behindTimeMoney);
        screenRuleDO.setBehindTimeNum(behindTimeNum);
        if (valueTime != null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date std=format.parse(valueTime);
                screenRuleDO.setValueTime(std);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        screenRuleDO.setMinAge(minAge);
        screenRuleDO.setDishonest(dishonest);
        screenRuleDO.setWorkState(workState);
        screenRuleDO.setFlag((byte) 1);

        screenService.setScreenRule(screenRuleDO);
        return CommonReturnType.create(null);

    }

    @ApiOperation("设置初筛规则------测试接口")
    @RequestMapping(value = "/put",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType put1(@RequestParam(name = "behindTimeDay",required = false,defaultValue = "-1")Integer behindTimeDay ,
                                          @RequestParam(name = "behindTimeMoney",required = false,defaultValue = "-1")Double behindTimeMoney ,
                                          @RequestParam(name = "behindTimeNum",required = false,defaultValue = "-1")Integer behindTimeNum ,
                                          @RequestParam(name = "valueTime",required = false)Date valueTime ,
                                          @RequestParam(name = "minAge",required = false,defaultValue = "-1")Integer minAge ,
                                          @RequestParam(name = "dishonest",required = false,defaultValue = "-1")Integer dishonest ,
                                          @RequestParam(name = "workState",required = false,defaultValue = "-1")Integer workState ) throws BusinessException {



        redisTemplate.opsForValue().set("ScreenRule",screenRuleDOMapper.selectByPrimaryKey(1));
        return CommonReturnType.create(null);
    }
}
