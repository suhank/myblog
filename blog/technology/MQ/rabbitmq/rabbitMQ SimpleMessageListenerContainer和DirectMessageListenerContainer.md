[TOC]



# RabbitMQ SimpleMessageListenerContainer和DirectMessageListenerContainer

2019年01月13日 14:37:02

在版本2.0之前的版本中，只有一种MessageListenerContainer—SimpleMessageListenerContainer;

2.0之后有第二个容器——DirectMessageListenerContainer

## SimpleMessageListenerContainer

默认情况下，侦听器容器将启动单个使用者，该使用者将从队列接收消息。

在检查上一节中的表时，您将看到许多控制并发性的属性。最简单的是concurrentConsumers，它只创建(固定的)将并发处理消息的使用者数量。

此外，还添加了一个新的属性maxConcurrentConsumers，容器将根据工作负载动态调整并发性。这与四个附加属性一起工作:continutiveactivetrigger、startConsumerMinInterval、continutiveidletrigger、stopConsumerMinInterval。

在默认设置下，增加消费者的算法工作如下:

如果尚未到达maxConcurrentConsumers，并且已有的使用者连续10个周期处于活动状态，并且自上一个使用者启动以来至少已经过了10秒，那么将启动一个新的使用者。如果使用者在txSize *中接收到至少一条消息，则认为该使用者处于活动状态。

在默认设置下，减少消费者的算法工作如下:

如果有多个concurrentConsumers正在运行，并且某个consumer检测到10个连续超时(空闲)，并且上一个consumer至少在60秒之前停止，那么该consumer将停止。超时取决于receiveTimeout和txSize属性。如果使用者在txSize *中没有接收到任何消息，则认为它是空闲的。因此，在默认超时(1秒)和txSize为4的情况下，在40秒的空闲时间(4个超时对应1个空闲检测)之后将考虑停止使用者。

配置如下

```java
@Bean
public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    //初始化消费者数量
    factory.setConcurrentConsumers(this.concurrentConsumers);
    //最大消费者数量
    factory.setMaxConcurrentConsumers(this.maxConcurrentConsumers);
    //手动确认消息
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    factory.setErrorHandler(rabbitErrorHandler);
    return factory;
}
```

## DirectMessageListenerContainer

使用DirectMessageListenerContainer，您需要确保ConnectionFactory配置了一个任务执行器，该执行器在使用该ConnectionFactory的所有侦听器容器中具有足够的线程来支持所需的并发性。默认连接池大小仅为5。

并发性基于配置的队列和consumersPerQueue。每个队列的每个使用者使用一个单独的通道，并发性由rabbit客户端库控制;默认情况下，它使用5个线程池;您可以配置taskExecutor来提供所需的最大并发性。

配置如下

```java
@Bean
public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
    DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    //每个队列的消费者数量
    factory.setConsumersPerQueue(this.consumersPerQueue);
    //手动确认消息
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    factory.setErrorHandler(rabbitErrorHandler);

    return factory;
}
 
```

## 对比

SimpleMessageListenerContainer提供了以下特性，但DirectMessageListenerContainer不提供:

- txSize—使用SimpleMessageListenerContainer，您可以将其设置为控制事务中传递的消息数量和/或减少ack的数量，但这可能会导致失败后重复传递的数量增加。(与txSize和SimpleMessageListenerContainer一样，DirectMessageListenerContainer也有mesagesPerAck，可以用来减少ack，但不能用于事务—每个消息都在单独的事务中交付和打包)。
- maxconcurrentconsumer和consumer伸缩间隔/触发器—DirectMessageListenerContainer中没有自动伸缩;但是，它允许您以编程方式更改consumersPerQueue属性，并相应地调整使用者。

然而，与SimpleMessageListenerContainer相比，DirectMessageListenerContainer有以下优点:

- 在运行时添加和删除队列更有效;使用SimpleMessageListenerContainer，整个使用者线程重新启动(所有使用者取消并重新创建);对于DirectMessageListenerContainer，不受影响的使用者不会被取消。
- 避免了RabbitMQ客户机线程和使用者线程之间的上下文切换。
- 线程是跨使用者共享的，而不是为SimpleMessageListenerContainer中的每个使用者都有一个专用线程。但是，请参阅“线程和异步使用者”一节中有关连接工厂配置的重要说明。







版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。

本文链接：<https://blog.csdn.net/yingziisme/article/details/86418580>