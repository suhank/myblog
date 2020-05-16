[TOC]



# vue实现动态注册并渲染组件

目前有个需求：需要动态的注册并加载一个dir目录下的所有组件。目录结构如下：

--index.vue

--dir

----comp1.vue

----comp2.vue

.....

按照常规做法是在index.vue中直接这样引入并注册：

```js
import comp1 from './dir/comp1.vue'
import comp2 from './dir/comp2.vue'
....
export default {
  components: {
    comp1,
    comp2...
  }
}
```

如果组件少还是很方便的，如果比较多比如30个, 100个，一个一个的import进来则很麻烦，因此如果能够动态遍历一个数组进行动态注册并渲染的话则很方便。

## 第一种方案

一个解决思路是在index.vue中利用Vue.component注册全局组件+

<component :is="app">进行动态渲染：

```html
<template>
    <div>
        <component v-for="app in comps" :is="app"></component>
    </div>
</template>
<script>
  import Vue from 'vue'
  export default {
    data () {
      return {
        comps: ['comp1', 'comp2']
      }
    },
    created () {
      this.comps.forEach(app => {
         Vue.components(app, res => require([`./dir/${app}.vue`], res))
      })
    }

  }
</script>
```

上面注册全局组件的方式看似很不错，但是这样的话每个组件都注册成了全局的了，这不是我们想要的结果。因此我们朝着注册成为局部组件的方向继续改进......

## 第二种方案

在index.vue中利用异步加载require + component标签:

```html
<template>
    <div>
        <component v-for="app in comps" :is="app"></component>
    </div>
</template>
<script>
  import Vue from 'vue'
  export default {
    data () {
      return {
        comps: ['comp1', 'comp2'],
        apps: []
      }
    },
    created () {
      this.comps.forEach(app => {
         this.apps.push({app: require(`./dir/${app}.vue`)})
      })
    }

  }
</script>
```

perfect ! ! !

## 第三种方案

利用render函数封装成一个类似于component标签的功能，不过可以在其中进行一些扩展：

dir目录下新建一个文件asyncLoadComp.vue:

```html
<script>
  export default {
    render (h, cxt) {
      return h(require(`./dir/${this.app}.vue`), {
        props: {
          prop: this.prop
        }
      })
    },
    props: {
      app: String,
      prop: Object
    }
  }
</script>
<style></style>
```

index.vue中使用：

```html
<template>
    <div>
        <async-load-comp v-for="comp in comps" :app="comp" :prop="prop"></async-load-comp>
    </div>
</template>
<script>
  import asyncLoadComp from './asyncLoadComp.vue'
  export default {
    components: {
      asyncLoadComp
    },
    data () {
      return {
        comps: ['comp1', 'comp2'],
        prop: {name: '张三'}
      }
    }
  }
</script>
<style></style>
```



<https://zhuanlan.zhihu.com/p/35535469>