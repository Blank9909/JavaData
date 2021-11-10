package com.yjxxt.service.impl;

import com.yjxxt.pojo.Order;
import com.yjxxt.pojo.Product;
import com.yjxxt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Resource
    private LoadBalancerClient loadBalancerClient;

    /**
     * 根据主键查询订单
     *
     * @param id
     * @return
     */
    @Override
    public Order selectOrderById(Integer id) {
        return new Order(id, "order-001", "中国", 99.9,
                //远程调用
                //1.DiscoveryClient
                //selectProductListByDiscoveryClient()
                //2.loadBalanceClient
                //selectProductListByLoadBalancerClient()
                //3.@loadBalanced
                selectProductListByLoadLanceAnnotation()
        );


    }

    /*1.DiscoveryClient*/
    private List<Product> selectProductListByDiscoveryClient() {
        StringBuffer sb = null;

        // 获取服务列表
        List<String> serviceIds = discoveryClient.getServices();
        if (CollectionUtils.isEmpty(serviceIds)) {
            return null;
        }

        // 根据服务名称获取服务
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("service-provider");

        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }

        ServiceInstance si = serviceInstances.get(0);
        sb = new StringBuffer();
        sb.append("http://" + si.getHost() + ":" + si.getPort() + "/product/list");

        // ResponseEntity: 封装了返回数据
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                sb.toString(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Product>>() {
                });

        return response.getBody();
    }

    /*loadBalanceClient*/
    private List<Product> selectProductListByLoadBalancerClient() {
        StringBuilder sb = null;

        //根据服务名称获取服务
        ServiceInstance si = loadBalancerClient.choose("service-provider");
        if (si == null) {
            return null;
        }
        sb = new StringBuilder();
        sb.append("http://" + si.getHost() + ":" + si.getPort() + "/product/list");

        // ResponseEntity: 封装了返回数据
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                sb.toString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                });
        return response.getBody();
    }

    //3.@LoadBalanced
    private List<Product> selectProductListByLoadLanceAnnotation() {
        //ResponseEntity:封装了返回数据
        ResponseEntity<List<Product>> response = restTemplate.exchange(
                "http://service-provider/product/list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                });

        return response.getBody();
    }
}
