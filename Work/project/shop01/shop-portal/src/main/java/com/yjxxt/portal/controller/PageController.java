package com.yjxxt.portal.controller;


import com.yjxxt.rpc.service.IGoodsCategoryService;
import com.yjxxt.rpc.vo.GoodsCategoryVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PageController {


   /* @Reference(interfaceClass = IGoodsCategoryService.class,version = "1.0")
    private IGoodsCategoryService goodsCategoryService;*/

   /* @RequestMapping("/{page}")
    public String page(@PathVariable  String page){
        return  page;
    }*/


    @RequestMapping("login")
    public String login(){
        return "login";
    }



   /* @RequestMapping("index")
    public String index(Model model){
        model.addAttribute("gcList",goodsCategoryService.queryAllGoodsCategories());
        return "index";
    }


    @RequestMapping("test")
    @ResponseBody
    public List<GoodsCategoryVo> queryAllGoodsCategories(){
        return goodsCategoryService.queryAllGoodsCategories();
    }
*/






}
