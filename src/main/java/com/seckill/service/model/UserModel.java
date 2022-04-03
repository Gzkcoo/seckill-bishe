package com.seckill.service.model;

import com.seckill.dataobject.AccountDO;
import com.seckill.dataobject.OverdueRecordDO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class UserModel implements Serializable {

    private Integer id;
    @NotBlank(message = "用户名不能为空")
    private String name;
    private String nickName;

    @NotNull(message = "性别不能不写")
    private Byte gender;

    @NotNull(message = "年龄不能不填写")
    @Min(value = 0,message = "年龄必须大于0岁")
    @Max(value = 150,message = "年龄必须小于150岁")
    private Integer age;

    @NotBlank(message = "用户名不能为空")
    private String phone;

    @NotBlank(message = "用户名不能为空")
    private String idCard;

    public List<OverdueRecordDO> getOverdueRecordDOS() {
        return overdueRecordDOS;
    }

    public void setOverdueRecordDOS(List<OverdueRecordDO> overdueRecordDOS) {
        this.overdueRecordDOS = overdueRecordDOS;
    }

    @NotNull(message = "性别不能不写")
    private String workState;

    private Integer dishonest;

    private List<OverdueRecordDO> overdueRecordDOS;

    public Integer getDishonest() {
        return dishonest;
    }

    public void setDishonest(Integer dishonest) {
        this.dishonest = dishonest;
    }

    private List<AccountDO> accountDOS;

    public List<AccountDO> getAccountDOS() {
        return accountDOS;
    }

    public void setAccountDOS(List<AccountDO> accountDOS) {
        this.accountDOS = accountDOS;
    }

    private String encrptPassword;

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

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }
}
