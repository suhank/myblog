# springboot 多语言的处理.md

Spring-boot 实现多语言切换，很简单：

加入一个配置类就可以

```java
package net.watermelon;  
  
import java.util.Locale;  
  
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.ComponentScan;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.web.servlet.LocaleResolver;  
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;  
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;  
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;  
import org.springframework.web.servlet.i18n.SessionLocaleResolver;  
  
  
/** 
 * 设置切换语言的功能 
 * @author samsung 
 * 
 */  
@Configuration  
@EnableAutoConfiguration  
@ComponentScan  
public class Application extends WebMvcConfigurerAdapter {  
    public static void main(String[] args) {  
        SpringApplication.run(Application.class, args);  
    }  
  
    @Bean  
    public LocaleResolver localeResolver() {  
        SessionLocaleResolver slr = new SessionLocaleResolver();  
        slr.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);  
        return slr;  
    }  
  
    @Bean  
    public LocaleChangeInterceptor localeChangeInterceptor() {  
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();  
        lci.setParamName("lang");  
        return lci;  
    }  
  
    @Override  
    public void addInterceptors(InterceptorRegistry registry) {  
        registry.addInterceptor(localeChangeInterceptor());  
    }  
}   
```

   在页面上，如下的切换方式：        

```
<a href="?lang=en_US" > 英语</a>  
<a href="?lang=zh_CN" > 中文</a>  
```

点击以后切换 缺省的语言。