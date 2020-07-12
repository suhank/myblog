# mysql spring事务隔离级别与数据库隔离级别的优先级

例如，数据库的隔离级别设置为`READ_COMMITED`。
然后通过spring事务管理，我将事务隔离级别设置为
1）`READ_UNCOMMITED`-那么该事务的有效隔离级别是多少。
2）`REPEATABLE_READ`-那么该事务的有效隔离级别是什么。



**最佳答案**

在数据库中设置了默认的隔离级别（在您的情况下是READ-committed）和一个事务隔离级别。如果未明确指定，则使用默认级别。
Spring只打开声明的隔离级别，当然会“覆盖”DB的默认级别。
实际上，在没有spring的情况下，也可以通过调用SQL

```sql
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
```





https://www.coder.work/article/2533722







