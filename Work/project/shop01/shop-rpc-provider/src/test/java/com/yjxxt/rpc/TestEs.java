package com.yjxxt.rpc;


import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;

@SpringBootTest
public class TestEs {


    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Test
    public void test() throws IOException {
        // 指定索引库
        SearchRequest searchRequest = new SearchRequest("shop");
        // 构建查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 添加分页条件，从第 0 个开始，返回 5 个
        searchSourceBuilder.from((2-1)*5).size(5);
        // 构建高亮对象
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // 指定高亮字段和高亮样式
        highlightBuilder.field("goodsName")
                .preTags("<span style='color:red;'>")
                .postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 添加查询条件
        // 指定从 goodsName 字段中查询
        String key = "中国移动联通电信";
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(key, "goodsName"));
        // 执行请求
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 总条数
        System.out.println(searchResponse.getHits().getTotalHits().value);
        // 结果数据(如果不设置返回条数，大于十条默认只返回十条)
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            // 构建项目中所需的数据结果集
            String highlightMessage = String.valueOf(hit.getHighlightFields().get("goodsName").fragments()[0]);
            Integer goodsId = Integer.valueOf((Integer) hit.getSourceAsMap().get("goodsId"));
            String goodsName = String.valueOf(hit.getSourceAsMap().get("goodsName"));
            BigDecimal marketPrice = new BigDecimal(String.valueOf(hit.getSourceAsMap().get("marketPrice")));
            String originalImg = String.valueOf(hit.getSourceAsMap().get("originalImg"));
            System.out.println("goodsId -> " + goodsId);
            System.out.println("goodsName -> " + goodsName);
            System.out.println("highlightMessage -> " + highlightMessage);
            System.out.println("marketPrice -> " + marketPrice);
            System.out.println("originalImg -> " + originalImg);
            System.out.println("----------------------------");
        }
    }
}
