package com.yjxxt.rpc.service;

import com.yjxxt.common.result.ShopPageInfo;
import com.yjxxt.rpc.vo.GoodsVo;

public interface ISearchService {

    public ShopPageInfo<GoodsVo> search(String key,Integer pageNum,Integer pageSize);
}
