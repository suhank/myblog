[TOC]



# spring cloud 之 客户端负载均衡 Ribbon

## 一、负载均衡

**负载均衡（Load Balance）**： 建立在现有网络结构之上，它提供了一种廉价有效透明的方法扩展网络设备和服务器的带宽、增加吞吐量、加强网络数据处理能力、提高网络的灵活性和可用性。其意思就是分摊到多个操作单元上进行执行，例如Web服务器、FTP服务器、企业关键应用服务器和其它关键任务服务器等，从而共同完成工作任务。

**1、服务端负载均衡：**客户端请求到负载均衡服务器，负载均衡服务器根据自身的算法将该请求转给某台真正提供业务的服务器，该服务器将响应数据给负载均衡服务器，负载均衡服务器最后将数据返回给客服端。（nginx）

**2、客服端负载均衡：**基于客户端的负载均衡，简单的说就是在客户端程序里面，自己设定一个调度算法，在向服务器发起请求的时候，先执行调度算法计算出向哪台服务器发起请求，然后再发起请求给服务器。

基于客户端负载均衡的特点：

- 由客户端内部程序实现，不需要额外的负载均衡器软硬件投入。
- 程序内部需要解决业务服务器不可用的问题，服务器故障对应用程序的透明度小。
- 程序内部需要解决业务服务器压力过载的问题。

## 二、Ribbon实现客户端的负载均衡

### 项目配置

我们使用spring boot 来测试。

pom文件：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.jalja.org</groupId>
  <artifactId>spring-consumer-server-ribbon</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  
   <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.2.RELEASE</version>
    </parent>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Camden.SR4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
         <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
    
</project>
```

application.yml

```
stores:
  ribbon:
    listOfServers: www.baidu.com,www.jalja.org,www.163.com
```

### Ribbon的负载均衡策略

#### 1、RoundRobinRule(轮询模式) 

public class RoundRobinRule extends AbstractLoadBalancerRule roundRobin方式轮询选择server 轮询index，选择index对应位置的server          该策略也是ribbon的默认策略

```java
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class SpringCloudRibbonApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudRibbonApplication.class, args);
    }
    @Autowired
    private LoadBalancerClient loadBalancer;
    @RequestMapping(value="static")
    public String staticRibbon(){
         ServiceInstance instance = loadBalancer.choose("stores");
         URI storesUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
         System.out.println(storesUri);
        return "static";
    }
}

连续请求6次执行结果：
```

http://www.baidu.com:80
http://www.jalja.org:80
http://www.163.org:80
http://www.baidu.com:80
http://www.jalja.org:80
http://www.163.org:80



#### 2、RandomRule(随机策略) 

public class RandomRule extends AbstractLoadBalancerRule 随机选择一个server 在index上随机，选择index对应位置的server。

在配置文件application.yml加入 

```
NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

```
stores:
  ribbon:
    listOfServers: www.baidu.com,www.jalja.org,www.163.org
    #随机
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

在SpringCloudRibbonApplication.java 中加入

```
    @Bean
    public IRule ribbonRule() {
        return new RandomRule();//这里配置策略，和配置文件对应
    }
```

执行6次的结果：

```
http://www.baidu.com:80
http://www.baidu.com:80
http://www.baidu.com:80
http://www.163.org:80
http://www.baidu.com:80
http://www.jalja.org:80
```

#### 3、BestAvailableRule（并发量） 

public class BestAvailableRule extends ClientConfigEnabledRoundRobinRule 选择一个最小的并发请求的server 逐个考察Server，如果Server被tripped了，则忽略，在选择其中ActiveRequestsCount最小的server

在配置文件application.yml加入

NFLoadBalancerRuleClassName: com.netflix.loadbalancer.BestAvailableRule

在SpringCloudRibbonApplication.java 中加入

```
@Bean
    public IRule ribbonRule() {
        return new BestAvailableRule();//这里配置策略，和配置文件对应
    }
```

执行6次的结果：

```
http://www.baidu.com:80
http://www.baidu.com:80
http://www.baidu.com:80
http://www.baidu.com:80
http://www.baidu.com:80
http://www.baidu.com:80
```

#### 4、AvailabilityFilteringRule(服务器状态)

 public class AvailabilityFilteringRule extends PredicateBasedRule 过滤掉那些因为一直连接失败的被标记为circuit tripped的后端server，并过滤掉那些高并发的的后端server（active connections 超过配置的阈值） 使用一个AvailabilityPredicate来包含过滤server的逻辑，其实就就是检查status里记录的各个server的运行状态

 

#### 5、WeightedResponseTimeRule（根据响应时间） 

public class WeightedResponseTimeRule extends RoundRobinRule 根据响应时间分配一个weight，相应时间越长，weight越小，被选中的可能性越低。 一个后台线程定期的从status里面读取评价响应时间，为每个server计算一个weight。Weight的计算也比较简单responsetime 减去每个server自己平均的responsetime是server的权重。当刚开始运行，没有形成statas时，使用roubine策略选择server。

#### 6、RetryRule(根据策略+重试)	

public class RetryRule extends AbstractLoadBalancerRule	对选定的负载均衡策略机上重试机制。	在一个配置时间段内当选择server不成功，则一直尝试使用subRule的方式选择一个可用的server

#### 7、ZoneAvoidanceRule（Zone状态+服务状态）	

public class ZoneAvoidanceRule extends PredicateBasedRule	复合判断server所在区域的性能和server的可用性选择server	使用ZoneAvoidancePredicate和AvailabilityPredicate来判断是否选择某个server，前一个判断判定一个zone的运行性能是否可用，剔除不可用的zone（的所有server），AvailabilityPredicate用于过滤掉连接数过多的Server。

 

4、5、6、7这些策略使用方式与上述方式相同这里不在演示 

每天用心记录一点点。内容也许不重要，但习惯很重要！





http://www.cnblogs.com/jalja/p/6984541.html