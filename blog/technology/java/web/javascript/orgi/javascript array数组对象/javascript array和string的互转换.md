# javascript array和string的互转换

## Array类说明

Array类可以如下定义：

​     var aValues = new Array();  

​    如果预先知道数组的长度，可以用参数传递长度 

​    var aValues = new Array(20);  

​    ------------------如下2种定义方式是一样的--------1-----------

　　var aColors = new Array();

​     aColors[0] = "red";

​     aColors[1] = "green";

​     aColors[2] = "blue";

​     alert(aColors[0]);  // output "red"

​     -------------------------------------------------2-----------

​     var aColors = new Array("red","green","blue");  // 和Array定义数组是等同的。

　　alert(aColors[0]);  // output "red" too

​    --------------------------

## （1）Array 转换成 string 

### toString

​    把以上2种数组定义方式，输出都是一样的，发现中间有个逗号分隔符。

```
aColors.toString();   // output "red,green,blue";
```

### join方法

```
aColors.join(","); // output "red,green,blue";
```

## （2）string转换成Array

​      我们发现Array转换成字符串，数组之间多了1个分隔符',' ,那么string转换成Array数组，必须要有分隔符才行。可以是逗号，也可以是其它分隔符。

```
var sColors = "red,green,blue";
var aColors = sColors.split(',');   // 字符串就转换成Array数组了
```





https://blog.csdn.net/u010682330/article/details/78250200?locationNum=8&fps=1