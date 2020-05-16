[TOC]



# RedisTemplate常用集合使用说明-boundValueOps 

​      基础配置介绍已经在前面的《[RedisTemplate常用集合使用说明(一)](http://357029540.iteye.com/blog/2388706)》中已经介绍了，现在我们直接介绍boundValueOps()方法的使用：

​       首先要定义一个BoundValueOperations

 ```
BoundValueOperations boundValueOperations = redisTemplate.boundValueOps("bvo"); 
 ```

 1.[append](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#append-java.lang.String-)([String](http://docs.oracle.com/javase/8/docs/api/java/lang/String.html?is-external=true) value)

​     在原来值的末尾添加值

```
boundValueOperations.append("a");  
boundValueOperations.append("b");  
```

2.[get](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#get-long-long-)(long start, long end)

​    获取指定区间位置的值

```
//获取从指定位置开始，到指定位置为止的值  
System.out.println("获取从指定位置开始，到指定位置为止的值:" + boundValueOperations.get(0,2));  
```

3.[get](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#get--)()

​     获取字符串所有值

```
//获取所有值  
System.out.println("获取所有值:" + boundValueOperations.get());  
```

 4.[set](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#set-V-)([V](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html) value)

​     给绑定键重新设置值

```
//重新设置值  
boundValueOperations.set("f");  
System.out.println("重新设置值:" + boundValueOperations.get());  
```

5.[set](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#set-V-long-java.util.concurrent.TimeUnit-)([V](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html) value, long timeout, [TimeUnit](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html?is-external=true) unit)

​    在指定时间后重新设置值

```
//在指定时间后重新设置  
boundValueOperations.set("wwww",5,TimeUnit.SECONDS);  
System.out.println("在指定时间后重新设置:" + boundValueOperations.get());  
```

 6.[set](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#set-V-long-)([V](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html) value, long offset)

​    截取原有值的指定长度后添加新值在后面

```
//截取原有值的指定长度后添加新值在后面  
boundValueOperations.set("nnnnnn",3);  
System.out.println("截取原有值的指定长度后添加新值在后面:" + boundValueOperations.get()); 
```

 

 7.[setIfAbsent](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#setIfAbsent-V-)([V](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html) value)

​    没有值存在则添加

```
 //没有值存在则添加  
boundValueOperations.setIfAbsent("mmm");  
System.out.println("没有值存在则添加:" + boundValueOperations.get());  
```

 8.[getAndSet](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#getAndSet-V-)([V](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html) value)

​     获取原来的值并重新赋新值

```
 //获取原来的值，并覆盖为新值  
Object object = boundValueOperations.getAndSet("yyy");  
System.out.print("获取原来的值" + object);  
System.out.println("，覆盖为新值:" + boundValueOperations.get());  
```

 9.[size](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#size--)()

​    获取绑定值的长度

```
System.out.println("value值的长度:" + boundValueOperations.size()); 
```

 10.[increment](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#increment-double-)(double delta)和[increment](https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/BoundValueOperations.html#increment-long-)(long delta)

  自增长键值，前提是绑定值的类型是doule或long类型

```
//自增长只能在为数字类型的时候才可以  
boundValueOperations.increment(1);  
System.out.println("自增长只能在为数字类型的时候才可以:" + boundValueOperations.get()); 
```

 