package com.yjxxt.service.Impl;

import com.yjxxt.pojo.Product;
import com.yjxxt.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 商品服务
 */
@Service
public class ProductServiceImpl implements ProductService {

    /**
     * 查询商品列表
     * @return
     */
    @Override
    public List<Product> selectProductList() {
        return Arrays.asList(
                new Product(4, "华为手机", 2, 5888D),
                new Product(5, "联想笔记本", 1, 6888D),
                new Product(6, "小米平板", 5, 2666D)
        );
    }
}