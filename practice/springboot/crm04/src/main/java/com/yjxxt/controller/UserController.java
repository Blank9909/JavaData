package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.bean.User;
import com.yjxxt.model.UserModel;
import com.yjxxt.query.UserQuery;
import com.yjxxt.service.UserService;
import com.yjxxt.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 用户的登录
     * @param userName
     * @param userPwd
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public ResultInfo Login(String userName,String userPwd){
        //登录
        UserModel userModel = userService.userLogin(userName, userPwd);
        //返回对象
        return success("添加成功了",userModel);
    }

    /**
     * 密码修改
     * @param req
     * @param oldPassword
     * @param newPassword
     * @param confirmPwd
     * @return
     */
    @RequestMapping("updatePwd")
    public ResultInfo changePassword(HttpServletRequest req,String oldPassword,String newPassword,String confirmPwd){
        //获取id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用方法修改
        userService.changePassword(userId,oldPassword,newPassword,confirmPwd);
        //返回
        return success("密码修改成功了");
    }

    //跳转页面
    @RequestMapping("toPasswordPage")
    public String update(){
        return "user/password";
    }

    /**
     * 查查基本信息
     * @param req
     * @return
     */
    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest req){
        //从cookie获取id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //查询信息
        User user = userService.selectByPrimaryKey(userId);
        //存储数据
        req.setAttribute("user",user);
        //返回setting页面
        return "user/setting";
    }


    /**
     * 查询所有用户数据
     * @return
     */
    @RequestMapping("findAll")
    @ResponseBody
    public Map<String,Object> findAll(){
        return userService.findAllUser();
    }


    /**
     * 根据条件查询
     * @param query
     * @return
     */
    @RequestMapping("find")
    @ResponseBody
    public Map<String,Object> find(UserQuery query){
        return userService.findUser(query);
    }


    /*跳转到用户管理界面*/
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo add(User user){
        userService.addUser(user);
        return success("添加成功了");
    }

    /**
     * 修改用户
     * @param user
     * @return
     */
    public ResultInfo change(User user){
        userService.changeUser(user);
        return success("更新成功了");
    }

    /*跳转到用户添加——更新界面*/
    @RequestMapping("addOrUpdateDialog")
    public String addOrChange(Integer id, Model model){
        if(id!=null){
            User user = userService.selectByPrimaryKey(id);
            model.addAttribute("user",user);
        }
        return "user/add_update";

    }
    /**
     * 删除用户
     * @param ids
     * @return
     */
    public ResultInfo remove(Integer[] ids){
        userService.deleteBatch(ids);
        return success("删除成功了");
    }
}
