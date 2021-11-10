package com.yjxxt.rpc.service;



import com.yjxxt.rpc.pojo.GoodsCategory;
import com.yjxxt.rpc.vo.GoodsCategoryVo;

import java.util.List;


public interface IGoodsCategoryService {

    /**
     * 查询所有商品分类数据
     * @return
     */
    public List<GoodsCategoryVo> queryAllGoodsCategories();





}
