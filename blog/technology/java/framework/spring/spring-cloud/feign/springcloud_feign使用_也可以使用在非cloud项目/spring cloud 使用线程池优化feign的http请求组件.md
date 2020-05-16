[TOC]



# Spring cloud 使用线程池优化feign的http请求组件

 

## 1. 概述

在默认情况下 spring cloud feign在进行各个子服务之间的调用时，http组件使用的是jdk的HttpURLConnection，没有使用线程池。本文先从源码分析feign的http组件对象生成的过程，然后通过为feign配置http线程池优化调用效率。

## 2. 源码分析

我们分析源码spring cloud feign。在spring-cloud-netflix-core/META-INF/spring.factories中可以看到，在spring boot自动配置会初始化FeignRibbonClientAutoConfiguration，这个类会生成Ribbon的使用http组件。

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration,\
```

分析配置类是FeignRibbonClientAutoConfiguration 下面分析此类import的3个类：HttpClientFeignLoadBalancedConfiguration，OkHttpFeignLoadBalancedConfiguration，DefaultFeignLoadBalancedConfiguration

```
@Import({ HttpClientFeignLoadBalancedConfiguration.class,
    OkHttpFeignLoadBalancedConfiguration.class,
    DefaultFeignLoadBalancedConfiguration.class })
public class FeignRibbonClientAutoConfiguration {
 …
}
```

**HttpClientFeignLoadBalancedConfiguration** 
为feigin配置appache client的线程池 
当引入ApacheHttpClient.class类时，会初始化这个配置类 
方法feignClient()中：根据@ConditionalOnMissingBean(Client.class)知道如果有HttpClient 对象，则创建的ApacheHttpClient使用自己定义的HttpClient 。如果没有，则使用默认值。最后生成LoadBalancerFeignClient对象

```
@Configuration
@ConditionalOnClass(ApacheHttpClient.class)
@ConditionalOnProperty(value = "feign.httpclient.enabled", matchIfMissing = true)
class HttpClientFeignLoadBalancedConfiguration {

    @Autowired(required = false)
    private HttpClient httpClient;

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
          SpringClientFactory clientFactory) {
        ApacheHttpClient delegate;
        if (this.httpClient != null) {
            delegate = new ApacheHttpClient(this.httpClient);
        } else {
            delegate = new ApacheHttpClient();
        }
        return new LoadBalancerFeignClient(delegate, cachingFactory, clientFactory);
    }
}
```

**OkHttpFeignLoadBalancedConfiguration** 
为feigin配置OkHttp，类似apache httpclient, 这里略。 
**DefaultFeignLoadBalancedConfiguration** 
为feigin配置HttpURLConnection， 
方法feignClient()：只有以上两个Client没有生产对象时，才在这个方法中使用Client.Default生成LoadBalancerFeignClient

```
@Configuration
class DefaultFeignLoadBalancedConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
          SpringClientFactory clientFactory) {
        return new LoadBalancerFeignClient(new Client.Default(null, null),
            cachingFactory, clientFactory);
    }
}
```

查看Client.Default的源码，Default 使用HttpURLConnection 建立连接且每次请求都建立一个新的连接

```
 public static class Default implements Client {
    @Override
    public Response execute(Request request, Options options) throws IOException {
      HttpURLConnection connection = convertAndSend(request, options);
      return convertResponse(connection).toBuilder().request(request).build();
    }
   ….
}
```

综上所述，在默认情况下，spring cloud 没有引入httpclient和okhttp的jar包，所有默认使用HttpURLConnection

## 3. 使用appach httpclient线程池

默认情况下，服务之间调用使用的HttpURLConnection，效率非常低。为了提高效率，可以通过连接池提高效率，本节我们使用appache httpclient做为连接池。配置OkHttpClient连接池，也是类似的方法，这里略。 经过上节的分析，配置线程池方法：引入appache httpclient并启动对应配置，最后还需要生成HttpClient对象。

### 3.1. pom.xml中引入feign-httpclient.jar

```xml
<!-- 增加feign-httpclient -->
 <dependency>
     <groupId>io.github.openfeign</groupId>
     <artifactId>feign-httpclient</artifactId>
  </dependency>
