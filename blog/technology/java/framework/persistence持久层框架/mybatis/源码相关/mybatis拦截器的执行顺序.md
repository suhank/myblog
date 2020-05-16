# mybatis拦截器的执行顺序

 在mybatis-config.xml中有拦截器时，而且多个拦截器拦截的方法是同一个时，怎么确定谁先谁后执行呢？

在plugins中配置如下的拦截器，

```
<plugins>  
    <plugin interceptor="cn.xxInterceptor" />  
    <plugin interceptor="cn.yyInterceptor" />  
</plugins>  
```

如果拦截的是同一个目标方法，那么**yy拦截器** 将先执行。

可拦截的目标方法有以下(大致的先后顺序)：

```
  Executor
(update, query, flushStatements, commit,  rollback, getTransaction, close, isClosed)
  ParameterHandler
(getParameterObject, setParameters)
  StatementHandler
(prepare, parameterize, batch, update, query)
  ResultSetHandler
(handleResultSets, handleOutputParameters)
```





https://blog.csdn.net/hxpjava1/article/details/53925396