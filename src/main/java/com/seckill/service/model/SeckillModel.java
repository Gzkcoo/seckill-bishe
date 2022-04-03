package com.seckill.service.model;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class SeckillModel implements Serializable {

    private Integer id;

    //1为未开始，2为进行中，3为已结束
    private Integer status;

    private Integer managerId;

    private String name;

    private DateTime startTime;

    private DateTime endTime;

    private Integer productId;

    private Double seckillPrice;

    private String info;

    private ProductModel productModel;

    public Integer getStatus() {
        return status;
    }

    public ProductModel getProductModel() {
        return productModel;
    }

    public void setProductModel(ProductModel productModel) {
        this.productModel = productModel;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
