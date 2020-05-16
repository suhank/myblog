[TOC]



# spring mvc解决js处理Long类型丢失精度的问题

## 问题

当 Java 后台有个 Long 型值`20175678901234567`转到前台时，发现精度存在问题:

```
var batchNumber = 20175678901234567;  
console.log(batchNumber);  // 20175678901234570
```

也就是说，到了前端 JavaScript 接收后，值变成了`20175678901234570`。

## 前言

项目中很多时候都会用到json，常用的有fastjson，Jackson等等这些，有时候为了统一，我们通常就会约定使用某一种。 
不管使用哪种，Spring MVC返回个前段Long类型的数据时，js在获取数据时会丢失精度，从而造成数据的不准确，解决方式呢，就是在序列化时，会将Long类型的数据转化为String类型 

## Jackson方式

在使用Spring MVC默认的Jackson时，我们可以这么做：

### 继承方式

```java
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = 
        new MappingJackson2HttpMessageConverter();

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

} 
```

### 非继承方式

```java
/**
 * <B>Description:</B> 自定义http消息转换器 <br>
 * <B>Create on:</B> 2018/6/23 下午5:09 <br>
 *
 * @author xiangyu.ye
 */
@Bean
public HttpMessageConverters fastJsonHttpMessageConverters() {
    MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
            new MappingJackson2HttpMessageConverter();

    //<editor-fold desc="===>>long序列化转成string,解决js处理Long类型丢失精度的问题">
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
    simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
    simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
    objectMapper.registerModule(simpleModule);
    jackson2HttpMessageConverter.setObjectMapper(objectMapper);
    //</editor-fold>

    StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
    HttpMessageConverters httpMessageConverters = new HttpMessageConverters(jackson2HttpMessageConverter,stringHttpMessageConverter);
    return httpMessageConverters;
}
```



## fastjson方式

当然，有时候项目中也可能会统一约定使用了fastjson，然而Spring MVC中默认是使用了Jackson的 
在Spring Boot中将Jackson替换为fastjson一般会有两种方式：

第一种：

```Java
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverter() {
        return new HttpMessageConverters(new FastJsonHttpMessageConverter());
    }
} 
```

第二种：

```Java
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = 
        new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
    }
} 
```

替换成fastjson之后，对于精度丢失问题，我们可以这么去做：

```Java
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = 
        new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
    }
} 
```

我想对于另一种方式，怎么去做大家也该明白的吧，这里就不多说了。 
需要注意的是，这里使用的fastjson的版本为1.2.31，版本不同，方式略有不同。





版权声明：本文为博主原创文章，转载请注明出处。	https://blog.csdn.net/xufei_0320/article/details/78243527