[TOC]



# vue中$emit如何取得返回值？

## 问题

问题对人有帮助，内容完整，我也想知道答案1问题没有实际价值，缺少关键内容，没有改进余地

感谢大家帮帮忙

html部分

```html
<div class="vue">
    <!-- 组件绑定了一个事件 -->
    <test-component v-on:on-ok="ok"></test-component>
</div>
```

js部分

```js
//组件定义
var tc = {
    template: '<div><button v-on:click="ok">click ok</button></div>',
    methods: {
        ok:function(){
            this.$emit('on-ok');
            //我想在这儿得到 'haha' 这个字符串
        }
    }
};

//根实例
new Vue({
    el:'.vue',
    components:{
        'test-component': tc
    },
    methods: {
        ok: function(){
            return 'haha';
        }
    }
});
```

## 答案

### 方案1

```
//组件中
this.$emit('on-ok', function(str){
    alert(str);
});

//根实例中
ok: function(callback){
    callback('haha');
}
```



### 方案2

在子组件想获取父组件的方法，可以使用this.$parent.ok()

//组件定义

```js
var tc = {
    template: '<div><button v-on:click="ok">click ok</button></div>',
    methods: {
        ok:function(){
            var haha = this.$parent.ok()
            alert(haha)
        }
    }
};
```



<https://segmentfault.com/q/1010000011143372>