## SpringBoot-自定义Starter

 2017-11-07 |  [SpringBoot ](https://www.carryingcoder.com/categories/SpringBoot/)， [Starters](https://www.carryingcoder.com/categories/SpringBoot/Starters/)

#### 摘要

[Spring官方提供的starter](https://docs.spring.io/spring-boot/docs/1.5.8.RELEASE/reference/htmlsingle/#using-boot-starter)

[Spring官方自定义stater规范](https://docs.spring.io/spring-boot/docs/1.5.2.RELEASE/reference/htmlsingle/#boot-features-custom-starter-naming)

#### 自动配置

##### 注解

使用spring boot 的starter目的是什么？就是减少各种xml配置或者注解的使用，各种依赖的管理，使开发（~~CRUD~~）更纯粹，怎么让这一切实现，就用自动配置类`@Configuration`来实现，其他一些辅助注解包括不限于

- @Conditional系列
  - @ConditionalOnBean 配置了某个特定Bean
  - @ConditionalOnMissingBean 没有配置特定的Bean
  - @ConditionalOnClass Classpath里有指定的类
  - @ConditionalOnMissingClass Classpath里缺少指定的类
  - @ConditionalOnExpression 给定的Spring Expression Language（SpEL）表达式计算结果为true
  - @ConditionalOnJava Java的版本匹配特定值或者一个范围值
  - @ConditionalOnJndi 参数中给定的JNDI位置必须存在一个，如果没有给参数，则要有JNDI InitialContext
  - @ConditionalOnProperty 指定的配置属性要有一个明确的值
  - @ConditionalOnResource Classpath里有指定的资源
  - @ConditionalOnWebApplication 这是一个Web应用程序
  - @ConditionalOnNotWebApplication 这不是一个Web应用程序
- @AutoConfigureAfter或 @AutoConfigureBefore，指定配置的启用顺序，如果不指定`@Order`注意配置的写的顺序，写在前面，比如在一个类中实例化@bean，前面的bean会首先加载执行，结合这里的after、before、以及上面的missclass、missbean等配合使用。
- @AutoconfigureOrder和@Order 指定配置顺序，也是可以的。



##### 如何让spring boot找到配置类

spring boot会检查发布的jar中`META-INF/spring.factories`文件，里面列出了所有的自动配置类的包路径

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration = \
com.mycorp.libx.autoconfigure.LibXAutoConfiguration，\
com.mycorp.libx.autoconfigure.LibXWebAutoConfiguration
```

这样，这些类会被SpringBoot扫描识别（默认Spring框架实现会从声明@ComponentScan所在类的package进行扫描。）

了解这些注解知识，下面就容易了。

自动配置不展开介绍，后面会有专门文章，请关注，[SpringBoot-自动配置](https://www.carryingcoder.com/2017/11/07/SpringBoot-%E8%87%AA%E5%AE%9A%E4%B9%89Starter/)和[SpringBoot-自定义Conditional](https://www.carryingcoder.com/2017/11/07/SpringBoot-%E8%87%AA%E5%AE%9A%E4%B9%89Starter/)

#### 如何命名自定义的启动器

Spring 官方 Starter通常命名为spring-boot-starter-{name}如 spring-boot-starter-web， Spring官方建议：非官方Starter命名应遵循{name}-spring-boot-starter的格式。

注意：如果你的Starter需要参数配置，尽量用自定义的前缀，不要采用spring保留或者自用的，其他随便起前缀都行，尽量结合业务需要。

#### 标准自动配置组件

##### autoconfigure模块

包含自动配置的相关代码

在`resources/META-INF/`下创建`spring.factories`文件

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\xxx.xxxx.xxx
```

##### starter模块

仅仅是提供autoconfigure模块的所需要的依赖，一般情况，stater中引用autoconfigure，应用中，只需要引入starter模块就可以了，自动依赖了autoconfigure模块。

`resources/META-INF/spring.provides`文件提供了所需要的依赖。

```
provides: xmemcached-spring-boot-autoconfigure
```

有一个例外，比如公司中推行微服务，有好多组件进行自动配置，难到需要每个组件都需要写一套autoconfigure和starter吗？参考Spring的做法，提供一个包含N中自动配置类的模块,

```
 <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
</dependency>
```

接下来只要把不同的依赖，拆到各个starter中就好了，用哪一个就引入哪一个，所以公司内部采用这种方式。

当然官方也说了，如果你不想把这两个分开，你可以都写到一个模块中，也是可以的。

#### 例子

[SpirngBoot-自用Starter](http://carryingcoder.com/2017/10/20/SpringBoot-%E8%87%AA%E7%94%A8Starter/)

#### 总结

- 写starter，除了注意上面提到的，命名，配置顺序，还要注意用好各种条件注解，配置开关，保证配置在不需要的情况下，一定不会自动开启，比如，别人引用了你的配置类，就一定要配置类中的参数吗？那样真的会恶心死。
- 上面提到让spring boot扫描配置类，通过在`META-INF/spring.factories`中添加配置类，还有种方式，通过自定义注解开关，结合@Import注解或@ImportSelector注解，起到引用到具体配置类的作用，具体会在[SpringBoot-自动配置](https://www.carryingcoder.com/2017/11/07/SpringBoot-%E8%87%AA%E5%AE%9A%E4%B9%89Starter/)中介绍
- 一般常引入maven依赖，下面两个就够了

```
<dependencies>
       <!-- @ConfigurationProperties annotation processing (metadata for IDEs)
                生成spring-configuration-metadata.json类，需要引入此类-->
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-configuration-processor</artifactId>
           <optional>true</optional>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-autoconfigure</artifactId>
       </dependency>
   </dependencies>
```

第一个使用元数据，第二个提供自动配置功能







https://www.carryingcoder.com/2017/11/07/SpringBoot-%E8%87%AA%E5%AE%9A%E4%B9%89Starter/