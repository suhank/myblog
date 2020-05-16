[TOC]



# vue 自定义指令

官网:https://cn.vuejs.org/v2/guide/custom-directive.html

## 前言

Vue.js是一套构建用户界面的渐进式框架（官方说明）。通俗点来说，Vue.js是一个轻量级的，易上手易使用的，便捷，灵活性强的前端MVVM框架。简洁的API，良好健全的中文文档，使开发者能够较容易的上手Vue框架。

本系列文章将结合个人在使用Vue中的一些经(cai)验(keng)和一些案例，对Vue框架掌握的部分知识进行输出，同时也巩固对Vue框架的理解。

## 简述

> Vue除了提供了默认内置的指令外，还允许开发人员根据实际情况自定义指令，它的作用价值在于当开发人员在某些场景下需要对普通DOM元素进行操作的时候。

## 注册自定义指令

Vue自定义指令和组件一样存在着全局注册和局部注册两种方式。先来看看注册全局指令的方式，通过 `Vue.directive( id, [definition] )` 方式注册全局指令，第一个参数为自定义指令名称（**指令名称不需要加 `v-` 前缀，默认是自动加上前缀的，使用指令的时候一定要加上前缀**），第二个参数可以是对象数据，也可以是一个指令函数。

```html
<div id="app" class="demo">
    <!-- 全局注册 -->
    <input type="text" placeholder="我是全局自定义指令" v-focus>
</div>
<script>
    Vue.directive("focus", {
        inserted: function(el){
            el.focus();
        }
    })
    new Vue({
        el: "#app"
    })
</script>
```

> 这个简单案例当中，我们通过注册一个 `v-focus` 指令，实现了在页面加载完成之后自动让输入框获取到焦点的小功能。其中 `inserted` 是自定义指令的钩子函数，后面的内容会详细讲解。

全局注册好了，那么再来看看如何注册局部自定义指令，通过在Vue实例中添加 `directives` 对象数据注册局部自定义指令。

```html
<div id="app" class="demo">
    <!-- 局部注册 -->
    <input type="text" placeholder="我是局部自定义指令" v-focus2>
</div>
<script>
    new Vue({
        el: "#app",
        directives: {
            focus2: {
                inserted: function(el){
                    el.focus();
                }
            }
        }
    })
</script>

```

## 钩子函数

一个指令定义对象可以提供如下几个钩子函数 (均为可选)：

- **bind**：只调用一次，指令第一次绑定到元素时调用。在这里可以进行一次性的初始化设置
- **inserted**：被绑定元素插入父节点时调用 (仅保证父节点存在，但不一定已被插入文档中)。
- **update**：所在组件的 VNode 更新时调用，但是可能发生在其子 VNode 更新之前。指令的值可能发生了改变，也可能没有。但是你可以通过比较更新前后的值来忽略不必要的模板更新 。
- **componentUpdated**：指令所在组件的 VNode 及其子 VNode 全部更新后调用。
- **unbind**：只调用一次，指令与元素解绑时调用。

> 这段是从官方文档copy来的，相信应该都一看就明白的。

那么这几个钩子函数怎么使用呢？先来看看钩子函数的几个参数吧。指令钩子函数会被传入以下参数:

- **el**: 指令所绑定的元素，可以用来直接操作 DOM，就是放置指令的那个元素。
- **binding**: 一个对象，里面包含了几个属性，这里不多展开说明，官方文档上都有很详细的描述。
- **vnode**：Vue 编译生成的虚拟节点。
- **oldVnode**：上一个虚拟节点，仅在 update 和 componentUpdated 钩子中可用。

> 自定义指令也可以传递多个值,可以用javascript表达式字面量传递，看例子：

```
<div v-demo="{ color: 'white', text: 'hello!' }"></div>
<script>
    Vue.directive('demo', function (el, binding) {
    console.log(binding.value.color) // "white"
    console.log(binding.value.text)  // "hello!"
    })
</script>

```

说了这么多理论知识，那么现在就来动手写一个简单的案例吧。假设这样的看一个场景：`当你在阅览某网站的图片时，可能会由于图片资源比较大而加载缓慢，需要消耗一小段时间来呈现到眼前，这个体验肯定是不太友好的（就像网站切换页面，有时候会加载资源比较慢，为了给用户较好的体验，一般都会先出一个正在加载的友好提示页面），所以这个案例的功能就是在图片资源还没加载出来时，先显示默认背景图，当图片资源真正加载出来了之后，再把真实图片放置到对应的位置上并显示出来。`

