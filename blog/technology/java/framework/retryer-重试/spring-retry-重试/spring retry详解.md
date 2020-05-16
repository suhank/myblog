[TOC]



# spring retry详解

2017年08月16日 17:07:44 [clj198606061111](https://me.csdn.net/clj198606061111) 阅读数 3633

 

当由于网络波动或者资源被锁等情况需要再次尝试的时候，可以使用spring-retry项目来实现，该项目已经应用到 Spring Batch, Spring Integration等项目。

spring-boot项目使用spring-retry非常简单，在配置类加上@EnableRetry注解启用spring-retry，然后在需要失败重试的方法加@Retryable注解即可，spring-retry通过捕获异常来触发重试机制。

### @Retryable

**注意：** 不能用于接口的实现类

```java
  @Retryable(value = RemoteAccessException.class, maxAttempts = 30,
      backoff= @Backoff(value = 10, maxDelay = 100000, multiplier = 3))
      public String getUser(Integer id){
          if (id == 444) {
            throw new RemoteAccessException(String.valueOf(id));
          }
      } 
```

**参数说明**

#### value

需要进行重试的异常，和参数`includes`是一个意思。默认为空，当参数`exclude`也为空时，所有异常都将要求重试。

#### include

需要进行重试的异常，默认为空。当参数`exclude`也为空时，所有异常都将要求重试。

#### exclude

不需要重试的异常。默认为空，当参`include`也为空时，所有异常都将要求重试。

#### stateful

标明重试是否是有状态的，异常引发事物失效的时候需要注意这个。该参数默认为false。远程方法调用的时候不需要设置，因为远程方法调用是没有事物的；只有当数据库更新操作的时候需要设置该值为true，特别是使用Hibernate的时候。抛出异常时，异常会往外抛，使事物回滚；重试的时候会启用一个新的有效的事物。



##### Stateless Retry 无状态重试

In the simplest case, a retry is just a while loop: the RetryTemplate can just keep trying until it either succeeds or fails. The RetryContext contains some state to determine whether to retry or abort, but this state is on the stack and there is no need to store it anywhere globally, so we call this stateless retry. The distinction between stateless and stateful retry is contained in the implementation of the RetryPolicy (the RetryTemplate can handle both). In a stateless retry, the callback is always executed in the same thread on retry as when it failed.

在最简单的示例中，重试仅仅是一个 while 循环：`RetryTemplate` 仅仅能保持尝试直到成功或者失败。 `RetryContext` 包含了一些状态用来决定是否重试还是中止，但是该状态是存储在内存栈中的，而不是作为一个全局属性存在，因此我们认为是无状态重试。无状态和有状态重试的区别是在于 `RetryPolicy` 的实现上（`RetryTemplate` 两种状态的处理都支持）。在无状态重试中，失败重试回调总是执行在同一个线程中。

##### Stateful Retry 有状态重试

Where the failure has caused a transactional resource to become invalid, there are some special considerations. This does not apply to a simple remote call because there is no transactional resource (usually), but it does sometimes apply to a database update, especially when using Hibernate. In this case it only makes sense to rethrow the exception that called the failure immediately so that the transaction can roll back and we can start a new valid one.

当故障导致事务资源变为无效时，需要做一些特殊的考虑。有状态重试并不适用于简单的远程调用，因为远程调用没有事务性资源（通常情况下），但当其被应用到数据库更新，尤其是使用Hibernate时，就显得重要了。在这种场景下，当调用失败时立即重新抛出异常以便事务可以回滚。

In these cases a stateless retry is not good enough because the re-throw and roll back necessarily involve leaving the RetryOperations.execute() method and potentially losing the context that was on the stack. To avoid losing it we have to introduce a storage strategy to lift it off the stack and put it (at a minimum) in heap storage. For this purpose Spring Batch provides a storage strategy RetryContextCache which can be injected into the RetryTemplate. The default implementation of the RetryContextCache is in memory, using a simple Map. Advanced usage with multiple processes in a clustered environment might also consider implementing the RetryContextCache with a cluster cache of some sort (though, even in a clustered environment this might be overkill).

这种请况下，一个无状态重试是不够的，因为重新抛出异常并回滚必然会离开 `RetryOperations.execute()` 方法，导致内存栈中的上下文信息丢失。为了避免丢失上下文，我们不得不使用一种存储策略将内存栈（局部的）中的信息存放到至少是内存堆（全局）一级的存储中。为此，Spring Batch 提供一种存储策略——可以注入到`RetryTemplate` 的 `RetryContextCache`。`RetryContextCache` 默认实现方式是通过使用一个简单的 `Map` 对象将其存储在内存中。集群环境中的多进程高级用法可以考虑通过集群的高速缓存来实现 `RetryContextCache`（不过在集群环境中，这种做法有点小题大做了）。

Part of the responsibility of the RetryOperations is to recognize the failed operations when they come back in a new execution (and usually wrapped in a new transaction). To facilitate this, Spring Batch provides the RetryState abstraction. This works in conjunction with a special execute methods in the RetryOperations.

`RetryOperations` 的职责之一就是在进行一个新的重试时记住失败的操作（通常被包装在一个新的事物中）。为此，Spring Batch 抽象出了 `RetryState` 接口。`RetryState` 被用在 `RetryOperations` 作为一个特殊的执行方法。

The way the failed operations are recognized is by identifying the state across multiple invocations of the retry. To identify the state, the user can provide an RetryState object that is responsible for returning a unique key identifying the item. The identifier is used as a key in the RetryContextCache.

这种方式下，失败操作被标识为状态在每次重试操作时返回。对于标识的状态，可以通过提供一个能返回唯一标识的`RetryState` 对象来定义。该标识在 `RetryContextCache` 中被当作唯一键处理。

**Warning**

Be very careful with the implementation of Object.equals() and Object.hashCode() in the key returned by RetryState. The best advice is to use a business key to identify the items. In the case of a JMS message the message ID can be used.

**注意：**通过 `RetryState` 返回 key 时，要小心 key 的 `equals()` 和 `hashCode()` 方法。最好的方式是使用一个业务键去标识。比如在使用JMS消息时，可以使用消息的ID作为key。

When the retry is exhausted there is also the option to handle the failed item in a different way, instead of calling the RetryCallback (which is presumed now to be likely to fail). Just like in the stateless case, this option is provided by the RecoveryCallback, which can be provided by passing it in to the execute method of RetryOperations.

当所有重试完成，仍然可以选择使用不同方式去替代 `RetryCallback`（RetryCallback 操作现在被假定为可能失败）去处理失败的操作。就像无状态重试下可以通过`RecoveryCallback` 接口传递对应的处理操作到 `RetryOperations` 的 execute 方法中。

The decision to retry or not is actually delegated to a regular RetryPolicy, so the usual concerns about limits and timeouts can be injected there (see below).

是否决定重试实际上是委托给 `RetryPolicy` 的，所以通常对于重试限制和超时重试可以放在`RetryPolicy` 中实现（见下文）。

http://iyiguo.net/blog/2016/01/17/spring-retry-simple-introduce/



#### maxAttempts

最大重试次数，默认为3。包括第一次失败。

#### backoff

回避策略，默认为空。该参数为空时是，失败立即重试，重试的时候阻塞线程。

详细参数参看@Backoff说明。

#### exceptionExpression

`SimpleRetryPolicy.canRetry()`返回true时该表达式才会生效，触发重试机制。如果抛出多个异常，只会检查最后那个。 
表达式举例：

```
"message.contains('you can retry this')"
```

并且

```
"@someBean.shouldRetry(#root)"
```

### @Backoff

**参数说明**

#### value

重试延迟时间，单位毫秒，默认值1000，即默认延迟1秒。

当未设置`multiplier`时，表示每隔`value`的时间重试，直到重试次数到达`maxAttempts`设置的最大允许重试次数。当设置了`multiplier`参数时，该值作为幂运算的初始值。

等同`delay`参数，两个参数设置一个即可。

#### delay

参看`value`说明。

#### maxDelay

两次重试间最大间隔时间。当设置`multiplier`参数后，下次延迟时间根据是上次延迟时间乘以`multiplier`得出的，这会导致两次重试间的延迟时间越来越长，该参数限制两次重试的最大间隔时间，当间隔时间大于该值时，计算出的间隔时间将会被忽略，使用上次的重试间隔时间。

#### multiplier

作为乘数用于计算下次延迟时间。公式：

```
delay = delay * multiplier1
```

#### random

是否启用随机退避策略，默认false。设置为true时启用退避策略，重试延迟时间将是`delay`和`maxDelay`间的一个随机数。设置该参数的目的是重试的时候避免同时发起重试请求，造成Ddos攻击。

### @Recover

该注解用于恢复处理方法，当全部尝试都失败时执行。返回参数必须和`@Retryable`修饰的方法返回参数完全一样。第一个参数必须是异常，其他参数和`@Retryable`修饰的方法参数顺序一致。

```
  @Recover
  public String recover(RemoteAccessException remoteaccessException, Integer id){
      // 全部重试失败处理
  } 
```

### 参考

**demo：** <https://github.com/clj198606061111/spring-boot-retry-demo>

**spring-retry项目：** <https://github.com/spring-projects/spring-retry>

**原文：** <http://www.itclj.com/blog/59940a4081c06e672f942ae1>





<https://blog.csdn.net/clj198606061111/article/details/77256033>