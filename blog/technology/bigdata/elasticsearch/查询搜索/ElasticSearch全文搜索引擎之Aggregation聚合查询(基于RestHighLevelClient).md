# ElasticSearch全文搜索引擎之Aggregation聚合查询(基于RestHighLevelClient)
2020-10-12 11:40:19
## 一、简介
前面一篇文章我们已经通过DSL构建JSON查询请求体，并且结合Kibana介绍了常见的聚合查询，本篇文章主要是结合ES Java 高级API实现聚合查询，工作中更多的时候都是使用客户端进行操作，使用命令太繁琐了。
## 二、度量查询
测试数据还是跟前面一篇文章一致，这里就不过多阐述，详细可以参照上文，执行脚本如下：
```
# 批量插入测试数据
POST /user/info/_bulk
{"index":{"_id":1}}
{"name":"zs","realname":"张三","age":10,"birthday":"2018-12-27","salary":1000.0,"address":"广州市天河区科韵路50号"}
{"index":{"_id":2}}
{"name":"ls","realname":"李四","age":20,"birthday":"2017-10-20","salary":2000.0,"address":"广州市天河区珠江新城"}
{"index":{"_id":3}}
{"name":"ww","realname":"王五","age":30,"birthday":"2016-03-15","salary":3000.0,"address":"广州市天河区广州塔"}
{"index":{"_id":4}}
{"name":"zl","realname":"赵六","age":40,"birthday":"2003-04-19","salary":4000.0,"address":"广州市海珠区"}
{"index":{"_id":5}}
{"name":"tq","realname":"田七","age":50,"birthday":"2001-08-11","salary":5000.0,"address":"广州市天河区网易大厦"}
```
### 【a】平均值聚合查询

> AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("avg_salary").field("salary")
如：求员工平均工资
```
/**
   * 平均值聚合查询: AggregationBuilders.avg("avg_salary").field("salary")
   */
  @Test
  void avgAggregation() throws IOException {
      //创建一个查询请求，并指定索引名称
      SearchRequest searchRequest = new SearchRequest("user");
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
       //平均值聚合查询: 求员工平均工资
      AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("avg_salary").field("salary");
      searchSourceBuilder.aggregation(avgAggregationBuilder);
      searchRequest.source(searchSourceBuilder);
      SearchResponse response;
      try {
          //发起请求，获取响应结果
          response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
          //获取聚合的结果
          Aggregations aggregations = response.getAggregations();
          System.out.println(JSON.toJSONString(aggregations));
          System.out.println(JSON.toJSONString(aggregations.getAsMap().get("avg_salary")));
      } catch (IOException e) {
          e.printStackTrace();
      }
      //关闭restHighLevelClient
      restHighLevelClient.close();
  }
```
响应结果：
```
{"asMap":{"avg_salary":{"fragment":true,"name":"avg_salary","type":"avg","value":3000.0,"valueAsString":"3000.0"}},"fragment":true}
{"fragment":true,"name":"avg_salary","type":"avg","value":3000.0,"valueAsString":"3000.0"}
```
### 【b】最大值聚合查询

> MaxAggregationBuilder maxAggregationBuilder = AggregationBuilders.max("max_salary").field("salary")
如：求员工最高工资
```
/**
   * 最大值聚合查询: AggregationBuilders.max("max_salary").field("salary")
   */
  @Test
  void maxAggregation() throws IOException {
      //创建一个查询请求，并指定索引名称
      SearchRequest searchRequest = new SearchRequest("user");
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
       //求员工最高工资
      MaxAggregationBuilder maxAggregationBuilder = AggregationBuilders.max("max_salary").field("salary");
      searchSourceBuilder.aggregation(maxAggregationBuilder);
      searchRequest.source(searchSourceBuilder);
      SearchResponse response;
      try {
          //发起请求，获取响应结果
          response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
          //获取聚合的结果
          Aggregations aggregations = response.getAggregations();
          System.out.println(JSON.toJSONString(aggregations));
          System.out.println(JSON.toJSONString(aggregations.getAsMap().get("max_salary")));
      } catch (IOException e) {
          e.printStackTrace();
      }
      //关闭restHighLevelClient
      restHighLevelClient.close();
  }
```
响应结果：
```
{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":5000.0,"valueAsString":"5000.0"}},"fragment":true}
{"fragment":true,"name":"max_salary","type":"max","value":5000.0,"valueAsString":"5000.0"}
```
### 【c】最小值聚合查询