```html
<div id="app2" class="demo">
    <div v-for="item in imageList">
        <img src="../assets/image/bg.png" alt="默认图" v-image="item.url">
    </div>
</div>
<script>
    Vue.directive("image", {
        inserted: function(el, binding) {
            //为了真实体现效果，用了延时操作
            setTimeout(function(){
                el.setAttribute("src", binding.value);
            }, Math.random() * 1200)
        }
    })
    new Vue({
        el: "#app2",
        data: {
            imageList: [
                {
                    url: "http://consumer-img.huawei.com/content/dam/huawei-cbg-site/greate-china/cn/mkt/homepage/section4/home-s4-p10-plus.jpg"
                },
                {
                    url: "http://consumer-img.huawei.com/content/dam/huawei-cbg-site/greate-china/cn/mkt/homepage/section4/home-s4-watch2-pro-banner.jpg"
                },
                {
                    url: "http://consumer-img.huawei.com/content/dam/huawei-cbg-site/en/mkt/homepage/section4/home-s4-matebook-x.jpg"
                }
            ]
        }
    })
</script>

```

## 源码解读

Vuetify 框架库中，有提供几种自定义指令API，包括浏览器窗口缩放 `v-resize`，浏览器滚动条滑动 `v-scroll` 等自定义指令，现在就来学习一波 Vuetify 中自定义指令源码吧。

### v-resize 自定义指令

在 `src/directives/resize.js` 中，是 `v-resize` 自定义指令操作的核心代码。

```js
function inserted (el, binding) {
    //指令的绑定值，是一个function函数
    const callback = binding.value

    //延时执行函数的毫秒数
    const debounce = binding.arg || 200

    //禁止执行与事件关联的默认动作
    const options = binding.options || { passive: true }

    let debounceTimeout = null
    const onResize = () => {
        clearTimeout(debounceTimeout)
        debounceTimeout = setTimeout(callback, debounce, options)
    }

    //监听窗口缩放
    window.addEventListener('resize', onResize, options)

    //存储监听窗口缩放事件的参数，方便在unbind钩子函数中解除事件绑定的时候使用到
    el._onResize = {
        callback,
        options
    }

    if (!binding.modifiers || !binding.modifiers.quiet) {
        onResize()
    }
}

//绑定的DOM元素被移除时触发
function unbind (el, binding) {
    const { callback, options } = el._onResize

    window.removeEventListener('resize', callback, options)
    delete el._onResize
}

export default {
    //指令名称
    name: 'resize',
    inserted,
    unbind
}

```

可以看到，定义了 `inserted` 和 `unbind` 两个钩子函数，unbind 钩子函数是用来解除监听事件的。inserted 钩子函数中，绑定监听了窗口缩放事件并执行回调函数，并采用简单的函数防抖来防止操作过度频繁，大致的流程就是这样子的。

可能你会发现，上面的代码中，采用的都是es6标准语法写的，对于还不太熟悉es6语法的童鞋来说，可能阅读起来会比较的吃力，那么下面就转换成es5语法来完整的实现这个指令的功能，但是建议还是尽量去熟悉es6标准语法，因为这是前端发展进程中的必然趋势。

```js
function insertedFn (el, binding) {
    var callback = binding.value;
    var debounce = 200;
    var options = {passive: true};
    var debounceTimeout = null;
    var onResize = function () {
        clearTimeout(debounceTimeout);
        debounceTimeout = setTimeout(callback, debounce, options);
    }

    window.addEventListener("resize", onResize, options);

    el._onResize = {
        callback: callback,
        options: options
    };
}

function unbindFn (el, binding) {
    var callback = el._onResize.callback;
    var options = el._onResize.options;
    window.removeEventListener("resize", callback, options);
    delete el._onResize;
}

Vue.directive("resize", {
    inserted: insertedFn,
    unbind: unbindFn
})

```

完整的案例可以点击这里查看：[v-resize自定义指令](https://github.com/webproblem/IntoVue/blob/master/example/directive/v-resize.html)。

> Vuetify 中更多的自定义指令案例源码都可以在 github 中找到，附上 Vuetify 的 github 地址：[github.com/vuetifyjs/v…](https://github.com/vuetifyjs/vuetify)。

## 总结

列举的都是一些简单的Vue自定义指令的知识，在实际项目中的不同场景会存在着各种坑。
Vue自定义指令还可以在图片懒加载的场景下使用，`vue-lazyload` 就是有利用自定义指令实现图片懒加载的，可能的话，后面可以分析一波 `vue-lazyload` 的源码。

## 后记

本着学习和总结的态度写的文章，文中有任何错误和问题，可以在github上指出 [issues](https://github.com/webproblem/IntoVue/issues) 。文中的案例都放置在github上，地址：[github.com/webproblem/…](https://github.com/webproblem/IntoVue)。





https://juejin.im/post/5a3933756fb9a045167d52b1