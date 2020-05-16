[TOC]



# Elastic Search Java Api

## 前言

[前文](https://link.juejin.im/?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2F5ad4d7c9a3a9)我们提到过Elastic Search 操作索引的 Rest Api。实际上 Elastic Search 的 Rest Api 提供了所有的操作接口。在编程语言中可以直接这么使用 Rest Api 可以调用 Elastic Search 的所有功能，但是非常的不方便和直观，所以Elastic Search 官方也为很多语言提供了访问的 Api 接口。官方提供的编程语言接口包括：

- Java
- JavaScript
- Groovy
- PHP
- .NET
- Perl
- Python
- Ruby

同时编程社区也提供了大量的编程语言的 Api。目前主要有

- B4J
- Clojure
- ColdFusion (CFML)
- Erlang
- Go
- Groovy
- Haskell
- Java
- JavaScript
- kotlin
- Lua
- .NET
- OCaml
- Perl
- PHP
- Python
- R
- Ruby
- Rust
- Scala
- Smalltalk
- Vert.x

平时我们都是用 Java 进行开发。所以这里我会谈谈 Elastic Search 的 Java Api 的使用方式

## 准备工作

为了说明 Java Api 的功能，我们准备了一个场景。在这里我们假定有一批作者，每个作者都有标识、姓名、性别、年龄，描述着几个字段。我们需要通过姓名、年龄、描述中的关键词来查询作者，

在这里，程序主要通过 JUnit 测试用例的方式来运行，所以首先引入了 JUnit 的依赖

```
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
复制代码
```

## Java Api 概述

Elastic Search 提供了官方的 Java Api。这里包括两类，一类是 Low Level Rest Api（低级 Rest Api）和 High Leve Rest Api（高级 Rest Api）。

所谓低级 Api 并不是功能比较弱，而是指 Api 离底层实现比较近。官方提供的低级 Api 是对原始的 Rest Api 的第一层封装。只是把 Http 调用的细节封装起来。程序还是要自己组装查询的条件字符串、解析返回的结果 json 字符串等。同时也要处理 http 协议的 各种方法、协议头等内容。

高级 api 是在低级 api 上的进一步封装，不用在在意接口的方法，协议头，也不用人工组合调用的参数字符串，同时对返回的 json 字符串有一定的解析。使用上更方便一些。但是高级 api 并没有实现所有低级 api 实现的功能。所以如果遇到这种情况，还需要利用低级 api 来实现自己功能。

第三方 Java 客户端是有社区自己开发的 Elastic Search 客户端。官方提到了两个开源在 GitHub 上的项目 [Flummi](https://link.juejin.im/?target=https%3A%2F%2Fgithub.com%2Fotto-de%2Fflummi)、[Jest](https://link.juejin.im/?target=https%3A%2F%2Fgithub.com%2Fsearchbox-io%2FJest)

## Java Low Level Rest Api 使用说明

低级 Api 的优势在于依赖的其他库非常少，而且功能完备。缺点在于封装不够高级，所以使用起来还是非常的繁琐。我们这里先来看看低级的 api 是怎么使用的。

#### 引入依赖

在前面建立的 Maven Java 工程中，要使用 Elastic Search 的低级 Api，首先要引入 低级 Api 的依赖。如下所示

```
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-client</artifactId>
    <version>6.1.1</version>
</dependency>
复制代码
```

#### 建立客户端

```
RestClient restClient = RestClient.builder(
        new HttpHost("localhost", 9200, "http"),
        new HttpHost("localhost", 9201, "http")).build();
复制代码
```

我们通过 RestClient 对象的静态方法 builder(HttpHost... hosts) 和 builder()建立一个 Elastic Search 的 Rest 客户端。其中 hosts 是一个可变参数，用来指定 Elastic Cluster 集群的节点的 ip、端口、协议。

#### 方法调用

建立了客户端以后，通过两类方法来调用 Rest Api。一类是同步调用，一类是异步调用。

###### 同步调用

同步调用主要的方法声明如下所示：

```
public Response performRequest(String method, String endpoint, Header... headers) throws IOException

public Response performRequest(String method, String endpoint, Map<String, String> params, Header... headers) throws IOException

public Response performRequest(String method, String endpoint, Map<String, String> params,
                                   HttpEntity entity, Header... headers) throws IOException

复制代码
```

这是三个重载的方法，参数 method 代表的是 Rest Api 的方法，例如 PUT、GET、POST、DELETE等；参数 endpoint 代表的是 Rest Api 参数的地址，从 Rest Api 的 URL 的 ip:port 字段之后开始；params 是通过 url 参数形式传递的参数；entity 是通过 http body 传递的参数；headers 是一个可变参数，可以传入对应的 http 头信息。

例如，我要查看一个索引 author_test 的信息，我们可以用如下的代码来获取

```
Response response = restClient.performRequest("GET", "/author_test");
复制代码
```

再比如，我们要查看一个索引 author_test 中 des 字段中包含软件的文档信息，我们可以用如下代码来获取：

```
String queryJson = "{\n" +
        "    \"query\": {\n" +
        "        \"match\": {\n" +
        "            \"des\": \"软件\"\n" +
        "        }\n" +
        "    }\n" +
        "}";
Response response = restClient.performRequest("POST",
        "/author_test/_search",
        new HashMap<String, String>(),
        new NStringEntity(queryJson,ContentType.APPLICATION_JSON));
复制代码
```

#### 异步调用

异步调用和同步调用的参数是一样的，但是异步调用没有返回值，而是在参数中有一个 ResponseListener 回调对象，在调用完成后自动调用。这个回调对象是一个接口，需要程序员自己来实现。

异步调用的方法声明如下所示：

```
public void performRequestAsync(String method, String endpoint, ResponseListener responseListener, Header... headers)

public void performRequestAsync(String method, String endpoint, Map<String, String> params,
                                    ResponseListener responseListener, Header... headers)

public void performRequestAsync(String method, String endpoint, Map<String, String> params,
                                    HttpEntity entity, ResponseListener responseListener, Header... headers) 
复制代码
```

例如，我要用异步调用的方式查询 author_test 索引中 des 中包含 “软件” 的所有文档，则代码实现如下

```
String queryJson = "{\n" +
        "    \"query\": {\n" +
        "        \"match\": {\n" +
        "            \"des\": \"软件\"\n" +
        "        }\n" +
        "    }\n" +
        "}";


restClient.performRequestAsync("POST",
        "/author_test/_search",
        new HashMap<String, String>(),
        new NStringEntity(queryJson, ContentType.APPLICATION_JSON), new ResponseListener() {
            public void onSuccess(Response response) {
                try {
                    String responseData = readResposne(response);
                    System.out.println("******* search success ******");
                    System.out.println(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Exception exception) {
                exception.printStackTrace();
            }
        });
复制代码
```

## Java High Level Rest Api 使用说明

Elastic Search 的 Java 高级 Api 相对低级 Api 来说，抽象程度更高一些。不过我个人觉得还是挺难用的。而且高级 Api 并不支持所有的 Rest Api 的功能。官方有高级 Api 支持的[功能列表](https://link.juejin.im/?target=https%3A%2F%2Fwww.elastic.co%2Fguide%2Fen%2Felasticsearch%2Fclient%2Fjava-rest%2Fcurrent%2Fjava-rest-high-supported-apis.html)。从这里看，如果你只是做查询，用高级 Api 接口还是够用的。

#### 引入依赖

在前面建立的 Maven Java 工程中，要使用 Elastic Search 的低级 Api，首先要引入 低级 Api 的依赖。如下所示

```
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>6.1.1</version>
</dependency>
复制代码
```

#### 建立客户端

```
RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")));
```

和低级接口类似，先通过 RestClient 对象的静态方法 builder(HttpHost... hosts)方法建立一个 RestClientBuilder 对象，然后作为 RestHighLevelClient 对象构造函数的参数，来创建一个新的高级客户端对象。其中 hosts 是一个可变参数，用来指定 Elastic Cluster 集群的节点的 ip、端口、协议。

#### 方法调用

这里用高级接口来实现低级接口中第一个查询的功能。代码如下

```
SearchRequest searchRequest = new SearchRequest("author_test");
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.matchQuery("des", "软件"));
sourceBuilder.from(0);
sourceBuilder.size(5);
searchRequest.source(sourceBuilder);
SearchResponse response = restClient.search(searchRequest);
复制代码
```

其他的接口的调用都可以查找对应的 api [文档说明](https://link.juejin.im/?target=https%3A%2F%2Fwww.elastic.co%2Fguide%2Fen%2Felasticsearch%2Fclient%2Fjava-rest%2Fcurrent%2Fjava-rest-high-supported-apis.html)来完成

## 完整代码

最后一个章节将完整的代码贴出来。

#### 初始化代码

这部分代码负责初始化测试的索引和索引文档。需要注意一下，前面我们说过 Elastic Search 是一个准实时的系统，所以索引完文档后，如果马上查询，可能查询不到数据，需要有一个小的延迟。

```
package com.x9710.es.test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndexInitUtil {
    public RestClient initLowLevelClient() {
        // 通过 ip 、port 和协议建立 Elastic Search 客户端
        RestClient restClient = RestClient.builder(
                new HttpHost("10.110.2.53", 9200, "http")).build();


        try {
            initIndex(restClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return restClient;
    }

    public RestHighLevelClient initHighLevelClient() {
        // 通过 ip 、port 和协议建立 Elastic Search 客户端
        RestHighLevelClient highLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("10.110.2.53", 9200, "http"))
        );

        RestClient restClient = RestClient.builder(
                new HttpHost("10.110.2.53", 9200, "http")).build();

        try {
            initIndex(restClient);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                restClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return highLevelClient;
    }

    private void initIndex(RestClient restClient) {

        String authIndexDefine = "{\n" +
                "\t\"settings\" : {\n" +
                "        \"index\" : {\n" +
                "            \"number_of_shards\" : 6,\n" +
                "            \"number_of_replicas\" : 0\n" +
                "        }\n" +
                "    },\n" +
                "    \"mappings\": {\n" +
                "        \"doc\": {\n" +
                "            \"properties\": {\n" +
                "            \t\"id\": {\"type\": \"text\"},\n" +
                "                \"name\": {\"type\": \"text\"},\n" +
                "                \"sex\": {\"type\": \"text\"},\n" +
                "                \"age\": {\"type\": \"integer\"},\n" +
                "                \"des\":{\n" +
                "                \t\"type\":\"text\",\n" +
                "                \t\"analyzer\": \"ik_max_word\",\n" +
                "\t\t\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        HttpEntity authorIndexEntity = new NStringEntity(authIndexDefine, ContentType.APPLICATION_JSON);
        //初始化要索引的 author 文档列表
        List<HttpEntity> authorDocs = new ArrayList<HttpEntity>();
        authorDocs.add(new NStringEntity(" {\n" +
                "\t\"id\":\"A1001\",\n" +
                "\t\"name\":\"任盈盈\",\n" +
                "\t\"age\":24,\n" +
                "\t\"sex\":\"女\",\n" +
                "\t\"des\":\"IT软件工程师，擅长Java和软件架构\"\n" +
                " }", ContentType.APPLICATION_JSON));
        authorDocs.add(new NStringEntity(" {\n" +
                "\t\"id\":\"A1002\",\n" +
                "\t\"name\":\"风清扬\",\n" +
                "\t\"age\":47,\n" +
                "\t\"sex\":\"男\",\n" +
                "\t\"des\":\"IT软件技术经理，擅长技术管理过程控制\"\n" +
                " }", ContentType.APPLICATION_JSON));


        try {
            //创建 author_test 索引
            restClient.performRequest("PUT", "/author_test", new HashMap<String, String>(), authorIndexEntity);

            //索引 author_index 文档
            for (int i = 0; i < authorDocs.size(); i++) {
                restClient.performRequest("POST", "/author_test/doc", new HashMap<String, String>(), authorDocs.get(i));
            }
            //注意索引文档完成后，做一个小的延迟，保证后续查询能查到数据
            Thread.currentThread().sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

复制代码
```

#### 低级 Api 测试样例

```
package com.x9710.es.test;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Elastic Search 低级 Api 测试类
 *
 * @author 杨高超
 * @since 2018-01-11
 */
public class LowLeveApiTest {
    RestClient restClient = null;

    @Before
    public void before() {
        restClient = new IndexInitUtil().initLowLevelClient();
    }

    @Test
    public void testLocateAuthorIndex() {
        try {
            Response response = restClient.performRequest("GET", "/author_test");
            String responseData = readResposne(response);
            Assert.assertTrue(new JSONObject(responseData).has("author_test"));
            System.out.println(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }


    @Test
    public void testQueryAuthDoc() {
        try {
            String queryJson = "{\n" +
                    "    \"query\": {\n" +
                    "        \"match\": {\n" +
                    "            \"des\": \"Java\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            Response response = restClient.performRequest("POST",
                    "/author_test/_search",
                    new HashMap<String, String>(),
                    new NStringEntity(queryJson, ContentType.APPLICATION_JSON));

            String responseData = readResposne(response);
            JSONObject responseJson = new JSONObject(responseData);
            Assert.assertTrue(responseJson.has("hits")
                    && responseJson.getJSONObject("hits").getInt("total") == 1);
            System.out.println(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testQueryAuthDocAsy() {
        try {
String queryJson = "{\n" +
        "    \"query\": {\n" +
        "        \"match\": {\n" +
        "            \"des\": \"软件\"\n" +
        "        }\n" +
        "    }\n" +
        "}";


restClient.performRequestAsync("POST",
        "/author_test/_search",
        new HashMap<String, String>(),
        new NStringEntity(queryJson, ContentType.APPLICATION_JSON), new ResponseListener() {
            public void onSuccess(Response response) {
                try {
                    String responseData = readResposne(response);
                    System.out.println("******* search success ******");
                    System.out.println(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Exception exception) {
                exception.printStackTrace();
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }


    @After
    public void after() {
        try {
            if (restClient != null) {
                restClient.performRequest("DELETE", "/author_test");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (restClient != null) {
                try {
                    restClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readResposne(Response response) throws Exception {
        BufferedReader brd = new BufferedReader(new BufferedReader(new InputStreamReader(response.getEntity().getContent())));
        String line;
        StringBuilder respongseContext = new StringBuilder();

        while ((line = brd.readLine()) != null) {
            respongseContext.append(line).append("\n");
        }
        //rd.close();
        if (respongseContext.length() > 0) {
            respongseContext.deleteCharAt(respongseContext.length() - 1);
        }
        return respongseContext.toString();
    }
}

复制代码
```

#### 高级 Api 测试样例

```
package com.x9710.es.test;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Elastic Search 高级 Api 测试类
 *
 * @author 杨高超
 * @since 2018-01-11
 */
public class HighLevelApiTest {
    RestHighLevelClient restClient = null;

    @Before
    public void before() {
        restClient = new IndexInitUtil().initHighLevelClient();
    }


    @Test
    public void testQueryAuthDoc() {
        try {
SearchRequest searchRequest = new SearchRequest("author_test");
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
sourceBuilder.query(QueryBuilders.matchQuery("des", "软件"));
sourceBuilder.from(0);
sourceBuilder.size(5);
searchRequest.source(sourceBuilder);
SearchResponse response = restClient.search(searchRequest);
            Assert.assertTrue(response.getHits().getTotalHits() == 2);
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }


    @After
    public void after() {
        try {
            if (restClient != null) {
                restClient.indices().deleteIndex(new DeleteIndexRequest("author_test"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (restClient != null) {
                try {
                    restClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
复制代码
```

## 后记

前面也提到了，社区也贡献了很多 Elastic Search 的客户端库，但是没有时间去研究。如果有人用过觉得好用，希望推荐。

原文发表在[简书](https://link.juejin.im/?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2Fbf21cb2bd79c)上。





<https://juejin.im/post/5a5dd24e6fb9a01cba4296b0#heading-3>