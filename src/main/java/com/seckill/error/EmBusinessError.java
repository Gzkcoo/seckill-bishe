package com.seckill.error;


public enum EmBusinessError implements CommonError {
    //通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),
    PERMISSION_ERROR(10003,"无此权限"),

    //20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAIL(20002,"用户手机号或密码不正确"),
    USER_NOT_LOGIN(20003,"用户还未登陆"),
    USER_HAD_BOUGHT(20004,"用户仅限购买一次"),
    //30000开头为交易信息错误定义
    STOCK_NOT_ENOUGH(30001,"库存不足"),
    MQ_SEND_FAIL(30002,"库存异步消息失败"),
    RATELIMITE(30003,"活动火爆，请稍后再试"),
    //40000开头为初筛信息不符合
    AGE_NOT_FIT(40001,"年龄不符合"),
    HONEST_NOT_FIT(40002,"被列为失信名单"),
    WORKSTATE_NOT_FIT(40003,"工作状态不符合"),
    BEHINDTIME_NOT_FIT(40004,"逾期次数过多"),
    SCREEN_NOT_ACTIVE(4005,"风控引擎未开启"),
    //5000开头为账户信息错误
    ACCOUNT_MONEY_ENOUGH(50001,"账户余额不足")
    ;

    EmBusinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }


    private int errCode;
    private String errMsg;


    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
