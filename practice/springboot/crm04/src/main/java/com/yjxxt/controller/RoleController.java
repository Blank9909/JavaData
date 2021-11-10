package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.bean.Role;
import com.yjxxt.query.RoleQuery;
import com.yjxxt.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.transform.Result;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;


    @RequestMapping("index")
    public String Index(){
        return "role/role";
    }
    /**
     * 查询所有
     * @return
     */
    @RequestMapping("findAll")
    @ResponseBody
    public Map<String,Object> findAll(){
        Map<String, Object> map = roleService.findAllRole();
        return map;
    }

    @RequestMapping("find")
    public Map<String,Object> find(RoleQuery query){
        Map<String, Object> map = roleService.findRole(query);
        return map;
    }

    @RequestMapping("addOrUpdateRole")
    public String addOrUpdate(Integer roleId, Model model) {
        //判断
        if(roleId!=null){
            Role role = roleService.selectByPrimaryKey(roleId);
            //存储
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }

    @RequestMapping("add")
    @ResponseBody
    public ResultInfo add(Role role){
        roleService.addRole(role);
        return success("添加成功了");
    }

    @RequestMapping("change")
    @ResponseBody
    public ResultInfo change(Role role){
        roleService.changeRole(role);
        return success("添加成功了");
    }

    public ResultInfo remove(Integer roleId){
        roleService.removeRole(roleId);
        return success("删除成功了");
    }

    public String toAddGrantPage(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    public ResultInfo addGrant(Integer[] mids,Integer roleId){
        roleService.addGrant(mids,roleId);
        return success("权限添加成功");
    }
}
