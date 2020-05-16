# vue scoped CSS 与深度作用选择器 /deep/

2018年07月23日 10:53:43 [留给时间](https://me.csdn.net/qq_32340877) 阅读数：2829



使用 scoped 后，父组件的样式将不会渗透到子组件中。

例如（无效）：

```
<template>
  <div id="app">
    <el-input  class="text-box" v-model="text"></el-input>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      text: 'hello'
    };
  }
};
</script>

<style lang="less" scoped>
.text-box {
   input {
    width: 166px;
    text-align: center;
  }
}
</style>
```

解决方法:

使用深度作用选择器 /deep/

```
<template>
  <div id="app">
    <el-input v-model="text" class="text-box"></el-input>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      text: 'hello'
    };
  }
};
</script>

<style lang="less" scoped>
.text-box {
  /deep/ input {
    width: 166px;
    text-align: center;
  }
}
</style>
```

官方文档：<https://vue-loader.vuejs.org/guide/scoped-css.html#deep-selectors>





<https://blog.csdn.net/qq_32340877/article/details/81164072>