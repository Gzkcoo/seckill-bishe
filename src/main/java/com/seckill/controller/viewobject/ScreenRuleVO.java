package com.seckill.controller.viewobject;

import org.joda.time.DateTime;

public class ScreenRuleVO {

    private Integer id;

    private Integer behindTimeDay;

    private Double behindTimeMoney;

    private Integer behindTimeNum;

    private Byte flag;

    private Integer minAge;

    private Integer workState;

    private Integer dishonest;

    private String valueTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBehindTimeDay() {
        return behindTimeDay;
    }

    public void setBehindTimeDay(Integer behindTimeDay) {
        this.behindTimeDay = behindTimeDay;
    }

    public Double getBehindTimeMoney() {
        return behindTimeMoney;
    }

    public void setBehindTimeMoney(Double behindTimeMoney) {
        this.behindTimeMoney = behindTimeMoney;
    }

    public Integer getBehindTimeNum() {
        return behindTimeNum;
    }

    public void setBehindTimeNum(Integer behindTimeNum) {
        this.behindTimeNum = behindTimeNum;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getWorkState() {
        return workState;
    }

    public void setWorkState(Integer workState) {
        this.workState = workState;
    }

    public Integer getDishonest() {
        return dishonest;
    }

    public void setDishonest(Integer dishonest) {
        this.dishonest = dishonest;
    }

    public String getValueTime() {
        return valueTime;
    }

    public void setValueTime(String valueTime) {
        this.valueTime = valueTime;
    }
}
