[TOC]



# rocketMQ最佳实践之Consumer

 

## 消费者组和订阅

你首先要知道的是，不同的消费群体可以独立地消费同样的主题，并且每个消费者都有自己的消费偏移量（offsets）。请确保同一组中的每个消费者订阅相同的主题。

## 消息监听器（MessageListener）

### 顺序（Orderly）

消费者将锁定每个MessageQueue，以确保每个消息被一个按顺序使用。这将导致性能损失，但是当您关心消息的顺序时，它就很有用了。不建议抛出异常，您可以返回`ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT`代替。

### 并行（Concurrently）

顾名思义，消费者将同时使用这些消息。为良好的性能，推荐使用此方法。不建议抛出异常，您可以返回`ConsumeConcurrentlyStatus.RECONSUME_LATER`代替。

### 消费状况（Consume Status）

对于 `MessageListenerConcurrently`，您可以返回 `RECONSUME_LATER` 告诉消费者，你当前不能消费它并且希望以后重新消费。然后您可以继续使用其他消息。对于`MessageListenerOrderly`，因为您关心顺序，您不能跳过消息，但是您可以返回 `SUSPEND_CURRENT_QUEUE_A_MOMENT` 来告诉消费者等待片刻。

### 阻塞（Blocking）

不建议阻塞 `Listener`，因为它会阻塞线程池，最终可能会停止消费程序。

## 线程数

消费者使用一个 `ThreadPoolExecutor` 来处理内部的消费，因此您可以通过设置`setConsumeThreadMin`或`setConsumeThreadMax`来更改它。

## 从何处开始消费

当建立一个新的 `Consumer Group` 时，需要决定是否需要消费 Broker 中已经存在的历史消息。`CONSUME_FROM_LAST_OFFSET` 将忽略历史消息，并消费此后生成的任何内容。`CONSUME_FROM_FIRST_OFFSET` 将消耗 Broker 中存在的所有消息。您还可以使用 `CONSUME_FROM_TIMESTAMP` 来消费在指定的时间戳之后生成的消息。

## 重复

许多情况可能导致重复，例如：

- 生产者重新发送消息（i.e, in case of FLUSH_SLAVE_TIMEOUT）
- 消费者关闭时未将offsets 更新到 Broker

因此，如果您的应用程序不能容忍重复，那么您可能需要做一些外部工作来处理这个问题。例如，您可以检查DB的主键。







<https://www.jianshu.com/p/3388bb2bbed9>