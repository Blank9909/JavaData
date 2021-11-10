package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.dto.TreeDto;
import com.yjxxt.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ModuleController extends BaseController {

    @Autowired
    private ModuleService moduleService;

    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeDto> queryAllModules(){
        return moduleService.queryAllModules();
    }

    public List<TreeDto> queryAllModules02(Integer roleId){
        return moduleService.queryAllModules02(roleId);
    }
}
