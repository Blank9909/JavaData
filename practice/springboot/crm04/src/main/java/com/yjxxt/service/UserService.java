package com.yjxxt.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.base.BaseService;
import com.yjxxt.bean.User;
import com.yjxxt.bean.UserRole;
import com.yjxxt.mapper.UserMapper;
import com.yjxxt.mapper.UserRoleMapper;
import com.yjxxt.model.UserModel;
import com.yjxxt.query.UserQuery;
import com.yjxxt.utils.AssertUtil;
import com.yjxxt.utils.Md5Util;
import com.yjxxt.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;

    /**
     * 登录
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName,String userPwd){
        //验证用户名与密码
        checkLogin01(userName,userPwd);
        //查询是否存在
        User user = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(user==null,"用户名不存在");
        //密码校验 加密后与数据库对比
        checkLogin02(userPwd,user.getUserPwd());
        //返回构建对象
        return buildUser(user);
    }

    //返回构建对象
    private UserModel buildUser(User user) {
        //实例化对象
        UserModel userModel = new UserModel();
        //加密id
        String userIdStr = UserIDBase64.encoderUserID(user.getId());
        //传入userModel
        userModel.setUserIdStr(userIdStr);
        //用户名
        userModel.setUserName(user.getUserName());
        //真实姓名
        userModel.setTureName(user.getTrueName());
        //返回userModel
        return userModel;
    }

    //密码校验 加密后与数据库对比
    private void checkLogin02(String userPwd, String userPwd1) {
        userPwd=Md5Util.encode(userPwd);
        AssertUtil.isTrue(!(userPwd.equals(userPwd1)),"密码错误");
    }

    //验证用户名与密码
    private void checkLogin01(String userName, String userPwd) {
        //用户名不为空
        AssertUtil.isTrue(userName==null,"用户名不能为空");
        //密码不为空
        AssertUtil.isTrue(userPwd==null,"密码不能为空");
    }


    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param confirmPwd
     */
    public void changePassword(Integer userId,String oldPassword,String newPassword,String confirmPwd){
        //根据用户id查询用户信息
        User user = userMapper.selectByPrimaryKey(userId);
        //验证
        checkUserPasswordParams(user,oldPassword,newPassword,confirmPwd);
        //加密
        user.setUserPwd(Md5Util.encode(newPassword));
        //判断
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败");
    }

    private void checkUserPasswordParams(User user, String oldPassword, String newPassword, String confirmPwd) {
        //旧密码不能为空
        AssertUtil.isTrue(oldPassword==null,"旧密码不能为空");
        //旧密码不正确，旧密码要加密
        AssertUtil.isTrue(!(Md5Util.encode(oldPassword).equals(user.getUserPwd())),"旧密码不正确");
        //新密码不能为空
        AssertUtil.isTrue(newPassword==null,"新密码不能为空");
        //新密码与旧密码不能相等
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码与旧密码不能相等");
        //确认密码不能为空
        AssertUtil.isTrue(confirmPwd==null,"确认密码不能为空");
        //确认密码与新密码要相等
        AssertUtil.isTrue(!(newPassword.equals(confirmPwd)),"确认密码与新密码相等");
    }

    /**
     * 查询所有用户
     * @return
     */
    public Map<String,Object> findAllUser(){
        return userMapper.selectUser();
    }

    /**
     * 根据条件查询信息
     * @param query
     * @return
     */
    public Map<String ,Object> findUser(UserQuery query){
        //初始化分页
        PageHelper.startPage(query.getPage(),query.getLimit());
        //查询条件
        List<User> lists = userMapper.selectByParams(query);
        //开始分页
        PageInfo<User> plist = new PageInfo<>(lists);
        //将数据存储到map中
        Map<String, Object> map = new HashMap<>();
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        return map;
    }

    /**
     * 批量删除用户
     * @param ids
     */
    public void removeUser(Integer[] ids){
        //判断选择的用户不为空
        AssertUtil.isTrue(ids==null||ids.length==0,"请选择要删除的用户");
        //判断要删除的用户
        AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"用户删除失败");
    }

    /**
     * 修改用户
     * @param user
     */
    public void changeUser(User user){
        //判断选择的用户不为空
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"请选择要修改的用户");
        //验证
        checkChangeUser(user.getUserName(),user.getPhone(),user.getPhone());
        //附默认值
        user.setUpdateDate(new Date());
        user.setIsValid(1);
        //判断是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"更新失败");
        //用户与角色关联
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 添加用户
     * @param user
     */
    public void addUser(User user){
        //验证
        checkAddUser(user.getPhone(),user.getEmail(),user.getUserName());
        //附默认值
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setIsValid(1);
        //判断是否添加成功
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加失败");
        //用户与角色关联
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /*用户与角色相关联*/
    private void relationUserRole(Integer userId, String roleIds) {
        //查询一个用户有多少个角色--->用户id查询
        Integer count=userRoleMapper.countUserRoleByUserId(userId);
        //判断角色个数是否大于零
        if(count>0){
            //双重判断角色是否关联成功
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色分配失败");
        }
        if(StringUtils.isNotBlank(roleIds)){
            //重新添加新的角色
            ArrayList<UserRole> userRoles = new ArrayList<>();
            //遍历
            for(String s:roleIds.split(",")){
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(s));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                userRoles.add(userRole);
            }
            //判断角色是否分配成功
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles)<userRoles.size(),"用户角色分配失败");
        }
    }

    /*删除用户*/
    public void deleteUser(Integer userId){
        //判断用户是否存在
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(userId==null||user==null,"待删除的记录不存在");
        //查询用户拥有角色的数量
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        if(count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色失败");
        }
    }
}
