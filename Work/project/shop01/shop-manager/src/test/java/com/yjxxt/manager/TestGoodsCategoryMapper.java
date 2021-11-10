package com.yjxxt.manager;


import com.yjxxt.manager.mapper.GoodsCategoryMapper;
import com.yjxxt.manager.pojo.GoodsCategoryExample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestGoodsCategoryMapper {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Test
    public void test01(){
        System.out.println(goodsCategoryMapper.selectByPrimaryKey((short) 1));
    }

    @Test
    public void test02(){
        GoodsCategoryExample goodsCategoryExample=new GoodsCategoryExample();
        goodsCategoryExample.createCriteria()
                .andNameEqualTo("家用电器").andLevelEqualTo((byte)1);
        System.out.println(goodsCategoryMapper.selectByExample(goodsCategoryExample));
    }
}
