

# mysql text和blob比较

## （1）相同

- 在TEXT或BLOB列的存储或检索过程中，不存在大小写转换,当未运行在严格模式时，如果你为BLOB或TEXT列分配一个超过该列类型的最大长度的值值，值被截取以保证适合。如果截掉的字符不是空格，将会产生一条警告。使用严格SQL模式，会产生错误，并且值将被拒绝而不是截取并给出警告.
- BLOB和TEXT列不能有 默认值.
- 当保存或检索BLOB和TEXT列的值时不删除尾部空格。(这与VARBINARY和VARCHAR列相同）.
- 对于BLOB和TEXT列的索引，必须指定索引前缀的长度。对于CHAR和VARCHAR，前缀长度是可选的.

## （2）相异 

 text :

- TEXT值是大小写不敏感的
- Text被视为非二进制字符串
- TEXT列有一个字符集，并且根据字符集的 校对规则对值进行排序和比较
- 可以将TEXT列视为VARCHAR列
- MySQL连接程序/ODBC将TEXT值定义为LONGVARCHAR
- BLOB 可以储存图片,TEXT不行，TEXT只能储存纯文本文件。4个TEXT类型TINYTEXT、TEXT、MEDIUMTEXT和LONGTEXT对应于4个BLOB类型，并且有同样的最大长度和存储需求。

blob:  

- BLOB值的排序和比较以大小写敏感方式执行;
- BLOB被视为二进制字符串;
- BLOB列没有字符集，并且排序和比较基于列值字节的数值值。
- 在大多数方面，可以将BLOB列视为能够足够大的VARBINARY列
- MySQL连接程序/ODBC将BLOB值定义为LONGVARBINARY
- 一个BLOB是一个能保存可变数量的数据的二进制的大对象。4个BLOB类型TINYBLOB、BLOB、MEDIUMBLOB和LONGBLOB仅仅在他们能保存值的最大长度方面有所不同。

## （3）其他：

VARCHAR，BLOB 和TEXT类型是变长类型，对于其存储需求取决于列值的实际长度(在前面的表格中用L表示)，而不是取决于类型的最大可能尺寸。例如，一个 VARCHAR(10)列能保存最大长度为10个字符的一个字符串，实际的存储需要是字符串的长度 ，加上1个字节以记录字符串的长度。对于字符串'abcd'，L是4而存储要求是5个字节。
    BLOB和TEXT类型需要1，2，3或4个字节来记录列值的长度，这取决于类型的最大可能长度。VARCHAR需要定义大小，有255的最大限制；TEXT则不需要。如果你把一个超过列类型最大长度的值赋给一个BLOB或TEXT列，值被截断以适合它。

CHAR(n) 固定长度，最多 255 个字符 
VARCHAR(n) 可变长度，MySQL 4.1 及以前最大 255 字符，MySQL 5 之后最大 65535 字节 
TINYTEXT 可变长度，最多 255 个字符 
TEXT 可变长度，最多 65535 个字符 
MEDIUMTEXT 可变长度，最多 16777215（2^24 - 1）个字符 
LONGTEXT 可变长度，最多 4294967295（2^32 - 1）（4G）个字符





https://blog.csdn.net/zuiaituantuan/article/details/6115938