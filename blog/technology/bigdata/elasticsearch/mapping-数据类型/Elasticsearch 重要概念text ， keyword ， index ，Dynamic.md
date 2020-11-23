# Elasticsearch 重要概念text ， keyword ， index ，Dynamic

## 核心数据类型 text & keyword

### Text：

```
    1:支持分词，全文检索,支持模糊、精确查询,不支持聚合,排序操作;
    2:test类型的最大支持的字符长度无限制,适合大字段存储；
使用场景：
    存储全文搜索数据, 例如: 邮箱内容、地址、代码块、博客文章内容等。
    默认结合standard analyzer(标准解析器)对文本进行分词、倒排索引。
    默认结合标准分析器进行词命中、词频相关度打分。
```

### keyword：

```
1:不进行分词，直接索引,支持模糊、支持精确匹配，支持聚合、排序操作。
2:keyword类型的最大支持的长度为——32766个UTF-8类型的字符,可以通过设置ignore_above指定自持字符长度，超过给定长度后的数据将不被索引，无法通过term精确匹配检索返回结果。

使用场景：
存储邮箱号码、url、name、title，手机号码、主机名、状态码、邮政编码、标签、年龄、性别等数据。
用于筛选数据(例如: select * from x where status='open')、排序、聚合(统计)。
直接将完整的文本保存到倒排索引中。
```

## Mapping参数

### index

```
index定义字段的分析类型以及检索方式，控制字段值是否被索引.他可以设置成 true 或者 false。没有被索引的字段将无法搜索

如果是no，则无法通过检索查询到该字段；
如果设置为not_analyzed则会将整个字段存储为关键词，常用于汉字短语、邮箱等复杂的字符串；
如果设置为analyzed则将会通过默认的standard分析器进行分析
```

### Dynamic

```
dynamic属性：默认值为true，允许动态地向文档类型中加入新的字段。推荐设置为false，禁止向文档中添加字段，这样，文档类型的所有字段必须在索引映射的properties属性中显式定义，在properties字段中未定义的字段都将会ElasticSearch忽略。
dynamic设置为ture：默认值，新增加的字段被添加到索引映射中；
dynamic设置为false：新增加的字段会被忽略；
dynamic设置为strict：当向文档中新增字段时，ElasticSearch引擎抛出异常；
```

### 集群分片

```
Elasticsearch 有一个硬编码限制，单个分片内的文档总数不得超过 2147483519 个。
一般来说这个限制在日志场景下是不太会触发的，但是如果做 TSDB 用，则需要多加注意！
```





[Elasticsearch 重要概念text ， keyword ， index ，Dynamic-康建华-51CTO博客](https://blog.51cto.com/michaelkang/2361259)