[TOC]

# vue修改对象的属性值后页面不重新渲染的实例 强制更新 forceUpdate

 

今天小编就为大家分享一篇vue修改对象的属性值后页面不重新渲染的实例，具有很好的参考价值，希望对大家有所帮助。一起跟随小编过来看看吧

最近项目在使用vue，遇到几次修改了对象的属性后，页面并不重新渲染，场景如下：

**HTML页面如下：**

```html
<template v-for="item in tableData">
    <div :class="{'redBorder':item.red}">
    <div>{{ item.name}}</div>
    <div>
     <el-button size="mini" @click="clickBtn(item.id)" type="info">编辑</el-button>
     <p class="el-icon-error" v-show="item.tip"></p>
    </div>
    </div>
</template>
```

**js部分如下：**

```js
<script>
  export default {
    data() {
      return {
        tableData:[{id:0,name:"lili",red:false,tip:false}]
      }
    },

    methods: {
      clickBtn(id){
        this.tableData[id].red=true;
        this.tableData[id].tip=true;
      }
    }
  }
</script>
```

**绑定的class是加一个红色的边框，如下：**

```css
.redBorder{
 border:1px solid #f00;
} 
```

在项目中点击button后不出现红色边框和提示错误框，打开debugger查看，发现运行到了这里却没有执行，tableData中的值并没有改变，这个方法在以前使用时会起作用，可能是这次的项目比较复杂引起的，具体原因不明。

**后通过查找资料修改为使用$set来设定修改值，js如下：**

```
this.$set(this.tableData[id],"red",true);
```

但是依然没有起作用，打开debugger发现tableData的值修改成功，没有渲染到页面上，查找的资料也是比较凌乱，并不能解决问题，后请教大神，才知道是数据层次太多，没有触发render函数进行自动更新，需手动调用，调用方式如下:

```js
this.$forceUpdate();
```

**js完整代码如下：**

```js
<script>
  export default {
    data() {
      return {
        tableData:[{id:0,name:"lili",red:false,tip:false}]
      }
    },
    methods: {
      clickBtn(id){
        this.$forceUpdate();
        this.$set(this.tableData[id],"red",true);
        this.$set(this.tableData[id],"tip",true);
   		}
   	}
   }
</script>
```

以上是我解决问题的全过程，有不对的地方请指教。

这篇vue修改对象的属性值后页面不重新渲染的实例就是小编分享给大家的全部内容了，希望能给大家一个参考，也希望大家多多支持脚本之家。





https://m.jb51.net/article/145323.htm