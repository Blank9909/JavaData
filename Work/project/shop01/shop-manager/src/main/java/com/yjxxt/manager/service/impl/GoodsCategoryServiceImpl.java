package com.yjxxt.manager.service.impl;

import com.yjxxt.manager.mapper.GoodsCategoryMapper;
import com.yjxxt.manager.pojo.GoodsCategory;
import com.yjxxt.manager.pojo.GoodsCategoryExample;
import com.yjxxt.manager.service.IGoodsCategoryService;
import com.yjxxt.manager.vo.GoodsCategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsCategoryServiceImpl  implements IGoodsCategoryService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Resource(name="redisTemplate")
    private RedisTemplate<String,Object> redisTemplate;


    @Value("${goodsCategory.list}")
    private String cacheKey;


    @Override
    public List<GoodsCategory> queryGoodsCategoriesByParentId(Short parentId) {
        GoodsCategoryExample example =new GoodsCategoryExample();
        example.createCriteria().andParentIdEqualTo(parentId);
        return goodsCategoryMapper.selectByExample(example);
    }



    @Override
    public void saveGoodsCategory(GoodsCategory goodsCategory) {
        /**
         * 1.参数校验
         *     商品分类名称非空
         *     parentId 非空
         *     level 非空
         *     同一级分类下名称不可重复
         * 2.默认值设置
         *    is_show  默认显示
         *    is_hot  是否推荐
         * 3.执行添加 判断结果
         *
         * 4.更新族谱
         *
         */
        checkParams(goodsCategory.getName(),goodsCategory.getParentId(),goodsCategory.getLevel());
        goodsCategory.setIsShow((byte)1);
        goodsCategory.setIsHot((byte)0);
        if(goodsCategoryMapper.insertSelective(goodsCategory)!=1){
            throw new RuntimeException("商品分类添加失败!");
        }
        // 更新族谱
        if(goodsCategory.getLevel()==1){
            goodsCategory.setParentIdPath("0_"+goodsCategory.getId());
        }else{
            GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getParentId());
            goodsCategory.setParentIdPath(temp.getParentIdPath()+"_"+goodsCategory.getId());
        }
        if(goodsCategoryMapper.updateByPrimaryKey(goodsCategory) !=1){
            throw new RuntimeException("商品分类添加失败!");
        }
    }


    private void checkParams(String name, Short parentId, Byte level) {
        if(StringUtils.isBlank(name)){
            throw  new RuntimeException("商品分类名称不能为空!");
        }
        if(null == parentId){
            throw  new RuntimeException("请指定上级分类!");
        }
        if(null == level){
            throw  new RuntimeException("请指定分类级别!");
        }
        GoodsCategoryExample example=new GoodsCategoryExample();
        example.createCriteria().andNameEqualTo(name).andLevelEqualTo(level);
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectByExample(example);
        if(!(CollectionUtils.isEmpty(goodsCategories))){
            throw  new RuntimeException("分类名称已存在!");
        }
    }




    /*@Override
    public List<GoodsCategoryVo> queryAllGoodsCategories() {
        *//**
         *  1.查询所有的一级节点分类数据  gcv01List
         *       parentId=0 and level=1
         *  2.循环遍历 gcv01List 获取一级节点数据
         *  3.根据一级节点id 查询所有的二级节点数据 gcv02List
         *  4. 循环遍历 gcv02List 获取二级节点数据
         *  5.根据二级节点id 查询所有的三级节点数据 gcv03List
         *  6.设置每一级children 属性值
         *//*
        GoodsCategoryExample example =new GoodsCategoryExample();
        example.createCriteria().andParentIdEqualTo((short)0).andLevelEqualTo((byte)1);
        // 获取一级节点数据
        List<GoodsCategory> gc01List = goodsCategoryMapper.selectByExample(example);
        List<GoodsCategoryVo> gcv01List=new ArrayList<GoodsCategoryVo>();
        for (GoodsCategory goodsCategory : gc01List) {
            GoodsCategoryVo goodsCategoryVo=new GoodsCategoryVo();
            BeanUtils.copyProperties(goodsCategory,goodsCategoryVo);
            // 清除原始提交
            example.clear();
            example.createCriteria().andParentIdEqualTo(goodsCategory.getId())
                    .andLevelEqualTo((byte)2);
            // 获取二级节点数据
            List<GoodsCategory> gc02List = goodsCategoryMapper.selectByExample(example);
            List<GoodsCategoryVo> gcv02List=new ArrayList<GoodsCategoryVo>();
            for (GoodsCategory category : gc02List) {
                GoodsCategoryVo goodsCategoryVo02=new GoodsCategoryVo();
                BeanUtils.copyProperties(category,goodsCategoryVo02);
                // 清除原始提交
                example.clear();
                example.createCriteria().andParentIdEqualTo(category.getId())
                        .andLevelEqualTo((byte)3);
                List<GoodsCategory> gc03List = goodsCategoryMapper.selectByExample(example);
                List<GoodsCategoryVo> gcv03List=new ArrayList<GoodsCategoryVo>();
                for (GoodsCategory goodsCategory1 : gc03List) {
                    // 构造三级节点数据
                    GoodsCategoryVo goodsCategoryVo03=new GoodsCategoryVo();
                    BeanUtils.copyProperties(goodsCategory1,goodsCategoryVo03);
                    gcv03List.add(goodsCategoryVo03);
                }
                // 将当前三级节点list 设置给当前goodsCategoryVo02 节点
                goodsCategoryVo02.setChildren(gcv03List);
                // 收集当前一级节点的所有二级节点
                gcv02List.add(goodsCategoryVo02);
            }
            goodsCategoryVo.setChildren(gcv02List);
            gcv01List.add(goodsCategoryVo);
        }
        return gcv01List;
    }*/



    @Override
    public List<GoodsCategoryVo> queryAllGoodsCategories() {
        /**
         * 缓存查找流程
         *     1.根据key 查询缓存数据
         *     2.如果缓存存在
         *         获取缓存数据返回
         *     3.如果缓存不存在
         *         到数据库查询数据
         *     4.如果数据库记录存在
         *         将数据存入缓存
         *     5.返回结果
         *
         *
         * 业务实现流程(无缓存)
         *  1.查询所有的一级节点分类数据  gcv01List
         *       parentId=0 and level=1
         *  2.循环遍历 gcv01List 获取一级节点数据
         *  3.根据一级节点id 查询所有的二级节点数据 gcv02List
         *  4. 循环遍历 gcv02List 获取二级节点数据
         *  5.根据二级节点id 查询所有的三级节点数据 gcv03List
         *  6.设置每一级children 属性值
         */
        List<GoodsCategoryVo> gcv01List=new ArrayList<GoodsCategoryVo>();
        if(redisTemplate.hasKey(cacheKey)){
            gcv01List  = (List<GoodsCategoryVo>) redisTemplate.opsForValue().get(cacheKey);
            if(!(CollectionUtils.isEmpty(gcv01List))){
                return gcv01List;
            }
        }
        GoodsCategoryExample example =new GoodsCategoryExample();
        example.createCriteria().andParentIdEqualTo((short)0).andLevelEqualTo((byte)1);
        // 获取一级节点数据
        List<GoodsCategory> gc01List = goodsCategoryMapper.selectByExample(example);
        for (GoodsCategory goodsCategory : gc01List) {
            GoodsCategoryVo goodsCategoryVo=new GoodsCategoryVo();
            BeanUtils.copyProperties(goodsCategory,goodsCategoryVo);
            // 清除原始提交
            example.clear();
            example.createCriteria().andParentIdEqualTo(goodsCategory.getId())
                    .andLevelEqualTo((byte)2);
            // 获取二级节点数据
            List<GoodsCategory> gc02List = goodsCategoryMapper.selectByExample(example);
            List<GoodsCategoryVo> gcv02List=new ArrayList<GoodsCategoryVo>();
            for (GoodsCategory category : gc02List) {
                GoodsCategoryVo goodsCategoryVo02=new GoodsCategoryVo();
                BeanUtils.copyProperties(category,goodsCategoryVo02);
                // 清除原始提交
                example.clear();
                example.createCriteria().andParentIdEqualTo(category.getId())
                        .andLevelEqualTo((byte)3);
                List<GoodsCategory> gc03List = goodsCategoryMapper.selectByExample(example);
                List<GoodsCategoryVo> gcv03List=new ArrayList<GoodsCategoryVo>();
                for (GoodsCategory goodsCategory1 : gc03List) {
                    // 构造三级节点数据
                    GoodsCategoryVo goodsCategoryVo03=new GoodsCategoryVo();
                    BeanUtils.copyProperties(goodsCategory1,goodsCategoryVo03);
                    gcv03List.add(goodsCategoryVo03);
                }
                // 将当前三级节点list 设置给当前goodsCategoryVo02 节点
                goodsCategoryVo02.setChildren(gcv03List);
                // 收集当前一级节点的所有二级节点
                gcv02List.add(goodsCategoryVo02);
            }
            goodsCategoryVo.setChildren(gcv02List);
            gcv01List.add(goodsCategoryVo);
        }

        if(!(CollectionUtils.isEmpty(gcv01List))){
            redisTemplate.opsForValue().set(cacheKey,gcv01List);
        }

        return gcv01List;
    }

    @Override
    public List<GoodsCategory> queryAllGoodsCategoriesForGoodsCategory() {
        return goodsCategoryMapper.selectByExample(new GoodsCategoryExample());
    }

}
