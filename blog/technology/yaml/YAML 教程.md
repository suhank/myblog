[TOC]



# 简明YAML教程

前言：yaml是一种用来描述配置的语言，其可读性和简洁性较json更胜一筹，用yml写成的配置文件，以.yml结尾。

### YAML的基本语法规则

1. 大小写敏感

2. 使用缩进表示层级关系

3. 缩进是使用空格，不允许使用tab

4. 缩进对空格数目不敏感，相同层级需对齐

5. 用“#”表示行注释

6. 在单一文件中，可用连续三个连字号（---）区分多个文件。

   ```yaml
   #公共部分
   spring:
     profiles:
       active: peer1 #默认的profile 
     application:
       name: EUREKA-HA

   server:
     port: 8761
   spring:
     profiles: peer1
   eureka:
     instance:
       hostname: peer1
     client:
       serviceUrl:
         defaultZone: http://peer2:8762/eureka/,http://peer3:8763/eureka/
         
   ---
   #文件1
   spring:
     profiles: peer1
   server:
     port: 8761
   eureka:
     instance:
       hostname: peer1
     client:
       serviceUrl:
         defaultZone: http://peer2:8762/eureka/,http://peer3:8763/eureka/
   ---
   #文件2
   spring:
     profiles: peer2
   server:
     port: 8762
   eureka:
     instance:
       hostname: peer2
     client:
       serviceUrl:
         defaultZone: http://peer1:8761/eureka/,http://peer3:8763/eureka/
   ---
   #文件3
   spring:
     profiles: peer3
   server:
     port: 8763
   eureka:
     instance:
       hostname: peer3
     client:
       serviceUrl:
         defaultZone: http://peer1:8761/eureka/,http://peer2:8762/eureka/
   ```


执行:

```
java -jar xxx.jar --spring.profiles.active=test 表示使用测试环境的配置

java -jar xxx.jar --spring.profiles.active=prod 表示使用生产环境的配置
```



   

7. 另外，还有选择性的连续三个点号（ ... ）用来表示文件结尾。

### YAML的数据结构

YAML的数据结构比较简单，只有三种： 

1. 对象：类似map，用键值对表示 
2. 数组：与java数组同含义 
3. 纯量（scalars）：元数据，不可再分，多数情况下指基本数据类型

以上三种数据接口分别如下表示：**(注意，冒号和连词线后边有一个空格)**

- 对象：

```
name:zhangsan1
```

或者用行内元素表示

```
student:{name:zhangsan,age:13}1
```

- 数组：一组以连词线`-`构成的数据结构,

```
- A
- B
- C123
```

- 纯量 
  包括：字符串，布尔值，整数，浮点数，null，时间，日期，

```
#数值直接表示
number: 1.1
#布尔用true, false
isOnline: false
#null用波浪线表示
isNUll: ~
#时间采用iso8601
time: 2001-12-14t21:59:43.10-05:00
#日期用复合ios8601表示
date: 2017-09-01
#两个感叹号表示强转数据类型
a: !!str 123
b: !!str true
```

其中字符串是比较复杂的一种情况：

```
#字符串默认不用引号
str: 这是一个字符串
#字符串有空格或者特殊字符时，放在引号内（单双都可）
str: 'this is a string'
#字符串中间有单引号，需要用两个单引号转义
str: 'he''s name is X'123456
```

 



http://blog.csdn.net/zxb136475688/article/details/77853909