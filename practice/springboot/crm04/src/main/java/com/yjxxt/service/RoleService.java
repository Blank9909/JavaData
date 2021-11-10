package com.yjxxt.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.base.BaseService;
import com.yjxxt.bean.Permission;
import com.yjxxt.bean.Role;
import com.yjxxt.mapper.ModuleMapper;
import com.yjxxt.mapper.PermissionMapper;
import com.yjxxt.mapper.RoleMapper;
import com.yjxxt.query.RoleQuery;
import com.yjxxt.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService extends BaseService<Role, Integer> {

    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Autowired(required = false)
    private ModuleMapper moduleMapper;
    /**
     * 查询所有角色
     *
     * @return
     */
    public Map<String, Object> findAllRole() {

        return roleMapper.selectRole();
    }

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    /**
     * 按条件查询
     *
     * @param query
     * @return
     */
    public Map<String, Object> findRole(RoleQuery query) {
        //初始化
        PageHelper.startPage(query.getPage(), query.getLimit());
        //准备数据
        List<Role> roles = roleMapper.selectByParams(query);
        //开始分页
        PageInfo<Role> lists = new PageInfo<>(roles);
        //将数据放入map中
        HashMap<String, Object> map = new HashMap<>();
        //放数据
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", lists.getTotal());
        map.put("list", lists.getList());
        return map;
    }

    public void addRole(Role role) {
        //角色名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()) || role.getRoleName() == null, "请填写用户名");
        //角色名不能重复
        Role temp = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp != null, "角色名不能重复");
        //附默认值
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        role.setIsValid(1);
        //判断是否添加成功
        AssertUtil.isTrue(roleMapper.insertSelective(role) < 1, "添加失败");
    }

    /**
     * 角色修改
     *
     * @param role
     */
    public void changeRole(Role role) {
        //判断角色不能为空
        AssertUtil.isTrue(roleMapper.selectByPrimaryKey(role.getId()) == null, "角色不能为空");
        //用户名不能重复
        Role temp = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp != null, "角色名不能重复");
        //给默认值
        role.setUpdateDate(new Date());
        role.setIsValid(1);
        //判断是否修改成功
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1, "修改失败");
    }

    public void removeRole(Integer roleId) {
        //判断选择的角色不为空
        AssertUtil.isTrue(roleId == null || roleMapper.selectByPrimaryKey(roleId) == null, "请选择删除的角色");
        //判断不能是否删除成功
        AssertUtil.isTrue(roleMapper.deleteByPrimaryKey(roleId) < 1, "删除失败了");
    }

    public void addGrant(Integer[] mids, Integer roleId) {
        //根据角色id查询用户信息
        Role temp = roleMapper.selectByPrimaryKey(roleId);
        //判断授权的角色是否存在
        AssertUtil.isTrue(roleId == null || temp == null, "待授权的角色不存在");
        //根据角色id判断有多少个权限
        Integer count = permissionMapper.countModulsByRoleId(roleId);
        //判断选择的权限是否正确是否分配失败
        if (count > 0) {
            AssertUtil.isTrue(permissionMapper.deleteModuleByRoleId(roleId) < count, "权限分配失败");
        }
        //
        if (null != mids && mids.length > 0) {
            List<Permission> permissions = new ArrayList<>();
            for (Integer mid : mids) {
                Permission permission = new Permission();
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);

                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permission.add(permission);
            }
            permissionMapper.insertBatch(permissions);
        }
    }
}

