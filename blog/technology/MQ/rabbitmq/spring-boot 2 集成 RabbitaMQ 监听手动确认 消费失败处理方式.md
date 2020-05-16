[TOC]



# spring-boot 2 集成 RabbitaMQ 监听手动确认 消费失败处理方式

2019.02.20 00:16:48字数 400阅读 353

## 简介

MQ全称（Message Queue）又名消息队列，是一种异步通讯的中间件。是分布式系统中重要的组件，主要解决应用解耦，异步消息，流量削锋等问题，实现高性能，高可用，可伸缩和最终一致性架构。常见的MQ有kafka、activemq、zeromq、rabbitmq 等等，各大MQ的对比和优劣势[请移步>>](https://blog.csdn.net/HD243608836/article/details/80217591)。

## 环境信息

> jdk1.8.0_45
> Spring Boot 2.0.1.RELEASE

## Maven 依赖

```xml
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
```

## 连接配置

> application.properties 文件增加以下配置

```properties
 #==========================================================
#  RabbitMQ 连接配置
#==========================================================
# 连接用户名
spring.rabbitmq.username=guest
# 连接密码
spring.rabbitmq.password=guest
# 服务地址
spring.rabbitmq.host=172.18.1.1
# 服务端口号
spring.rabbitmq.port=5672
# 在RabbitMQ中可以虚拟消息服务器VirtualHost，每个VirtualHost相当月一个相对独立的RabbitMQ服务器，每个VirtualHost之间是相互隔离的。exchange、queue、message不能互通。
spring.rabbitmq.virtual-host=/
# 手动ACK 不开启自动ACK模式,目的是防止报错后死循环重复消费错误消息，默认为 none
spring.rabbitmq.listener.simple.acknowledge-mode=manual
# 最大重试次数
spring.rabbitmq.listener.simple.retry.max-attempts=2
# 是否开启消费者重试
spring.rabbitmq.listener.simple.retry.enabled=true
# 重试间隔时间(毫秒）
spring.rabbitmq.listener.simple.retry.initial-interval=3000
# 重试次数超过上面的设置之后是否丢弃（false不丢弃时需要写相应代码将该消息加入死信队列）
spring.rabbitmq.listener.simple.default-requeue-rejected=false
```

## 测试代码

1. 配置队列



```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置
 * 
 * @author swordshake
 * @since 1.0
 */
@Slf4j
@Configuration
public class RabbitDemoConfig {

  public static final String DEMO_ROUTING_KEY = "dev.demo.register.manual.queue";

  /**
   * 配置RabbitMQ连接模板
   * 
   * @param connectionFactory
   * @return
   */
  @Bean
  public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
    connectionFactory.setPublisherConfirms(true);
    connectionFactory.setPublisherReturns(true);
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMandatory(true);
    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log
        .info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause));
    rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> log
        .info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange,
            routingKey, replyCode, replyText, message));
    return rabbitTemplate;
  }

  /**
   * 定义队列。
   * 
   * @return
   */
  @Bean
  public Queue demoQueue() {
    // 参数1：队列名称，参数2：是否持久化。
    return new Queue(DEMO_ROUTING_KEY, true);
  }

  /**
   * 一个消费者的情况下用于保证消息队列按顺序一条一条消费的容器配置。
   * 
   * @param configurer
   * @param connectionFactory
   * @return
   */
  @Bean
  public SimpleRabbitListenerContainerFactory orderContainerFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer,
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setPrefetchCount(1); // 用于保证一次只pop一条消息，只有当本条消息确认后才继续pop下一条。
    configurer.configure(factory, connectionFactory);
    return factory;
  }

}
```

1. 生产者



```java
// RabbitDemoMsg .java
@Data
@AllArgsConstructor
public class RabbitDemoMsg implements Serializable {
  private static final long serialVersionUID = -447646130662400154L;
  private String id;
  private String name;
}

// 用于生成消息的测试类
@Api(tags = "测试用的服务")
@RestController
@RequestMapping("/testservice")
public class RSTestController {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @ApiOperation("测试RabbitMQ")
  @GetMapping(value = "sendMsg")
  public void sendMsg(@RequestParam String p) {
    RabbitDemoMsg msg = new RabbitDemoMsg("1", "测试RabbitMQ" + p);
    rabbitTemplate.convertAndSend(RabbitDemoConfig .DEMO_ROUTING_KEY, msg);
  }
}
```

1. 消费者



```java
import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.saleservice.config.RabbitConfig;
import com.hd123.saleservice.config.RabbitDemoConfig;
import com.hd123.saleservice.rs.impl.rabbit.RabbitDemoMsg;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * 队列消费者。
 * 
 * @author swordshake
 * @since 1.0
 */
@Component
@Slf4j
public class RabbitDemoHandler {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = {
      RabbitConfig.DEMO_ROUTING_KEY }, containerFactory = "orderContainerFactory") // 使用自定义的容器工厂，内已配置消息一次只能消费一条
  public void handler(RabbitDemoMsg demo, Message message, Channel channel) throws IOException {
    log.info("[Rabbit DEMO Handler 监听的消息]-[{}]", demo.toString());
    try {

      Thread.currentThread().sleep(10000);
      int i = 1 / 0;
      // 手动确认消费消息
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    } catch (Exception e) {
      log.error("", e);
      // ========消费失败处理方式：1、重新入栈消费 (重复消费错误数据会死循环)=======
      // channel.basicRecover(false); // 重新压入MQ，参数表示是否能被其它消费者消费，效果类似第三种处理方式开启重新入栈的场景,不同的是它会触发 ListenerContainerConsumerFailedEvent

      // ========消费失败处理方式：2、转到其它队列，比如延迟队列进行特殊处理;然后继续消费下一条消息。（推荐做法）=======
      rabbitTemplate.convertAndSend(RabbitDemoConfig.DEMO_DEAD_LETTER_ROUTING_KEY, demo);
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

      // ========消费失败处理方式：3、拒绝并重新入栈(重复消费错误数据会死循环)=======
      // channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
      // // 第二个参数表示是否重新入栈,为false会直接丢弃当前消息；为true时会重新放入原消息队列位置，重新消费。

      // ========消费失败处理方式：4、抛异常,启用了最大重试次数后会被阻塞到unacked消息中=======
      // throw new IOException(e); //根据application.properties
      // 配置的最大重试次数进行重试，超过的话进入unacked状态。由于本消息未应答，因此下一条消息会被本消息阻塞，不会继续处理。会导致 Ready 消息堆积。
    }
  }
}
```

> 注意：进入 unacked 的消息会被堆积，直到消息服务器客户端同服务端断开，重新连接后， unacked 的消息才会重新进入 Ready 状态。

## 特殊应用

#### 顺序消费

1. 通过上方RabbitMQ配置类中的 orderContainerFactory 设置一次只消费一条消息，在保证只有一个消费者前提下即可达到按顺序消费消息。大部分要求顺序消费的场景，如遇到消息错误情况，会要求阻塞在错误消息处，参照手动ACK下的错误处理方式4。
2. 通过延时队列实现的伪顺序消费，适用于消息间不要求顺序强一致的场景。
3. 消息体中记录版本号，由业务代码根据版本号控制消息消费的顺序。

#### 重复消费

推荐业务代码中消费消息时，保证幂等性。



https://www.jianshu.com/p/800b449358da