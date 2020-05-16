# javascript 日期格式与时间戳相互转换

## 1.将日期格式转化为时间戳：

```js
var date = new Date('2018-06-08 18:00:00');
// 有三种方式获取
var time1 = date.getTime();
var time2 = date.valueOf();
var time3 = Date.parse(date);
console.log(time1);//1528452000000
console.log(time2);//1528452000000
console.log(time3);//1528452000000
```

注：在苹果手机里这样使用会因为日期中间有空格导致结果为NaN,可以将日期分割然后进行转化

```js
var str='2018-06-08 18:00:00' ;
var time=new Date(str.split(" ")).getTime();
```



## 2.将时间戳转化为日期格式：

```Js
function timestampToTime(timestamp) {
    var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = date.getMinutes() + ':';
    s = date.getSeconds();
    return Y+M+D+h+m+s;
}
timestampToTime(1528452000);
console.log(timestampToTime(1528452000));//2018-06-08 18:00:00
```





https://blog.csdn.net/Lc_style/article/details/80626748