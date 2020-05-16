# 关于 find_in_set 的性能问题

同事不少数据表设计的时候使用一个字段来存储多对多关系，比如 表 user中有一个字段叫 category, category存储的是 "1,3,9" 这样的类型的数据，实际上是category的id 用逗号分隔开来的。

 

要查询一个用户属于id为2分类的用户可以这么写

```
select * from user where find_in_set('2',user.category)  
```

具体find_in_set 的使用请参照手册

<http://dev.mysql.com/doc/refman/5.1/en/string-functions.html#function_find-in-set>

 

 

虽然这样很好用，但问题是如果数据量大的情况下怎么办，性能会是问题么，手册上有说对find_in_set 做的优化，但在没有索引的情况下他的性能应该是个问题。

 

于是做了个测试，user 表录入 100万的数据，同时建立 user_category 表，每个user有 3 个分类，那么category表里有300万条记录。

```
CREATE TABLE `user_category` (  
  `id` int(11) NOT NULL AUTO_INCREMENT,  
  `user_id` int(11) DEFAULT NULL,  
  `category_id` int(11) DEFAULT NULL,  
  PRIMARY KEY (`id`),  
  KEY `category_id` (`category_id`),  
  KEY `user_id` (`tax_id`)  
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT   
```

现在比较一下在百万级的数据量上使用 join 链接外键查询和find_in_set查询的性能

\1. 使用 find_in_set 查询，平均时间在2.2秒左右

```
SELECT SQL_NO_CACHE COUNT(*) FROM `user` WHERE FIND_IN_SET(65,category)  
```



\2. 使用left join ， 使用了右表中的索引，平均时间在0.2秒左右

```
SELECT SQL_NO_CACHE COUNT(DISTINCT(`user`.id)) FROM `user`   
LEFT JOIN `user_category` ON `user`.`id`= `user_category`.`user_id`  
WHERE `user_category`.`category_id`=75  
```

 

所以在大数据量的情况下还是不适合用find_in_set, 不过有些表的数据可能永远就那么点数据，这个时候为了减少表数量，倒是可以用这样的方法做。





https://jonny131.iteye.com/blog/771753