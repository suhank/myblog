[TOC]



# Spring Cloud中，Feign常见问题总结

 [Spring Cloud ](http://www.itmuch.com/categories/Spring%20Cloud) [Spring Cloud ](http://www.itmuch.com/tags/Spring%20Cloud) 2016/11/07  133

Spring Cloud中，Feign常见问题的总结。

## FeignClient接口，不能使用`@GettingMapping` 之类的组合注解

代码示例：

```
@FeignClient("microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
  public User findById(@PathVariable("id") Long id);
  ...
}
```

这边的`@RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)` 不能写成`@GetMapping("/simple/{id}")` 。

## FeignClient接口中，如果使用到`@PathVariable` ，必须指定其value

代码示例：

```
@FeignClient("microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
  public User findById(@PathVariable("id") Long id);
  ...
}
```

这边的`@PathVariable("id")` 中的”id”，不能省略，必须指定。

## FeignClient多参数的构造

如果想要请求`microservice-provider-user` 服务，并且参数有多个例如：[http://microservice-provider-user/query-by?id=1&username=张三](http://microservice-provider-user/query-by?id=1&username=%E5%BC%A0%E4%B8%89) 要怎么办呢？

直接使用复杂对象：

```
@FeignClient("microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/query-by", method = RequestMethod.GET)
  public User queryBy(User user);
  ...
}
```

该请求不会成功，只要参数是复杂对象，即使指定了是GET方法，feign依然会以POST方法进行发送请求。

**正确的写法**：

写法1：

```
@FeignClient("microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/query-by", method = RequestMethod.GET)
  public User queryBy(@RequestParam("id")Long id, @RequestParam("username")String username);
}
```

写法2：

```
@FeignClient(name = "microservice-provider-user")
public interface UserFeignClient {
  @RequestMapping(value = "/query-by", method = RequestMethod.GET)
  public List<User> queryBy(@RequestParam Map<String, Object> param);
}
```

## Feign如果想要使用Hystrix Stream，需要做一些额外操作

我们知道Feign本身就是支持Hystrix的，可以直接使用`@FeignClient(value = "microservice-provider-user", fallback = XXX.class)` 来指定fallback的类，这个fallback类集成@FeignClient所标注的接口即可。

但是假设我们需要使用Hystrix Stream进行监控，默认情况下，访问[http://IP:PORT/hystrix.stream](http://ip:PORT/hystrix.stream)是个404。如何为Feign增加Hystrix Stream支持呢？

需要以下两步：

第一步：添加依赖，示例：

```
<!-- 整合hystrix，其实feign中自带了hystrix，引入该依赖主要是为了使用其中的hystrix-metrics-event-stream，用于dashboard -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

第二步：在启动类上添加`@EnableCircuitBreaker` 注解，示例：

```
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
public class MovieFeignHystrixApplication {
  public static void main(String[] args) {
    SpringApplication.run(MovieFeignHystrixApplication.class, args);
  }
}
```

这样修改以后，访问任意的API后，再访问[http://IP:PORT/hystrix.stream，就会展示出一大堆的API监控数据了。](http://ip:PORT/hystrix.stream%EF%BC%8C%E5%B0%B1%E4%BC%9A%E5%B1%95%E7%A4%BA%E5%87%BA%E4%B8%80%E5%A4%A7%E5%A0%86%E7%9A%84API%E7%9B%91%E6%8E%A7%E6%95%B0%E6%8D%AE%E4%BA%86%E3%80%82)

## 如果需要自定义单个Feign配置，Feign的`@Configuration` 注解的类不能与`@ComponentScan` 的包重叠

如果包重叠，将会导致所有的Feign Client都会使用该配置。

## 首次请求失败

详见：[Spring Cloud中，如何解决Feign/Ribbon第一次请求失败的问题？](http://www.itmuch.com/spring-cloud-feign-ribbon-first-request-fail/)

## `@FeignClient` 的属性注意点

(1) serviceId属性已经失效，尽量使用name属性。例如：

```
@FeignClient(serviceId = "microservice-provider-user")
```

这么写是不推荐的，应写为：

```
@FeignClient(name = "microservice-provider-user")
```

(2) 在使用url属性时，在老版本的Spring Cloud中，不需要提供name属性，但是在新版本（例如Brixton、Camden）@FeignClient必须提供name属性，并且name、url属性支持占位符。例如：

```
@FeignClient(name = "${feign.name}", url = "${feign.url}")
```





http://www.itmuch.com/spring-cloud-sum-feign/