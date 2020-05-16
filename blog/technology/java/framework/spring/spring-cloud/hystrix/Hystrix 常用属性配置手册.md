# Hystrix 常用属性配置





| 配置参数                                                     | 默认值            | 说明                                                         |
| ------------------------------------------------------------ | ----------------- | ------------------------------------------------------------ |
| 命令-执行属性配置                                            |                   |                                                              |
| hystrix.command.default.execution.isolation.strategy         | THREAD            | 配置隔离策略，有效值 THREAD, SEMAPHORE                       |
| hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds | 1000              | 命令的执行超时时间，超出该时间会执行命令的 回退，可以使用 command.timeout.enabled 配置来关闭命令超时 |
| hystrix.command.default.execution.timeout.enabled            | true              | 配置命令的执行，是否会超时                                   |
| hystrix.command.default.execution.isolation.thread.interruptOnTimeout | true              | 配置命令的执行发生超时，是否中断命令的 run 方法的执行        |
| hystrix.command.default.execution.isolation.thread.interruptOnCancel | false             | 配置命令在取消时，是否中断命令的 run 方法的执行              |
| hystrix.command.default.execution<br />.isolation.semaphore.maxConcurrentRequests | 10                | 当使用 ExecutionIsolationStrategy.SEMAPHORE 隔离策略时，设置允许执行命令的 run 方法的最大请求数。如果此最大并发限制被命中，则后续请求将被拒绝。在调整信号量大小时使用的逻辑与选择要添加到线程池中的线程的数量基本相同,，但信号量的开销要小得多，通常执行速度要快得多 (子毫秒)，否则您将使用线程 |
| 命令-回退属性配置                                            |                   |                                                              |
| hystrix.command.default.fallback<br />.isolation.semaphore.maxConcurrentRequests | 10                | 设置 ExecutionIsolationStrategy.SEMAPHORE 隔离策略时，设置允许执行命令的 getFallback 方法的最大请求数量 |
| hystrix.command.default.fallback.enabled                     | true              | 此属性确定在命令发生调用失败或拒绝时是否会尝试调用命令的 getFallback 方法 |
| 命令-断路器属性配置                                          |                   |                                                              |
| hystrix.command.default.circuitBreaker.enabled               | true              | 此属性确定是否启用断路器跟踪运行状况和短路请求               |
| hystrix.command.default.circuitBreaker.requestVolumeThreshold | 20                | 此属性设置滚动窗口中将短路请求的最小请求数。                 |
| hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds |                   | 此属性设置在短路请求后拒绝请求的休眠时间，然后再允许尝试确定命令是否应再次关闭。 |
| hystrix.command.default.circuitBreaker.errorThresholdPercentage | 50                | 此属性设置断路器打开并启动对回退逻辑的短路请求的错误百分比。 |
| hystrix.command.default.circuitBreaker.forceOpen             | false             | 此属性如果为 true，则强制断路器进入打开状态                  |
| hystrix.command.default.circuitBreaker.forceClosed           | false             | 此属性如果为 true，则强制断路器进入关闭状态,                 |
| 命令-指标属性配置                                            |                   |                                                              |
| hystrix.command.default.metrics.rollingStats.timeInMilliseconds | 10000             | 此属性设置统计滚动窗口的持续时间                             |
| hystrix.command.default.metrics.rollingStats.numBuckets      | 10                | 属性设置滚动窗口划分的桶数，例如，滚动窗口持续时间为10秒，默认配置10个桶，那么每秒钟一个桶用于存放统计数据。配置值必须符合以下条件 metrics.rollingStats.timeInMilliseconds % metrics.rollingStats.numBuckets == 0，否则会抛出异常。此属性只影响初始度量值的创建，并且在启动后对此属性进行的调整将不会生效。 |
| hystrix.command.default.metrics.rollingPercentile.enabled    | true              | 此属性指示是否应跟踪执行延迟，并将其计算为百分点。如果禁用它们，则所有汇总统计 (平均值、百分点) 都将返回为 -1 |
| hystrix.command.default.metrics.rollingPercentile.timeInMilliseconds | 60000             | 此属性设置滚动百分比窗口的持续时间，其中保留执行时间以允许百分比计算 (以毫秒为单位)，此属性只影响初始度量值的创建, 并且在启动后对此属性进行的调整将不会生效 |
| hystrix.command.default.metrics.rollingPercentile.numBuckets | 6                 | 属性设置滚动百分比窗口划分的桶数，例如，滚动百分比窗口持续时间为60秒，默认配置6个桶，那么一个桶用于存放10秒的统计数据。配置值必须符合以下条件 metrics.rollingPercentile.timeInMilliseconds % metrics.rollingPercentile.numBuckets == 0，否则会抛出异常。此属性只影响初始度量值的创建，并且在启动后对此属性进行的调整将不会生效 |
| hystrix.command.default.metrics.rollingPercentile.bucketSize | 100               | 此属性设置每个桶保留的最大执行时间数，如果超出桶的最大执行数量，则会记录到下一个桶此属性只影响初始度量值的创建，并且在启动后对此属性进行的调整将不会生效 |
| hystrix.command.default.metrics.healthSnapshot.intervalInMilliseconds | 500               | 此属性设置在允许运行健康快照以计算成功和错误百分比并影响断路器状态的等待间隔的时间 (以毫秒为单位)。误差百分比的连续计算是 CPU 密集型的，因此此属性允许您控制计算的频率 |
| 命令-请求上下文属性配置                                      |                   |                                                              |
| hystrix.command.default.requestCache.enabled                 | true              | 此属性指示是否启用请求缓存                                   |
| hystrix.command.default.requestLog.enabled                   | true              | 此属性指示是否应将命令执行和事件记录到日志                   |
| 合并属性配置                                                 |                   |                                                              |
| hystrix.collapser.default.maxRequestsInBatch                 | Integer.MAX_VALUE | 此属性设置合并处理允许的最大请求数                           |
| hystrix.collapser.default.timerDelayInMilliseconds           | 10                | 此属性设置多长时间内的请求进行合并                           |
| hystrix.collapser.default.requestCache.enabled               | true              | 此属性设置启动合并请求缓存                                   |
| 线程池属性配置                                               |                   |                                                              |
| hystrix.threadpool.default.coreSize                          | 10                | 此属性配置线程池大小                                         |
| hystrix.threadpool.default.maximumSize                       | 10                | 此属性设置最大线程池大小。这是可在拒绝命令执行的最大并发量。请注意, 如果您必须同时设置allowMaximumSizeToDivergeFromCoreSize。在1.5.9版本 之前, 线程池和最大线程池总是相等的。 |
| hystrix.threadpool.default.allowMaximumSizeToDivergeFromCoreSize | false             | 此属性允许 maximumSize 的配置生效，如果maximumSize 大于 coreSize 配置，则在 keepAliveTimeMinutes 时间后回收线程 |
| hystrix.threadpool.default.keepAliveTimeMinutes              | 1                 | 此属性设置线程空闲生存时间 (分钟)                            |
| hystrix.threadpool.default.metrics.rollingStats.timeInMilliseconds | 10000             | 此属性设置统计滚动窗口的持续时间 (以毫秒为单位)              |
| hystrix.threadpool.default.metrics.rollingStats.numBuckets   | 10                | 属性设置滚动窗口划分的桶数，例如，滚动窗口持续时间为10秒，默认配置10个桶，那么每秒钟一个桶用于存放统计数据。配置值必须符合以下条件 metrics.rollingStats.timeInMilliseconds % metrics.rollingStats.numBuckets == 0，否则会抛出异常。此属性只影响初始度量值的创建，并且在启动后对此属性进行的调整将不会生效 |

   

