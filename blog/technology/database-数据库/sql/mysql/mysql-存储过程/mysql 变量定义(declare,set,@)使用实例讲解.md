[TOC]



# mysql变量定义(declare,set,@)使用实例讲解

标签: [mysql](http://www.manongjc.com/tag/mysql)[变量](http://www.manongjc.com/tag/%E5%8F%98%E9%87%8F)[declare](http://www.manongjc.com/tag/declare)[set](http://www.manongjc.com/tag/set)	  时间:2016-09-03

变量在mysql中会经常被使用，变量的使用方法是一个重要的知识点，特别是在定义条件这块比较重要。本文章向大家介绍mysql用户变量使用实例，需要的朋友可以参考一下。

mysql定义用户边框可以使用declare或set，本文章向大家介绍declare和set定义变量的实例及区别。

 

## mysql变量的种类

1. 用户变量：以"@"开始，形式为"@变量名"。用户变量跟mysql客户端是绑定的，设置的变量，只对当前用户使用的客户端生效
2. 全局变量：定义时，以如下两种形式出现，set GLOBAL 变量名  或者  set @@global.变量名，对所有客户端生效。只有具有super权限才可以设置全局变量
3. 会话变量：只对连接的客户端有效。
4. 局部变量：作用范围在begin到end语句块之间。在该语句块里设置的变量。declare语句专门用于定义局部变量。set语句是设置不同类型的变量，包括会话变量和全局变量

通俗理解术语之间的区别：

用户定义的变量就叫用户变量。这样理解的话，会话变量和全局变量都可以是用户定义的变量。只是他们是对当前客户端生效还是对所有客户端生效的区别了。所以，用户变量包括了会话变量和全局变量

局部变量与用户变量的区分在于两点:1.用户变量是以"@"开头的。局部变量没有这个符号。2.定义变量不同。用户变量使用set语句，局部变量使用declare语句定义 3.作用范围。局部变量只在begin-end语句块之间有效。在begin-end语句块运行完之后，局部变量就消失了。

所以，最后它们之间的层次关系是：变量包括局部变量和用户变量。用户变量包括会话变量和全局变量。

 

## mysql declare定义变量

mysql declare用于定义变量，在存储过程和函数中通过declare定义变量在BEGIN...END中，且在语句之前。并且可以通过重复定义多个变量

declare变量的作用范围同编程里面类似，在这里一般是在对应的begin和end之间。在end之后这个变量就没有作用了，不能使用了。这个同编程一样。

注意：declare定义的变量名不能带‘@’符号，mysql在这点做的确实不够直观，往往变量名会被错成参数或者字段名。

mysql存储过程中使用declare定义变量，实例如下：

```
DROP PROCEDURE IF EXISTS insert_ten_rows $$

CREATE PROCEDURE insert_ten_rows () 
    BEGIN
        DECLARE crs INT DEFAULT 0;

        WHILE crs < 10 DO
            INSERT INTO `continent`(`name`) VALUES ('cont'+crs);
            SET crs = crs + 1;
        END WHILE;
    END $$

DELIMITER ;
```

 

## mysql SET定义变量

mysql set也可以用来定于变量，定义变量的形式是以"@"开始，如："@变量名"。

mysql SET定义变量实例：

```
mysql> SET @t1=0, @t2=1, @t3=2;

mysql> select @t1;
+------+
| @t1  |
+------+
| 0    |
+------+

mysql> select @t2;
+------+
| @t2  |
+------+
| 1    |
+------+
/*  http://www.manongjc.com/article/1442.html */
mysql> select @t3;
+------+
| @t3  |
+------+
| 2    |
+------+

mysql>
```

复杂一点的实例：

```
mysql> SET @t1=0, @t2=1, @t3=2;

mysql> SELECT @t1:=(@t2:=1)+@t3:=4,@t1,@t2,@t3;
+----------------------+------+------+------+
| @t1:=(@t2:=1)+@t3:=4 | @t1  | @t2  | @t3  |
+----------------------+------+------+------+
|                    5 | 5    | 1    | 4    |
+----------------------+------+------+------+
```

 

## mysql declare和set定义变量的区别

mysql declare和set定义变量，除了一个不加@和一个加@这个区别之外，还有以下区别：

declare用来定义局部变量

@用来定义会话变量

declare变量的作用范围同编程里面类似，在这里一般是在对应的begin和end之间。在end之后这个变量就没有作用了，不能使用了。这个同编程一样。

另外有种变量叫做会话变量(session variable)，也叫做用户定义的变量(user defined variable)。这种变量要在变量名称前面加上“@”符号，叫做会话变量，代表整个会话过程他都是有作用的，这个有点类似于全局变量一样。这种变量用途比较广，因为只要在一个会话内(就是某个应用的一个连接过程中)，这个变量可以在被调用的存储过程或者代码之间共享数据。

原文地址：<http://www.manongjc.com/article/1441.html>