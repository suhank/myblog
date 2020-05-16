# javascript 获取当前时间的前一天/后一天 ，前一月/后一月

2018年06月16日 11:53:01 [全栈修炼](https://me.csdn.net/ch834301) 阅读数 5656

 

## **js获取当前时间的前一天/后一天**

```js
Date curDate = new Date();
var preDate = new Date(curDate.getTime() - 24 * 60 * 60 * 1000); //前一天 
var nextDate = new Date(curDate.getTime() + 24 * 60 * 60 * 1000); //后一天
```

 

## **前一月/后一月**

```js
var now = new Date();
now.setMonth(now.getMonth() - 1);
var now2 = new Date();
now2.setMonth(now.getMonth() + 1);
alert(now2);
```

 

 

<https://blog.csdn.net/ch834301/article/details/80712112>