注意：配置参数都是默认全局配置，如果需要针对命令配置，则将 default 替换为 HystrixCommandKey 、 HystrixCollapserKey 和 HystrixThreadPoolKey；在 HystrixCommand 子类的构造函数调用中，可以指定命令的 HystrixCommandKey 和 HystrixThreadPoolKey，示例如下：

 

```java
  public SpeakCommand(String msg){
    super(
            Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("BatchSpeak"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("commandkey"))
                    .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPoolKey"))
    );
  }
```

在 HystrixCollapser 子类的构造函数调用中，可以指定合并请求的 HystrixCollapserKey ，示例如下：

```java
  public SpeakCollapserCommand(String paramName){
    super(
            Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("SpeakCollapser"))
    );
    this.paramName=paramName;

  }
```

   

 

http://www.cnblogs.com/li3807/p/8780983.html









# Hystrix 参数详解

 

hystrix.command.default和hystrix.threadpool.default中的default为默认CommandKey

## Command Properties

### Execution相关的属性的配置：

- hystrix.command.default.execution.isolation.strategy 隔离策略，默认是Thread, 可选Thread｜Semaphore
  - thread 通过线程数量来限制并发请求数，可以提供额外的保护，但有一定的延迟。一般用于网络调用
  - semaphore 通过semaphore count来限制并发请求数，适用于无网络的高并发请求
- hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 命令执行超时时间，默认1000ms
- hystrix.command.default.execution.timeout.enabled 执行是否启用超时，默认启用true
- hystrix.command.default.execution.isolation.thread.interruptOnTimeout 发生超时是是否中断，默认true
- hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests 最大并发请求数，默认10，该参数当使用ExecutionIsolationStrategy.SEMAPHORE策略时才有效。如果达到最大并发请求数，请求会被拒绝。理论上选择semaphore size的原则和选择thread size一致，但选用semaphore时每次执行的单元要比较小且执行速度快（ms级别），否则的话应该用thread。
  semaphore应该占整个容器（tomcat）的线程池的一小部分。

### Fallback相关的属性

这些参数可以应用于Hystrix的THREAD和SEMAPHORE策略

- hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests 如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。默认10
- hystrix.command.default.fallback.enabled 当执行失败或者请求被拒绝，是否会尝试调用hystrixCommand.getFallback() 。默认true

### Circuit Breaker相关的属性

- hystrix.command.default.circuitBreaker.enabled 用来跟踪circuit的健康性，如果未达标则让request短路。默认true
- hystrix.command.default.circuitBreaker.requestVolumeThreshold 一个rolling window内最小的请求数。如果设为20，那么当一个rolling window的时间内（比如说1个rolling window是10秒）收到19个请求，即使19个请求都失败，也不会触发circuit break。默认20
- hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds 触发短路的时间值，当该值设为5000时，则当触发circuit break后的5000毫秒内都会拒绝request，也就是5000毫秒后才会关闭circuit。默认5000
- hystrix.command.default.circuitBreaker.errorThresholdPercentage错误比率阀值，如果错误率>=该值，circuit会被打开，并短路所有请求触发fallback。默认50
- hystrix.command.default.circuitBreaker.forceOpen 强制打开熔断器，如果打开这个开关，那么拒绝所有request，默认false
- hystrix.command.default.circuitBreaker.forceClosed 强制关闭熔断器 如果这个开关打开，circuit将一直关闭且忽略circuitBreaker.errorThresholdPercentage

### Metrics相关参数

