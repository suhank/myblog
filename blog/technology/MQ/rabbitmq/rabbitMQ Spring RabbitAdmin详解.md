[TOC]



# rabbitMQ Spring RabbitAdmin详解

2018.11.23 16:04:10字数 55阅读 587 

## pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.edu.mq</groupId>
  <artifactId>spring-amqp-rabbitmq</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-amqp-rabbitmq</name>
  <url>http://maven.apache.org</url>

  <properties>
      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
      <maven.compiler.source>1.8</maven.compiler.source>
      <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <dependencies>
      <dependency>
          <groupId>org.springframework.amqp</groupId>
          <artifactId>spring-rabbit</artifactId>
          <version>1.7.3.RELEASE</version>
      </dependency>
  </dependencies>
  
</project>
```

## 注册rabbitAdmin和connectionFactory

```java
package com.edu.mq.spring;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册到spring容器
 */
@Configuration
public class MQConfig {

    /**
     * 注册ConnectionFactory工厂类，这里是spring封装的工厂类，和之前的rabbitclient的工程类
     * 不是同一个
     * org.springframework.amqp.rabbit.connection.ConnectionFactory;
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUri("amqp://guest:guest@localhost:5672");
        return factory;
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
```

## RabbitAdmin的具体操作

```java
package com.edu.mq.spring;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：
 *
 * @author zhangkewei
 * @date 2018/11/22下午8:08
 */
@ComponentScan
public class RabbmitAdminDetail {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RabbmitAdminDetail.class);
        RabbitAdmin rabbit = context.getBean(RabbitAdmin.class);
        System.out.println(rabbit);

        /**
         * 创建四种不同的exchange，这里的exchange都是用spring-amqp封装的exchange，并指定exchange的属性
         * 以下代码都可以重复执行
         */
        rabbit.declareExchange(new DirectExchange("log.direct.exchange", true, false));
        rabbit.declareExchange(new TopicExchange("log.topic.exchange", true, false));
        rabbit.declareExchange(new FanoutExchange("log.fanout.exchange", true, false));
        rabbit.declareExchange(new HeadersExchange("log.headers.exchange", true, false));

        //  rabbit.deleteExchange("log.headers.exchange");

        /**
         * 创建queue和创建exchange一样
         */
        rabbit.declareQueue(new Queue("log.debug", true));
        rabbit.declareQueue(new Queue("log.info", true));
        rabbit.declareQueue(new Queue("log.error", true));

            //      rabbit.deleteQueue("log.error");
        /**
         * 清空某个队列中的消息，注意，清空的消息并没有被消费
         */
        rabbit.purgeQueue("log.info", false);

        /**
         * 声明绑定，1，exchange和queue绑定；2，exchange和exchange绑定及不同的绑定方式
         */
        rabbit.declareBinding(new Binding("log.debug", Binding.DestinationType.QUEUE, "log.direct.exchange", "debug", new HashMap<>()));
        rabbit.declareBinding(new Binding("log.info", Binding.DestinationType.QUEUE, "log.direct.exchange", "info", new HashMap<>()));
        rabbit.declareBinding(new Binding("log.error", Binding.DestinationType.QUEUE, "log.direct.exchange", "error", new HashMap<>()));

        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.debug")).to(new TopicExchange("log.topic.exchange")).with("debug.*"));
        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.info")).to(new TopicExchange("log.topic.exchange")).with("info.#"));
        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.error")).to(new TopicExchange("log.topic.exchange")).with("error.#"));

        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.debug")).to(new FanoutExchange("log.fanout.exchange")));
        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.info")).to(new FanoutExchange("log.fanout.exchange")));
        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.error")).to(new FanoutExchange("log.fanout.exchange")));

        Map<String, Object> headerValues = new HashMap<>();
        headerValues.put("type", 1);
        headerValues.put("size", 10);

        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.debug")).to(new HeadersExchange("log.headers.exchange")).whereAll(headerValues).match());
        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.info")).to(new HeadersExchange("log.headers.exchange")).whereAny(headerValues).match());

        Map<String, Object> headerValues2 = new HashMap<>();
        headerValues2.put("type", 2);
        headerValues2.put("size", 10);
        rabbit.declareBinding(BindingBuilder.bind(new Queue("log.error")).to(new HeadersExchange("log.headers.exchange")).whereAll(headerValues2).match());

        /**
         * 删除绑定
         */
//      rabbit.removeBinding(BindingBuilder.bind(new Queue("log.debug")).to(new FanoutExchange("log.fanout.exchange")));

        //exchange和exchange的binding
      //  rabbit.declareBinding(new Binding("log.all", Binding.DestinationType.EXCHANGE, "log.info", "info", new HashMap<>()));
      //  rabbit.declareBinding(BindingBuilder.bind(new TopicExchange("sms.all")).to(new TopicExchange("sms.reg")).with("reg"));

        context.close();
    }
}
```

如上可以看到，RabbitAdmin只是对RabbitTemplent的一个封装使用，完成exchange，queue，binding的创建，删除等操作。

 





<https://www.jianshu.com/p/c301d156be37>