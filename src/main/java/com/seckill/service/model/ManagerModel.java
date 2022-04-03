package com.seckill.service.model;

import java.io.Serializable;

public class ManagerModel implements Serializable {

    private Integer id;

    private String password;

    private String nickName;

    private String phone;

    private Byte superManager;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Byte getSuperManager() {
        return superManager;
    }

    public void setSuperManager(Byte superManager) {
        this.superManager = superManager;
    }
}