- hystrix.command.default.metrics.rollingStats.timeInMilliseconds 设置统计的时间窗口值的，毫秒值，circuit break 的打开会根据1个rolling window的统计来计算。若rolling window被设为10000毫秒，则rolling window会被分成n个buckets，每个bucket包含success，failure，timeout，rejection的次数的统计信息。默认10000
- hystrix.command.default.metrics.rollingStats.numBuckets 设置一个rolling window被划分的数量，若numBuckets＝10，rolling window＝10000，那么一个bucket的时间即1秒。必须符合rolling window % numberBuckets == 0。默认10
- hystrix.command.default.metrics.rollingPercentile.enabled 执行时是否enable指标的计算和跟踪，默认true
- hystrix.command.default.metrics.rollingPercentile.timeInMilliseconds 设置rolling percentile window的时间，默认60000
- hystrix.command.default.metrics.rollingPercentile.numBuckets 设置rolling percentile window的numberBuckets。逻辑同上。默认6
- hystrix.command.default.metrics.rollingPercentile.bucketSize 如果bucket size＝100，window＝10s，若这10s里有500次执行，只有最后100次执行会被统计到bucket里去。增加该值会增加内存开销以及排序的开销。默认100
- hystrix.command.default.metrics.healthSnapshot.intervalInMilliseconds 记录health 快照（用来统计成功和错误绿）的间隔，默认500ms

### Request Context 相关参数

hystrix.command.default.requestCache.enabled 默认true，需要重载getCacheKey()，返回null时不缓存
hystrix.command.default.requestLog.enabled 记录日志到HystrixRequestLog，默认true

## Collapser Properties 相关参数

hystrix.collapser.default.maxRequestsInBatch 单次批处理的最大请求数，达到该数量触发批处理，默认Integer.MAX_VALUE
hystrix.collapser.default.timerDelayInMilliseconds 触发批处理的延迟，也可以为创建批处理的时间＋该值，默认10
hystrix.collapser.default.requestCache.enabled 是否对HystrixCollapser.execute() and HystrixCollapser.queue()的cache，默认true

## ThreadPool 相关参数

线程数默认值10适用于大部分情况（有时可以设置得更小），如果需要设置得更大，那有个基本得公式可以follow：
requests per second at peak when healthy × 99th percentile latency in seconds + some breathing room
每秒最大支撑的请求数 *(99%平均响应时间 + 缓存值)比如：每秒能处理1000个请求，99%的请求响应时间是60ms，那么公式是：1000* （0.060+0.012）

基本得原则时保持线程池尽可能小，他主要是为了释放压力，防止资源被阻塞。
当一切都是正常的时候，线程池一般仅会有1到2个线程激活来提供服务

- hystrix.threadpool.default.coreSize 并发执行的最大线程数，默认10
- hystrix.threadpool.default.maxQueueSize BlockingQueue的最大队列数，当设为－1，会使用SynchronousQueue，值为正时使用LinkedBlcokingQueue。该设置只会在初始化时有效，之后不能修改threadpool的queue size，除非reinitialising thread executor。默认－1。
- hystrix.threadpool.default.queueSizeRejectionThreshold 即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝。因为maxQueueSize不能被动态修改，这个参数将允许我们动态设置该值。if maxQueueSize == -1，该字段将不起作用
- hystrix.threadpool.default.keepAliveTimeMinutes 如果corePoolSize和maxPoolSize设成一样（默认实现）该设置无效。如果通过plugin（[https://github.com/Netflix/Hystrix/wiki/Plugins）使用自定义实现，该设置才有用，默认1](https://github.com/Netflix/Hystrix/wiki/Plugins%EF%BC%89%E4%BD%BF%E7%94%A8%E8%87%AA%E5%AE%9A%E4%B9%89%E5%AE%9E%E7%8E%B0%EF%BC%8C%E8%AF%A5%E8%AE%BE%E7%BD%AE%E6%89%8D%E6%9C%89%E7%94%A8%EF%BC%8C%E9%BB%98%E8%AE%A41).
- hystrix.threadpool.default.metrics.rollingStats.timeInMilliseconds 线程池统计指标的时间，默认10000
- hystrix.threadpool.default.metrics.rollingStats.numBuckets 将rolling window划分为n个buckets，默认10

http://tietang.wang/2016/02/25/hystrix/Hystrix%E5%8F%82%E6%95%B0%E8%AF%A6%E8%A7%A3/