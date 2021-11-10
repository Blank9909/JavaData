package com.yjxxt.consumer.service;

import com.yjxxt.consumer.pojo.Order;

public interface OrderService {

    /**
     * 根据主键查询订单
     *
     * @param id
     * @return
     */
    Order selectOrderById(Integer id);
}