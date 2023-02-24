package com.lucky.platform.entity;

import java.io.Serializable;

/**
 * @program: lucky-module-service
 * @description: 用户信息
 * @author: Loki
 * @data: 2023-02-22 13:43
 **/
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String userName;

    private String password;

    private String heroAvatar;

    private String currHp;

    public User(){

    }

    public User (String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public String getCurrHp() {
        return currHp;
    }

    public void setCurrHp(String currHp) {
        this.currHp = currHp;
    }
}
