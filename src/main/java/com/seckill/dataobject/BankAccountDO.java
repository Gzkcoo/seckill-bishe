package com.seckill.dataobject;

public class BankAccountDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column bank_account.id
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column bank_account.name
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column bank_account.balance
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    private Double balance;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column bank_account.id
     *
     * @return the value of bank_account.id
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column bank_account.id
     *
     * @param id the value for bank_account.id
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column bank_account.name
     *
     * @return the value of bank_account.name
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column bank_account.name
     *
     * @param name the value for bank_account.name
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column bank_account.balance
     *
     * @return the value of bank_account.balance
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    public Double getBalance() {
        return balance;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column bank_account.balance
     *
     * @param balance the value for bank_account.balance
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    public void setBalance(Double balance) {
        this.balance = balance;
    }
}