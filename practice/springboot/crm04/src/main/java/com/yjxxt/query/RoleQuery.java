package com.yjxxt.query;

import com.yjxxt.base.BaseQuery;

public class RoleQuery extends BaseQuery {
    private String RoleName;

    public RoleQuery() {
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    @Override
    public String toString() {
        return "RoleQuery{" +
                "RoleName='" + RoleName + '\'' +
                '}';
    }
}
