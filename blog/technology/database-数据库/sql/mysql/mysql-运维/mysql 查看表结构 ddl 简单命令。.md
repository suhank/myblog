

[TOC]



# mysql 查看表结构简单命令。

2012年09月13日 08:27:25

阅读数：56985

## 一、简单描述表结构，字段类型

desc tabl_name;

显示表结构，字段类型，主键，是否为空等属性，但不显示外键。

## 二、查询表中列的注释信息

select * from information_schema.columns

where table_schema = 'db'  #表所在数据库

and table_name = 'tablename' ; #你要查的表

只查询列名和注释

select  column_name, column_comment from information_schema.columns where table_schema ='db'  and table_name = 'tablename' ;

## 四、#查看表的注释

select table_name,table_comment from information_schema.tables  where table_schema = 'db' and table_name ='tablename'

## 五、查看表生成的DDL   推荐

show create table table_name;

这个命令虽然显示起来不是太容易看， 这个不是问题可以用\G来结尾，使得结果容易阅读；该命令把创建表的DDL显示出来，于是表结构、类型，外键，备注全部显示出来了。我比较喜欢这个命令：输入简单，显示结果全面。





https://blog.csdn.net/yageeart/article/details/7973381