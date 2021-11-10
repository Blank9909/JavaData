package com.yjxxt.portal.controller;


import com.yjxxt.common.result.ShopPageInfo;
import com.yjxxt.rpc.service.ISearchService;
import com.yjxxt.rpc.vo.GoodsVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {

    /*@Reference(interfaceClass = ISearchService.class,version = "1.0")
    private ISearchService searchService;*/



    @RequestMapping("search/index")
    public String index(String searchStr, Model model){
        model.addAttribute("searchStr",searchStr);
        return "doSearch";
    }



    /*@RequestMapping("search")
    @ResponseBody
    public ShopPageInfo<GoodsVo> search(String key,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize){
        return searchService.search(key,pageNum,pageSize);
    }*/
}
