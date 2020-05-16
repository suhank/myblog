# SpringMVC通过静态方法获得请求

1.在Web.xml中加入

```xml
<listener>
    <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
</listener>
```

2.调用代码

```java
public static HttpServletRequest getRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
}
```

