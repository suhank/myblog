# elasticsearch利用filter让你的查询效率飞起来-must-should介绍

## bool查询简介

Elasticsearch(下面简称ES)中的bool查询在业务中使用也是比较多的。在一些非实时的分页查询，导出的场景，我们经常使用bool查询组合各种查询条件。

Bool查询包括四种子句，

- must
- filter
- should
- must_not

我这里只介绍下must和filter两种子句，因为是我们今天要讲的重点。其它的可以自行查询官方文档。

1. must， 返回的文档必须满足must子句的条件，并且参与计算分值
2. filter， 返回的文档必须满足filter子句的条件。但是跟Must不一样的是，不会计算分值， 并且可以使用缓存

从上面的描述来看，你应该已经知道，如果只看查询的结果，must和filter是一样的。区别是场景不一样。如果结果需要算分就使用must，否则可以考虑使用filter。

光说比较抽象，看个例子，下面两个语句，查询的结果是一样的。

使用filter过滤时间范围，

```
GET kibana_sample_data_ecommerce/_search
{
  "size": 1000, 
  "query": {
    "bool": {
      "must": [
        {"term": {
          "currency": "EUR"
        }}
      ],
      "filter": {
        "range": {
          "order_date": {
            "gte": "2020-01-25T23:45:36.000+00:00",
            "lte": "2020-02-01T23:45:36.000+00:00"
          }
        }
      }
    }
  }
}
```

使用must过滤时间范围，

```
GET kibana_sample_data_ecommerce/_search
{
  "size": 1000, 
  "query": {
    "bool": {
      "must": [
        {"term": {
          "currency": "EUR"
        }},
        {"range": {
          "order_date": {
            "gte": "2020-01-25T23:45:36.000+00:00",
            "lte": "2020-02-01T23:45:36.000+00:00"
          }
        }}
      ]
    }
  }
}
```

查询的结果都是，

```
{
  "took" : 25,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1087,
      "relation" : "eq"
    },
    
    ...
```

## filter比较高效的原理

上一节你已经知道了must和filter的基本用法和区别。简单来讲，如果你的业务场景不需要算分，使用filter可以真的让你的查询效率飞起来。

为了说明filter查询高效的原因，我们需要引入ES的一个概念`query context`和`filter context`。

**query context**

`query context`关注的是，文档到底有多匹配查询的条件，这个匹配的程度是由相关性分数决定的，分数越高自然就越匹配。所以这种查询除了关注文档是否满足查询条件，还需要额外的计算相关性分数.

**filter context**

`filter context`关注的是，文档是否匹配查询条件，结果只有两个，是和否。没有其它额外的计算。它常用的一个场景就是过滤时间范围。

并且filter context会自动被ES缓存结果，效率进一步提高。

对于bool查询，must使用的就是`query context`，而filter使用的就是`filter context`。

我们可以通过一个示例验证下。继续使用第一节的例子，我们通过kibana自带的`search profiler`来看看ES的查询的详细过程。

使用must查询的执行过程是这样的：

![1-1.jpg](image-202012011205/bVbG2ud)

![1-2.jpg](image-202012011205/bVbG2ug)

可以明显看到，此次查询计算了相关性分数，而且score的部分占据了查询时间的10分之一左右。

filter的查询我就不截图了，区别就是score这部分是0，也就是不计算相关性分数。

除了是否计算相关性算分的差别，经常使用的过滤器将被Elasticsearch自动缓存，以提高性能。

我自己曾经在一个项目中，对一个业务查询场景做了这种优化，当时线上的索引文档数量大概是3000万左右，改成filter之后，查询的速度几乎快了一倍。

我截了几张图，你来感受下。

![1-3.jpg](image-202012011205/bVbG2uh)

![1-4.jpg](image-202012011205/bVbG2ui)

## 总结

我们应该根据自己的实际业务场景选择合适的查询语句，在某些不需要相关性算分的查询场景，尽量使用`filter context`可以让你的查询更加高效。





[ES系列之利用filter让你的查询效率飞起来_犀牛饲养员的技术笔记 - SegmentFault 思否](https://segmentfault.com/a/1190000022611664)