# SpringBoot整合ELK实现日志收集

# 为什么要用ELK收集日志

目前大多数项目都是采用微服务架构，在项目的初期，为了按计划上线就没有搭建日志收集分析平台，日志都是保存在服务器本地，看日志时一个个的找。随着项目的服务越来越多，各个服务都是集群部署，服务器数量也快速增长，此时就暴露了很多的问题：

- 问题排查困难，查询一个服务的日志，需要登录多台服务器；
- 日志串接困难，一个流程有多个节点，要把整个流程的日志串接起来工作量大；
- 运维管理困难，不是每个同事都有登录服务器查看日志的权限，但又需要根据日志排查问题，就需要有权限的同事下载日志后给到相应负责的同事。
- 系统预警困难，无法实现服务出现异常后，及时通知到相应的负责人

后期采用了蚂蚁金融云上的loghub，对日志进行统一的收集、存储。由于loghub不是开源的，对于loghub的具体实现不是太清楚。但是业界一般采用ELK（elasticsearch+logstash+kibana）来收集日志，其实原理和loghub差不多，下面就结合SpringBoot整合ELK进行讲解。

# ELK的简介

ELK是三个开源软件的缩写，分别表示：elasticsearch、logstash、kibana

- Elasticsearch是个开源分布式搜索引擎，提供搜集、分析、存储数据三大功能。它的特点有：分布式，零配置，自动发现，索引自动分片，索引副本机制，restful风格接口，多数据源，自动搜索负载等
- Logstash 主要是用来日志的搜集、分析、过滤日志的工具，支持大量的数据获取方式。一般工作方式为c/s架构，client端安装在需要收集日志的主机上，server端负责将收到的各节点日志进行过滤、修改等操作在一并发往elasticsearch上去。
- Kibana可以为 Logstash 和 ElasticSearch 通过报表、图形化数据进行可视化展示 Web 界面，可以帮助汇总、分析和搜索重要数据日志。

# 实现日志收集的方案

> 方案一：logstash->elasticsearch->kibana



![img](image-202007272053/16ec1821185988cb)

将logstash部署到每个节点，收集相关的日志，并经过分析过滤后发送到elasticsearch进行存储，elasticsearch将数据以分片的形势进行压缩存储，通过kibana对日志进行图形化的展示。



优点：此架构搭建简单，容易上手

缺点：

- 1、每个节点部署logstash，运行时占用CPU，内存大，会对节点性能造成一定的影响
- 2、没有将日志数据进行缓存，存在丢失的风险

> 方案二：logstash->kafka->elasticsearch->kibana



![img](image-202007272053/16ec187e05dc4f05)

logstash agent监控过滤日志，将过滤的日志内容发送给Kafka，logstash server将日志收集一起交给elasticsearch，引入了消息队列机制作为缓存池，即使logstash server出现异常，由于日志暂存在kafka消息队列中，能避免日志数据丢失，但是还是没有解决性能问题。



这次讲解选择的是第一种方案，第二种方案后期再进行实现

# 使用docker compose搭建ELK环境

需要提前下载好docker镜像，elasticsearch、logstash、kibana我选都是6.4.0版本，最好版本要一致

```
docker pull elasticsearch:6.4.0
docker pull logstash:6.4.0
docker pull kibana:6.4.0
复制代码
```

## 在本地创建好存放文件的目录

### 创建elasticsearch和logstash目录，后面用于存放配置文件

![img](image-202007272053/16ec1927b2a9dbb3)



### 新建logstash的配置文件logstash.conf，并上传到logstash的目录下

![img](image-202007272053/16ec1944d488cf4a)

logstash.conf的内容：

```
input {
  tcp {
    mode => "server"
    host => "0.0.0.0"
    port => 4560
    codec => json_lines
  }
}
output {
  elasticsearch {
    hosts => "es:9200"
    index => "springboot-%{+YYYY.MM.dd}"
  }
}
复制代码
```

## 使用docker-compose.yml脚本启动ELK服务

docker-compose.yml的内容为：

