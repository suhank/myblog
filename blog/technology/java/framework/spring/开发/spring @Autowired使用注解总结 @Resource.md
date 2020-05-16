[TOC]



# @Autowired使用注解总结 @Resource

## 1.使用方法

### 1.1 作用在构造器上

```java
public class MovieRecommender {
 private final CustomerPreferenceDao customerPreferenceDao;
 @Autowired
 public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
 this.customerPreferenceDao = customerPreferenceDao;
 }
 // ...
}
```

注意：

> As of Spring Framework 4.3, the @Autowired constructor is no longer necessary if the target
> bean only defines one constructor. If several constructors are available, at least one must be
> annotated to teach the container which one it has to use.

如果使用该注解的类有一个构造器，则 `is no longer necessary` ，没有必要再单独放置一个@Autowired注解了，Spring框架会自动帮助我们完成注入的功能。但是如果该该有多个注解 `several constructors are available`，这个时候我们就需要选择一个构造函数，并标注上该注解@Autowired了。

### 1.2 使用在setter方法上

```java
public class SimpleMovieLister {
 private MovieFinder movieFinder;
 @Autowired
 public void setMovieFinder(MovieFinder movieFinder) {
 this.movieFinder = movieFinder;
 }
 // ...
}
```

### 1.3 使用在域属性上

```java
public class MovieRecommender {
 private final CustomerPreferenceDao customerPreferenceDao;
 @Autowired
 private MovieCatalog movieCatalog;
 @Autowired
 public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
 this.customerPreferenceDao = customerPreferenceDao;
 }
 // ...
}
```

### 1.4 使用在任意方法名和参数的普通方法上



```java
public class MovieRecommender {
 private MovieCatalog movieCatalog;
 private CustomerPreferenceDao customerPreferenceDao;
 @Autowired
 public void prepare(MovieCatalog movieCatalog,
 CustomerPreferenceDao customerPreferenceDao) {
 this.movieCatalog = movieCatalog;
 this.customerPreferenceDao = customerPreferenceDao;
 }
 // ...
}
```

### 1.5 使用在域属性数组上

> It is also possible to provide all beans of a particular type from the ApplicationContext by adding
> the annotation to a field or method that expects an array of that type:



```java
public class MovieRecommender {
 @Autowired
 private MovieCatalog[] movieCatalogs;
 // ...
}
```

### 1.6 使用在java集合类型上



```java
public class MovieRecommender {
 private Set<MovieCatalog> movieCatalogs;
 @Autowired
 public void setMovieCatalogs(Set<MovieCatalog> movieCatalogs) {
 this.movieCatalogs = movieCatalogs;
 }
 // ...
}
```

小提示：如果当初定义的bean上有@Order注解或者标准注解@Priority的话，那么注入的集合就是有序的，这个元素顺序是当初注解定义的优先顺序。

> Your beans can implement the org.springframework.core.Ordered interface or either use
> the @Order or standard @Priority annotation if you want items in the array or list to be sorted
> into a specific order.

#### 使用在Map上

> Even typed Maps can be autowired as long as the expected key type is String. The Map values will
> contain all beans of the expected type, and the keys will contain the corresponding bean names:

如果注入的Map中Key为`String`类型，并且代表每个bean的name,value为指定的bean类型，则也可以直接注入.



```java
public class MovieRecommender {
 private Map<String, MovieCatalog> movieCatalogs;
 @Autowired
 public void setMovieCatalogs(Map<String, MovieCatalog> movieCatalogs) {
 this.movieCatalogs = movieCatalogs;
 }
 // ...
}
```

### 1.9 使用在Spring内部的接口上

> You can also use @Autowired for interfaces that are well-known resolvable
> dependencies: BeanFactory, ApplicationContext, Environment, ResourceLoader,
> ApplicationEventPublisher, and MessageSource. These interfaces and their extended
> interfaces, such as ConfigurableApplicationContext or ResourcePatternResolver, are
> automatically resolved, with no special setup necessary.

该注解同样可以使用在`BeanFactory`, `ApplicationContext`, `Environment`, `ResourceLoader`,
`ApplicationEventPublisher` 这些我们都知道而且很重要的接口上。

### 1.8 required属性

> @Autowired’s required attribute is recommended over the `@Required
> annotation. The required attribute indicates that the property is not required for autowiring purposes, the property is ignored if it cannot be autowired. @Required, on the other hand, is
> stronger in that it enforces the property that was set by any means supported by the container. If
> no value is injected, a corresponding exception is raised.

@Autowired注解的required属性值可以为true和false。如果为true的话，则在进行注入的时候，如果找不到要注入的类，则会抛错。如果为false，则表示不是强制必须能够找到相应的类，无论是否注入成功，都不会抛错。

另外，当利用该注解注入相应的对象（其实就是类初始化为一个对象的过程），会调用该对象的构造方法，如果该对象有多个构造方法，则Spring就会“贪心”地调用参数最多的那个构造方法。相关的官方说明如下：

> Only one annotated constructor per-class can be marked as required, but multiple non-required
> constructors can be annotated. In that case, each is considered among the candidates and Spring
> uses the greediest constructor whose dependencies can be satisfied, that is the constructor that
> has the largest number of arguments.

## 2.其他注入注解

- @Inject (javax.inject JSR330 (Dependency Injection for Java))
- @Resource (javax.annotation JSR250 (Common Annotations for Java))

这里引用参考文章的区别总结：

@Autowired和@Resource

- autowired by type
- 可以 通过@Qualifier 显式指定 autowired by qualifier name（非集合类。注意：不是autowired by bean name！）
- 如果 autowired by type 失败（找不到或者找到多个实现），则退化为autowired by field name（非集合类）

@Resource

- 默认 autowired by field name
- 如果 autowired by field name失败，会退化为 autowired by type
- 可以 通过@Qualifier 显式指定 autowired by qualifier name
- 如果 autowired by qualifier name失败，会退化为 autowired by field name。但是这时候如果 autowired by field name失败，就不会再退化为autowired by type了。

更多具体细节参考这篇文章: [Spring各种依赖注入注解的区别](https://link.jianshu.com/?t=http://blog.arganzheng.me/posts/difference-between-inject-resource-autowired-anotation.html)

## 说明

1. 这篇总结中所引用的英文描述和代码片段全部来自与Spring的官方文档：Spring Framework Reference Documentation 4.3.3 RELEASE
2. 参考文章:[Spring各种依赖注入注解的区别](https://link.jianshu.com/?t=http://blog.arganzheng.me/posts/difference-between-inject-resource-autowired-anotation.html)





https://www.jianshu.com/p/ee456139f949