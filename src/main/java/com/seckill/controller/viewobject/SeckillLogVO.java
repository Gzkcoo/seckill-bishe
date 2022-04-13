package com.seckill.controller.viewobject;

public class SeckillLogVO {

    private Integer userId;
    private Integer seckillId;
    private String phone;
    private String userName;
    private String seckillName;
    private String msg;
    private String seckillTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Integer seckillId) {
        this.seckillId = seckillId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSeckillName() {
        return seckillName;
    }

    public void setSeckillName(String seckillName) {
        this.seckillName = seckillName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSeckillTime() {
        return seckillTime;
    }

    public void setSeckillTime(String seckillTime) {
        this.seckillTime = seckillTime;
    }
}
