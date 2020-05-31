 

# Spring boot 启动报错 Failed to auto-configure a DataSource

​    今天Spring Boot 2.0正式版发布，寻思着搭个小demo尝试一下Spring Boot的新特性，使用idea创建项目。在选择组件时添加了mysql、mybatis 然后在第一次启动的时候启动报错，错误信息如下：

```
***************************
APPLICATION FAILED TO START
***************************
Description:
Failed to auto-configure a DataSource: 'spring.datasource.url' is not specified and no embedded datasource could be auto-configured.
Reason: Failed to determine a suitable driver class
Action:
Consider the following:
	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.

	If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active).
```

在多方查证后，需要在启动类的@EnableAutoConfiguration(这个测试不行)或@SpringBootApplication中添加exclude

= {DataSourceAutoConfiguration.class}，排除此类的autoconfig。启动以后就可以正常运行。

这是因为添加了数据库组件，所以autoconfig会去读取数据源配置，而我新建的项目还没有配置数据源，所以会导致异常出现。





https://blog.csdn.net/daxiang52/article/details/79420777