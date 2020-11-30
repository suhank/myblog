# Elasticsearch引擎的分页技术Search After

Author：飘易 Source：[飘易](http://www.piaoyi.org/)
Categories：[数据库](http://www.piaoyi.org/database/) PostTime：2018-6-5 10:16:29

正 文：

在使用Elasticsearch的过程中，一般的分页需求我们可以使用form和size的方式实现，但是这种分页方式在深度分页的场景下应该是要避免使用的。深度分页会随着请求的页次增加，所消耗的内存和时间的增长也是成比例的增加，为了避免深度分页产生的问题，elasticsearch从2.0版本开始，增加了一个限制：

`index.max_result_window =`10000

限制了最多返回前1万条结果，以避免小白用户陷入深度分页的魔障里。



对于深度分页，es推荐使用 scroll 接口，详情请查看《[Elasticsearch普通分页from&size VS scroll滚动分页](http://www.piaoyi.org/database/Elasticsearch-from-size-scroll.html)》。注意，scroll接口不适合用在实时搜索的场景里。

从es 5.0版本开始，es提供了新的参数 search_after 来解决这个问题，search_after 提供了一个活的游标来拉取从上次返回的最后一个请求开始拉取下一页的数据。

假设我们拉取的第一页请求如下:

```http
GET twitter/tweet/_search
{
    "size": 10,
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    },
    "sort": [
        {"date": "asc"},
        {"_uid": "desc"}
    ]
}
```

【注意】：sort参数里必须至少使用一个唯一的字段来进行排序，推荐的做法是使用 _uid 字段。

上面的请求会为每一个文档返回一个包含sort排序值的数组。这些sort排序值可以被用于 search_after 参数里以便抓取下一页的数据。比如，我们可以使用最后的一个文档的sort排序值，将它传递给 search_after 参数：

```js
GET twitter/tweet/_search
{
    "size": 10,
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    },
    "search_after": [1463538857, "tweet#654323"],
    "sort": [
        {"date": "asc"},
        {"_uid": "desc"}
    ]
}
```

【注意】：当我们使用 search_after 参数的时候，from参数必须被设置成 0 或 -1 （当然你也可以不设置这个from参数）。

search_after 不是为了解决能跳转到随机的任何一个分页而设计的，而是为了并行的拉取大量数据。它和 scroll 接口的方式类似，但search_after 是无状态的，而且能用于用户的实时搜索。





【参考来源】：

1、Elasticsearch search_after：<https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-request-search-after.html>





<http://www.piaoyi.org/database/Elasticsearch-Search_After.html>