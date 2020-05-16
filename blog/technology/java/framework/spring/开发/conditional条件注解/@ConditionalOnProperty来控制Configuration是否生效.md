[TOC]



# @ConditionalOnProperty来控制Configuration是否生效

## 1. 简介

> **Spring Boot**通过**@ConditionalOnProperty**来控制**Configuration**是否生效

## 2. 说明

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {

    String[] value() default {}; //数组，获取对应property名称的值，与name不可同时使用  
  
    String prefix() default "";//property名称的前缀，可有可无  
  
    String[] name() default {};//数组，property完整名称或部分名称（可与prefix组合使用，组成完整的property名称），与value不可同时使用  
  
    String havingValue() default "";//可与name组合使用，比较获取到的属性值与havingValue给定的值是否相同，相同才加载配置  
  
    boolean matchIfMissing() default false;//缺少该property时是否可以加载。如果为true，没有该property也会正常加载；反之报错  
  
    boolean relaxedNames() default true;//是否可以松散匹配，至今不知道怎么使用的  
} 
}
```

## 3. 使用方法

> 通过其两个属性**name**以及**havingValue**来实现的，其中**name**用来从**application.properties**中读取某个属性值。
> **如果该值为空，则返回false**;
> **如果值不为空，则将该值与havingValue指定的值进行比较，如果一样则返回true;否则返回false。**
> **如果返回值为false，则该configuration不生效；为true则生效。**

## 4. code

```java
@Configuration
//在application.properties配置"mf.assert"，对应的值为true
@ConditionalOnProperty(prefix="mf",name = "assert", havingValue = "true")
public class AssertConfig {
    @Autowired
    private HelloServiceProperties helloServiceProperties;
    @Bean
    public HelloService helloService(){
        HelloService helloService = new HelloService();
        helloService.setMsg(helloServiceProperties.getMsg());
        return helloService;
    }
}
```

## 5. 收获

**springboot注解丰富，我们可以利用好这些注解来实现我们自定义的starter配置，减少硬编码的校验，降低组件间的耦合性!!!**



https://www.jianshu.com/p/68a75c093023