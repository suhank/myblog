# sql语句把数据库表导成insert语句 select语句导出insert语句

用java怎么把数据库的一个表（部分记录）导成insert语句啊 ？



刚找到牛人给出zd的方法：
oracle用sql语句

```sql
select 'insert into teacher values('''||id||''','''||name||''');' from teacher;
```

mysql用sql语句

```mysql
select concat('insert into teacher values(''',IFNULL(id,'null'),''',''',IFNULL(name,'null'),''');')
from teacher;
```

结果如下：

```sql
insert into teacher values('1','bb');
insert into teacher values('2','aa');
insert into teacher values('3','cc');
insert into teacher values('3','cc');
insert into teacher values('4','cc');
```

我是在cmd命令执行的 然后右键全选 新建一个。sql文件，复制属进去，把开头删掉就行。
上面这一段 ，我自己也保存一份 自己看





https://zhidao.baidu.com/question/289558730.html