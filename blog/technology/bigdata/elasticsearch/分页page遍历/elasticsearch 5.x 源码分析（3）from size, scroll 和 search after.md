# Elasticsearch 5.x 源码分析（3）from size, scroll 和 search after

 

关注

 0.2 2017.06.05 12:15* 字数 3377 阅读 4502评论 13喜欢 20

前两天突然被业务部的同事问了一句：“我现在要做搜索结果全量导，该用哪个接口，性能要好的？”之前虽然是知道这三种方法都是可以做分页的深度查询，但是由于具体的代码实现细节没看过，因此心里一下子就没有了底气，只好回答说先看看。

------

## from size

from size是最家喻户晓的，也是最暴力的，需要查询from + size 的条数时，coordinate node就向该index的其余的shards 发送同样的请求，等汇总到`（shards * （from + size））`条数时在coordinate node再做一次排序，最终抽取出真正的 from 后的 size 条结果，所以from size 的源码也懒得过了，这里只是顺带提一下。实在没弄明白Elasticsearch的from size机制的必须先做功课，下面的文章带图，通俗易懂：

> [http://lxwei.github.io/posts/%E4%BD%BF%E7%94%A8scroll%E5%AE%9E%E7%8E%B0Elasticsearch%E6%95%B0%E6%8D%AE%E9%81%8D%E5%8E%86%E5%92%8C%E6%B7%B1%E5%BA%A6%E5%88%86%E9%A1%B5.html](https://link.jianshu.com/?t=http://lxwei.github.io/posts/%E4%BD%BF%E7%94%A8scroll%E5%AE%9E%E7%8E%B0Elasticsearch%E6%95%B0%E6%8D%AE%E9%81%8D%E5%8E%86%E5%92%8C%E6%B7%B1%E5%BA%A6%E5%88%86%E9%A1%B5.html)

所以说当索引非常大时（千万级或亿级）时是无法用这个方法做深度分页的（启用routing机制可以减少这种中间态的条数，降低 OOO的风险，看是始终不是长远之计，而且性能风险摆在那里。

------

## search after

这是Elasticsearch 5 新引入的一种分页查询机制，其实原理几乎就是和scroll一样，因此代码也是几乎一样的， 简单三句话介绍search after怎么用就是：

- 它必须先要指定排序（因为一定要按排序记住坐标）
- 必须从第一页开始搜起（你可以随便指定一个坐标让它返回结果，只是你不知道会在全量结果的何处）
- 从第一页开始以后每次都带上`search_after=lastEmittedDocFieldValue` 从而为无状态实现一个状态，说白了就是把每次固定的from size偏移变成一个确定值`lastEmittedDocFieldValue`，而查询则从这个偏移量开始获取size个doc（每个shard 获取size个，coordinate node最后汇总
  shards*size 个。

最后一点非常重要，也就是说，无论去到多少页，coordinate node向其它node发送的请求始终就是请求size个docs，是个常量，而不再是from size那样，越往后，你要请求的docs就越多，而要丢弃的垃圾结果也就越多
也就是，**如果我要做非常多页的查询时，最起码search after是一个常量查询延迟和开销，并无什么副作用**。
有人就会问，为啥每次提供一个search_after值就可以找到确定的那一页的内容呢，Elasticsearch 不是分布式的么，每个shard只维护一部分的离散的文档，其实这个我之前也没搞懂，自从群上一小伙扔我一干货后就秒懂了，这里也推荐大家先做做功课，看看目前一些分库分表的数据查询的方式方法：
[业界难题-“跨库分页”的四种方案](https://link.jianshu.com/?t=https://mp.weixin.qq.com/s?srcid=05313jMsVT3zyHFn2DcX4PLU&scene=23&mid=2651959942&sn=e9d3fe111b8a1d44335f798bbb6b9eea&idx=1&__biz=MjM5ODYxMDA5OQ%3D%3D&chksm=bd2d075a8a5a8e4cad985b847778aa83056e22931767bb835132c04571b66d5434020fd4147f&mpshare=1#rd&appinstall=0)
如果你实在懒得看完，我就贴出search_after 的实现原理吧，如下：

> **三、业务折衷法**
> “全局视野法”虽然性能较差，但其业务无损，数据精准，不失为一种方案，有没有性能更优的方案呢？
> “**任何脱离业务的架构设计都是耍流氓**”，技术方案需要折衷，在技术难度较大的情况下，业务需求的折衷能够极大的简化技术方案。
> **业务折衷一：禁止跳页查询**
> 在数据量很大，翻页数很多的时候，很多产品并不提供“直接跳到指定页面”的功能，而只提供“下一页”的功能，这一个小小的业务折衷，就能极大的降低技术方案的复杂度。
>
> 
>
> ![img](https://upload-images.jianshu.io/upload_images/6181457-13b4ae68c2cd2080?imageMogr2/auto-orient/strip%7CimageView2/2/w/328/format/webp)
>
> 如上图，不够跳页，那么第一次只能够查第一页：
>
> （1）将查询order by time offset 0 limit 100，改写成order by time where time>0 limit 100
>
> （2）上述改写和offset 0 limit 100的效果相同，都是每个分库返回了一页数据（上图中粉色部分）；
>
> 
>
> ![img](https://upload-images.jianshu.io/upload_images/6181457-14c2454ffdc301dd?imageMogr2/auto-orient/strip%7CimageView2/2/w/321/format/webp)
>
> （3）服务层得到2页数据，内存排序，取出前100条数据，作为最终的第一页数据，这个全局的第一页数据，一般来说每个分库都包含一部分数据（如上图粉色部分）；
>
> 咦，这个方案也需要服务器内存排序，岂不是和“全局视野法”一样么？第一页数据的拉取确实一样，但每一次“下一页”拉取的方案就不一样了。
>
> 点击“下一页”时，需要拉取第二页数据，在第一页数据的基础之上，能够找到第一页数据time的最大值：
>
> 
>
> ![img](https://upload-images.jianshu.io/upload_images/6181457-c6e01e66a0174c92?imageMogr2/auto-orient/strip%7CimageView2/2/w/260/format/webp)
>
> 这个上一页记录的time_max，会作为第二页数据拉取的查询条件：
>
> （1）将查询order by time offset 100 limit 100，改写成order by time where time>$time_max limit 100
>
> 
>
> ![img](https://upload-images.jianshu.io/upload_images/6181457-1fc5b66c667ee1c2?imageMogr2/auto-orient/strip%7CimageView2/2/w/328/format/webp)
>
> （2）这下不是返回2页数据了（“全局视野法，会改写成offset 0 limit 200”），每个分库还是返回一页数据（如上图中粉色部分）；
>
> 
>
> ![img](https://upload-images.jianshu.io/upload_images/6181457-dec7709afe986eb8?imageMogr2/auto-orient/strip%7CimageView2/2/w/326/format/webp)
>
> （3）服务层得到2页数据，内存排序，取出前100条数据，作为最终的第2页数据，这个全局的第2页数据，一般来说也是每个分库都包含一部分数据（如上图粉色部分）；
>
> 如此往复，查询全局视野第100页数据时，不是将查询条件改写为offset 0 limit 9900+100（
>
> 返回100页数据
>
> ），而是改写为time>$time_max99 limit 100（
>
> 仍返回一页数据
>
> ），以保证数据的传输量和排序的数据量不会随着不断翻页而导致性能下降。

------

## Scroll

上面有说到search after的总结就是**如果我要做非常多页的查询时，最起码search after是一个常量查询延迟和开销，并无什么副作用**，可是，就像要查询结果全量导出那样，要在短时间内不断重复同一查询成百甚至上千次，效率就显得非常低了。scroll就是把一次的查询结果缓存一定的时间，如`scroll=1m`则把查询结果在下一次请求上来时暂存1分钟，response比传统的返回多了一个`scroll_id`，下次带上这个scroll_id即可找回这个缓存的结果。这里就scroll完成的逻辑去看看源代码。
scroll的查询可以简单分成下面几步：

- client端向coordinate node发起类似 `/{index}/_search?scroll=1m`的请求
- coordinate node会根据参数初始化一个`QueryThenFetch`请求或者`QueryAndFetch`请求，这个步骤和其它请求无异，这里有个概念就是coordinate node会在自己的节点查一遍数据（取决于它自身是否一个data节点）再往其他节点发送一遍请求，收到结果时再提炼出最终结果，再发起一个fetch 请求取最终数据
- client往后会向coordinate node发起类似 `_search/scroll` 请求，在这个请求里会带上上次查询返回的`scroll_id`参数，循环这个阶段知道无结果返回
- client端会发起一个DELETE 操作向服务器请求查询已结束，清楚掉相关缓存

Elasticsearch 中处理REST相关的客户端请求的类都放在`org.elasticsearch.action.rest`下



![img](https://upload-images.jianshu.io/upload_images/6181457-567f9d702e98fd79.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/298/format/webp)

scroll会涉及到的两个RestAction类

这两个类的逻辑基本都是一样的，只是构造出的`request`对象类型不同而已，最后RestSearchAction调用的是`return channel -> client.search(searchRequest, new RestStatusToXContentListener<>(channel));`而RESTSearchScrollAction调用的是`return channel -> client.searchScroll(searchScrollRequest, new RestStatusToXContentListener<>(channel));`

所有的Rest**Action是通过注册自己感兴趣的url来提供服务的

```
public RestSearchAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(GET, "/_search", this);
        controller.registerHandler(POST, "/_search", this);
        controller.registerHandler(GET, "/{index}/_search", this);
        controller.registerHandler(POST, "/{index}/_search", this);
        controller.registerHandler(GET, "/{index}/{type}/_search", this);
        controller.registerHandler(POST, "/{index}/{type}/_search", this);
    }
public RestSearchScrollAction(Settings settings, RestController controller) {
        super(settings);

        controller.registerHandler(GET, "/_search/scroll", this);
        controller.registerHandler(POST, "/_search/scroll", this);
        controller.registerHandler(GET, "/_search/scroll/{scroll_id}", this);
        controller.registerHandler(POST, "/_search/scroll/{scroll_id}", this);
    }
```

这一层主要处理用户的请求参数，构造出统一的`SearchRequest`类型，就会继续传递到下一层Action，下一层Action都是以`Transport***Action`，可以在`ActionModule`中找到相关的映射，可以把`Transport***Action`看作是`***Service`的的一层包装。
继续看`TransportSearchAction`,它主要做了两件事

1. 根据查询的index[ ]找到涉及的所有nodes
2. 构造出异步请求job发送到这些nodes完成查询
   在第2步里，需要确定searchRequest的type, 因为每种type它的fetch逻辑可能不一样,其中重要的代码是

```
            case QUERY_AND_FETCH:
            case QUERY_THEN_FETCH:
                searchAsyncAction = new SearchQueryThenFetchAsyncAction(logger, searchTransportService, connectionLookup,
                    aliasFilter, concreteIndexBoosts, searchPhaseController, executor, searchRequest, listener, shardIterators,
                    timeProvider, clusterStateVersion, task);
                break;
           
```

然后在`SearchQueryThenFetchAsyncAction`里就管理着具体的请求阶段了，这里有三个阶段：

1. query
2. fetch
3. merge

这些逻辑和一般请求无异，就不再仔细分析了，后面在叙述我遇到的一个问题时再回来看

而SearchService 处理scroll 的请求其实也很容易理解，如果是scroll属性，则把查询结果缓存，如果带着scrollContext上来，则从上次游标开始，再抓取size的结果集，我们看相关的部分代码

直接跳到QueryPhase 的相关处理代码：

```
           else {
                // Perhaps have a dedicated scroll phase?
                final ScrollContext scrollContext = searchContext.scrollContext();
                assert (scrollContext != null) == (searchContext.request().scroll() != null);
                final Collector topDocsCollector;
                ScoreDoc after = null;
                if (searchContext.request().scroll() != null) {
                    numDocs = Math.min(searchContext.size(), totalNumDocs);
                    after = scrollContext.lastEmittedDoc;

                    if (returnsDocsInOrder(query, searchContext.sort())) {
                        if (scrollContext.totalHits == -1) {
                            // first round
                            assert scrollContext.lastEmittedDoc == null;
                            // there is not much that we can optimize here since we want to collect all
                            // documents in order to get the total number of hits
                        } else {
                            // now this gets interesting: since we sort in index-order, we can directly
                            // skip to the desired doc and stop collecting after ${size} matches
                            if (scrollContext.lastEmittedDoc != null) {
                                BooleanQuery bq = new BooleanQuery.Builder()
                                    .add(query, BooleanClause.Occur.MUST)
                                    .add(new MinDocQuery(after.doc + 1), BooleanClause.Occur.FILTER)
                                    .build();
                                query = bq;
                            }
                            searchContext.terminateAfter(numDocs);
                        }
                    }
                } else {
                    after = searchContext.searchAfter();
                }
```

这里就看到了，如果启用的是scroll的话那么 from参数是会ignore的 ，也就是每次只会请求size的数量文档，而在这里也看到，它是通过`lastEmittedDoc` 这个游标来保持状态的，如果该参数不为空的话，就会在`SearchRequest`的`query`外面再包一层`MUST`的`BooleanQuery`来指定边界。
这里也看到了search after的身影了，所以我说search after和scroll的代码几乎都是一样的，如果指定after的话那么在构造Lucene调用时也把这个边界的fieldDoc传进去。
当然这个方法有300多行，只是剩下的就是一些Lucene的API了，我也还没有去研究Lucene的源码，最后得到的结果是

```
            topDocsCallable = () -> {
                    final TopDocs topDocs;
                    if (topDocsCollector instanceof TopDocsCollector) {
                        topDocs = ((TopDocsCollector<?>) topDocsCollector).topDocs();
                    } else if (topDocsCollector instanceof CollapsingTopDocsCollector) {
                        topDocs = ((CollapsingTopDocsCollector) topDocsCollector).getTopDocs();
                    } else {
                        throw new IllegalStateException("Unknown top docs collector " + topDocsCollector.getClass().getName());
                    }
                    if (scrollContext != null) {
                        if (scrollContext.totalHits == -1) {
                            // first round
                            scrollContext.totalHits = topDocs.totalHits;
                            scrollContext.maxScore = topDocs.getMaxScore();
                        } else {
                            // subsequent round: the total number of hits and
                            // the maximum score were computed on the first round
                            topDocs.totalHits = scrollContext.totalHits;
                            topDocs.setMaxScore(scrollContext.maxScore);
                        }
                        if (searchContext.request().numberOfShards() == 1) {
                            // if we fetch the document in the same roundtrip, we already know the last emitted doc
                            if (topDocs.scoreDocs.length > 0) {
                                // set the last emitted doc
                                scrollContext.lastEmittedDoc = topDocs.scoreDocs[topDocs.scoreDocs.length - 1];
                            }
                        }
                    }
                    return topDocs;
                };
```

这个也只是中间状态，结果还要在coordinate node再做一次汇总。

------

其实这里基本上就把scroll的代码看完了，不过在最后我还有两个疑问，第一： 关于search after，我每次传的只是sort中一个field 或几个field的一个具体值，后台根据这个值找到的一个doc来作为“游标”，但是万一我有大量的相同的值的话它如何找到对应的doc？例如我一个索引一半是“男”一半是“女”那我猜测如果用性别来排序那么search after = “男” 应该是工作异常的，不过我还没试，有答案的朋友告知一声。

第二就是一个插曲，也是我为啥看了2天scroll源码的原因，在最后这段代码里，我们看到每个shard 它是通 通过`lastEmittedDoc`来确定游标位置的，而我们也已经知道，所有结果还需要再在coordinate node上做汇总，也就是说，这次这个shard的偏移量并不是最终的偏移量，这个shard的结果集有可能最后会全用上，又或者全用不上，因此这个lastEmittedDoc 肯定是动态set的。
一开始我自然而然就觉得应该是这个`scroll_id`来每次指定每个shard应该从哪里开始查，因为群里的兄弟也告诉我，ES2 中这个scroll_id是每次返回都是不一样的，一定要每次传这个最后的id过去才可以继续，但ES5里面就杯具了，这个值是固定的...

```
static String buildScrollId(AtomicArray<? extends SearchPhaseResult> searchPhaseResults) throws IOException {
        try (RAMOutputStream out = new RAMOutputStream()) {
            out.writeString(searchPhaseResults.length() == 1 ? ParsedScrollId.QUERY_AND_FETCH_TYPE : ParsedScrollId.QUERY_THEN_FETCH_TYPE);
            out.writeVInt(searchPhaseResults.asList().size());
            for (SearchPhaseResult searchPhaseResult : searchPhaseResults.asList()) {
                out.writeLong(searchPhaseResult.getRequestId());
                out.writeString(searchPhaseResult.getSearchShardTarget().getNodeId());
            }
            byte[] bytes = new byte[(int) out.getFilePointer()];
            out.writeTo(bytes, 0);
            return Base64.getUrlEncoder().encodeToString(bytes);
        }
    }
```

在scroll 查询里，RequestID也是不变的，那么`scroll_id`确实就是不变的，然后我又跑去问了一兄弟，他又告诉我，ES2 中scroll是无排序的，size是指每个shard的size，而不是总size，所以shard获取的结果是一定会返回的，可是这逻辑ES5 又不一样了，size就是总结果集的size而不是shard的，而且scroll是支持sort的...
继续郁闷了好久，我始终坚信ES5 中这个`lastEmittedDoc`值肯定是每次查询动态调的，最终终于发现了端倪...

还记得scroll章一开始说的，`SearchQueryThenFetchAsyncAction` 会做 query，fetch，和merge三件事情，我们只看了query，然后我们继续看 fetch阶段：

```
private void innerRun() throws IOException {
        final int numShards = context.getNumShards();
        final boolean isScrollSearch = context.getRequest().scroll() != null;
        List<SearchPhaseResult> phaseResults = queryResults.asList();
        String scrollId = isScrollSearch ? TransportSearchHelper.buildScrollId(queryResults) : null;
        final SearchPhaseController.ReducedQueryPhase reducedQueryPhase = resultConsumer.reduce();
        final boolean queryAndFetchOptimization = queryResults.length() == 1;
        final Runnable finishPhase = ()
            -> moveToNextPhase(searchPhaseController, scrollId, reducedQueryPhase, queryAndFetchOptimization ?
            queryResults : fetchResults);
        if (queryAndFetchOptimization) {
            assert phaseResults.isEmpty() || phaseResults.get(0).fetchResult() != null;
            // query AND fetch optimization
            finishPhase.run();
        } else {
            final IntArrayList[] docIdsToLoad = searchPhaseController.fillDocIdsToLoad(numShards, reducedQueryPhase.scoreDocs);
            if (reducedQueryPhase.scoreDocs.length == 0) { // no docs to fetch -- sidestep everything and return
                phaseResults.stream()
                    .map(SearchPhaseResult::queryResult)
                    .forEach(this::releaseIrrelevantSearchContext); // we have to release contexts here to free up resources
                finishPhase.run();
            } else {
                final ScoreDoc[] lastEmittedDocPerShard = isScrollSearch ?
                    searchPhaseController.getLastEmittedDocPerShard(reducedQueryPhase, numShards)
                    : null;
                final CountedCollector<FetchSearchResult> counter = new CountedCollector<>(r -> fetchResults.set(r.getShardIndex(), r),
                    docIdsToLoad.length, // we count down every shard in the result no matter if we got any results or not
                    finishPhase, context);
                for (int i = 0; i < docIdsToLoad.length; i++) {
                    IntArrayList entry = docIdsToLoad[i];
                    SearchPhaseResult queryResult = queryResults.get(i);
                    if (entry == null) { // no results for this shard ID
                        if (queryResult != null) {
                            // if we got some hits from this shard we have to release the context there
                            // we do this as we go since it will free up resources and passing on the request on the
                            // transport layer is cheap.
                            releaseIrrelevantSearchContext(queryResult.queryResult());
                        }
                        // in any case we count down this result since we don't talk to this shard anymore
                        counter.countDown();
                    } else {
                        SearchShardTarget searchShardTarget = queryResult.getSearchShardTarget();
                        Transport.Connection connection = context.getConnection(searchShardTarget.getClusterAlias(),
                            searchShardTarget.getNodeId());
                        ShardFetchSearchRequest fetchSearchRequest = createFetchRequest(queryResult.queryResult().getRequestId(), i, entry,
                            lastEmittedDocPerShard, searchShardTarget.getOriginalIndices());
                        executeFetch(i, searchShardTarget, counter, fetchSearchRequest, queryResult.queryResult(),
                            connection);
                    }
                }
            }
        }
    }
```

`final ScoreDoc[] lastEmittedDocPerShard = isScrollSearch ? searchPhaseController.getLastEmittedDocPerShard(reducedQueryPhase, numShards) : null;`终于在fetch阶段发现了这个`lastEmittedDoc`数组，原来在coordinate node上进入了fetch阶段时会一并发送这个值给每个shard的，把这个变量一并写入构造的fetchRequest里了，而searchService里在做fetch阶段时做了一把记录:

```
public FetchSearchResult executeFetchPhase(ShardFetchRequest request, SearchTask task) {
        final SearchContext context = findContext(request.id());
        final SearchOperationListener operationListener = context.indexShard().getSearchOperationListener();
        context.incRef();
        try {
            context.setTask(task);
            contextProcessing(context);
            if (request.lastEmittedDoc() != null) {
                context.scrollContext().lastEmittedDoc = request.lastEmittedDoc();
            }
            context.docIdsToLoad(request.docIds(), 0, request.docIdsSize());
```

谜底终于解开，安心睡觉去了...

全篇完





<https://www.jianshu.com/p/91d03b16af77>