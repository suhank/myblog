# mysql 获取成绩排序后的名次

其实就是输出mysql的排序后的行号

RT：获取单个用户的成绩在所有用户成绩中的排名

可以分两步：

1、查出所有用户和他们的成绩排名

```Mysql
select id,maxScore,(@rowNum:=@rowNum+1) as rowNo
from t_user,
(select (@rowNum :=0) ) b
order by t_user.maxScore desc 
```

2、查出某个用户在所有用户成绩中的排名

```mysql
select u.rowNo from (
select id,(@rowNum:=@rowNum+1) as rowNo
from t_user,
(select (@rowNum :=0) ) b
order by t_user.maxScore desc ) u where u.id="2015091810371700001";
```



https://blog.csdn.net/moneyshi/article/details/48543695