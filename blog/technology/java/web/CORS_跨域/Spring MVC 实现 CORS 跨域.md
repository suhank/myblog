[TOC]



# Spring MVC 实现 CORS 跨域

关注

2017.10.07 23:50* 字数 1579 阅读 1251评论 0喜欢 0

> 前言：众所周知，出于安全考虑，XMLHttpReqeust 对象发起的 HTTP 请求必须满足同源策略（same-origin policy）的规定，否则浏览器将会对该请求进行限制。虽然同源策略的实现带来的Web安全的提升，但是却为一些正规的跨域需求带来不便，故此衍生出了若干种绕开同源策略的跨域方案，其中 JSONP 就是使用的比较多的方案，但 JSONP 是一个非官方的跨域协议同时也只支持 GET 请求，而后来 W3C 推出 CORS 协议相比 JSONP 支持更多的方法也允许使用普通 XMLHttpRequest 发送请求，所以我们有理由使用更加现代的跨域方案。关于 CORS 的详细内容可以阅读文章[HTTP访问控制（CORS）](https://link.jianshu.com/?t=https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Access_control_CORS) 。
>
> 下面我将介绍如何在 Spring MVC 中实现 CORS 跨域方案，但由于 Spring MVC 在 4.2 版本中才开始原生支持CORS ，故我将介绍两种实现方法，一种是借助 Servlet Filter 实现，而另一种则是直接使用 Spring MVC 的 @CrossOrigin 注解实现。

## 使用Servlet Filter 实现 CORS

 CORS 跨域协议的实现是通过使用一组 HTTP 首部字段实现的，其核心是服务端返回响应中的 `Access-Control-Allow-Origin` 首部字段，这个字段来声明服务端允许来自哪些源的请求访问该资源，浏览器可以根据这个响应首部字段来判断是否可以放行跨域请求。因此要实现 CORS ，我们可以在项目中声明一个 Filter 过滤器为响应加上需要的 `Access-Control-Allow-*`首部。

1. 首先创建一个实现 `javax.servlet.Filter` 接口的过滤器

   ```
   package com.ken.localserver.filter;

   import javax.servlet.*;
   import javax.servlet.http.HttpServletResponse;
   import java.io.IOException;

   public class CORSFilter implements Filter{

       @Override
       public void init(FilterConfig filterConfig) throws ServletException {

       }

       @Override
       public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
           System.out.println("work");
           HttpServletResponse response = (HttpServletResponse) servletResponse;
           response.setHeader("Access-Control-Allow-Origin", "*");
           response.setHeader("Access-Control-Allow-Methods", "POST, GET");
           response.setHeader("Access-Control-Max-Age", "3600");
           response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
           filterChain.doFilter(servletRequest, servletResponse);
       }

       @Override
       public void destroy() {

       }
   }
   ```

2. 然后在 `web.xml` 文件中配置该过滤器

   ```
   <!-- CORS Filter -->
   <filter>
       <filter-name>CORSFilter</filter-name>
       <filter-class>com.ken.localserver.filter.CORSFilter</filter-class>
   </filter>
   <filter-mapping>
       <filter-name>CORSFilter</filter-name>
       <url-pattern>/*</url-pattern>
   </filter-mapping>
   ```

到这里，就可以简单的实现 CORS 跨域请求了，上面的过滤器将会为所有请求的响应加上`Access-Control-Allow-*`首部，换言之就是允许来自任意源的请求来访问该服务器上的资源。而在实际开发中可以根据需要开放跨域请求权限以及控制响应头部等等。

## 使用 Spring MVC 的 @CrossOrigin 注解实现 CORS 跨域

 Spring Framework 从 4.2 版本中开始原生支持 CORS，相比上面的需要配置 Filter 过滤器的实现方式，使用原生的 `@CrossOrigin` 注解的实现方式来得更加简单。

 要启用 Spring MVC 的 CORS 支持十分简单，只需要添加一个`@CrossOrigin`注解即可，根据添加注解位置可以控制配置的细粒度，如：允许这个Controller 还是特定的方法

### 在方法上使用 `@CrossOrigin` 注解

```
@RestController
@RequestMapping("/account")
public class AccountController {

  @CrossOrigin
  @GetMapping("/{id}")
  public Account retrieve(@PathVariable Long id) {
      // ...
  }

  @DeleteMapping("/{id}")
  public void remove(@PathVariable Long id) {
      // ...
  }
}
```

上例中将允许对`retrieve()`方法的跨域访问。默认情况下， `@CrossOrigin` 注解将允许来自任意的源站以及任意 HTTP 请求方法的请求访问。

### 在Controller 上使用 `@CrossOrigin` 注解

```
@CrossOrigin(origins = "http://domain2.com", maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {

  @GetMapping("/{id}")
  public Account retrieve(@PathVariable Long id) {
      // ...
  }

  @DeleteMapping("/{id}")
  public void remove(@PathVariable Long id) {
      // ...
  }
}
```

当 `@CrossOrigin` 注解声明在Controller 时，将允许对 Controller 下得所有方法得跨域请求。另外，如果不满足与 `@CrossOrigin` 注解得默认属性，我们可以自定义配置 CORS 属性。

### 同时在 Controller 和方法上使用 `@CrossOrigin` 注解

```
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {

  @CrossOrigin(origins = "http://domain2.com")
  @GetMapping("/{id}")
  public Account retrieve(@PathVariable Long id) {
      // ...
  }

  @DeleteMapping("/{id}")
  public void remove(@PathVariable Long id) {
      // ...
  }
}
```

如果同时在 Controller 和方法上都有使用`@CrossOrigin` 注解，那么在具体某个方法上的 CORS 属性将是两个注解属性合并的结果，如果属性的设置发生冲突，那么Controller 上的主机属性将被覆盖。

### 全局 CORS 配置

在某些情况，我们并不需要针对不同的URL来配置不同 CORS 属性，那么我们可以通过一个全局的 CORS 配置来避免单独注解配置的麻烦。

#### 基于 JavaConfig

```
@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}
```

在以及 Java 配置的配置方式中，我们只需要简单加入以上的代码就可以配置全局的 CORS。默认情况下，将允许来自任意源站以及任意 HTTP 请求方法的请求访问。

```
@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://domain2.com")
                .allowedMethods("PUT", "DELETE")
                .allowedHeaders("header1", "header2", "header3")
                .exposedHeaders("header1", "header2")
                .allowCredentials(false).maxAge(3600);
    }
}
```

同时，我们也可以根据自己的需要 CORS 的相关属性进行配置，配置的方式如上面的代码所示。

#### 基于 XML 配置文件

```
<mvc:cors>
    <mvc:mapping path="/**" />
</mvc:cors>
```

基于 XML 配置文件的配置方式也是十分简单，只需要 Spring 的Context 配置文件中加入上面的 `<mvc:cors>` 即可。同样的，默认情况下将允许来自任意源站以及任意 HTTP 请求方法的请求访问。

```
<mvc:cors>

    <mvc:mapping path="/api/**"
        allowed-origins="http://domain1.com, http://domain2.com"
        allowed-methods="GET, PUT"
        allowed-headers="header1, header2, header3"
        exposed-headers="header1, header2" allow-credentials="false"
        max-age="123" />

    <mvc:mapping path="/resources/**"
        allowed-origins="http://domain1.com" />

</mvc:cors>
```

另外，也可以通过上面的方式来自定义 CORS 属性。

## Reference

- [spring-rest-ajax-and-cors](https://link.jianshu.com/?t=https://www.javacodegeeks.com/2014/07/spring-rest-ajax-and-cors.html)
- [CORS with Spring](https://link.jianshu.com/?t=http://www.baeldung.com/spring-cors)
- [cors-support-in-spring-framework](https://link.jianshu.com/?t=https://spring.io/blog/2015/06/08/cors-support-in-spring-framework)





https://www.jianshu.com/p/9203e9b14465