package com.yjxxt.query;

import com.yjxxt.base.BaseQuery;

public class UserQuery extends BaseQuery {
    private String userName;
    private String email;
    private Integer phone;

    public UserQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserQuery{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phone=" + phone +
                '}';
    }
}