```

### 3.2. 配置参数application-hystrix-feign.yml启动httpclient

```yaml
# feign配置
feign:
  hystrix:
    # 在feign中开启hystrix功能，默认情况下feign不开启hystrix功能
    enabled: true
  ## 配置httpclient线程池
  httpclient:
    enabled: true
  okhttp:
    enabled: false
```

### 3.3. 自定义配置类

使用配置类，生成HttpClient 对象。因为使用PoolingHttpClientConnectionManager连接池，我们需要启动定时器，定时回收过期的连接。配置定时回收连接池的原因，见[问题备忘: httpclient连接池异常引发的惨案](https://link.juejin.im/?target=https%3A%2F%2Fblog.csdn.net%2Fhry2015%2Farticle%2Fdetails%2F78965690)

```java
@Configuration
public class HttpPool {

    @Bean
    public HttpClient httpClient(){
        System.out.println("init feign httpclient configuration " );
        // 生成默认请求配置
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        // 超时时间
        requestConfigBuilder.setSocketTimeout(5 * 1000);
        // 连接时间
        requestConfigBuilder.setConnectTimeout(5 * 1000);
        RequestConfig defaultRequestConfig = requestConfigBuilder.build();
        // 连接池配置
        // 长连接保持30秒
        final PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.MILLISECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(200);
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(50);

        // httpclient 配置
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
        HttpClient client = httpClientBuilder.build();


        // 启动定时器，定时回收过期的连接
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //        System.out.println("=====closeIdleConnections===");
                pollingConnectionManager.closeExpiredConnections();
                pollingConnectionManager.closeIdleConnections(5, TimeUnit.SECONDS);
            }
        }, 10 * 1000, 5 * 1000);
        System.out.println("===== Apache httpclient 初始化连接池===");

        return client;
    }


}
```

### 3.4. 测试：

启动工程：cloud-registration-center、cloud-service-hystrix 
启动服务：HystrixFeignCloudConsumerApplication 
执行请求：[http://127.0.0.1:12082/hystrix-feign/simple](https://link.juejin.im/?target=http%3A%2F%2F127.0.0.1%3A12082%2Fhystrix-feign%2Fsimple)

配置日志为debug输出(设置logback-spring.xml为 level为DEBUG)，如果日志有类似一下的输出（包含PoolingHttpClientConnectionManager ），则表示连接池配置成功

```
2018-04-09 23:11:49.017 [hystrix-cloud-hystrix-service-1] DEBUG o.a.h.i.c.PoolingHttpClientConnectionManager - Connection request: [route: {}->http://192.168.0.113:12081][total kept alive: 0; route allocated: 0 of 100; total allocated: 0 of 5000]
2018-04-09 23:11:49.020 [hystrix-cloud-hystrix-service-1] DEBUG o.a.h.i.c.PoolingHttpClientConnectionManager - Connection leased: [id: 0][route: {}->http://192.168.0.113:12081][total kept alive: 0; route allocated: 1 of 100; total allocated: 1 of 5000]
```

## 4. 代码

以上的详细的代码见下面 
[github代码，请尽量使用tag v0.12，不要使用master，因为我不能保证master代码一直不变](https://link.juejin.im/?target=https%3A%2F%2Fgithub.com%2Fhryou0922%2Fspring_cloud%2Ftree%2Fv0.12%2Fcloud-consumer-hystrix%2Fsrc%2Fmain%2Fjava%2Fcom%2Fhry%2Fspring%2Fcloud%2Fconsumer%2Fhystrix%2Ffeign)





https://juejin.im/entry/5ace2d256fb9a028c22b21e4