# Spring @Async 应用于出现循环依赖的 Bean 报错的解决方案

当在出现循环依赖的 Spring Bean 中使用 `@Async` 时，会报以下错误：

```
Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'a': Bean with name 'a' has been injected into other beans [b] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
```

该问题出现的原因是：在正常加载完循环依赖后，因为 `@Async` 注解的出现，又需将该 Bean 代理一次，然后 Spring 发现该 Bean 已经被其他对象注入，且注入的是没被代理过的版本，于是报错。这个问题也会出现在使用 AOP 等需要代理原类的场景下。

StackOverflow 有人提到可以使用 `@Lazy` 或者 `@ComponentScan(lazyInit = true)` 解决该问题，经过我实验，只有在注入的 `@Autuwired` 字段添加 `@Lazy` 才能生效，单纯给类添加 `@Lazy` 并无意义。

```java
    @Lazy
    @Autowired
    private XxxService xxxService;
```

[Spring 官方文档](https://www.oschina.net/action/GoToLink?url=http%3A%2F%2Fdocs.spring.io%2Fspring%2Fdocs%2F4.3.x%2Fspring-framework-reference%2Fhtml%2Fbeans.html%23beans-setter-injection) 提示，通过在循环依赖的每个部分都使用 Setter 注入的方式可以解决该问题，但出现循环依赖并不是值得推荐的做法。

当然，因为构造器注入本身就不允许循环依赖，所以这里不讨论它。

具体写法请参考 [Spring 依赖注入的三种方式](https://my.oschina.net/tridays/blog/805098) 。





https://my.oschina.net/tridays/blog/805111