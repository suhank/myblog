# springcloud--zuul跨域访问问题

 

springcloud微服务框架，是一组组件，eureka服务注册中心，zuul路由等等

一般都是在zuul上配好url路径映射到各个服务，所以对外都是访问zuul服务的端口，但是在web服务设置了跨域的Interceptor后没有起作用(我的chrome浏览器，postman正常)，关掉web服务，依然有返回http

最后确定是在zuul上没有设置跨域header

跨域时，可能会先OPTIONS访问，zuul直接返回了，所以需要给zuul添加跨域header

```java
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.cors.CorsConfiguration

@Bean
public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    List<String> origins = new ArrayList<>();
    origins.add("*");
    config.addAllowedOrigin(origins);
    List<String> headers = new ArrayList<>();
    headers.add("*");
    config.addAllowedHeader(headers);
    List<String> methods = new ArrayList<>();
    methods.add("OPTIONS");
    methods.add("GET");
    methods.add("POST");
    config.addAllowedMethod(methods);
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}
```





https://blog.csdn.net/c5113620/article/details/79132148