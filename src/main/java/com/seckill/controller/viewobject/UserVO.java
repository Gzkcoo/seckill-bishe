package com.seckill.controller.viewobject;

import com.seckill.dataobject.AccountDO;

import java.util.List;

public class UserVO {

    private Integer id;
    private String name;
    private String nickName;
    private Byte gender;
    private Integer age;
    private String phone;
    private String idCard;
    private String workState;

    public List<AccountDO> getAccountDOS() {
        return accountDOS;
    }

    public void setAccountDOS(List<AccountDO> accountDOS) {
        this.accountDOS = accountDOS;
    }

    private List<AccountDO> accountDOS;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

}
