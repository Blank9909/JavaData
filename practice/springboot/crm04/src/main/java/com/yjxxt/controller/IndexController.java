package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.bean.User;
import com.yjxxt.mapper.PermissionMapper;
import com.yjxxt.service.UserService;
import com.yjxxt.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    //返回登录页面
    @RequestMapping("index")
    public String Index(){
        return "index";
    }

    //返回欢迎界面
    @RequestMapping("welcome")
    public String Welcome(){
        return "welcome";
    }

    //返回主页面
    @RequestMapping("main")
    public String main(HttpServletRequest req ){
        //从cookie中获取id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //根据id获取信息
        User user = userService.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user",user);
        //根据id获取资源的信息
        List<String> permissions = permissionMapper.selectUserHasRolePermission(userId);
        //存储到request
        req.setAttribute("permissionss",permissions);
        //返回
        return "main";
    }





}
