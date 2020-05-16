[TOC]



# spring 手动提交事务 编程式的事务管理

## 简介

　　在使用Spring声明式事务时，不需要手动的开启事务和关闭事务，但是对于一些场景则需要开发人员手动的提交事务，比如说一个操作中需要处理大量的数据库更改，可以将大量的数据库更改分批的提交，又比如一次事务中一类的操作的失败并不需要对其他类操作进行事务回滚，就可以将此类的事务先进行提交，这样就需要手动的获取Spring管理的Transaction来提交事务。

## 编程式的事务管理

Spring 提供了两个模板类 TransactionTemplate 和 PlatformTransactionManager 来支持编程式事务管理，与其他的持久化模板一样，都是线程安全的。一般使用前者，后者类似与 JTA UserTransaction API。

## 编程式的事务管理方式

### 1, TransactionTemplate方式

这个支持单个数据源

编写一个 TransactionCallback 实现类（通常为匿名内部类），该实现包含需要在事务上下文中执行的代码。然后，将自定义 TransactionCallback 的实例传递给 TransactionTemplate 的 execute() 方法。

```java
public class SimpleService implements Service {
    // single TransactionTemplate shared amongst all methods in this instance
    private final TransactionTemplate transactionTemplate;

    // use constructor-injection to supply the PlatformTransactionManager
    public SimpleService(PlatformTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Object someServiceMethod() {
        return transactionTemplate.execute(new TransactionCallback() {
            // the code in this method executes in a transactional context
            public Object doInTransaction(TransactionStatus status) {
                updateOperation1();
                return resultOfUpdateOperation2();
            }
        });
    }
}
```

如果执行事务回调的时候没有返回值，则使用 TransactionCallbackWithoutResult 代替 TransactionCallback，且可以调用 TransactionStatus 对象的 setRollbackOnly() 方法回滚事务：

```java
transactionTemplate.execute(new TransactionCallbackWithoutResult() {
    protected void doInTransactionWithoutResult(TransactionStatus status) {
        try {
            updateOperation1();
            updateOperation2();
        } catch (SomeBusinessExeption ex) {
            status.setRollbackOnly();
        }
    }
});
```

TransactionTemplate 的事务设置可以通过编程方式或配置文件指定，如传播方式，隔离级别，超时等，TransactionTemplate 实例默认情况下具有默认事务设置。

- 编程方式：

  ```java
  public class SimpleService implements Service {
      private final TransactionTemplate transactionTemplate;
      public SimpleService(PlatformTransactionManager transactionManager) {
          Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");
          this.transactionTemplate = new TransactionTemplate(transactionManager);
          // the transaction settings can be set here explicitly if so desired
          this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
          this.transactionTemplate.setTimeout(30); // 30 seconds
          // and so forth...
      }
  }
  ```

- 配置文件方式：

  ```xml
  <bean id="sharedTransactionTemplate"
          class="org.springframework.transaction.support.TransactionTemplate">
      <property name="isolationLevelName" value="ISOLATION_READ_UNCOMMITTED"/>
      <property name="timeout" value="30"/>
  </bean>
  ```

  

### 2, PlatformTransactionManager方式

#### 单个数据源事务示例

```java
@Resource(name = "mainTransactionManager")
private DataSourceTransactionManager mainTransactionManager;
    
 
//这里会获取数据库连接并传入配置事务属性,开启事务
TransactionStatus transStatus = mainTransactionManager.getTransaction(new DefaultTransactionDefinition());
 
        
try {

    // todo 处理具体需要事务的逻辑

    
    mainTransactionManager.commit(transStatus);
} catch (Throwable e) {
    mainTransactionManager.rollback(transStatus);
    //异常处理
    throw e;
} 
```

#### 多个数据源事务示例

```java
@Resource(name = "mainTransactionManager")
private DataSourceTransactionManager mainTransactionManager;
@Resource(name = "docTransactionManager")
private DataSourceTransactionManager docTransactionManager;



TransactionStatus transStatusByMain = mainTransactionManager.getTransaction(new DefaultTransactionDefinition());
TransactionStatus transStatusByDoc = docTransactionManager.getTransaction(new DefaultTransactionDefinition());
try {


    // todo 处理具体需要事务的逻辑
    billService.insert(bill);
    billDetailService.batchInsert(billDetailList);

    billDetailExtendShippingService.batchInsert(billDetailExtendShippingList);


    docTransactionManager.commit(transStatusByDoc);
    mainTransactionManager.commit(transStatusByMain);
} catch (Throwable throwable) {
    docTransactionManager.rollback(transStatusByDoc);
    mainTransactionManager.rollback(transStatusByMain);
    //异常处理
    throw throwable;
}
```

LIFO/stack behavior的方式进行的，所以在多个事务进行提交时必须按照上述规则进行，否则就会报异常。java.lang.IllegalStateException: Cannot deactivate transaction synchronization - not active



事务的提交和回滚的顺序根据事务开启的顺序进行后进先出提交和回滚





http://www.cnblogs.com/banning/p/6346669.html