package com.yjxxt.sso.service;

import com.yjxxt.sso.pojo.Admin;

public interface ISSOService {

    /**
     * 登录认证方法返回票据ticket
     * @param userName
     * @param password
     * @return
     */
    public String login(String userName,String password);


    /**
     * 验证票据ticket返回用户信息
     * @param ticket
     * @return
     */
    public Admin validateTicket(String ticket);



    public void expireTicket(String ticket);


    public void removeTicket(String ticket);

}
