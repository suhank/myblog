[TOC]



# spring里面的 @Import @Configuration和@Bean的用法和理解以及区别

## 1.@Import

- @Import注解在4.2之前只支持导入配置类
- 在4.2,@Import注解支持导入普通的java类,并将其声明成一个bean

演示java类

```
public class DemoService {
    public void doSomething(){
        System.out.println("everything is all fine");
    }

}
```

演示配置

```
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
@Configuration
@Import(DemoService.class)//在spring 4.2之前是不不支持的
public class DemoConfig {

}
```

运行

```
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.wisely.spring4_2.imp");
        DemoService ds = context.getBean(DemoService.class);
        ds.doSomething();

    }

}
```

输出结果

```
everything is all fine
```

 

## **2. @Bean:**

①注解@Bean的属性initMethod, destroyMethod 

②接口InitializingBean, DisposableBean

③注解@PostConstruct,@PreDestroy
都作用于同样的两个过程——初始化阶段和销毁阶段

1.1 定义

从定义可以看出，@Bean只能用于注解方法和注解的定义。

```
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
```

1.2 spring文档中对 @Bean的说明

The `@Bean` annotation is used to indicate that a method instantiates, configures and initializes a new object to be managed by the Spring IoC container.

For those familiar with Spring’s `<beans/>` XML configuration the `@Bean` annotation plays the same role as the `<bean/>`element. 

用@Bean注解的方法：会实例化、配置并初始化一个新的对象，这个对象会由spring IoC 容器管理。

实例：

```
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

}
```

相当于在 XML 文件中配置

```
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```

1.3 生成对象的名字：默认情况下用@Bean注解的方法名作为对象的名字。但是可以用 name属性定义对象的名字，而且还可以使用name为对象起多个名字。

```
@Configuration
public class AppConfig {

    @Bean(name = "myFoo")
    public Foo foo() {
        return new Foo();
    }

}
```

 

```
@Configuration
public class AppConfig {

    @Bean(name = { "dataSource", "subsystemA-dataSource", "subsystemB-dataSource" })
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }

}
```

## 3.@Component和@Configuration区别和联系

@Bean 一般和 @Component或者@Configuration 一起使用。

@Component和@Configuration不同之处

（1）`This method of declaring inter-bean dependencies only works when the @Bean method is declared within a@Configuration class. You cannot declare inter-bean dependencies using plain @Component classes.`



在 @Component 注解的类中不能定义 类内依赖的@Bean注解的方法。@Configuration可以。

@Configuration可理解为用spring的时候xml里面的<beans>标签

@Bean可理解为用spring的时候xml里面的<bean>标签





https://my.oschina.net/u/1266221/blog/799378