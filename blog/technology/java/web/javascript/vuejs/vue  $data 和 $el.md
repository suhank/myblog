[TOC]



# vue  `$data` 和 `$el`

## 说明

每个 Vue 的实例都应该有自己的 `$data` 和 `$el`

$data就是data 对象 如下内容:

```js
{
   message: "xuxiao is boy"
}
```

$el就是页面的所有 HTML Element  html bodey标签内部的所有html内容

```html
<div id="app">
    <p>{{ message }}</p>
    <button @click="jiafen"></button>
</div>
```



## demo

```Html
<!DOCTYPE html>
<html lang="en">
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <!--<script type="text/javascript" src="https://cdn.jsdelivr.net/vue/2.1.3/vue.js"></script>-->
    <script type="text/javascript" src="../assets/js/vue.js"></script>
</head>
<body>

<div id="app">
    <p>{{ message }}</p>
    <button @click="jiafen"></button>
</div>

<script type="text/javascript">

    var app = new Vue({
        el: '#app',
        data: {
            message: "xuxiao is boy"
        },
        methods: {
            jiafen: function () {
                this.count++;

            }
        }
        ,
        beforeCreate: function () {
            console.group('beforeCreate 创建前状态===============》');
            console.log("%c%s", "color:red", "el     : " + this.$el); //undefined
            console.log("%c%s", "color:red", "data   : " + this.$data); //undefined
            console.log("%c%s", "color:red", "message: " + this.message)
        },
        created: function () {
            console.group('created 创建完毕状态===============》');
            console.log("%c%s", "color:red", "el     : " + this.$el); //undefined
            console.log("%c%s", "color:red", "data   : " + this.$data); //已被初始化
            console.log("%c%s", "color:red", "message: " + this.message); //已被初始化
        },
        beforeMount: function () {
            console.group('beforeMount 挂载前状态===============》');
            console.log("%c%s", "color:red", "el     : " + (this.$el)); //已被初始化
            console.log(this.$el);
            console.log("%c%s", "color:red", "data   : " + this.$data); //已被初始化
            console.log("%c%s", "color:red", "message: " + this.message); //已被初始化
        },
        mounted: function () {
            console.group('mounted 挂载结束状态===============》');
            console.log("%c%s", "color:red", "el     : " + this.$el); //已被初始化
            console.log(this.$el);
            console.log("%c%s", "color:red", "data   : " + this.$data); //已被初始化
            console.log("%c%s", "color:red", "message: " + this.message); //已被初始化
        },
        beforeUpdate: function () {
            console.group('beforeUpdate 更新前状态===============》');
            console.log("%c%s", "color:red", "el     : " + this.$el);
            console.log(this.$el);
            console.log("%c%s", "color:red", "data   : " + this.$data);
            console.log("%c%s", "color:red", "message: " + this.message);
        },
        updated: function () {
            console.group('updated 更新完成状态===============》');
            console.log("%c%s", "color:red", "el     : " + this.$el);
            console.log(this.$el);
            console.log("%c%s", "color:red", "data   : " + this.$data);
            console.log("%c%s", "color:red", "message: " + this.message);
        },
        beforeDestroy: function () {
            console.group('beforeDestroy 销毁前状态===============》');
            console.log("%c%s", "color:red", "el     : " + this.$el);
            console.log(this.$el);
            console.log("%c%s", "color:red", "data   : " + this.$data);
            console.log("%c%s", "color:red", "message: " + this.message);
        },
        destroyed: function () {
            console.group('destroyed 销毁完成状态===============》');
            console.log("%c%s", "color:red", "el     : " + this.$el);
            console.log(this.$el);
            console.log("%c%s", "color:red", "data   : " + this.$data);
            console.log("%c%s", "color:red", "message: " + this.message)
        }
    })
</script>
</body>
</html>
```

