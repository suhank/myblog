# mysql 查询数据,特定条件排序靠前

```Mysql
SELECT * FROM `w_user_contacts` WHERE `uid` = 76042197140504576
ORDER BY 
CASE 
            WHEN phone = 18141901322 THEN 1 
            WHEN phone = 17695561638 THEN 1
ELSE
        phone
END 

LIMIT 0,10
```



https://blog.csdn.net/maosaiwei/article/details/79919597

