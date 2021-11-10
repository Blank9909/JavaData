package com.yjxxt.manager.service;

import com.yjxxt.common.result.BaseResult;
import com.yjxxt.manager.pojo.Goods;
import com.yjxxt.manager.query.GoodsQuery;

public interface IGoodsService {

    public Integer saveGoods(Goods goods);

    BaseResult queryGoodsForListPage(GoodsQuery goodsQuery);
}
