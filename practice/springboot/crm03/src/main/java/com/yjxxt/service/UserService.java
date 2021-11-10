package com.yjxxt.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.yjxxt.base.BaseService;
import com.yjxxt.bean.User;
import com.yjxxt.bean.UserRole;
import com.yjxxt.mapper.UserMapper;
import com.yjxxt.mapper.UserRoleMapper;
import com.yjxxt.model.UserModel;
import com.yjxxt.query.UserQuery;
import com.yjxxt.utils.AssertUtil;
import com.yjxxt.utils.Md5Util;
import com.yjxxt.utils.PhoneUtil;
import com.yjxxt.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    //根据用户名密码登录
    public UserModel userLogin(String userName, String userPwd){
        //校验用户和密码
        checkLoginParams(userName, userPwd);
        //查询用户是否存在
        User user = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(user == null, "用户名已注销或者不存在");
        //密码校验，加密+比对==
        checkLoginPwd(userPwd,user.getUserPwd());
        //构建返回对象
        return buidlerUserInfo(user);
    }

    /**
     * 构建返回对象的
     * @param user
     * @return
     */
    private UserModel buidlerUserInfo(User user) {
        //实例化对象
        UserModel userModel=new UserModel();
        //加密
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        //返回
        return  userModel;
    }

    /**
     * 校验用户密码，和数据库中密码是否配置
     * @param userPwd
     * @param userPwd1
     */
    private void checkLoginPwd(String userPwd, String userPwd1) {
        //将输入的密码加密
        userPwd=Md5Util.encode(userPwd);
        //比对密码是否正确
        AssertUtil.isTrue(!userPwd.equals(userPwd1),"用户密码不正确");
    }

    /**
     * 验证用户名称和密码的
     *
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户名不能为空");
    }


    /**
     * 修改用户的密码
     * @param userId 用户的ID
     * @param oldPassword 原始密码
     * @param newPassword 新密码
     * @param confirmPwd  确认密码
     */
    public void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPwd){
        //根据ID查询用户信息
        User user = userMapper.selectByPrimaryKey(userId);
        //验证
        checkUserPasswordParams(user,oldPassword,newPassword,confirmPwd);
        //加密，修改用户密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //判断修改是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败了");
    }

    /**
     * 验证用户的密码参数
     * @param user 当前用户对象
     * @param oldPassword
     * @param newPassword
     * @param confirmPwd
     */
    private void checkUserPasswordParams(User user, String oldPassword, String newPassword, String confirmPwd) {
        //当前用户是否存储
        AssertUtil.isTrue(user==null,"用户未登录或者已经注销");
        //原始密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码");
        //原始密码和数据中的密码（加密）一致
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPassword)),"原始密码不正确");
        //新密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码");
        //新密码和原始密码不能相同
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码和原始不能一样");
        //确认密码，不能为空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPwd),"确认密码不能为空");
        //确认密码和新密码要一致
        AssertUtil.isTrue(!confirmPwd.equals(newPassword),"确认密码和新密码必须一致");
    }


    /**
     * 查询所有销售人员
     * @return
     */
    public List<Map<String,Object>> findSales(){
       return userMapper.selectSales();
    }


    /**
     * 条件查询
     * @param query 查询条件
     * @return
     */
    public Map<String,Object> findAll(UserQuery query){
        //初始化分页数据
        PageHelper.startPage(query.getPage(),query.getLimit());
        //条件查询
        List<User> ulist = userMapper.selectByParams(query);
        //开始分页
        PageInfo<User> plist=new PageInfo<>(ulist);

        Map<String,Object> map=new HashMap();
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        //返回目标map
        return  map;
    }


    /**
     * 添加操作
     * @param user
     *
     * 验证：
     *  1:用户名不能为空
     *  2:邮件不能为空
     *  3:手机号非空，且是正确的手机号
     *  4：是否能添加成功
     */
    public void saveUser(User user){
        //验证
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        //设定默认值
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        user.setIsValid(1);
        //是否能添加成功
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加失败了");
        //添加中间表t_user_role user_id,role_id
        System.out.println(user.getId()+"--->"+user.getRoleIds());
        //
        //    insert into tb_user (name,age) values(),(),(),
        //关联用户和角色的关系
        relaionUserRole(user.getId(),user.getRoleIds());
    }

    /**
     *
     * @param userId 用户Id
     * @param roleIds 角色Ids
     *                "1,3,14"
     */
    private void relaionUserRole(Integer userId, String roleIds) {
        //用户赋予角色
        //原来用户没有角色，添加新的角色
        //用户原来存角色，添加新角色
        //??????
        //统计一下用户有多少个角色，删除原来角色，然后赋予新的角色；
        int count=userRoleMapper.countUserOfRoleByUserId(userId);
        if(count>0){
            //删除原来的角色
            AssertUtil.isTrue( userRoleMapper.deleteUserRoleByUserId(userId)!=count,"角色分配失败");
        }
        //实例化容器list
        List<UserRole> urlist=new ArrayList<UserRole>();
        //判断数据是否有角色ID
        if(roleIds!=null && roleIds.length()>0){
            //准备数据
            for (String roleId:roleIds.split(",")) {
                //实例化对象
                UserRole userRole=new UserRole();
                //****
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //将对象存储道容器
                urlist.add(userRole);
            }
        }
        //调用方法添加
        AssertUtil.isTrue(userRoleMapper.insertBatch(urlist)!=urlist.size(),"用户角色添加失败");
    }

    /**
     * 验证方法
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUser(String userName, String email, String phone) {
        // 1:用户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        //根据用户查询用户对象
//        User temp = userMapper.selectUserByName(userName);
//        AssertUtil.isTrue(temp!=null,"用户名已经存在");
        //邮件不能为空
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮件不能为空");
        //3:手机号非空，且是正确的手机号
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号非空");
        //且是正确的手机号
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"必须输入正确的手机号");
    }


    /**
     * 修改用户信息
     * @param user
     */
    public void changeUser(User user){
        //修改用户的id存在
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"待修改的记录不存在");
        //验证
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        //默认值
        user.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败了");
        //修改用户角色的关系；
        relaionUserRole(user.getId(),user.getRoleIds());
    }


    /**
     * 批量删除
     */
    public void removeIds(Integer[] ids){
        //验证
       AssertUtil.isTrue(ids==null || ids.length==0,"选择数据");
       //删除是否成功
        AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"删除异常");
        //遍历
        for (Integer userId: ids ) {
            int count=userRoleMapper.countUserOfRoleByUserId(userId);
            if(count>0){
                //删除原来的角色
                AssertUtil.isTrue( userRoleMapper.deleteUserRoleByUserId(userId)!=count,"角色删除失败");
            }
        }
    }

}
