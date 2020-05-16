# springboot 如何配置静态资源的地址与访问路径

静态资源，例如HTML文件、JS文件，设计到的Spring Boot配置有两项，一是“spring.mvc.static-path-pattern”，一是“spring.resources.static-locations”，很多人都难以分辨它们之间的差异，所以经常出现的结果就是404错误，无法找到静态资源。

## 1. “spring.mvc.static-path-pattern”

spring.mvc.static-path-pattern代表的含义是我们应该以什么样的路径来访问静态资源，换句话说，只有静态资源满足什么样的匹配条件，Spring Boot才会处理静态资源请求，以官方配置为例：

```
#   这表示只有静态资源的访问路径为/resources/**时，才会处理请求
spring.mvc.static-path-pattern=/resources/**， 
```

假定采用默认的配置端口，那么只有请求地址类似于“<http://localhost:8080/resources/jquery.js>”时，Spring Boot才会处理此请求，处理方式是将根据模式匹配后的文件名查找本地文件，那么应该在什么地方查找本地文件呢？这就是“spring.resources.static-locations”的作用了。

## 2. “spring.resources.static-locations”

“spring.resources.static-locations”用于告诉Spring Boot应该在何处查找静态资源文件，这是一个列表性的配置，查找文件时会依赖于配置的先后顺序依次进行，默认的官方配置如下：

```
spring.resources.static-locations=classpath:/static,classpath:/public,classpath:/resources,classpath:/META-INF/resources1
```

继续以上面的请求地址为例，“<http://localhost:8080/resources/jquery.js>”就会在上述的四个路径中依次查找是否存在“jquery.js”文件，如果找到了，则返回此文件，否则返回404错误。

## 3. 静态资源的Bean配置

从上面可以看出，“spring.mvc.static-path-pattern”与“spring.resources.static-locations”组合起来演绎了nginx的映射配置，如果熟悉Spring MVC，那么理解起来更加简单，它们的作用可以用Bean配置表示，如下：

```
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public-resources/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
    }

} 
```

或者等同与以下的XML。

```
<mvc:resources mapping="/resources/**" location="/public-resources/">
    <mvc:cache-control max-age="3600" cache-public="true"/>
</mvc:resources> 
```

## 结论

“spring.mvc.static-path-pattern”用于阐述HTTP请求地址，而“spring.resources.static-locations”则用于描述静态资源的存放位置。





https://blog.csdn.net/yiifaa/article/details/78299052