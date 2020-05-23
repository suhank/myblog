# hive SQL COALESCE 函数

COALESCE是一个函数， (expression_1, expression_2, ...,expression_n)依次参考各参数表达式，遇到非null值即停止并返回该值。如果所有的表达式都是空值，最终将返回一个空值。

 

比如我们要登记用户的电话，数据库中包含他的person_tel,home_tel,office_tel,我们只要取一个非空的就可以，则我们可以写查询语句

```
select COALESCE(person_tel,home_tel,office_tel) as contact_number from Contact；
```

 

 

**使用实例：**

这个参数使用的场合为：假如某个字段默认是null，你想其返回的不是null，而是比如0或其他值，可以使用这个函数 

```
SELECT COALESCE(field_name,0) as value from table;
```




```
select coalesce(a,b,c);
```

参数说明：如果a==null,则选择b；如果b==null,则选择c；如果a!=null,则选择a；如果a b c 都为null ，则返回为null。

 

 





https://www.cnblogs.com/Allen-rg/p/10712833.html