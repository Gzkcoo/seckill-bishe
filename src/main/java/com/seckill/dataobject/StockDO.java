package com.seckill.dataobject;

public class StockDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock.id
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock.stock
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    private Integer stock;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock.product_id
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    private Integer productId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock.id
     *
     * @return the value of stock.id
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock.id
     *
     * @param id the value for stock.id
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock.stock
     *
     * @return the value of stock.stock
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock.stock
     *
     * @param stock the value for stock.stock
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock.product_id
     *
     * @return the value of stock.product_id
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock.product_id
     *
     * @param productId the value for stock.product_id
     *
     * @mbg.generated Sat Mar 05 15:48:15 CST 2022
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}