package com.yjxxt.controller;

import com.yjxxt.pojo.Product;
import com.yjxxt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 查询商品列表
     * @return
     */
    @GetMapping("list")
    protected List<Product> selectProductList(){
        return productService.selectProductList();
    }
}
