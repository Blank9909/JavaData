package com.yjxxt.rpc.mapper;


import com.yjxxt.rpc.pojo.GoodsCategory;
import com.yjxxt.rpc.pojo.GoodsCategoryExample;

import java.util.List;

public interface GoodsCategoryMapper {

    List<GoodsCategory> selectByExample(GoodsCategoryExample example);

}