[TOC]



# element-ui [Bug Report] el-table-column 异常行为与 v-if or v-show #5712

## 问题

wangtjwork commented on 5 Jul 2017

Element UI version1.3.7OS/Browsers versionwin 10/ chrome 58.0.3029.110Vue version2.3.4Reproduction Linkhttps://jsfiddle.net/co8c0sk4/Steps to reproduceClick on the change button twice will make the table appear broken.If v-if changed to v-show, the columns will not be hidden, and clicking on the button will not do anything.What is Expected?Clicking on the change button will be able to show between Address and Name.What is actually happening?Clicking on the change button will break the table. It doesn't matter if I use v-show or v-if, it simply won't work.

Wangtjork于2017年7月5日发表评论

element ui version1.3.7os/browsers versionwin 10/chrome 58.0.3029.110vue version2.3.4复制链接https://jsfiddle.net/co8c0sk4/steps to replick on the change button two将使表显示为中断状态。如果v-if更改为v-show，列将不会被隐藏，单击该按钮将不会执行任何操作。预期会发生什么？单击“更改”按钮可以在地址和名称之间显示。实际发生了什么？单击“更改”按钮将破坏表。不管我是用V-Show还是V-if，它都不起作用。

  

## 答案

Leopoldthecoder commented [on 24 Sep 2017](https://github.com/ElemeFE/element/issues/5712#issuecomment-331705800)

You need to add `key`: <https://jsfiddle.net/co8c0sk4/7/>

 

```
<script src="//unpkg.com/vue/dist/vue.js"></script>
<script src="//unpkg.com/element-ui@1.3.7/lib/index.js"></script>
<div id="app">
<template>
    <el-button @click="toggleAddress">change</el-button>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="date" label="日期" width="180" >
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="180" v-if="showAddress" key="1">
      </el-table-column>
      <el-table-column prop="address" label="地址" v-if="!showAddress" key="2">
      </el-table-column>
    </el-table>
  </template>
</div>
```



https://github.com/ElemeFE/element/issues/5712