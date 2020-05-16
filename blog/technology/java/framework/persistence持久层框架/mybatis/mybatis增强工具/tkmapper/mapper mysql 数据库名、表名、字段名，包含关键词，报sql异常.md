[TOC]



# mapper mysql 数据库名、表名、字段名，包含关键词，报sql异常

## 问题

mysql 数据库名、表名、字段名，包含关键词，自动拼接的sql，没有添加`反勾号（Esc下面的那个键）做转义。导致查询报sql错误。如 `describe`



## 方法一

加注解指定：

[@column](https://github.com/column)(name="`describe`")

## 方法二

默认是没有处理的， 可以参照下Wiki： [mapping](https://github.com/abel533/Mapper/wiki/2.2-mapping) 2.2.3项 说明
mysql 配置可以为

boot方式添加配置

```
mapper:
  wrap-keyword: `{0}`
```

spring方式添加配置

```
    /**
     * <B>Description:</B> 配置MapperScannerConfigurer <br>
     * <B>Create on:</B> 2018/4/15 上午11:05 <br>
     * 
     */
    @Bean
    public MapperScannerConfigurer baseMapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("com.mapper.demo.dao");
        configurer.setAnnotationClass(Repository.class);
        configurer.setSqlSessionFactoryBeanName("baseSqlSessionFactory");
        // 全局配置相关 tk.mybatis.mapper.entity.Config
        Properties properties = new Properties();
        properties.setProperty("mappers", "tk.mybatis.mapper.common.Mapper");
        properties.setProperty("wrapKeyword", "`{0}`");
        configurer.setProperties(properties);
        return configurer;
    }
```

看下源码中： [Config.java](https://github.com/abel533/Mapper/blob/master/core/src/main/java/tk/mybatis/mapper/entity/Config.java) 这个字段的注释`wrapKeyword`







<https://github.com/abel533/Mapper/issues/147>

<https://github.com/abel533/Mapper/issues/438>