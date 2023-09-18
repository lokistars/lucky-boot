package com.lucky.platform.util;

import java.io.Serializable;

/**
 * @author Nuany
 * JWTInfo JTW存储对象
 */
public class JWTInfo implements Serializable {
    private String username;
    private String userId;
    private String name;
    private String tokenId;

    public JWTInfo() {
    }

    public JWTInfo(String username, String userId, String name, String tokenId) {
        this.username = username;
        this.userId = userId;
        this.name = name;
        this.tokenId = tokenId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "JWTInfo{" +
                "username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", tokenId='" + tokenId + '\'' +
                '}';
    }
}
