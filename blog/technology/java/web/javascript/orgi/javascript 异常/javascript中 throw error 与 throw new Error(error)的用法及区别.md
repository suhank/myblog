# javascript中 throw error 与 throw new Error(error)的用法及区别

 2020-12-08 17:28:20

抛出错误一般都是与try catch 同时出现的

先看定义：

1. throw new Error(error); 这个是创建错误，创造一个错误类型抛出
2. throw error 这个是抛出错误。

上代码：throw new Error(error)

```javascript
var a = 5;
try{
    if(a==5){
        //抛出错误
        throw new Error("loopTerminates"); //Error要大写
    }
}catch(e){
    console.log(e);    //打印出Error对象：Error: loopTerminates
    console.log(e.message); //打印：loopTerminates
}
```

打印结果：

![img](javascript中 throw error 与 throw new Error(error)的用法及区别.assets/20201208172245655.png)

throw error:

```javascript
var a = 5;
try{
   if(a==5){
        //抛出错误
        throw "loopTerminates";
     }
}catch(e){
    console.log(e);//打印: loopTerminates
    console.log(e.message); //打印：undefined
}
```

打印结果：

![在这里插入图片描述](javascript中 throw error 与 throw new Error(error)的用法及区别.assets/20201208172446531.png)



参考：https://www.cnblogs.com/jijm123/p/13930951.html

[(4条消息) javascript中 throw error 与 throw new Error(error)的用法及区别_Sol的博客-CSDN博客](https://blog.csdn.net/weixin_40024174/article/details/110877682)

