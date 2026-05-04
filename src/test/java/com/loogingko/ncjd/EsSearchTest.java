package com.loogingko.ncjd;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(properties = "spring.profiles.active=local")
public class EsSearchTest {

    private RestHighLevelClient client;

    // 初始化方法（一切测试方法运行前执行）
    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://47.116.37.204:9200")
        ));
    }

    // 销毁方法（一切测试方法结束后执行）
    @AfterEach
    void tearDown() throws IOException {
        if (client != null) client.close();
    }

    @Test
    void testSearch() throws IOException {
        // 1. 创建查询req对象
        SearchRequest req = new SearchRequest("items");
        
        // 2. 配置req
        req.source()
                .query(QueryBuilders.matchAllQuery());

        SearchResponse search = client.search(req, RequestOptions.DEFAULT);
        System.out.println(search);
    }
    
    // 聚合查询 Aggre
    @Test
    void testAgg() throws IOException {
        // 1. 创建查询req
        SearchRequest req = new SearchRequest("items");
        
        // 2. 组建DSL参数
        req.source().size(0); // 分页为零
        req.source().aggregation(
                AggregationBuilders.terms("brandAgg").field("brand").size(10)
        );
        // 3. 拿到结果
        SearchResponse resp = client.search(req, RequestOptions.DEFAULT);
        System.out.println(resp);
        
        // 4. 解析聚合结果
        Aggregations aggs = resp.getAggregations();
        Terms brandAgg = aggs.get("brandAgg"); // 多态
        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();

        for (Terms.Bucket bucket : buckets) {
            System.out.println("品牌：" + bucket.getKey());
            System.out.println("数量：" + bucket.getDocCount());
        }

    }
}
