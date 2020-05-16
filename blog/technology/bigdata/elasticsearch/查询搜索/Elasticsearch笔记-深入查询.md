[TOC]



# Elasticsearch笔记-深入查询

2016年10月28日 09:18:21

 

梧桐和风

 

阅读数：4914

更多

所属专栏： [ElasticSearch笔记系列](https://blog.csdn.net/column/details/17702.html)





上一篇笔记[Elasticsearch笔记-索引与查询](http://blog.csdn.net/wthfeng/article/details/52918003)我们介绍了ES的简单的增删查改，这一次我们深入ES较为复杂的查询，比如SQL中常用的select in 、模糊查询、返回部分字段等等。

一般来说，ES的真正用途在于分布式的搜索引擎，由于其稳定快速的查询性能又被当做数据库使用。

承接上一篇数据，此时posts索引article类型中的数据如下：

```json
{
    "hits": {
        "total": 4,
        "max_score": 1,
        "hits": [{
            "_index": "posts",
            "_type": "article",
            "_id": "5",
            "_score": 1,
            "_source": {
                "id": 5,
                "name": "生活日志",
                "author": "wthfeng",
                "date": "2015-09-21",
                "contents": "这是日常生活的记录"
            }
        }, {
            "_index": "posts",
            "_type": "article",
            "_id": "4",
            "_score": 1,
            "_source": {
                "id": 4,
                "name": "javascript指南",
                "author": "wthfeng",
                "date": "2016-09-21",
                "contents": "js的权威指南"
            }
        }, {
            "_index": "posts",
            "_type": "article",
            "_id": "2",
            "_score": 1,
            "_source": {
                "id": 2,
                "name": "更新后的文档",
                "author": "wthfeng",
                "date": "2016-10-23",
                "contents": "这是我的javascript学习笔记",
                "brief": "简介，这是新加的字段"
            }
        }, {
            "_index": "posts",
            "_type": "article",
            "_id": "1",
            "_score": 1,
            "_source": {
                "id": 1,
                "name": "ES更新过的文档",
                "author": "wthfeng",
                "date": "2016-10-25",
                "contents": "这是更新内容"
            }
        }]
    }
}1234567891011121314151617181920212223242526272829303132333435363738394041424344454647484950515253545556
```

### 1. 返回指定字段（fields）

查看我们的返回结果，每一个都带有`_source`字段，里面包含原始的数据。我们都是从这个字段查询出所需的结果。返回`_source` 字段是默认的行为，**如果我们只想返回某些字段，那这些字段在创建索引时必须开启store字段。**

上一篇定义的article映射部分：

```json
{
    "properties": {
        "id": {
            "type": "long",
            "store": "yes"  //开启储存
        },
        //......省略其他字段
        "contents": {
            "type": "string",
            "store": "no"  //未开启储存，默认
        }
    }
}12345678910111213
```

现在要求返回文章名称带`文档` 两词的所有文章，且只返回`id`和`name`字段。

> curl localhost:9200/posts/article/_search?pretty -d @search.json

```json
{
    "fields": ["id", "name"],
    "query": {
        "match": {
            "name": "文档"
        }
    }
}
```

返回数据部分

```json
  {
    "hits": {
        "total": 2,
        "max_score": 0.53033006,
        "hits": [{
            "_index": "posts",
            "_type": "article",
            "_id": "2",
            "_score": 0.53033006,
            "fields": {
                "name": ["更新后的文档"],
                "id": [2]
            }
        }, {
            "_index": "posts",
            "_type": "article",
            "_id": "1",
            "_score": 0.16273327,
            "fields": {
                "name": ["ES更新过的文档"],
                "id": [1]
            }
        }]
    }
  }12345678910111213141516171819202122232425
```

可见结果只有指定的字段。且用`fields`字段代替了`_source`字段。类似SQL中：

```sql
select id,name from article where name like '%文档%'1
```

> - 如果查询中没有`fields`字段，那默认返回_source字段
> - 虽然返回更少字段，但返回_source字段比返回多个储存字段性能要好，所以无特殊要求，返回`_source`较好。

### 2. 多词条查询（terms）

多词条查询类似于SQL中的`in` 操作符。可以匹配在查询内容中含有的多个词条。注意，`terms`匹配的是未经分析的词条，也就是必须完全匹配。

查询16年10月23号和25号发表的文章

> curl localhost:9200/posts/article/_search -d @search.json

```json
{
    "query":{
        "terms":{
            "date":["2016-10-23","2016-10-25"]
        }
    }
}
```

**注意：terms查询的词是未经分析的。它相当于term的复数版本。** 
类似于SQL：

```sql
select * from article where date in('2016-10-23','2016-10-25') 1
```

### 3. 范围查询（range）

范围查询(range)即返回指定范围的文档，多用于数值型和日期型中。

查询日期在10月23日及之前发表的，且文章名含有“指南”两字文章。

> curl localhost:9200/posts/article/_search?pretty -d @search.json

```json
{
    "query":{
        "range":{
            "date":{
                "lte":"2016-10-23"
            }
        }
    }
}123456789
```

结果正确返回，这里就不贴了。范围查询支持以下参数：

> - gte ：大于或等于
> - gt ：大于
> - lte ：小于或等于
> - lt ：小于

此例在SQL中很容易表示：

```sql
select * from article where date <= '2016-10-23'1
```

### 4. 排序（sort）

默认的ES按文档的相关度排序，即与指定查询吻合度最高的排在前面。如果我们想修改默认排序，需使用`sort` 字段与`query` 字段并列。

查询文档内容含有”的”字所有文档并按日期、id排序

> curl localhost:9200/posts/article/_search?pretty -d @search.json

```json
{
    "query":{
        "match":{
            "contents":"的"
        }
    },
    "sort":[
        {"date":"desc"},
        {"id":"asc"}
    ]
}1234567891011
```

sort接收一个数组，可以指定多个排序字段，用`asc`或`desc` 指定正序倒序

SQL 语句为

```sql
select * from article where contents like '%的%' order by date desc,id asc1
```

#### 缺省字段的排序

默认情况下，如果某文档没有指定的排序字段或该值为null，那么，如果升序排，该文档排在最前面，倒序排排在最后面。也就是缺省值的文档序列号是最小的。 
我们可以用`missing` 修改这种默认行为。`_first` 指定排在最前列，`_last` 指定排在最后列。还可指定具体数值，则缺省值就当做此值。

将缺省值都放在前列

```json
{
    "query":{
        "match":{
            "contents":"的"
        }
    },
    "sort":[
        {"date":{
            "order":"desc",
            "missing":"_first"

        }},
        {"id":{
            "order":"asc",
            "missing":"_first"
        }}
    ]
}123456789101112131415161718
```

### 布尔查询（bool）

是时候将这些查询聚在一起了，布尔查询负责此工作。类似于SQL中`and` 和`or` 等的作用 
在布尔查询中

> - should ： 类似于`or`,可以匹配条件也可以不匹配
> - must : 类似于`and`,必须匹配
> - must_not : 类似于`!=`，必须不匹配

以上关键字需包含在`bool` 语句中。

查询16年10月以来名称含有“ES”的文档，且id不为1

```json
{
    "query": {
        "bool": {
            "should": {

            },
            "must": [{
                "match": {
                    "name": {
                        "query": "ES"
                    }
                }
            }, {
                "range": {
                    "date": {
                        "gte": "2016-10-01"
                    }
                }
            }],
            "must_not": {
                "term": {
                    "id": "1"
                }
            }
        }
    }
}123456789101112131415161718192021222324252627
```

需要注意的是查询的格式要求，should、must、must_not可以带一个或多个查询条件。注意无论怎样的查询条件都首先满足json的格式要求。

上面写成SQL类似于

```sql
select * from article where name like '%ES%' and date > '2016-10-01' and id !=1 1
```

———————end ———————–





 版权声明：可以转载，需注明出处	https://blog.csdn.net/wthfeng/article/details/52953317