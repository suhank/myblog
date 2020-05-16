# MySQL创建索引方法：ALTER TABLE和CREATE INDEX的区别

 

众所周知，MySQL创建索引有两种语法，即：

```mysql
ALTER TABLE table_name ADD INDEX index_name (LastName, FirstName);

CREATE INDEX index_name table_name (LastName, FirstName);
```

那么，这两种语法有什么区别呢？  
在网上找了一下，在一个英文网站上，总结了下面几个区别，我翻译出来，如下：

1、CREATE INDEX必须提供索引名，对于ALTER TABLE，将会自动创建，如果你不提供；

2、CREATE INDEX一个语句一次只能建立一个索引，ALTER TABLE可以在一个语句建立多个，如：
      ALTER TABLE HeadOfState ADD PRIMARY KEY (ID), ADD INDEX (LastName,FirstName);

3、只有ALTER TABLE 才能创建主键，

英文原句如下：
With CREATE INDEX, we must provide a name for the index. With ALTER TABLE, MySQL creates an index name automatically if you don’t provide one.Unlike ALTER TABLE, the CREATE INDEX statement can create only a single index per statement. In addition, only ALTER TABLE supports the use of PRIMARY KEY.



https://blog.csdn.net/qq_34578253/article/details/72236808