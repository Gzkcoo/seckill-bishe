package com.seckill.dao;

import com.seckill.dataobject.SeckillDO;

import java.util.List;

public interface SeckillDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    int insert(SeckillDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    int insertSelective(SeckillDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    SeckillDO selectByPrimaryKey(Integer id);
    SeckillDO selectByProductId(Integer productId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    int updateByPrimaryKeySelective(SeckillDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seckill_info
     *
     * @mbg.generated Sun Mar 06 16:06:14 CST 2022
     */
    int updateByPrimaryKey(SeckillDO record);
    List<SeckillDO> seckillDOList();
}