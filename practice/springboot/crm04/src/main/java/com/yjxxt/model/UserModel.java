package com.yjxxt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class UserModel {
    private String userIdStr;
    private String userName;
    private String tureName;

    public UserModel() {
    }

    public UserModel(String userIdStr, String userName, String tureName) {
        this.userIdStr = userIdStr;
        this.userName = userName;
        this.tureName = tureName;
    }

    public String getUserIdStr() {
        return userIdStr;
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTureName() {
        return tureName;
    }

    public void setTureName(String tureName) {
        this.tureName = tureName;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userIdStr='" + userIdStr + '\'' +
                ", userName='" + userName + '\'' +
                ", tureName='" + tureName + '\'' +
                '}';
    }
}
