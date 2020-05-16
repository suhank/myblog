[TOC]



# Spring Cloud Feign Hystrix 超时 启动 禁用等配置

## 前言

 在 Spring Cloud Feign 中，除了引入了用户客户端负载均衡的 Spring Cloud Ribbon 之外，还引入了服务保护与容错的工具 Hystrix，默认情况下，Spring Cloud Feign 会为将所有 Feign客户端的方法都封装到 Hystrix 命令中进行服务保护，需要注意的是 Ribbon 的超时与 Hystrix 的超时是二个概念，需要让 Hystrix 的超时时间大于 Ribbon 的超时时间，否则 Hystrix 命令超时后，该命令直接熔断，重试机制就没有意义了。

## **全局配置**

对于 Hystrix 的全局配置同 Spring Cloud Ribbon 的全局配置一样，直接使用他的默认配置前缀 hystrix.command.default 就可以设置，比如，设置全局的超时时间，yml 配置格式如下：

```yaml
hystrix:
    command:
        default:
            execution:
                isolation:
                    thread:
                        timeoutInMillisecondes: 5000

#这个格式也可以
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 5000
```

在对Hystrix进行配置之前，我们需要确认 feign.hystrix.enabled 参数没有被设置为 false,该参数的含义就是关闭 Feign 客户端的 Hystrix 支持.

## **禁用 Hystrix**

在 Spring Cloud Feign 中，可以通过 feign.hystrix.enabled=false 来关闭Hystrix 功能，如果不想全局关闭 Hystrix 支持，而只想针对某个服务客户端关闭 Hystrix 支持，而要通过使用 @Scope("prototype") 注解为指定的客户端配置 Feign.Builder 实例，实现步骤如下：

- 构建一个关闭 Hystrix 的配置类：

  ```java
  @Configurable
  public class DisableHystrixConfiguration {
          @Bean
          @Scope ("prototype")
          public Feign.Builder feignBuilder() {
                  return Feign.builder();
          }
  }
  ```

  ​

- 在服务的 @FeignClient 注解中，通过 configuration 参数引入上面的配置：

  ```java
  @FeignClient (value = "ORG.LIXUE.HELLOWORLD", configuration = DisableHystrixConfiguration.class)
  public interface HelloWorldServiceProxy extends HelloWorldService {
  }
  ```

     

## **指定命令的超时时间配置**

对于 Hystrix 命令的配置，在实际应用时往往也会根据实际业务情况制定出不同的配置方案，配置方法也跟传统的 Hystrix 命令的参数配置相似，采用 hystrix.command.<commandKey> 作为前缀，而 <commandKey>默认会采用 Feign 客户端中的方法名作为标识，由于方法名很有可能重复，这个时候相同方法名的 Hystrix 配置会共用，所以在进行方法定义与配置的时候需要做好一定的规划，也可以重写 Feign.Builder 的实现，并在应用主类中创建它的实例来覆盖自动化配置的 HystrixFeign.Builder 实现，示例配置如下：

```yaml
hystrix:
  command:
    default: #默认命令配置
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 10000 #全局的超时时间 默认1秒
#      tiemout:
#        enabled: false
#    UserFeignClient: #hi 命名配置    Feign 客户端中的方法名作为标识  配置是无效的,但是可以配置 feign的超时时间来熔断
#      execution:
#        isolation:
#          thread:
#            timeoutInMilliseconds: 10000
                                    
                                    
#properties写法
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 1000   #全局的超时时间 默认1秒
```

## **服务降级配置**

Hystrix 提供的服务降级是服务容错的重要功能，由于 Spring Cloud Feign 在定义服务客户端时候与 Spring Cloud Ribbon 有很大差别，HystrxCommand 定义被封装起来了 Spring Cloud Feign 提供了另一种简单的定义方式，服务降级逻辑的实现只需要为Feign 客户端的定义接口编写一个具体的接口实现类，然后通过 @FeignClient 注解的 fallback 属性来指定对应的服务降级实现类即可，示例如下：

- 定义服务实现的客户端接口，创建 HelloWorldClient 接口，并继承与服务接口 HelloWorldService，增加 @FeignClient 注解，并指定服务名称、fallback设置服务降级实现类（代码在后面）和，代码如下：

  ```
  @FeignClient (value = "ORG.LIXUE.HELLOWORLD", configuration=FeignConfig.class,

  fallback = HelloWorldServiceFallback.class)

  public interface HelloWorldClient extends HelloWorldService {

     

  }
  ```

  ​

- 创建服务降级类，继承与 HelloWorldClient 接口，该类不能和应用主类为同包及其子包内，并实现降级业务，代码如下：

  ```
  @Component

  public class HelloWorldServiceFallback implements HelloWorldClient {

          @Override

          public String hi() {

                  return "fallback hi";

          }

     

          @Override

          public String hi(String name) {

                  return "fallback hi name=" + name;

          }

     

          @Override

          public String hi(@RequestBody User user) {

                  return "fallback hi user=" + user;

          }

  }
  ```

  ​

- 创建配置类 FeingConfig ，并在配置类中创建 HelloWorldServiceFallback 类的 Bean，代码如下：

  ```
  @Configurable

  public class FeignConfig {

          @Bean

          public HelloWorldServiceFallback helloWorldServiceFallback() {

                  return new HelloWorldServiceFallback();

          }

  }
  ```

  ​

- 测试验证，启动服务注册中心和Feign客户端项目，不启动 ORG.LIXUE.HELLOWORLD 服务项目，访问服务会直接触发服务降级。

     

 

http://www.cnblogs.com/li3807/p/7502727.html