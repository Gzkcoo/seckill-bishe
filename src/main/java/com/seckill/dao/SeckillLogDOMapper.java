package com.seckill.dao;

import com.seckill.dataobject.SeckillLogDO;

public interface SeckillLogDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_log
     *
     * @mbg.generated Sun Mar 13 20:08:26 CST 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_log
     *
     * @mbg.generated Sun Mar 13 20:08:26 CST 2022
     */
    int insert(SeckillLogDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_log
     *
     * @mbg.generated Sun Mar 13 20:08:26 CST 2022
     */
    int insertSelective(SeckillLogDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_log
     *
     * @mbg.generated Sun Mar 13 20:08:26 CST 2022
     */
    SeckillLogDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_log
     *
     * @mbg.generated Sun Mar 13 20:08:26 CST 2022
     */
    int updateByPrimaryKeySelective(SeckillLogDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_log
     *
     * @mbg.generated Sun Mar 13 20:08:26 CST 2022
     */
    int updateByPrimaryKey(SeckillLogDO record);
}