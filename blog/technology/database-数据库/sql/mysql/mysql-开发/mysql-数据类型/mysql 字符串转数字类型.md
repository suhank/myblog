# mysql 字符串转数字类型



**Mysql字符串'123'转为数字123**

```
方法一：SELECT CAST('123' AS SIGNED);
方法二：SELECT CONVERT('123',SIGNED);
方法三：SELECT '123'+0;
```



https://www.cnblogs.com/emanlee/p/5998683.html