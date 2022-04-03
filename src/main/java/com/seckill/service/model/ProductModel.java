package com.seckill.service.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class ProductModel implements Serializable {

    private Integer id;

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    private Integer deadline;

    private Double rate;

    @NotNull(message = "商品起存价格不能为空")
    @Min(value = 0,message = "起存价格必须大于0")
    private Double startAmount;

    private Double increAmount;

    private Double personLimit;

    private Double dayLimit;

    private String risk;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getDeadline() {
        return deadline;
    }

    public void setDeadline(Integer deadline) {
        this.deadline = deadline;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(Double startAmount) {
        this.startAmount = startAmount;
    }

    public Double getIncreAmount() {
        return increAmount;
    }

    public void setIncreAmount(Double increAmount) {
        this.increAmount = increAmount;
    }

    public Double getPersonLimit() {
        return personLimit;
    }

    public void setPersonLimit(Double personLimit) {
        this.personLimit = personLimit;
    }

    public Double getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(Double dayLimit) {
        this.dayLimit = dayLimit;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    private Date valueDate;

    private String method;

    private Date endDate;

    private Integer sales;

    private String description;

    @NotNull(message = "商品类别不能不填")
    private Byte flag;

    private int stock;

}