```
version: '3'
services:
  elasticsearch:
    image: elasticsearch:6.4.0
    container_name: elasticsearch
    environment:
      - "cluster.name=elasticsearch" #设置集群名称为elasticsearch
      - "discovery.type=single-node" #以单一节点模式启动
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m" #设置使用jvm内存大小
    volumes:
      - /Users/storage/software/docker/elk/elasticsearch/plugins:/usr/share/elasticsearch/plugins #插件文件挂载
      - /Users/storage/software/docker/elk/elasticsearch/data:/usr/share/elasticsearch/data #数据文件挂载
    ports:
      - 9200:9200
      - 9300:9300
  kibana:
    image: kibana:6.4.0
    container_name: kibana
    links:
      - elasticsearch:es #可以用es这个域名访问elasticsearch服务
    depends_on:
      - elasticsearch #kibana在elasticsearch启动之后再启动
    environment:
      - "elasticsearch.hosts=http://es:9200" #设置访问elasticsearch的地址
    ports:
      - 5601:5601
  logstash:
    image: logstash:6.4.0
    container_name: logstash
    volumes:
      - /Users/storage/software/docker/elk/logstash/logstash.conf:/usr/share/logstash/pipeline/logstash.conf #挂载logstash的配置文件
    depends_on:
      - elasticsearch #kibana在elasticsearch启动之后再启动
    links:
      - elasticsearch:es #可以用es这个域名访问elasticsearch服务
    ports:
      - 4560:4560

复制代码
```

在该文件的目录下执行docker-compose命令运行

```
docker-compose up -d
复制代码
```

启动时间可能有点长，需要耐心等待



![img](image-202007272053/16ec197ec5e778e7)



## 在logstash中安装json_lines插件

```
# 进入logstash容器(e9c845c8d48e为容器id)
docker exec -it e9c845c8d48e /bin/bash
# 进入bin目录
cd /bin/
# 安装插件
logstash-plugin install logstash-codec-json_lines
# 退出容器
exit
# 重启logstash服务
docker restart logstash

复制代码
```

访问地址：http://127.0.0.1:9200/



![img](image-202007272053/16ec19ae9f02da27)



访问地址：[http://127.0.0.1:5601](http://127.0.0.1:5601/)



![img](image-202007272053/16ec19a246296213)



以上就是elasticsearch和kibana启动成功的界面

# Springboot集成logstash

## 在pom.xml中添加logstash-logback-encoder依赖

```
<!--集成logstash-->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>5.3</version>
</dependency>
 
```

## 添加配置文件logback-spring.xml让logback的日志输出到logstash

```
 <!--输出到logstash的appender-->
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <!--可以访问的logstash日志收集端口-->
        <destination>127.0.0.1:4560</destination>
        <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    
     <springProfile name="dev">
        <root>
            <level value="INFO"/>
            <appender-ref ref="stdout"/>
            <appender-ref ref="asyncInfo"/>
            <appender-ref ref="asyncWarn"/>
            <appender-ref ref="asyncError"/>
            <appender-ref ref="LOGSTASH"/>
        </root>
    </springProfile>

    <springProfile name="test,prod">
        <root>
            <level value="INFO"/>
            <appender-ref ref="asyncInfo"/>
            <appender-ref ref="asyncWarn"/>
            <appender-ref ref="asyncError"/>
             <appender-ref ref="LOGSTASH"/>
        </root>
    </springProfile>
复制代码
```

# 在kibana中查看日志信息

## 创建index pattern



![img](image-202007272053/16ec19fdc3566dbe)





![img](image-202007272053/16ec1a13b6fe9304)





![img](image-202007272053/16ec1a1ea3be4e9a)



## 查看收集的日志

启动我们的项目就可以看到启动日志已经输出到elasticsearch中了



![img](image-202007272053/16ec1a3e7955cb43)



# 总结

搭建了ELK日志系统后，我们就可以直接在kibana上看系统的日志了，还可以进行搜索



![img](image-202007272053/16ec1a56617e70ec)



https://juejin.im/post/5de3b1b86fb9a071a828fefd#heading-2