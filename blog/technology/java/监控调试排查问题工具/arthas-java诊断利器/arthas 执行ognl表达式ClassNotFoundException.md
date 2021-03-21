# arthas 执行ognl表达式ClassNotFoundException



## 1 问题描述

不希望通过编码的方式，想通过arthas 获取spring 属性。

参考一篇文章https://my.oschina.net/u/4255537/blog/3357593 根据applicationcontext 工具类获取配置属性的方法。

实际执行时总是报找不到类， 但是通过sc 命令可以找到该类

```java
[arthas@312]$ ognl '#context=@com.xxx.ApplicationContextHelper@appCtx,#context.getEnvironment().getProperty("author")'
Failed to execute ognl, exception message: ognl.OgnlException: Could not get static field appCtx from class com.xxx.ApplicationContextHelper [java.lang.ClassNotFoundException: Unable to resolve class: com.xxx.ApplicationContextHelper], please check $HOME/logs/arthas/arthas.log for more details. 

[arthas@312]$ sc com.xxx.ApplicationContextHelper
com.xxx.ApplicationContextHelper
12345
```

## 2 原因分析

搜到了 arthas 对该问题解释,大意是自定义类加载器导致找不到类

https://github.com/alibaba/arthas/issues/71

解决办法：

第一步，查询指定类的classloader信息，命令为：sc -d xxxx，根据结果可以看到类对应classloader的hash值；
第二步，ognl命令指定classloader，命令为：ognl -c xxxx(classloader的hash值) xxx（表达式）

## 3 解决方案

1. su app
2. as.sh
3. sc -d com.xxx.ApplicationContextHelper
   找到 classLoaderHash 41a2befb
4. ognl -c 41a2befb ‘#context=@com.xxx.ApplicationContextHelper@appCtx,#context.getEnvironment().getProperty(“druid.datasource.password”)’
5. 

## 4 相关资料

https://github.com/alibaba/arthas/issues/71

https://blog.csdn.net/qq826654664jx/article/details/101512468

https://commons.apache.org/proper/commons-ognl/language-guide.html





https://blog.csdn.net/w605283073/article/details/106535170/?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-1