package com.seckill.dataobject;

public class ManagerPermissionDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column manager_permission.manager_id
     *
     * @mbg.generated Sat Mar 19 14:03:24 CST 2022
     */
    private Integer managerId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column manager_permission.super_manager
     *
     * @mbg.generated Sat Mar 19 14:03:24 CST 2022
     */
    private Byte superManager;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column manager_permission.manager_id
     *
     * @return the value of manager_permission.manager_id
     *
     * @mbg.generated Sat Mar 19 14:03:24 CST 2022
     */
    public Integer getManagerId() {
        return managerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column manager_permission.manager_id
     *
     * @param managerId the value for manager_permission.manager_id
     *
     * @mbg.generated Sat Mar 19 14:03:24 CST 2022
     */
    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column manager_permission.super_manager
     *
     * @return the value of manager_permission.super_manager
     *
     * @mbg.generated Sat Mar 19 14:03:24 CST 2022
     */
    public Byte getSuperManager() {
        return superManager;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column manager_permission.super_manager
     *
     * @param superManager the value for manager_permission.super_manager
     *
     * @mbg.generated Sat Mar 19 14:03:24 CST 2022
     */
    public void setSuperManager(Byte superManager) {
        this.superManager = superManager;
    }
}