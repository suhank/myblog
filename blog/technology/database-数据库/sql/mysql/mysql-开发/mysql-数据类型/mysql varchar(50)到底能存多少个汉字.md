# mysql varchar(50)到底能存多少个汉字

2017年10月25日 11:25:11 [流浪猫走失了](https://me.csdn.net/u012491783) 阅读数 13588

 

mysql 4.0版本以下，varchar(50), 指的是50字节，如果存放utf8汉字时，只能存放16个（每个汉字3字节）

mysql 5.0版本以上，varchar(50), 指的是50字符，无论存放的是数字、字母还是UTF8汉字（每个汉字3字节），都可以存放50个。

可以自己建个表试试varchar(50)可以放多少汉字。





<https://blog.csdn.net/u012491783/article/details/78339269>

