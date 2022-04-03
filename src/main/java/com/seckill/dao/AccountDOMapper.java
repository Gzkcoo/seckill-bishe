package com.seckill.dao;

import com.seckill.dataobject.AccountDO;

import java.util.List;

public interface AccountDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_info
     *
     * @mbg.generated Fri Mar 04 19:25:56 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_info
     *
     * @mbg.generated Fri Mar 04 19:25:56 CST 2022
     */
    int insert(AccountDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_info
     *
     * @mbg.generated Fri Mar 04 19:25:56 CST 2022
     */
    int insertSelective(AccountDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_info
     *
     * @mbg.generated Fri Mar 04 19:25:56 CST 2022
     */
    AccountDO selectByPrimaryKey(String id);

    List<AccountDO> selectByUserId(Integer userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_info
     *
     * @mbg.generated Fri Mar 04 19:25:56 CST 2022
     */
    int updateByPrimaryKeySelective(AccountDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table account_info
     *
     * @mbg.generated Fri Mar 04 19:25:56 CST 2022
     */
    int updateByPrimaryKey(AccountDO record);


}