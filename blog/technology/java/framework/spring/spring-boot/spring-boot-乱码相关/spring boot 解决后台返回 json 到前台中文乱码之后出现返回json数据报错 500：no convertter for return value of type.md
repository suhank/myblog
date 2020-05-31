# spring boot 解决后台返回 json 到前台中文乱码之后出现返回json数据报错 500：no convertter for return value of type



## 问题描述

- spring Boot 中文返回给浏览器乱码 解析成问号？？ fastJson jackJson
- spring boot 新增配置解决后台返回 json 到前台中文乱码之后,出现返回json数据报错：no convertter for return value of type
- 注释掉解决中文乱码的问题之后返回对象json正常

## 解决中文乱码的配置

```java
@Configuration
@EnableWebMvc
@ComponentScan
public class MvcConfiguration extends WebMvcConfigurerAdapter {
    @Bean
    public HttpMessageConverter<String> responseBodyConverter(){
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return converter;
    }
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        //解决中文乱码
        converters.add(responseBodyConverter());
        //解决 添加解决中文乱码后 上述配置之后，返回json数据直接报错 500：no convertter for return value of type
        converters.add(messageConverter());
    }
}
```

#### 除了上述配置之后还有添加MappingJackson2HttpMessageConverter

```java
 @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getObjectMapper());
        return converter;
    }
```

**注意:responseBodyConverter和MappingJackson2HttpMessageConverter如果分开配置要确保前者不被覆盖**，不然就会出现返回springboot返回json正常，但是返回中文乱码，或者返回中文不乱吗，但是返回对象或者json异常。

## 解决springboot范湖中文乱码和返回json 500错误的完整代码

```java
/**
 * spring boot 解决后台返回 json 到前台出现中文乱码的问题
 * 在线助手博客 https://www.it399.com/blog/index.jsp
 */
@Configuration
@EnableWebMvc
@ComponentScan
public class MvcConfiguration extends WebMvcConfigurerAdapter {
    @Bean
    public HttpMessageConverter<String> responseBodyConverter(){
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return converter;
    }
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getObjectMapper());
        return converter;
    }
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        //解决中文乱码
        converters.add(responseBodyConverter());
        //解决 添加解决中文乱码后 上述配置之后，返回json数据直接报错 500：no convertter for return value of type
        converters.add(messageConverter());
    }
}
```







https://www.it399.com/blog/web/201805081017