[TOC]



# element-ui样式覆盖问题及解决办法

2018年08月23日 18:59:00 [weixin_34050005](https://me.csdn.net/weixin_34050005) 阅读数：40



最近用vue + element-ui做项目，element-ui样式覆盖遇到点问题。整理如下。

------

## 问题

- el在vue文件中是标签，但浏览器渲染出来是嵌套的结构，导致样式不能加到对应的类名上
- 优先级原因，部分样式很难覆盖
- 拿element-ui的默认样式来改，代码太多，混乱

## 解决方法

1. 通用样式

- 建立element.css文件，将element-ui中的通用样式写入此文件；将element.css引入main.js（即放vue实例的文件）

1. 非通用样式

- 非通用样式，写入.vue文件中的`<style scoped></style>`中
- 写非通用样式时，在浏览器中用审查元素的方式来找到对应的类名或者标签
- 如果优先级不够，可以在元素外加div盒子自定义类名修改，或者直接自定义类名进行修改
- 配置vue-loader后，可以使用深度作用选择器（样式中的选择器能够作用的“更深”，例如影响子组件），`>>> 或者 /deep/ .a >>> b .a /deep/ .b`





<https://blog.csdn.net/weixin_34050005/article/details/87246234>