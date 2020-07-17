# springmvc json序列化和反序列化自定义配置 fastjson jackson配置

## fastjson 配置

```java
package com.kfit.common.config.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {

   //spirngmvc json序列化指定
   @Override
   public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
       /*
        * 1、需要先定义一个 convert 转换消息的对象;
        * 2、添加fastJson 的配置信息，比如：是否要格式化返回的json数据;
        * 3、在convert中添加配置信息.
        * 4、将convert添加到converters当中.
        *
        */

       // 1、需要先定义一个 convert 转换消息的对象;
       FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

       //2、添加fastJson 的配置信息，比如：是否要格式化返回的json数据;
       FastJsonConfig fastJsonConfig = new FastJsonConfig();
       fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
               SerializerFeature.WriteMapNullValue);

       //3、在convert中添加配置信息.
       fastConverter.setFastJsonConfig(fastJsonConfig);

       //4、将convert添加到converters当中.  注意添加在列表的前面才能生效
       converters.add(0,fastConverter);
   }
 


}

```

## jackson配置

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //解决中文乱码
        converters.add(new StringHttpMessageConverter(
                StandardCharsets.UTF_8));

        //jackson自定义序列化配置
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));//日期全局格式化
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));//时区
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//解决没有匹配到对应字段报错：springmvc @RequestBody问题：Unrecognized field , not marked as ignorable
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);

        //将convert添加到converters当中.  注意添加在列表的前面才能生效
        converters.add(0, jackson2HttpMessageConverter);
    }
}
```

————————————————
版权声明：本文为CSDN博主「JethroShen」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/b260681014/java/article/details/103265007