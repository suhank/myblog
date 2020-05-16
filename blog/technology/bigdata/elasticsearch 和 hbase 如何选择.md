## elasticsearch 和 hbase 如何选择  



这两个不应该拿来做对比。看一下各自的介绍：

> **Elasticsearch** is a search server based on Lucene. （wiki：Elasticsearch）
>
> **HBase** is an open source, non-relational, distributed database modeled after Google'sBigTable and written in Java.（wiki：Apache HBase）

一个是搜索引擎，一个是数据库。
在分布式环境中，两者得到广泛使用，但发挥的作用是不一样的。具体要怎么使用怎么配合，要看实际业务了。但 es 并不局限于搜索引擎，做数据库也是个不错的选择，可以跟 spark 很好的结合用来做数据分析。

从基本功能来说这两个确实有相似性，但是根据业务需求不同，我觉得有几点可以考虑：

**1. 查询复杂度：**HBase支持简单的行或者range查询，比如给一个PK查该行的数据，或者给一个begin/end查这个范围的数据，如果想完成更复杂的功能就不太容易。而ES支持的查询比较丰富，或者说这些查询都带有一点复杂计算的味道了。比如你有个论坛，你想查帖子里面是否包含敏感词，如果采用HBase就比较麻烦，使用HBase你可以将帖子存进来、读出去，但是要查内容里面的东西，只能一点点过滤；而ES是可以比较方便的帮助你完成这个功能的；

**2. 数据量：**按道理说两者都是支持海量数据的，但是据我个人感觉，HBase可能更容易支持更多的数据，因为其一开始设计就是解决海量问题的；而ES是后来慢慢增强其存储扩展性的；那么也就是说，HBase上手起来扩展性不太会阻碍你使用；ES可能要多费点劲。

**3. 剩下的就是比较远的考虑，比如维护性**，HBase基于Hadoop那一套，组件多，维护起来代价也不低，而ES自成体系，维护起来稍微好点；当然这个是相对的，绝对来说都不会容易。比如新功能开发，比如成本控制等等。。。



我个人觉得，es注重的还是检索，hbase擅长的是读写操作，所以还需要看见你的应用场景

hbase存多读少，不适合高并发查询，适合存数据；
es是全文检索，适合日志分析日志统计之类。

如果主要做实时、动态的计数，则推荐ES。
如果主要跑些月报表什么的，则推荐Hbase。

参考链接：

https://www.zhihu.com/question/41109030



<https://my.oschina.net/jiangbianwanghai/blog/779190>