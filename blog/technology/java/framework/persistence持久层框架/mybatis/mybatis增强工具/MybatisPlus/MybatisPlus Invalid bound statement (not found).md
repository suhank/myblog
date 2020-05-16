# MybatisPlus Invalid bound statement (not found)

2018年08月04日 10:19:26 [我自横刀向天笑-去留肝胆两昆仑](https://me.csdn.net/weigang200820chengdu) 阅读数：5742



 版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/weigang200820chengdu/article/details/81407995

##### 项目依赖

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.3.RELEASE</version>
</parent>

<properties>
    <mybatis-plus.version>2.3</mybatis-plus.version>
</properties>

<dependencies>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus</artifactId>
        <version>${mybatis-plus.version}</version>
    </dependency>
    ...
</dependencies>123456789101112131415161718
```

##### 项目为单模块

##### 出问题配置

application.yml

```
# MybatisPlus 配置
mybatis-plus:
  mapper-locations: classpath:mapper/*/*Mapper.xml123
```

##### UserMapper.java

```
package com.zbj.user.dao.mapper;

public interface UserMapper extends BaseMapper<User> {
    Map<String, String> queryByUserId(@Param("userId") long userId, @Param("fields") List<String> fields);
}12345
```

##### UserMapper.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zbj.user.dao.mapper.UserMapper">

<!-- 其他配置和查询方法省略 -->
<select id="queryByUserId" resultMap="java.util.Map">
        select
        <foreach collection="fields" item="item" separator=",">
            ${item}
        </foreach>
        from user where user_id = ${userId}
    </select>
</mapper>12345678910111213
```

##### UserMapper.queryByUserId找不到异常

- 参照官方文档(没有解决问题)
- <http://mp.baomidou.com/#/question?id=%E5%BC%82%E5%B8%B8invalid-bound-statement-not-found-%E8%A7%A3%E5%86%B3%E6%96%B9%E6%B3%95mp%E6%96%B9%E6%B3%95%E6%97%A0%E6%B3%95%E8%B0%83%E7%94%A8>
- 参照自定义方法无法执行
- <http://mp.baomidou.com/#/question?id=%E8%87%AA%E5%AE%9A%E4%B9%89sql%E6%97%A0%E6%B3%95%E6%89%A7%E8%A1%8C>
- 文档中配置 application.yml

```
mybatis-plus:
    mapper-locations: classpath:/mapper/**/*.xml12
```

- 文档中多模块才需要这样配置

```
注意！maven 多模块 jar 依赖 xml 扫描需为 classpath*:mapper/**/*Mapper.xml 加载多个 jar 下的 xml1
```

- 经本地调试，单模块也需要像多模块那样配置

```
mybatis-plus:
  # 此处有坑 单模块加载也需要配置*(classpath 紧挨着的星) 解决自定义SQL无法执行
  mapper-locations: classpath*:mapper/*/*Mapper.xml123
```

- 至此问题解决
- 一个星号引发的错误

##### 参考链接

<http://mp.baomidou.com/#/?id=%E7%AE%80%E4%BB%8B>





https://blog.csdn.net/weigang200820chengdu/article/details/81407995