[TOC]



# mpvue使用小程序组件和Vue 组件

## 组件

### Vue 组件

组件是整个 Vue.js 中最复杂的部分，当然要支持 [官方文档：组件](https://cn.vuejs.org/v2/guide/components.html) 。

**有且只能使用单文件组件（.vue 组件）的形式进行支持。**其他的诸如：动态组件，自定义 render，和`<script type="text/x-template">` 字符串模版等都不支持。原因很简单，因为我们要预编译出 wxml。

如果未来小程序支持了动态增删改查 wxml 节点信息，那我们就能做到全支持。

详细的不支持列表：

- 暂不支持在组件引用时，在组件上定义 click 等原生事件、v-show（可用 v-if 代替）和 class style 等样式属性(例：`<card class="class-name"> </card>` 样式是不会生效的)，因为编译到 wxml，小程序不会生成节点，建议写在内部顶级元素上。
- Slot（scoped 暂时还没做支持）
- 动态组件
- 异步组件
- inline-template
- X-Templates
- keep-alive
- transition
- class
- style

### 小程序组件

mpvue 可以支持小程序的原生组件，比如： `picker,map` 等，需要注意的是原生组件上的事件绑定，需要以 `vue` 的事件绑定语法来绑定，如 `bindchange="eventName"` 事件，需要写成 `@change="eventName"`

示例代码：

```
<picker mode="date" :value="date" start="2015-09-01" end="2017-09-01" @change="bindDateChange">
    <view class="picker">
      当前选择: {{date}}
    </view>
</picker>
```





http://mpvue.com/mpvue/#_15