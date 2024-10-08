package com.seckill.dao;

import com.seckill.dataobject.BankAccountDO;

public interface BankAccountDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bank_account
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bank_account
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    int insert(BankAccountDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bank_account
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    int insertSelective(BankAccountDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bank_account
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    BankAccountDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bank_account
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    int updateByPrimaryKeySelective(BankAccountDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table bank_account
     *
     * @mbg.generated Mon Mar 14 16:26:59 CST 2022
     */
    int updateByPrimaryKey(BankAccountDO record);

    int updateBankAmount(Double amount);
}