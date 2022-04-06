package com.seckill.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.seckill.dao.OrderDOMapper;
import com.seckill.dataobject.AccountDO;
import com.seckill.dataobject.OrderDO;
import com.seckill.dataobject.OverdueRecordDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.mq.MqProducer;
import com.seckill.response.CommonReturnType;
import com.seckill.service.OrderService;
import com.seckill.service.ProductService;
import com.seckill.service.SeckillService;
import com.seckill.service.UserService;
import com.seckill.service.model.OrderModel;
import com.seckill.service.model.UserModel;
import com.seckill.utill.CodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Api("订单相关api")
@Controller("order")
@RequestMapping("/order")
@CrossOrigin
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private UserService userService;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private ProductService productService;

    private ExecutorService executorService;

    private RateLimiter orderCreateRateLimiter;


    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(20);
        orderCreateRateLimiter = RateLimiter.create(300);
    }

    //生成验证码
    @ApiOperation("生成验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "已登录用户唯一id", required = true,
                    dataType = "string"),
    })
    @RequestMapping(value = "/generateverifycode",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public void generateVerifyCode(HttpServletResponse response,@RequestParam(name = "token",required = false) String token) throws BusinessException, IOException {
        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录，不能生成验证码");
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录，不能生成验证码");
        }

        Map<String,Object> map = CodeUtil.generateCodeAndPic();
        redisTemplate.opsForValue().set("verify_code_"+userModel.getId(),map.get("code"));
        redisTemplate.expire("verify_code_"+userModel.getId(),10,TimeUnit.MINUTES);
        ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", response.getOutputStream());

        System.out.println("验证码的值为："+map.get("code"));
    }

    //获得秒杀令牌
    @ApiOperation("生成秒杀令牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "商品id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "verifyCode", value = "秒杀验证码", required = true,
                    dataType = "String"),
            @ApiImplicitParam(name = "token", value = "已登录用户唯一id", required = true,
                    dataType = "String")
    })
    @RequestMapping(value = "/generatetoken",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType generateToken(@RequestParam(name = "productId") Integer productId,
                                          @RequestParam(name = "seckillId",required = false,defaultValue = "0") Integer seckillId,
                                          @RequestParam(name = "verifyCode") String verifyCode,
                                          @RequestParam(name = "token") String token) throws BusinessException {

        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        //通过verifycode验证验证码的有效性
        String redisVerifyCode = (String) redisTemplate.opsForValue().get("verify_code_"+userModel.getId());
        if (StringUtils.isEmpty(redisVerifyCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"请求非法");
        }
        if (!redisVerifyCode.equalsIgnoreCase(verifyCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码错误");
        }

        //用户初筛
        if (redisTemplate.hasKey("screen_active")){
            userService.userScreening(userModel,seckillId);
        }

        //获取秒杀访问令牌
        String seckillToken = seckillService.generateSeckillToken(seckillId,productId,userModel.getId());
        if (seckillToken == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"生成令牌失败");
        }

        return CommonReturnType.create(seckillToken);
    }

    //取消订单
    @ApiOperation("取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "已登录用户唯一id", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "cancel",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType cancelOrder(@RequestParam(name = "token")String token,
                                        @RequestParam(name = "orderId")String orderId) throws BusinessException {

        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        OrderDO orderDO = orderDOMapper.selectByPrimaryKey(orderId);
        if (orderDO == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        if (!orderDO.getUserId().equals(userModel.getId())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        orderService.cancelOrder(orderDO);

        return CommonReturnType.create(null);

    }

    //支付接口
    @ApiOperation("支付接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单编号", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "accountId", value = "用户账户id", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "token", value = "已登录用户唯一id", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "pay",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType payOrder(@RequestParam(name = "orderId") String orderId,
                                     @RequestParam(name = "accountId") String accountId,
                                     @RequestParam(name = "password" ) String password,
                                     @RequestParam(name = "token") String token) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {


        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        //判断密码是否正确
        String encrptPassword = EncodeByMd5(password);
        //比对用户信息内加密的密码是否和传输进来的密码相匹配
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"银行卡密码错误");
        }

        OrderModel orderModel = orderService.payOrder(userModel,orderId,accountId);

        return CommonReturnType.create(orderModel);
    }

    //查看个人历史订单
    @ApiOperation("查看个人历史订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "已登录用户唯一id", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/person/list",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType selectPersonalOrderList(@RequestParam(name = "token") String token) throws BusinessException {

        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        List<OrderModel> orderModels = orderService.selectOrderListByUserId(userModel.getId());

        return CommonReturnType.create(orderModels);
    }


    //普通商品下单
    @ApiOperation("普通商品下单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "amount", value = "数量", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "token", value = "已登录用户唯一id", required = true,
                    dataType = "String")
    })
    @RequestMapping(value = "/normal/create",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "productId") Integer productId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "token") String token) throws BusinessException {


        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        OrderModel orderModel = orderService.createNormalOrder(userModel.getId(),productId,amount);
        return CommonReturnType.create(orderModel);
    }

    //封装下单请求
    @ApiOperation("用户下单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "动态路径", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "productId", value = "产品id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "amount", value = "数量", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "seckillToken", value = "秒杀令牌", required = true,
                    dataType = "String"),
            @ApiImplicitParam(name = "seckillId", value = "秒杀活动id", required = true,
                    dataType = "int"),
            @ApiImplicitParam(name = "token", value = "已登录用户唯一id", required = true,
                    dataType = "string")
    })
    @RequestMapping(value = "/{path}/create",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@PathVariable(name = "path") String path,
                                        @RequestParam(name = "productId") Integer productId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "seckillToken",required = false) String seckillToken,
                                        @RequestParam(name = "seckillId") Integer seckillId,
                                        @RequestParam(name = "token") String token) throws BusinessException {

        //guava限流
        if (!orderCreateRateLimiter.tryAcquire()){
            throw new BusinessException(EmBusinessError.RATELIMITE);
        }

        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        //判断路径是否合法
        String str = (String) redisTemplate.opsForValue().get("seckillPath_seckillId_"+seckillId+"userId_"+userModel.getId());
        if (StringUtils.isEmpty(str)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if (!str.equals(path)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //秒杀令牌是否正确
        String inRedisSeckillToken = (String) redisTemplate.opsForValue().get("seckill_token_"+seckillId+"_userId_"+userModel.getId());
        if (inRedisSeckillToken == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
        }
        if (!org.apache.commons.lang3.StringUtils.equals(inRedisSeckillToken,seckillToken)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
        }

        //同步调用线程池的submit方法
        //拥塞窗口为20的等待队列，用来队列化泄洪
        Future<Object> future = executorService.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                //加入库存流水init状态
                String stockLogId = productService.initStockLog(productId,amount);
                //再去完成下单事务型消息机制
                //OrderModel orderModel = orderService.createOrder(userModel.getId(),productId,amount,seckillId);
                if(!mqProducer.transactionAsyncReduceStock(userModel.getId(),productId,amount,seckillId,stockLogId)){
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"下单失败");
                }
                return null;
            }
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }

        return CommonReturnType.create(null);
    }


    //封装下单请求
    @ApiOperation("用户下单---测试接口")
    @RequestMapping(value = "/test/create",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrderTest(@RequestParam(name = "productId") Integer productId,
                                            @RequestParam(name = "amount") Integer amount,
                                            @RequestParam(name = "seckillId") Integer seckillId,
                                            @RequestParam(name = "token") String token) throws BusinessException {

        //guava限流
        if (!orderCreateRateLimiter.tryAcquire()){
            throw new BusinessException(EmBusinessError.RATELIMITE);
        }

        if (redisTemplate.hasKey("seckill_product_stock_invalid_"+productId)){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        if (StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }


        //同步调用线程池的submit方法
        //拥塞窗口为20的等待队列，用来队列化泄洪
        Future<Object> future = executorService.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                //加入库存流水init状态
                String stockLogId = productService.initStockLog(productId,amount);
                //再去完成下单事务型消息机制
                //OrderModel orderModel = orderService.createOrder(userModel.getId(),productId,amount,seckillId);
                if(!mqProducer.transactionAsyncReduceStock(userModel.getId(),productId,amount,seckillId,stockLogId)){
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"下单失败");
                }
                return null;
            }
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }

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

}
