package com.loogingko.ncjd;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.loogingko.ncjd.model.entity.Item;
import com.loogingko.ncjd.model.entity.ItemDoc;
import com.loogingko.ncjd.service.ItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(properties = "spring.profiles.active=local")
public class EsDocumentTest {

    private RestHighLevelClient client;
    
    @Autowired
    private ItemService itemService;
    
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

    //批量新增
    @Test
    void bulkAdd() throws IOException {
        int pageNo = 1;
        int pageSize = 500;
        
        while (true) {
            Page<Item> page = itemService.lambdaQuery()
                    .eq(Item::getStatus, 1)
                    .page(Page.of(pageNo, pageSize));
            List<Item> itemList = page.getRecords();

            if (CollUtil.isEmpty(itemList)) {
                return;
            }
            if (pageNo == 20) {
                return;
            }

            BulkRequest req = new BulkRequest();
            for (Item item : itemList) {
                req.add(
                        new IndexRequest("items").id(item.getId().toString()).source(JSONUtil.toJsonStr(BeanUtil.copyProperties(item, ItemDoc.class)), XContentType.JSON)
                );
            }
            client.bulk(req, RequestOptions.DEFAULT);
            pageNo++;
        }
    }

    @Test
    void getCount() {
        // 注意：RestHighLevelClient 没有直接提供 count API，通常使用 SearchRequest + SearchSourceBuilder.size(0) 来统计
        try {
            SearchRequest req = new SearchRequest("items");
            req.source(new SearchSourceBuilder().size(0)); // 不返回具体文档，只获取总数
            
            SearchResponse searchResponse = client.search(req, RequestOptions.DEFAULT);
            long count = searchResponse.getHits().getTotalHits().value;
            System.out.println("Total documents in 'items' index: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 新增/全量更新
    @Test
    void insOrUpd() throws IOException {
        Item item = itemService.getById("584392");
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
        itemDoc.setPrice(32100);

        IndexRequest req = new IndexRequest("items").id(itemDoc.getId());
        req.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
        IndexResponse resp = client.index(req, RequestOptions.DEFAULT);
        System.out.println("resp=" + resp.toString());
    }

    // 局部更新
    @Test
    void testUpd() throws IOException {
        UpdateRequest req = new UpdateRequest("items", "584392");
        req.doc(
                "price", 45600,
                "name", "123321哈哈哈哈哈哈哈哈哈哈"
        );
        client.update(req, RequestOptions.DEFAULT); 
    }

    @Test
    void getDoc() throws IOException {
        GetRequest req = new GetRequest("items", "584392");
        GetResponse documentFields = client.get(req, RequestOptions.DEFAULT);
        ItemDoc bean = JSONUtil.toBean(documentFields.getSourceAsString(), ItemDoc.class);
        System.out.println(bean);
    }

    @Test
    void deleteDoc() throws IOException {
        DeleteRequest req = new DeleteRequest("items", "584392");
        client.delete(req, RequestOptions.DEFAULT);
    }
}
