[TOC]



# Elasticsearch Java Transport Client和Java REST Client比较

2017年04月06日 01:22:49 [shengpli](https://me.csdn.net/qq_23146763) 阅读数：7984

## 介绍

官方已经建议用REST,而 TransportClient 在7.0 开始废弃,在8.0版本移除. 详情请见:<https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/client.html>

以下为使用2.3.X的个人经验

JAVA API 使用的netty协议TransportClient 端口9300 性能好 上手麻烦，需熟悉API和ES DSL，适合大量频繁数据查询 
JAVA REST Client使用http协议 端口9200 上手简单,懂ES DSL查询即可

Rest API使用了HTTP协议，调用比较困难。Rest API的核心是url和post数据，url直接需传入字符串，这样就不能使用IDE的查错功能。需要记忆的东西太多，不确定时就要去查API，影响开发效率。



​	官方推荐使用Java High Level REST Client，因为在实际使用中，Java Transport Client在大并发的情况下会出现连接不稳定的情况。

## ESJavaClient的历史

#### JavaAPI Client

- 优势：基于transport进行数据访问，能够使用ES集群内部的性能特性，性能相对好
- 劣势：client版本需要和es集群版本一致，数据序列化通过java实现，es集群或jdk升级，客户端需要伴随升级。

 

​    ES官网最早提供的Client，spring-data-elasticsearch也基于该client开发，使用transport接口进行通信，其工作方式是将webserver当做集群中的节点，获取集群数据节点(DataNode)并将请求路由到Node获取数据将，返回结果在webserver内部进行结果汇总。 client需要与es集群保持相对一致性，否则会出现各种『奇怪』的异常。由于ES集群升级很快，集群升级时客户端伴随升级的成本高。

 

官网已声明es7.0将不在支持transport client(API Client),8.0正时移除

 

#### REST Client

- 优势：REST风格交互，符合ES设计初衷；兼容性强；
- 劣势：性能相对API较低

 

ESREST基于http协议，客户端发送请求到es的任何节点，节点会通过transport接口将请求路由到其他节点，完成数据处理后汇总并返回客户端，客户端负载低，。

 

RestFul是ES特性之一，但是直到5.0才有了自己的客户端，6.0才有的相对好用的客户端。在此之前JestClient作为第三方客户端，使用非常广泛。

 

本文将对Java Rest Client、Java High Level Client、Jest三种Restfull风格的客户端做简单的介绍，个人推荐使用restClient，详见后文。





https://blog.csdn.net/qq_23146763/article/details/69370929