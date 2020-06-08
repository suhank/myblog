[TOC]



# java Spring Boot Druid连接池数据源配置和控制台页面开启

2017-10-13 10:23:38

## 需求

  druid作为数据源的一名后起之秀，凭借其出色的性能，渐渐被大家使用。当然还有他的监控页面也有这非常大的作用。但是监控页面往往包含了很多隐私的数据信息，所以需要将其保密，所以可以为监控页面添加一个用户名和密码，确保其安全。

## 配置类如下：

```java
package com.example.spring.druid;

import java.util.HashMap;

import javax.servlet.Servlet;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

/**
 * @DESC druid配置类，会被springboot扫描然后将相应的service加到容器中
 * @author guchuang
 *
 */
@Configuration
public class DruidConfiguration {
    private static Logger log = LoggerFactory.getLogger(DruidConfiguration.class);
    @Bean  
    @ConfigurationProperties(prefix="spring.datasource")  
    public DataSource druid() {  
        return new DruidDataSource();  
    }  
    /**
     * 配置druid管理页面的访问控制
     * 访问网址: http://127.0.0.1:8080/druid
     * @return
     */
    @Bean
    public ServletRegistrationBean<Servlet> druidServlet() {
        log.info("init Druid Servlet Configuration");
        ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(new StatViewServlet());  //配置一个拦截器
        servletRegistrationBean.addUrlMappings("/druid/*");    //指定拦截器只拦截druid管理页面的请求
        HashMap<String, String> initParam = new HashMap<String,String>();
        initParam.put("loginUsername", "admin");    //登录druid管理页面的用户名
        initParam.put("loginPassword", "admin");    //登录druid管理页面的密码
        initParam.put("resetEnable", "true");       //是否允许重置druid的统计信息
        initParam.put("allow", "");         //ip白名单，如果没有设置或为空，则表示允许所有访问
        servletRegistrationBean.setInitParameters(initParam);
        return servletRegistrationBean;
    }
    
    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<WebStatFilter>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
    
}
```

## 配置文件如下：

```properties
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/druiddb
spring.datasource.username=postgres
spring.datasource.password=postgres
#config druid
spring.datasource.initialSize=5
spring.datasource.minIdle=5
spring.datasource.maxActive=20
spring.datasource.maxWait=10000
spring.datasource.timeBetweenEvictionRunMillis=60000
spring.datasource.minEvictableIdleTimeMillis=10000
spring.datasource.validationQuery=SELECT 'x'
spring.datasource.testWhileIdle=true
spring.datasource.testOnBorrow=true
spring.datasource.testOnReturn=false
spring.datasource.poolPreparedStatements=true
spring.datasource.maxPoolPreparedStatementPerConnectionSize=10
spring.datasource.filters=stat
```



<https://www.cnblogs.com/gc65/p/11183814.html>