> MinAggregationBuilder minAggregationBuilder = AggregationBuilders.min("min_salary").field("salary")
如：求员工最低工资
```
/**
     * 最小值聚合查询: AggregationBuilders.min("min_salary").field("salary")
     */
    @Test
    void minAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //求员工最低工资
        MinAggregationBuilder minAggregationBuilder = AggregationBuilders.min("min_salary").field("salary");
        searchSourceBuilder.aggregation(minAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            System.out.println(JSON.toJSONString(aggregations));
            System.out.println(JSON.toJSONString(aggregations.getAsMap().get("min_salary")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"asMap":{"min_salary":{"fragment":true,"name":"min_salary","type":"min","value":1000.0,"valueAsString":"1000.0"}},"fragment":true}
{"fragment":true,"name":"min_salary","type":"min","value":1000.0,"valueAsString":"1000.0"}
```
### 【d】求和聚合查询

> SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("sum_salary").field("salary")
如：求所有员工总工资
```
/**
     * 求和聚合查询: AggregationBuilders.sum("sum_salary").field("salary")
     */
    @Test
    void sumAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //求员工总工资
        SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("sum_salary").field("salary");
        searchSourceBuilder.aggregation(sumAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            System.out.println(JSON.toJSONString(aggregations));
            System.out.println(JSON.toJSONString(aggregations.getAsMap().get("sum_salary")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果如下：
```
{"asMap":{"sum_salary":{"fragment":true,"name":"sum_salary","type":"sum","value":15000.0,"valueAsString":"15000.0"}},"fragment":true}
{"fragment":true,"name":"sum_salary","type":"sum","value":15000.0,"valueAsString":"15000.0"}
```
### 【e】stats聚合查询

> StatsAggregationBuilder statsAggregationBuilder = AggregationBuilders.stats("stats_salary").field("salary")
如：一次性查询出员工最低工资、最高工资、总工资等
```
/**
     * stats聚合查询: AggregationBuilders.stats("stats_salary").field("salary")
     */
    @Test
    void statsAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //统一求员工最低工资、最高工资、总工资等
        StatsAggregationBuilder statsAggregationBuilder = AggregationBuilders.stats("stats_salary").field("salary");
        searchSourceBuilder.aggregation(statsAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            System.out.println(JSON.toJSONString(aggregations));
            System.out.println(JSON.toJSONString(aggregations.getAsMap().get("stats_salary")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"asMap":{"stats_salary":{"avg":3000.0,"avgAsString":"3000.0","count":5,"fragment":true,"max":5000.0,"maxAsString":"5000.0","min":1000.0,"minAsString":"1000.0","name":"stats_salary","sum":15000.0,"sumAsString":"15000.0","type":"stats"}},"fragment":true}
{"avg":3000.0,"avgAsString":"3000.0","count":5,"fragment":true,"max":5000.0,"maxAsString":"5000.0","min":1000.0,"minAsString":"1000.0","name":"stats_salary","sum":15000.0,"sumAsString":"15000.0","type":"stats"}
```
### 【f】value count聚合查询

> ValueCountAggregationBuilder valueCountAggregationBuilder = AggregationBuilders.count("price_value_count").field("salary")
如：统计工资字段有值的文档数目
```
/**
     * value count聚合查询: AggregationBuilders.count("price_value_count").field("salary")
     */
    @Test
    void valueCountAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //统计某字段有值的文档数
        ValueCountAggregationBuilder valueCountAggregationBuilder = AggregationBuilders.count("price_value_count").field("salary");
        searchSourceBuilder.aggregation(valueCountAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            System.out.println(JSON.toJSONString(aggregations));
            System.out.println(JSON.toJSONString(aggregations.getAsMap().get("price_value_count")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"asMap":{"price_value_count":{"fragment":true,"name":"price_value_count","type":"value_count","value":5,"valueAsString":"5.0"}},"fragment":true}
{"fragment":true,"name":"price_value_count","type":"value_count","value":5,"valueAsString":"5.0"}
```
## 三、桶查询
### 【a】range范围桶聚合查询

> RangeAggregationBuilder rangeAggregationBuilder = AggregationBuilders.range("age_ranges_count").field("age").addRange(0, 20).addRange(20, 40).addRange(40, 60)
如：统计0-20岁，20-40岁，40~60岁各个区间段的员工人数
```
/**
     * range范围桶聚合查询: AggregationBuilders.range("age_ranges_count").field("age").addRange(0, 20).addRange(20, 40).addRange(40, 60)
     */
    @Test
    void rangeAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //统计0-20岁，20-40岁，40~60岁各个区间段的用户人数
        RangeAggregationBuilder rangeAggregationBuilder = AggregationBuilders.range("age_ranges_count")
                .field("age")
                .addRange(0, 20)
                .addRange(20, 40)
                .addRange(40, 60);
        searchSourceBuilder.aggregation(rangeAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            Aggregation aggregation = aggregations.get("age_ranges_count");
            System.out.println(JSON.toJSONString(aggregation));
            //获取桶聚合结果
            List<? extends Range.Bucket> buckets = ((Range) aggregation).getBuckets();
            //循环遍历各个桶结果
            for (Range.Bucket bucket : buckets) {
                //分组的key
                String key = bucket.getKeyAsString();
                //分组的值
                long docCount = bucket.getDocCount();
                System.out.println(key + "------->" + docCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"buckets":[{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"from":0.0,"fromAsString":"0.0","key":"0.0-20.0","keyAsString":"0.0-20.0","to":20.0,"toAsString":"20.0"},{"aggregations":{"asMap":{},"fragment":true},"docCount":2,"fragment":true,"from":20.0,"fromAsString":"20.0","key":"20.0-40.0","keyAsString":"20.0-40.0","to":40.0,"toAsString":"40.0"},{"aggregations":{"asMap":{},"fragment":true},"docCount":2,"fragment":true,"from":40.0,"fromAsString":"40.0","key":"40.0-60.0","keyAsString":"40.0-60.0","to":60.0,"toAsString":"60.0"}],"fragment":true,"name":"age_ranges_count","type":"range"}
0.0-20.0------->1
20.0-40.0------->2
40.0-60.0------->2
```
### 【b】term自定义分组桶聚合查询

> TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("age_count").field("age").size(3)
如：根据年龄分组，统计相同年龄的用户，并只返回3条记录
```
/**
     * term自定义分组桶聚合查询: AggregationBuilders.terms("age_count").field("age").size(3)
     */
    @Test
    void termsAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //根据年龄分组，统计相同年龄的用户，并只返回3条记录
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("age_count").field("age").size(3);
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            Aggregation aggregation = aggregations.get("age_count");
            System.out.println(JSON.toJSONString(aggregation));
            //获取桶聚合结果
            List<? extends Terms.Bucket> buckets = ((Terms) aggregation).getBuckets();
            //循环遍历各个桶结果
            for (Terms.Bucket bucket : buckets) {
                //分组的key
                String key = bucket.getKeyAsString();
                //分组的值
                long docCount = bucket.getDocCount();
                System.out.println(key + "------->" + docCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"buckets":[{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"docCountError":0,"fragment":true,"key":10,"keyAsNumber":10,"keyAsString":"10"},{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"docCountError":0,"fragment":true,"key":20,"keyAsNumber":20,"keyAsString":"20"},{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"docCountError":0,"fragment":true,"key":30,"keyAsNumber":30,"keyAsString":"30"}],"docCountError":0,"fragment":true,"name":"age_count","sumOfOtherDocCounts":2,"type":"lterms"}
10------->1
20------->1
30------->1
```
### 【c】日期范围分组桶聚合查询

> DateRangeAggregationBuilder dateRangeAggregationBuilder = AggregationBuilders.dateRange("birthday_count").field("birthday").addRange("now/y-1y", "now/y").addRange("now/y-2y", "now/y-1y").addRange("now/y-3y", "now/y-2y").format("yyyy-MM-dd")
如：统计生日在2017年、2018年、2019年的员工
```
/**
     * 日期范围分组桶聚合查询: AggregationBuilders.dateRange("birthday_count").field("birthday").addRange("now/y-1y", "now/y").addRange("now/y-2y", "now/y-1y").addRange("now/y-3y", "now/y-2y").format("yyyy-MM-dd")
     */
    @Test
    void dateRangeAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //统计生日在2017年、2018年、2019年的用户
        DateRangeAggregationBuilder dateRangeAggregationBuilder = AggregationBuilders.dateRange("birthday_count")
                .field("birthday")
                .addRange("now/y-1y", "now/y")  //  now/y：当前年的1月1日  now：当前时间
                .addRange("now/y-2y", "now/y-1y")  //  now/y-1y：当前年上一年的1月1日
                .addRange("now/y-3y", "now/y-2y")
                .format("yyyy-MM-dd");
        searchSourceBuilder.aggregation(dateRangeAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            Aggregation aggregation = aggregations.get("birthday_count");
            System.out.println(JSON.toJSONString(aggregation));
            //获取桶聚合结果
            List<? extends Range.Bucket> buckets = ((Range) aggregation).getBuckets();
            //循环遍历各个桶结果
            for (Range.Bucket bucket : buckets) {
                //分组的key
                String key = bucket.getKeyAsString();
                //分组的值
                long docCount = bucket.getDocCount();
                System.out.println(key + "------->" + docCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"buckets":[{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"from":"2017-01-01T00:00Z","fromAsString":"2017-01-01","key":"2017-01-01-2018-01-01","keyAsString":"2017-01-01-2018-01-01","to":"2018-01-01T00:00Z","toAsString":"2018-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"from":"2018-01-01T00:00Z","fromAsString":"2018-01-01","key":"2018-01-01-2019-01-01","keyAsString":"2018-01-01-2019-01-01","to":"2019-01-01T00:00Z","toAsString":"2019-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"from":"2019-01-01T00:00Z","fromAsString":"2019-01-01","key":"2019-01-01-2020-01-01","keyAsString":"2019-01-01-2020-01-01","to":"2020-01-01T00:00Z","toAsString":"2020-01-01"}],"fragment":true,"name":"birthday_count","type":"date_range"}
2017-01-01-2018-01-01------->1
2018-01-01-2019-01-01------->1
2019-01-01-2020-01-01------->0
```
### 【d】直方图桶聚合查询

>  HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram("age_histogram_count").field("age").interval(20)
如：根据年龄间隔（20岁）统计各个年龄段的员工总人数
```
/**
     * 直方图桶聚合查询: AggregationBuilders.histogram("age_histogram_count").field("age").interval(20);
     */
    @Test
    void histogramAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //根据年龄间隔（20岁）统计各个年龄段的员工总人数
        HistogramAggregationBuilder histogramAggregationBuilder = AggregationBuilders.histogram("age_histogram_count")
                .field("age")
                .interval(20);
        searchSourceBuilder.aggregation(histogramAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            Aggregation aggregation = aggregations.get("age_histogram_count");
            System.out.println(JSON.toJSONString(aggregation));
            //获取桶聚合结果
            List<? extends Histogram.Bucket> buckets = ((Histogram) aggregation).getBuckets();
            //循环遍历各个桶结果
            for (Histogram.Bucket bucket : buckets) {
                //分组的key
                String key = bucket.getKeyAsString();
                //分组的值
                long docCount = bucket.getDocCount();
                System.out.println(key + "------->" + docCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"buckets":[{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"key":0.0,"keyAsString":"0.0"},{"aggregations":{"asMap":{},"fragment":true},"docCount":2,"fragment":true,"key":20.0,"keyAsString":"20.0"},{"aggregations":{"asMap":{},"fragment":true},"docCount":2,"fragment":true,"key":40.0,"keyAsString":"40.0"}],"fragment":true,"name":"age_histogram_count","type":"histogram"}
0.0------->1
20.0------->2
40.0------->2
```
### 【e】日期直方图桶聚合查询

> DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("birthday_data_histogram_count").field("birthday").calendarInterval(DateHistogramInterval.YEAR).format("yyyy-MM-dd");
如：按年统计员工生日的总人数
```
/**
     * 日期直方图桶聚合查询: AggregationBuilders.dateHistogram("birthday_data_histogram_count").field("birthday").calendarInterval(DateHistogramInterval.YEAR).format("yyyy-MM-dd");
     */
    @Test
    void dateHistogramAggregation() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //按年统计用户生日的总人数
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("birthday_data_histogram_count")
                .field("birthday")
                .calendarInterval(DateHistogramInterval.YEAR)
                .format("yyyy-MM-dd");
        searchSourceBuilder.aggregation(dateHistogramAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            Aggregation aggregation = aggregations.get("birthday_data_histogram_count");
            System.out.println(JSON.toJSONString(aggregation));
            //获取桶聚合结果
            List<? extends Histogram.Bucket> buckets = ((Histogram) aggregation).getBuckets();
            //循环遍历各个桶结果
            for (Histogram.Bucket bucket : buckets) {
                //分组的key
                String key = bucket.getKeyAsString();
                //分组的值
                long docCount = bucket.getDocCount();
                System.out.println(key + "------->" + docCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"buckets":[{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"key":"2001-01-01T00:00Z","keyAsString":"2001-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2002-01-01T00:00Z","keyAsString":"2002-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"key":"2003-01-01T00:00Z","keyAsString":"2003-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2004-01-01T00:00Z","keyAsString":"2004-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2005-01-01T00:00Z","keyAsString":"2005-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2006-01-01T00:00Z","keyAsString":"2006-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2007-01-01T00:00Z","keyAsString":"2007-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2008-01-01T00:00Z","keyAsString":"2008-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2009-01-01T00:00Z","keyAsString":"2009-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2010-01-01T00:00Z","keyAsString":"2010-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2011-01-01T00:00Z","keyAsString":"2011-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2012-01-01T00:00Z","keyAsString":"2012-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2013-01-01T00:00Z","keyAsString":"2013-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2014-01-01T00:00Z","keyAsString":"2014-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":0,"fragment":true,"key":"2015-01-01T00:00Z","keyAsString":"2015-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"key":"2016-01-01T00:00Z","keyAsString":"2016-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"key":"2017-01-01T00:00Z","keyAsString":"2017-01-01"},{"aggregations":{"asMap":{},"fragment":true},"docCount":1,"fragment":true,"key":"2018-01-01T00:00Z","keyAsString":"2018-01-01"}],"fragment":true,"name":"birthday_data_histogram_count","type":"date_histogram"}
2001-01-01------->1
2002-01-01------->0
2003-01-01------->1
2004-01-01------->0
2005-01-01------->0
2006-01-01------->0
2007-01-01------->0
2008-01-01------->0
2009-01-01------->0
2010-01-01------->0
2011-01-01------->0
2012-01-01------->0
2013-01-01------->0
2014-01-01------->0
2015-01-01------->0
2016-01-01------->1
2017-01-01------->1
2018-01-01------->1
```
## 四、嵌套查询
使用RestHighLevelClient实现嵌套聚合查询主要通过下面的代码实现，通过设置subAggregation实现。
> xxxAggregationBuilder.subAggregation(xxxAggregationBuilder);
下面通过两个案例介绍es中嵌套聚合查询如何实现。 
- 示例一: 统计每年中用户的最高工资
```java
	/**
     * 示例一: 统计每年中用户的最高工资
     */
    @Test
    void countEveryYearMaxSalary() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //统计每年中用户的最高工资
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("birthday_data_histogram_count")
                .field("birthday")
                .calendarInterval(DateHistogramInterval.YEAR)
                .format("yyyy-MM-dd");
 
        //嵌套子聚合查询
        MaxAggregationBuilder maxAggregationBuilder = AggregationBuilders.max("max_salary")
                .field("salary");
        dateHistogramAggregationBuilder.subAggregation(maxAggregationBuilder);
        searchSourceBuilder.aggregation(dateHistogramAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            Aggregation aggregation = aggregations.get("birthday_data_histogram_count");
            System.out.println(JSON.toJSONString(aggregation));
            //获取桶聚合结果
            List<? extends Histogram.Bucket> buckets = ((Histogram) aggregation).getBuckets();
            //循环遍历各个桶结果
            for (Histogram.Bucket bucket : buckets) {
                //分组的key
                String key = bucket.getKeyAsString();
                Aggregations subAggregations = bucket.getAggregations();
                Aggregation maxSalaryAggregation = subAggregations.getAsMap().get("max_salary");
                System.out.println(JSON.toJSONString(maxSalaryAggregation));
                Map<String, Object> maxSalaryMap = (Map<String, Object>) JSONObject.parse(JSON.toJSONString(maxSalaryAggregation));
                String maxSalary = null != maxSalaryMap.get("value") ? maxSalaryMap.get("value").toString() : "";
                System.out.println(key + "------->" + maxSalary);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果：
```
{"buckets":[{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":5000.0,"valueAsString":"5000.0"}},"fragment":true},"docCount":1,"fragment":true,"key":"2001-01-01T00:00Z","keyAsString":"2001-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2002-01-01T00:00Z","keyAsString":"2002-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":4000.0,"valueAsString":"4000.0"}},"fragment":true},"docCount":1,"fragment":true,"key":"2003-01-01T00:00Z","keyAsString":"2003-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2004-01-01T00:00Z","keyAsString":"2004-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2005-01-01T00:00Z","keyAsString":"2005-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2006-01-01T00:00Z","keyAsString":"2006-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2007-01-01T00:00Z","keyAsString":"2007-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2008-01-01T00:00Z","keyAsString":"2008-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2009-01-01T00:00Z","keyAsString":"2009-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2010-01-01T00:00Z","keyAsString":"2010-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2011-01-01T00:00Z","keyAsString":"2011-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2012-01-01T00:00Z","keyAsString":"2012-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2013-01-01T00:00Z","keyAsString":"2013-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2014-01-01T00:00Z","keyAsString":"2014-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}},"fragment":true},"docCount":0,"fragment":true,"key":"2015-01-01T00:00Z","keyAsString":"2015-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":3000.0,"valueAsString":"3000.0"}},"fragment":true},"docCount":1,"fragment":true,"key":"2016-01-01T00:00Z","keyAsString":"2016-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":2000.0,"valueAsString":"2000.0"}},"fragment":true},"docCount":1,"fragment":true,"key":"2017-01-01T00:00Z","keyAsString":"2017-01-01"},{"aggregations":{"asMap":{"max_salary":{"fragment":true,"name":"max_salary","type":"max","value":1000.0,"valueAsString":"1000.0"}},"fragment":true},"docCount":1,"fragment":true,"key":"2018-01-01T00:00Z","keyAsString":"2018-01-01"}],"fragment":true,"name":"birthday_data_histogram_count","type":"date_histogram"}
{"fragment":true,"name":"max_salary","type":"max","value":5000.0,"valueAsString":"5000.0"}
2001-01-01------->5000.0
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2002-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":4000.0,"valueAsString":"4000.0"}
2003-01-01------->4000.0
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2004-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2005-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2006-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2007-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2008-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2009-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2010-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2011-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2012-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2013-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2014-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":null,"valueAsString":"-Infinity"}
2015-01-01------->
{"fragment":true,"name":"max_salary","type":"max","value":3000.0,"valueAsString":"3000.0"}
2016-01-01------->3000.0
{"fragment":true,"name":"max_salary","type":"max","value":2000.0,"valueAsString":"2000.0"}
2017-01-01------->2000.0
{"fragment":true,"name":"max_salary","type":"max","value":1000.0,"valueAsString":"1000.0"}
2018-01-01------->1000.0
```
-  示例二: 统计每个年龄区间段的工资总和
```
/**
     * 示例二: 统计每个年龄区间段的工资总和
     */
    @Test
    @SuppressWarnings("unchecked")
    void countEveryRangeAgeSumSalary() throws IOException {
        //创建一个查询请求，并指定索引名称
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
 
        //统计每年中用户的最高工资
        DateRangeAggregationBuilder dateRangeAggregationBuilder = AggregationBuilders.dateRange("age_ranges_count")
                .field("age")
                .addRange(0, 20)
                .addRange(20, 40)
                .addRange(40, 60);
 
        //嵌套子聚合查询
        SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("sum_salary")
                .field("salary");
        dateRangeAggregationBuilder.subAggregation(sumAggregationBuilder);
        searchSourceBuilder.aggregation(dateRangeAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            //发起请求，获取响应结果
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取聚合的结果
            Aggregations aggregations = response.getAggregations();
            Aggregation aggregation = aggregations.get("age_ranges_count");
            System.out.println(JSON.toJSONString(aggregation));
            //获取桶聚合结果
            List<? extends Range.Bucket> buckets = ((Range) aggregation).getBuckets();
            //循环遍历各个桶结果
            for (Range.Bucket bucket : buckets) {
                //分组的key
                String key = bucket.getKeyAsString();
                Aggregations subAggregations = bucket.getAggregations();
                Aggregation maxSalaryAggregation = subAggregations.getAsMap().get("sum_salary");
                System.out.println(JSON.toJSONString(maxSalaryAggregation));
                Map<String, Object> sumSalaryMap = (Map<String, Object>) JSONObject.parse(JSON.toJSONString(maxSalaryAggregation));
                String maxSalary = null != sumSalaryMap.get("value") ? sumSalaryMap.get("value").toString() : "";
                System.out.println(key + "------->" + maxSalary);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭restHighLevelClient
        restHighLevelClient.close();
    }
```
响应结果： 
```
{"buckets":[{"aggregations":{"asMap":{"sum_salary":{"fragment":true,"name":"sum_salary","type":"sum","value":1000.0,"valueAsString":"1000.0"}},"fragment":true},"docCount":1,"fragment":true,"from":"1970-01-01T00:00Z","fromAsString":"0.0","key":"0.0-20.0","keyAsString":"0.0-20.0","to":"1970-01-01T00:00:00.020Z","toAsString":"20.0"},{"aggregations":{"asMap":{"sum_salary":{"fragment":true,"name":"sum_salary","type":"sum","value":5000.0,"valueAsString":"5000.0"}},"fragment":true},"docCount":2,"fragment":true,"from":"1970-01-01T00:00:00.020Z","fromAsString":"20.0","key":"20.0-40.0","keyAsString":"20.0-40.0","to":"1970-01-01T00:00:00.040Z","toAsString":"40.0"},{"aggregations":{"asMap":{"sum_salary":{"fragment":true,"name":"sum_salary","type":"sum","value":9000.0,"valueAsString":"9000.0"}},"fragment":true},"docCount":2,"fragment":true,"from":"1970-01-01T00:00:00.040Z","fromAsString":"40.0","key":"40.0-60.0","keyAsString":"40.0-60.0","to":"1970-01-01T00:00:00.060Z","toAsString":"60.0"}],"fragment":true,"name":"age_ranges_count","type":"date_range"}
{"fragment":true,"name":"sum_salary","type":"sum","value":1000.0,"valueAsString":"1000.0"}
0.0-20.0------->1000.0
{"fragment":true,"name":"sum_salary","type":"sum","value":5000.0,"valueAsString":"5000.0"}
20.0-40.0------->5000.0
{"fragment":true,"name":"sum_salary","type":"sum","value":9000.0,"valueAsString":"9000.0"}
40.0-60.0------->9000.0
```
## 五、总结
本篇文章主要总结了使用RestHighLevelClient客户端实现ES聚合查询，通过示例介绍了一些常见的聚合查询，希望能对小伙伴们的学习有所帮助，由于笔者水平有限，可能存在不对之处，还望指正，相互学习，一起进步！