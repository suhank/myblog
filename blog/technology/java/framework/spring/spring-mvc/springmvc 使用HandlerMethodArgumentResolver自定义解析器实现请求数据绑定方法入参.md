 

[TOC]



# SpringMVC 使用HandlerMethodArgumentResolver自定义解析器实现请求数据绑定方法入参

## 问题

首先，我们遇到的问题是…当我们需要在controller中频繁的从session中获取数据，比如向下面这样↓

我在controller中需要从session中获取user对象，那么可能你会想到在controller里面或者其他类里面写这样的代码，然后在controller里面调用….

```java
public User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("user");
} 
```

总感觉特别的不好…

现在如果我们看了下面介绍的HandlerMethodArgumentResolver自定义解析器实现的请求数据绑定方法入参，你就会看到像下面的代码只需要一个注解就能解决上面的问题↓

```java
@RequestMapping("/index")
public String index(@MyUser User user,ModelMap modelMap){
    logger.info(user.getUsername()+"---------------------------");
    return "login";
} 
```

## 用HandlerMethodArgumentResolver解决

首先，我们需要知道一点的就是SpringMVC的工作流程，SpringMVC的DispatchServlet会根据请求来找到对应的HandlerMapping，最终Spring会选择用RequestMappingHandlerMapping，然后根据RequestMappingHandlerMapping来获取HandlerMethod，然后来找支持的HandlerMethodArgumentResolver来处理对应controller的方法的入参。

首先，我们需要做的就是创建一个Annotation↓ 具体怎么创建自定义Annotation可以看我的[自定义Annotation](http://blog.csdn.net/u013632755/article/details/45483677)

```
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyUser {
} 
```

然后我们要做的就是创建一个MyUserMethodArgumentResolver这个类来实现HandlerMethodArgumentResolver这个接口

```java
public class MyUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
@Override
public boolean supportsParameter(MethodParameter methodParameter) {
    Class<MyUser> userClass =MyUser.class;
    if(methodParameter.hasParameterAnnotation(userClass)){
        return true;
    }
    return false;
}

@Override
public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer   modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return nativeWebRequest.getAttribute("user", NativeWebRequest.SCOPE_REQUEST);;
        //或者这里你也可以直接返回自己创建的User对象用于测试
        /*
            User user = new User();
            user.setUsername("yangpeng");
            return user;
        */
    }
} 
```

Spring默认会注册多个HandlerMethodArgumentResolver来处理不同的请求，Spring会根据HandlerMethodArgumentResolver的supportsParameter()方法来判断是否支持处理当前请求。 
第一个supportsParameter方法是判断这个MyUserMethodArgumentResolver是否支持传入的MethodParameter对象。 
第二个resolveArgument方法是处理具体的需要绑定到方法入参，返回的对象就是需要绑定的对象，这里我是直接从session里面获取了一个user的对象直接返回，或者你也可以在这里直接创建一个User对象然后返回用于测试是一样的。

接下来就是在spring-mvc.xml中配置了↓

```xml
<mvc:annotation-driven>
   <mvc:argument-resolvers>
        <bean class="com.yp.code.common.bind.method.MyUserMethodArgumentResolver"></bean>
    </mvc:argument-resolvers>
</mvc:annotation-driven> 
```

 在springboot中配置了

```java
import org.linlinjava.litemall.wx.annotation.support.LoginUserHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class UseConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new MyUserMethodArgumentResolver());
    }
}

```

最后就是使用创建的@MyUser这个Annotation来让SpringMVC自动的帮你绑定到Controller的方法里面了↓

```java
@RequestMapping("/index")
public String index(@MyUser User user,ModelMap modelMap){
    System.out.println(user.getUsername());
    return "login";
} 
```

这样就非常优雅的解决了上面的问题。

还可以参考这个博客看看：<http://blog.csdn.net/truong/article/details/30971317>





https://blog.csdn.net/u013632755/article/details/49891035