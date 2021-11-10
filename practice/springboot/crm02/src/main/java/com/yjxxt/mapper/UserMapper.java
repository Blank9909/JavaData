package com.yjxxt.mapper;

import com.yjxxt.base.BaseMapper;
import com.yjxxt.bean.User;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {
    //新增方法
    public User selectUserByName(String userName);

    //查询所有的销售人员
    @MapKey("")
    List<Map<String, Object>> selectSales();
}