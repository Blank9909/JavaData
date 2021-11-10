package com.yjxxt.manager.service.impl;


import com.yjxxt.common.result.BaseResult;
import com.yjxxt.manager.mapper.GoodsImagesMapper;
import com.yjxxt.manager.pojo.GoodsImages;
import com.yjxxt.manager.service.IGoodsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsImagesServiceImpl implements IGoodsImageService {

    @Autowired
    private GoodsImagesMapper goodsImagesMapper;

    @Override
    public BaseResult saveGoodsImages(GoodsImages goodsImages) {
        return goodsImagesMapper.insertSelective(goodsImages)==1?
                BaseResult.success():BaseResult.error();
    }
}
