[TOC]



# zookeeper Curator zkClient 客户端对比

 

## 1.1. **zookeeper应用开发**

Zookeeper应用开发，需要使用Zookeeper的java 客户端API ，去连接和操作Zookeeper 集群。

可以供选择的java 客户端API 有：Zookeeper 官方的 java客户端API，第三方的java客户端API。

Zookeeper官方的 客户端API提供了基本的操作，比如，创建会话、创建节点、读取节点、更新数据、删除节点和检查节点是否存在等。但对于开发人员来说，Zookeeper提供的基本操纵还是有一些不足之处。

Zookeeper API不足之处如下：

（1）Zookeeper的Watcher是一次性的，每次触发之后都需要重新进行注册；

（2）Session超时之后没有实现重连机制；

（3）异常处理繁琐，Zookeeper提供了很多异常，对于开发人员来说可能根本不知道该如何处理这些异常信息；

（4）只提供了简单的byte[]数组的接口，没有提供针对对象级别的序列化；

（5）创建节点时如果节点存在抛出异常，需要自行检查节点是否存在；

（6）删除节点无法实现级联删除；

第三方开源客户端主要有zkClient和Curator。

### 1.1.1. ZkClient简介

ZkClient是一个开源客户端，在Zookeeper原生API接口的基础上进行了包装，更便于开发人员使用。zkClient客户端，在一些著名的互联网开源项目中，得到了应用，比如：阿里的分布式dubbo框架，对它进行了集成使用。

zkClient解决了Zookeeper原生API接口的很多问题。比如，zkClient提供了更加简洁的api，实现了session会话超时重连、Watcher反复注册等问题。虽然ZkClient对原生API进行了封装，但也有它自身的不足之处。

具体如下：

（1）zkClient社区不活跃，文档不够完善，几乎没有参考文档；

（2）异常处理简化（抛出RuntimeException）；

（3）重试机制比较难用；

（4）没有提供各种使用场景的参考实现；

### 1.1.2. Curator简介

Curator是Netflix公司开源的一套Zookeeper客户端框架，和ZkClient一样，解决了非常底层的细节开发工作，包括连接重连、反复注册Watcher和NodeExistsException异常等。Curator是Apache基金会的顶级项目之一，Curator具有更加完善的文档，另外还提供了一套易用性和可读性更强的Fluent风格的客户端API框架。

不止上面这些，Curator中还提供了Zookeeper各种应用场景（Recipe，如共享锁服务、Master选举机制和分布式计算器等）的抽象封装。

另外，Curator供了一套非常优雅的链式调用api，对比ZkClient客户端 Api的使用，发现 Curator的api 优雅太多。

使用ZkClient客户端，创建节点的代码为：

```java
     ZkClient client = new ZkClient("192.168.1.105:2181",
                10000, 10000, new SerializableSerializer());
        System.out.println("conneted ok!");
        String zkPath = "/test/node-1";
        Stat stat = new Stat();
        User u = client.readData(zkPath, stat);
```

使用Curator客户端，创建节点的代码如下：

```java
   CuratorFramework client =  CuratorFrameworkFactory.newClient(
                connectionString, retryPolicy);
        String zkPath = "/test/node-1";
        client.create().withMode(mode).forPath(zkPath);
```

总之，由于Curator客户端确实非常优秀，Patrixck Hunt（Zookeeper）以一句“Guava is to Java that Curator to Zookeeper”，对Curator给予了高度评价。

因此，对于Zookeeper的客户端，我们这里只学习和研究Curator的使用。而且，在实际的开发场景中，使用Curator客户端，就足可以应付日常的Zookeeper集群操作需求。

疯狂创客圈社群的亿级流量IM实战项目，也使用Curator客户端来操作Zookeeper集群。

 



<https://www.cnblogs.com/crazymakercircle/p/10225739.html>