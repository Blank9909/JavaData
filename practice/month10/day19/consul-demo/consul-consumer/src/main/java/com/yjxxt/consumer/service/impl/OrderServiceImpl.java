package com.yjxxt.consumer.service.impl;

import com.yjxxt.consumer.pojo.Order;
import com.yjxxt.consumer.pojo.Product;
import com.yjxxt.consumer.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据主键查询订单
     * 8.4.6. 控制层
     * OrderController.java
     *
     * @param id
     * @return
     */
    @Override
    public Order selectOrderById(Integer id) {
        return new Order(id, "order-001", "中国", 22788D,
                selectProductListByLoadBalancerAnnotation());
    }

    private List<Product> selectProductListByLoadBalancerAnnotation() {
        // ResponseEntity: 封装了返回数据
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                "http://service-provider/product/list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                });
        return response.getBody();
    }
}