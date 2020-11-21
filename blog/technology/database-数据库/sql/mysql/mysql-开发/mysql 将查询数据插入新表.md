# mysql 将查询数据插入新表

2016.12.14 19:06:00

将查询数据插入新表

```mysql
INSERT INTO 目标表 SELECT * FROM 来源表;
比如要将 articles 表插入到 newArticles 表中，则是：
INSERT INTO newArticles SELECT * FROM articles;
如果只希望导入指定字段，可以用这种方法：
INSERT INTO 目标表 (字段1, 字段2, ...) SELECT 字段1, 字段2, ... FROM 来源表;
```

eg:

```csharp
INSERT INTO api_pass_rate (if_name) SELECT DISTINCT `if_name` FROM test_result;
```




链接：https://www.jianshu.com/p/e89abe1e5223

