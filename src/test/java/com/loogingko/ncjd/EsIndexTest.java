//package com.loogingko.ncjd;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.CreateIndexRequest;
//import org.elasticsearch.client.indices.GetIndexRequest;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.IOException;
//
//@SpringBootTest
//public class EsIndexTest {
//
//    private RestHighLevelClient client;
//    
//    // 初始化方法（一切测试方法运行前执行）
//    @BeforeEach
//    void setUp() {
//        client = new RestHighLevelClient(RestClient.builder(
//                HttpHost.create("http://47.116.37.204:9200")
//        ));
//    }
//
//    // 销毁方法（一切测试方法结束后执行）
//    @AfterEach
//    void tearDown() throws IOException {
//        if (client != null) client.close();
//    }
//
//    @Test   
//    void testConnect() {
//        System.out.println("client=" + client);
//    }
//
//    @Test
//    void testCreateIndex() throws IOException {
//        // 1. 准备req对象
//        CreateIndexRequest req = new CreateIndexRequest("items");// 索引库名称
//        
//        // 2. 准备请求参数
//        req.source(TEMPLATE, XContentType.JSON);
//        
//        // 3. 发送请求
//        client.indices().create(req, RequestOptions.DEFAULT); 
//    }
//
//    @Test
//    void testGetIndex() throws IOException {
//        GetIndexRequest req = new GetIndexRequest("items");
//        boolean exists = client.indices().exists(req, RequestOptions.DEFAULT);
//        System.out.println("exists="+exists);
//    }
//
//    @Test
//    void testDeleteIndex() throws IOException {
//        DeleteIndexRequest req = new DeleteIndexRequest("items");
//        client.indices().delete(req, RequestOptions.DEFAULT);
//    }
//
//    static final String TEMPLATE = "{\n" +
//            "  \"mappings\": {\n" +
//            "    \"properties\": {\n" +
//            "      \"id\": {\n" +
//            "        \"type\": \"keyword\"\n" +
//            "      },\n" +
//            "      \"name\":{\n" +
//            "        \"type\": \"text\",\n" +
//            "        \"analyzer\": \"ik_max_word\"\n" +
//            "      },\n" +
//            "      \"price\":{\n" +
//            "        \"type\": \"integer\"\n" +
//            "      },\n" +
//            "      \"stock\":{\n" +
//            "        \"type\": \"integer\"\n" +
//            "      },\n" +
//            "      \"image\":{\n" +
//            "        \"type\": \"keyword\",\n" +
//            "        \"index\": false\n" +
//            "      },\n" +
//            "      \"category\":{\n" +
//            "        \"type\": \"keyword\"\n" +
//            "      },\n" +
//            "      \"brand\":{\n" +
//            "        \"type\": \"keyword\"\n" +
//            "      },\n" +
//            "      \"sold\":{\n" +
//            "        \"type\": \"integer\"\n" +
//            "      },\n" +
//            "      \"commentCount\":{\n" +
//            "        \"type\": \"integer\",\n" +
//            "        \"index\": false\n" +
//            "      },\n" +
//            "      \"isAD\":{\n" +
//            "        \"type\": \"boolean\"\n" +
//            "      },\n" +
//            "      \"updateTime\":{\n" +
//            "        \"type\": \"date\"\n" +
//            "      }\n" +
//            "    }\n" +
//            "  }\n" +
//            "}";
//    
//}
