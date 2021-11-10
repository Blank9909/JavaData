package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.bean.Permission;
import com.yjxxt.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("permission")
public class PermissionController extends BaseController {

    @Autowired
    private PermissionService permissionService;




    @RequestMapping("userRolePermission")
    @ResponseBody
    public List<String> queryUserHasRolePermission(Integer userId) {
        return permissionService.queryUserHasRolesHasPermissions(userId);
    }


}
