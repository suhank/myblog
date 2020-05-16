[TOC]



# springboot多种自定义错误页面方式

在项目中为了友好化，对于错误页面，我们常常会使用自定义的页面。SSM框架组合时代，我们通常通过拦截或者在web.xml中设置对于错误码的错误页面，然而到了SpringBoot，web.xml消失了，SpringBootServletInitializer初始化servlet代替了web.xml。难道要再把web.xml加回去？这样虽然可以做到，但并不合理。下面提供了多种在SpringBoot中实现自定义错误页面的方法。

## 以前web.xml方式

先来看下在web.xml中配置错误页面的方式：

```
<error-page>
    <error-code>404</error-code>
    <location>/error/404.jsp</location>
</error-page> 
```

## SpringBoot中实现方式

在SpringBoot后，可以通过如下几种方式实现自定义错误页面。

### **1.实现EmbeddedServletContainerCustomizer的bean**

适合内嵌服务器，先在controller中定义我们的错误页面Mapping，通过在配置类中实现EmbeddedServletContainerCustomizer的bean，加入对应状态码的错误页面。注意这种方式在打成war后，供外部tomcat使用时，将会失效。 
定义错误页面：

```java
   @RequestMapping(value = "/error/{code}")
    public String error(@PathVariable int code, Model model) {
        String pager = "/content/error-pager";
        switch (code) {
            case 404:
                model.addAttribute("code", 404);
                pager = "/content/error-pager";
                break;
            case 500:
                model.addAttribute("code", 500);
                pager = "/content/error-pager";
                break;
        }
        return pager;
    } 
```

在配置类中加入EmbeddedServletContainerCustomizer：

```java
  /**
     * 配置默认错误页面（仅用于内嵌tomcat启动时）
     * 使用这种方式，在打包为war后不起作用
     *
     * @return
     */  
@Bean
public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
            container.addErrorPages(error404Page, error500Page);
        }; 
```

### 2.通过拦截器方式

适合内嵌Tomcat或者war方式。

```java
/**
 * @author hgs
 * @version ErrorPageInterceptor.java, v 0.1 2018/03/04 20:52 hgs Exp $
 * <p>
 * 错误页面拦截器
 * 替代EmbeddedServletContainerCustomizer在war中不起作用的方法
 */
@Component
public class ErrorPageInterceptor extends HandlerInterceptorAdapter {
    private List<Integer> errorCodeList = Arrays.asList(404, 403, 500, 501);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
        Exception {
       if (errorCodeList.contains(response.getStatus())) {
            response.sendRedirect("/error/" + response.getStatus());
            return false;
        }
        return super.preHandle(request, response, handler);
    }
} 
```

在配置类中添加拦截

```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
 @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(errorPageInterceptor);//.addPathPatterns("/action/**", "/mine/**");默认所有
        super.addInterceptors(registry);
    }
} 
```

### **3.自定义静态error页面方法**

在resource/templates下添加error.html页面，springBoot会自动找到该页面作为错误页面，适合内嵌Tomcat或者war方式。SpringBoot错误视图提供了以下错误属性：

- timestamp：错误发生时间；
- status：HTTP状态吗；
- error：错误原因；
- exception：异常的类名；
- message：异常消息（如果这个错误是由异常引起的）；
- errors：BindingResult异常里的各种错误（如果这个错误是由异常引起的）；
- trace：异常跟踪信息（如果这个错误是由异常引起的）；
- path：错误发生时请求的URL路径。

SpringBoot使用的前端框架模板不同，页面的名称也有所不同：

- 实现Spring的View接口的Bean，其ID需要设置为error（由Spring的BeanNameViewResolver所解析）；
- 如果配置了Thymeleaf，则需命名为error.html的Thymeleaf模板；
- 如果配置了FreeMarker，则需命名为error.ftl的FreeMarker模板；
- 如果配置了Velocity，则需命名为error.vm的Velocity模板；
- 如果是用JSP视图，则需命名为error.jsp的JSP模板。

**Thymeleaf实例：**

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}"></title>
</head>
<body class="layout">
<div class="wrap">
    <!-- S top -->
    <div th:include="/header/module-header::module-header"></div>
    <!-- S 内容 -->
    <div class="panel-l container clearfix">
        <div class="error">
            <p class="title"><span class="code" th:text="${status}"></span>非常抱歉，没有找到您要查看的页面</p>
            <a href="/" class="btn-back common-button">返回首页
                <img class="logo-back" src="/img/back.png">
            </a>
            <div class="common-hint-word">
                <div th:text="${#dates.format(timestamp,'yyyy-MM-dd HH:mm:ss')}"></div>
                <div th:text="${messages}"></div>
                <div th:text="${error}"></div>
            </div>
        </div>
    </div>
</div>
</div>
</body>
</html> 
```

**对于外部Tomcat第三中方案是比较推荐的一种实现方式，但不够灵活，我们不好定义自己的属性，如果想对其做相应修改，可以参见源码BasicErrorController，通过继承AbstractErrorController，并重写errorHtml方法，达到自己想要的效果。在内嵌Tomcat时，第一种推荐使用，更具灵活性。**

## **应用实践**

实现效果可以访问如下地址查看： 
<http://www.lunshuge.com/err>





https://blog.csdn.net/IT_faquir/article/details/79521417