package com.yjxxt.rpc.service.impl;

import com.yjxxt.common.result.ShopPageInfo;
import com.yjxxt.rpc.service.ISearchService;
import com.yjxxt.rpc.vo.GoodsVo;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(version = "1.0")
public class SearchServiceImpl  implements ISearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Override
    public ShopPageInfo<GoodsVo> search(String key, Integer pageNum, Integer pageSize) {
        try {
            // 指定索引库
            SearchRequest searchRequest = new SearchRequest("shop");
            // 构建查询对象
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // 添加分页条件，从第 0 个开始，返回 5 个
            searchSourceBuilder.from(((pageNum-1)*pageSize)).size(pageSize);
            // 构建高亮对象
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            // 指定高亮字段和高亮样式
            highlightBuilder.field("goodsName")
                    .preTags("<span style='color:red;'>")
                    .postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
            // 添加查询条件
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(key, "goodsName"));
            // 执行请求
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<GoodsVo> goodsVoList=null;
            if(searchResponse.getHits().getTotalHits().value>0){
                goodsVoList=new ArrayList<GoodsVo>();
                // 结果数据(如果不设置返回条数，大于十条默认只返回十条)
                SearchHit[] hits = searchResponse.getHits().getHits();
                for (SearchHit hit : hits) {
                    // 构建项目中所需的数据结果集
                    String highlightMessage = String.valueOf(hit.getHighlightFields().get("goodsName").fragments()[0]);
                    Integer goodsId = Integer.valueOf((Integer) hit.getSourceAsMap().get("goodsId"));
                    String goodsName = String.valueOf(hit.getSourceAsMap().get("goodsName"));
                    BigDecimal marketPrice = new BigDecimal(String.valueOf(hit.getSourceAsMap().get("marketPrice")));
                    String originalImg = String.valueOf(hit.getSourceAsMap().get("originalImg"));
                    goodsVoList.add(new GoodsVo(goodsId, goodsName,highlightMessage,marketPrice,originalImg));
                }
                ShopPageInfo<GoodsVo> shopPageInfo=new ShopPageInfo<GoodsVo>(pageNum,pageSize,Integer.parseInt(searchResponse.getHits().getTotalHits().value+""));
                shopPageInfo.setResult(goodsVoList);
                return shopPageInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ShopPageInfo<GoodsVo> shopPageInfo=new ShopPageInfo<GoodsVo>(pageNum,pageSize,0);
        return shopPageInfo;
    }
}
