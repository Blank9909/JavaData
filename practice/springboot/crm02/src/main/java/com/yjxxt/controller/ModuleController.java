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
@RequestMapping("module")
public class ModuleController extends BaseController {


    @Autowired
    private ModuleService moduleService;

    @RequestMapping("findModules")
    @ResponseBody
    public List<TreeDto> findModules(){
        return moduleService.findModules();
    }

    @RequestMapping("findModules2")
    @ResponseBody
    public List<TreeDto> findModules02(Integer roleId){

        return moduleService.findModules2(roleId);
    }
}
