# 解决ElasticSearch深度分页机制中Result window is too large问题

2018-01-12 10:39:32



## 问题描述

今天在使用ElacticSearch做分页查询的时候，遇到一个奇怪的问题，分页获取前9999条数据的时候都是正常的，但每次获取第10000条数据的时候就无法获取到结果。检查自己代码中的分页逻辑也未发现什么问题，于是进行单步调试，当单步获取第10000条数据的时候捕捉到了下面的异常：

> Failed to execute phase [query_fetch], all shards failed; shardFailures {[1w_m0BF0Sbir4I0hRWAmDA][fuxi_user_feature-2018.01.09][0]: RemoteTransportException[[10.1.113.169][10.1.113.169:9300][indices:data/read/search[phase/query+fetch]]]; nested: QueryPhaseExecutionException[Result window is too large, from + size must be less than or equal to: [10000] but was [10100]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level parameter.]; }

## 解决方案

最后通过查阅了解到出现这个问题是由于ElasticSearch的默认 **深度翻页** 机制的限制造成的。ES默认的分页机制一个不足的地方是，比如有5010条数据，当你仅想取第5000到5010条数据的时候，ES也会将前5000条数据加载到内存当中，所以ES为了避免用户的过大分页请求造成ES服务所在机器内存溢出，默认对深度分页的条数进行了限制，默认的最大条数是10000条，这是正是问题描述中当获取第10000条数据的时候报`Result window is too large`异常的原因。

要解决这个问题，可以使用下面的方式来改变ES默认深度分页的`index.max_result_window` 最大窗口值

```
curl -XPUT http://127.0.0.1:9200/my_index/_settings -d '{ "index" : { "max_result_window" : 500000}}'
```

其中my_index为要修改的index名，500000为要调整的新的窗口数。将该窗口调整后，便可以解决无法获取到10000条后数据的问题。

## 注意事项

通过上述的方式解决了我们的问题，但也引入了另一个需要我们注意的问题，窗口值调大了后，虽然请求到分页的数据条数更多了，但它是用牺牲更多的服务器的内存、CPU资源来换取的。要考虑业务场景中过大的分页请求，是否会造成集群服务的**OutOfMemory**问题。在ES的官方文档中对深度分页也做了讨论

> https://www.elastic.co/guide/en/elasticsearch/guide/current/pagination.html
>
> https://www.elastic.co/guide/en/elasticsearch/guide/current/pagination.html

核心的观点如下：

> Depending on the size of your documents, the number of shards, and the hardware you are using, paging 10,000 to 50,000 results (1,000 to 5,000 pages) deep should be perfectly doable. But with big-enough from values, the sorting process can become very heavy indeed, using vast amounts of CPU, memory, and bandwidth. For this reason, we strongly advise against deep paging.

这段观点表述的意思是：根据文档的大小，分片的数量以及使用的硬件，分页10,000到50,000个结果（1,000到5,000页）应该是完全可行的。 但是，从价值观上来看，使用大量的CPU，内存和带宽，分类过程确实会变得非常重要。 为此，**我们强烈建议不要进行深度分页**。

自己的看法是，ES作为一个搜索引擎，更适合的场景是使用它进行搜索，而不是大规模的结果遍历。 大部分场景下，没有必要得到超过10000个结果项目， 例如，只返回前1000个结果。如果的确需要大量数据的遍历展示，考虑是否可以用其他更合适的存储。或者根据业务场景看能否用ElasticSearch的 **滚动API** (类似于迭代器，但有时间窗口概念)来替代。





[(20条消息) 解决ElasticSearch深度分页机制中Result window is too large问题_拂晓的专栏-CSDN博客](https://blog.csdn.net/lisongjia123/article/details/79041402)