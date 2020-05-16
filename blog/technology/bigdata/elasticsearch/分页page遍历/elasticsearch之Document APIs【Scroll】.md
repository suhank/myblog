[TOC]



# elasticsearch之Document APIs【Scroll】

2017年08月14日 22:56:49 [山鬼谣me](https://me.csdn.net/u013066244) 阅读数：692



# scroll

一个搜索请求返回“一页”的结果，`scroll api` 可以用于从一次请求中提取大量的数据结果（甚至是全部结果）， 
类似于传统数据库的`cursor`游标。

`Scrolling`不适用于即时搜索请求，而是适用于处理大量数据， 
例如：把一个索引（即：数据库）中的内容重新索引到一个配置不同的新的索引（即：数据库）中去。

为了使用`scrolling`，原始搜索请求应该指定查询字符串`scroll`参数，其告诉`elasticsearch`应该保持`搜索 上下文`多久。例如：`?scroll=1m`。

```
POST /twitter/tweet/_search?scroll=1m
{
    "size": 100,
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    }
} 
```

上述请求的结果中包括一个`_scroll_id`，其应该通过`scroll api`来检索下一批次的结果。

```
POST①  /_search/scroll ②
{
    "scroll" : "1m", ③
    "scroll_id" : "DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAAD4WYm9laVYtZndUQlNsdDcwakFMNjU1QQ==" ④
} 
```

① GET 或 POST 都可以使用。 

② 这个URL不应该指定 index 或者 type —– 这些都是在原始请求中指定的 

③ scroll 参数告诉elasticsearch 保持 搜索上下文 打开一分钟。 

④ scroll_id 参数

`size`参数允许你配置返回每批次命中结果的最大数量。每调用`scroll api`返回下一批次的结果，直到没有剩余的结果返回，例如`hits`数组为空。

**重点** 
最初的搜索请求和每个后续的`scroll`请求都会返回一个新的`_scroll_id` —只有最新的`_scroll_id`才会被使用。

**注意** 
如果是聚合请求，那么只有最初的搜索响应结果将会包含聚合结果。

**注意** 
当排序字段是`_doc`时，`scroll`请求将会优化使得速度更快。 
如果你想迭代所有的文档不管顺序，这是最有效的选项：

```
GET /_search?scroll=1m
{
  "sort": [
    "_doc"
  ]
} 
```

# Keeping the search context alive

`scroll`参数（传递给`search`请求和每个`scroll`请求）告诉`elasticsearch`应该保持搜索上下文（`search context`）存活多久。 
其值（例如：1m，详情[`the section called “Time unitsedit”`](https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html#time-units)）表示不需要足够上长的时间来处理所有的数据 — 其只需要足够上的时间来处理上一批次的结果。每个`scroll`请求（即：带`scroll`参数的请求）设置一个新的到期时间。 
通常，后台合并处理优化索引是通过合并相近的小的段来创建一个新的更大的段，与此同时小的段会被删除。在`scrolling`期间，这个过程会继续，除了打开的搜索上下文，这是为了防止删除仍在使用的旧段。这就是`elasticsearch`能够返回最初请求的结果的原因，而不管后续文档是否发生改变。

**小提示**保留旧段存活意味着需要更多的文件句柄。所以要确保你在节点中配置了足够多的空闲的文件句柄（`file handles`）。详情查看[`File Descriptors`](https://www.elastic.co/guide/en/elasticsearch/reference/current/file-descriptors.html)。

你可以使用`nodes stas API`来查看有多少打开的搜索上下文（`search contexts`）:

```
GET /_nodes/stats/indices/search1
```

# Clear scroll API

当`scroll`的`timeout`超时时，`search context`会自动删除。 
然而保持`scrolls`打开着是有成本的，正如上一章节讨论的一样，只要`scroll`没有不再使用，就应该显示的使用`clear-scroll api`清除掉：

```
DELETE /_search/scroll
{
    "scroll_id" : "DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAAD4WYm9laVYtZndUQlNsdDcwakFMNjU1QQ=="
}1234
```

多个`scroll IDs`可以通过传递数组形式：

```
DELETE /_search/scroll
{
    "scroll_id" : [
      "DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAAD4WYm9laVYtZndUQlNsdDcwakFMNjU1QQ==",
      "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAABFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAAAxZrUllkUVlCa1NqNmRMaUhiQlZkMWFBAAAAAAAAAAIWa1JZZFFZQmtTajZkTGlIYkJWZDFhQQAAAAAAAAAFFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAABBZrUllkUVlCa1NqNmRMaUhiQlZkMWFB"
    ]
} 
```

清除所有的`search contexts`可以使用`_all`参数：

```
DELETE /_search/scroll/_all1
```

`scroll_id` 也可以传递给查询字符串，或者请求体。多个`scrolls IDs`可以通过逗号进行分割：

```
DELETE /_search/scroll/DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAAD4WYm9laVYtZndUQlNsdDcwakFMNjU1QQ==,DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAABFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAAAxZrUllkUVlCa1NqNmRMaUhiQlZkMWFBAAAAAAAAAAIWa1JZZFFZQmtTajZkTGlIYkJWZDFhQQAAAAAAAAAFFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAABBZrUllkUVlCa1NqNmRMaUhiQlZkMWFB
12
```

# Sliced Scroll

对于返回大量文档的`scroll`查询，其实可以在多个切片中分割这个`scroll`，其每个切片都是可以独立使用的。

```
GET /twitter/tweet/_search?scroll=1m
{
    "slice": {
        "id": 0, ①
        "max": 2 ②
    },
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    }
}
GET /twitter/tweet/_search?scroll=1m
{
    "slice": {
        "id": 1,
        "max": 2
    },
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    }
}123456789101112131415161718192021222324
```

①切片id 
②切片最大数量

从第一个请求返回的文档结果属于第一个切片（id：0），第二个请求返回的文档结果属于第二个切片（id:1）。因为切片的最大数量设置为2，所以这两次请求合并起来的结果等于没有使用切片的`scroll`查询结果。默认情况下，首先是在分片上进行切片处理，紧接着在每个分片上使用`_uid`字段根据公式：`slice(doc) = floorMod(hashCode(doc._uid), max)`进行本地化，例如，如果分片数量是2，用户请求4个切片，接着切片0和2会被分配到第一个分片上，切片1和3会被分配到第二个切片上。

每个`scroll`都是独立的，并且可以并行化处理任何`scroll`请求。

**注意** 
如果切片数量大于分片的数量，在第一次调用时，`slice`过滤器是非常缓慢的，其复杂度为`O(N)`，并且内存的成本等于每个切片的N个比特位，N是在分片上文档的总和。在多次调用之后，该过滤器会被缓存，后面的查询应该会快些，但是你应该限制在并行执行查询切片的数量，以避免内存爆掉。

为了避免这种成本，可以使用`doc_values`的其他字段来做切片，但是使用时，必须确保该字段具有以下特性：

1. 字段是数字
2. `doc_values`在该字段上启用了
3. 每个文档应该包含一个值。如果一个文档的指定字段包含多个值的话，第一个值应该被使用。
4. 每个文档的值应该设置一旦被创建并没有更新。这确保每个切片都能得到确切的结果。
5. 字段的基数应该高些。这确保每个切片得到大约相同的文档。

```
GET /twitter/tweet/_search?scroll=1m
{
    "slice": {
        "field": "date",
        "id": 0,
        "max": 10
    },
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    }
}12345678910111213
```

在索引中添加时间基准，`timestamp`字段可以安全使用。

**注意** 
默认情况下，每个`scroll`的最大切片数量限制在`1024`。你可以在索引中更新`index.max_slices_per_scroll`这个值来绕开这个限制。

参考地址：

<https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html>