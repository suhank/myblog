   



# spring-boot 2.0 配置全局时间格式化

## 方式一: 在yml配置文件中添加以下配置

```java
spring:
   jackson:
      date-format: yyyy-MM-dd HH:mm:ss
      time-zone: GMT+8
      serialization:
         write-dates-as-timestamps: false 
```

write-dates-as-timestamps: 表示不返回时间戳，如果为 true 返回时间戳，如果这三行同时存在，以第3行为准即返回时间戳

## 方式二

创建一个WebConfig配置类并实现WebMvcConfigurationSupport类

```java
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
    /*处理返回的long类型前端无法显示问题*/
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        /**
         * 日期全局格式化
         * */
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));//GMT+8
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);

    }
} 
```



## 注意事项

如果你配置了WebConfig类并继承了WebMvcConfigurationSupport 那么上述配置就会失效





https://blog.csdn.net/b260681014/article/details/103265007