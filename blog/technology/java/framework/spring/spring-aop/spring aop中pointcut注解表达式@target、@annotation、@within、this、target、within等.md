# spring aop中pointcut注解表达式@target、@annotation、@within、this、target、within等

2019-09-18 01:28:43

  

至于最常见的 execution表达式；由于网上一搜基本都是用的这个，这里就不在赘述了；这里将我知道的分享给大家；

```java
//@Around("@annotation(自定义注解)")//自定义注解标注在方法上的方法执行aop方法
如：@Around("@annotation(org.springframework.transaction.annotation.Transactional)")

//@Around("@within(自定义注解)")//自定义注解标注在的类上；该类的所有方法（不包含子类方法）执行aop方法
如：@Around("@within(org.springframework.transaction.annotation.Transactional)")

//@Around("within(包名前缀.*)")//com.aop.within包下所有类的所有的方法都会执行(不包含子包) aop方法
如：@Around("within(com.aop.test.*)")

//@Around("within(包名前缀..*)")//com.aop.within包下所有的方法都会执行(包含子包)aop 方法
如：@Around("within(com.aop.test..*)")

//@Around("this(java类或接口)")//实现了该接口的类、继承该类、该类本身的类---的所有方法（包括不是接口定义的方法，但不包含父类的方法）都会执行aop方法
如：@Around("this(com.aop.service.TestService)")

//@Around("target(java类或接口)")//实现了该接口的类、继承该类、该类本身的类---的所有方法（包括不是接口定义的方法，包含父类的方法）
如：@Around("this(com.aop.service.TestService)")

//@Around("@target(自定义注解)")//springboot项目启动报如下错误，没有解决
// Caused by: java.lang.IllegalStateException:
// StandardEngine[Tomcat].StandardHost[localhost].TomcatEmbeddedContext[] failed to start
```

目前分享到这里了！

 





<https://blog.csdn.net/scos_sxb/article/details/100945389>