package com.seckill.dataobject;

import java.util.Date;

public class SeckillDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.manager_id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private Integer managerId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.name
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.start_time
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private Date startTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.end_time
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private Date endTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.product_id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private Integer productId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.seckill_price
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private Double seckillPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_info.info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    private String info;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.id
     *
     * @return the value of seckill_info.id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.id
     *
     * @param id the value for seckill_info.id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.manager_id
     *
     * @return the value of seckill_info.manager_id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public Integer getManagerId() {
        return managerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.manager_id
     *
     * @param managerId the value for seckill_info.manager_id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.name
     *
     * @return the value of seckill_info.name
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.name
     *
     * @param name the value for seckill_info.name
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.start_time
     *
     * @return the value of seckill_info.start_time
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.start_time
     *
     * @param startTime the value for seckill_info.start_time
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.end_time
     *
     * @return the value of seckill_info.end_time
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.end_time
     *
     * @param endTime the value for seckill_info.end_time
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.product_id
     *
     * @return the value of seckill_info.product_id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.product_id
     *
     * @param productId the value for seckill_info.product_id
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.seckill_price
     *
     * @return the value of seckill_info.seckill_price
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public Double getSeckillPrice() {
        return seckillPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.seckill_price
     *
     * @param seckillPrice the value for seckill_info.seckill_price
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_info.info
     *
     * @return the value of seckill_info.info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public String getInfo() {
        return info;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_info.info
     *
     * @param info the value for seckill_info.info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }
}