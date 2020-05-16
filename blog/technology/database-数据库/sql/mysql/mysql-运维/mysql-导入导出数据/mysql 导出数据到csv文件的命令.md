[TOC]



# mysql 导出数据到csv文件的命令

2012年07月12日 17:20:27 [陈小峰_iefreer](https://me.csdn.net/iefreer) 阅读数 39914



## 1.导出本地数据库数据到本地文件

```
mysql -A service_db -h your_host -utest -ptest

mysql> select * from t_apps where created>'2012-07-02 00:00:00' into outfile /tmp/apps.csv


```



## 2.导出远程数据库数据到本地文件

```
mysql -A service_db -h your_host -utest -ptest -ss -e "SELECT * from t_apps limit 300;" | sed 's/\t/","/g;s/^/"/;s/$/"/;s/\n//g' > apps.csv
```

(sed部分可略,尤其是处理包含汉字的数据时. 如果内容包含中文,可在select语句前加入set names utf8;)

实际示例

```
mysql  -h xxxx -P 33071 -u xxx -p'xxx'  -ss -e "select * from db.table ;" | sed 's/\t/","/g;s/^/"/;s/$/"/;s/\n//g' > apps.csv;
```



## 3.使用mysqldump导出远程或本地数据到本地文件

mysqldump -h your_host -utest -ptest -w "id<300" service_db t_apps > tt.sql

如果只导数据加上 -t or --no-create- info ;

如果只导结构加上 -d or --no-data;



## 4. mysqldump不能指定导出的列,变通方法如下:

mysql -u USERNAME --password=PASSWORD --database=DATABASE --execute='SELECT `FIELD`, `FIELD` FROM `TABLE` LIMIT 0, 10000 ' -X > file.csv



 



<https://blog.csdn.net/iefreer/article/details/7740950>