# 关于怎么让div宽度自适应文字内容？

## 问题

 问题对人有帮助，内容完整，我也想知道答案0问题没有实际价值，缺少关键内容，没有改进余地

因为文字多少不能固定，如果div固定宽度的话，内容又很少，这样会很难看，但是如果div不固定宽度，内容又很多的话，这样文字会把div宽度撑的很大，所以该怎么做？
如图所示，我把头像和用户昵称+文字内容放到两个div里面，然后两者用inline-block排列起来,所以中间那个文字内容怎么解决让文字自适应呢？

![图片描述](image-201810242042/bVzEIK.png)

![图片描述](image-201810242042/bVzEIR.png)



## 答

设置一个最大宽度和最小宽度就好了

min-width



直接用css3的fit-content：

```
width:fit-content;
width:-webkit-fit-content;
width:-moz-fit-content;
```

https://segmentfault.com/q/1010000006075682