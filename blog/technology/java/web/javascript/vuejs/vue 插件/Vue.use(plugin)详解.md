[TOC]



# Vue.use(plugin)详解

## 前言

阅读此文章，你可以了解到：

- Vue.use(plugin)基础概念（什么是Vue.use(plugin)）
- Vue.use的简单使用
- 为什么在引入Vue-Router、ElementUI的时候需要Vue.use()？而引入axios的时候，不需要Vue.use()?
- Vue-Router、ElementUI在Vue.use()分别做了什么？
- Vue.use原理
- 如何编写一个Vue插件？

------

## 什么是Vue.use(plugin)

`Vue.use`是用来安装插件的。

**用法**：

Vue.use(plugin)

- **如果插件是一个对象**，必须提供 `install` 方法。
- **如果插件是一个函数，它会被作为 install 方法**。install 方法调用时，`会将 Vue 作为参数传入`。
- Vue.use(plugin)调用之后，插件的install方法就会默认接受到一个参数，这个参数就是Vue（原理部分会将）

该方法需要在调用 `new Vue()` 之前被调用。

当 install 方法被同一个插件多次调用，插件将只会被安装一次。(源码解析的时候会解析如何实现)

> **总结：Vue.use是官方提供给开发者的一个api，用来注册、安装类型Vuex、vue-router、ElementUI之类的插件的。**

------

## Vue.use的简单使用

#### 看一下具体的例子：

我们在用Vue-cli3.0里面初始化项目的时候，会生成一个入口文件`main.js`

在`main.js`中，如何我们安装了Vue-Router、Vuex、ElementUI，并且想要在项目中使用，就得在入口文件`main.js`中调用一下Vue.use()

```
Vue.use(ElementUi);
Vue.use(Vuex);
Vue.use(Router);

```

这样就算是完成了对三个插件的安装，我们就可以在组件中调用 `this.$router`、`this.$route`、`this.$store`、`this.$alert()`(ElementUI的弹窗组件)参数(方法)。

------

## 为什么在引入Vue-Router、Vuex、ElementUI的时候需要Vue.use()？而引入axios的时候，不需要Vue.use()?

我们在讲什么是Vue.use的时候，已经说明要用use安装的插件，要么是一个对象里面包含install方法，要么本身就是一个方法(自身就是install方法)。

> 也就是说，这个题目的答案，本质就是：**Vue-Router、Vuex、ElementUI三者都具有install方法，并且插件的运行依赖于install方法里的一些操作，才能正常运行，而axios没有install方法也能正常运行。**

看到这里你一定会疑惑：

- 同样是插件，为什么有些插件要有install方法才能正常运行（如VueRouter），有一些却可以没有install方法也可以使用（如axios）？
- 插件的install方法，可以为我们做什么？

------

## Vue-Router、ElementUI在install里面到底做了什么？

**在探究这个问题之前，我们先看看Vue.use这个方法到底做了什么。**

### **Vue中的use原理**

```
export function initUse (Vue: GlobalAPI) {
  Vue.use = function (plugin: Function | Object) {
    // 获取已经安装的插件
    const installedPlugins = (this._installedPlugins || (this._installedPlugins = []))
    // 看看插件是否已经安装，如果安装了直接返回
    if (installedPlugins.indexOf(plugin) > -1) {
      return this
    }

    // toArray(arguments, 1)实现的功能就是，获取Vue.use(plugin,xx,xx)中的其他参数。
    // 比如 Vue.use(plugin,{size:'mini', theme:'black'})，就会回去到plugin意外的参数
    const args = toArray(arguments, 1)
    // 在参数中第一位插入Vue，从而保证第一个参数是Vue实例
    args.unshift(this)
    // 插件要么是一个函数，要么是一个对象(对象包含install方法)
    if (typeof plugin.install === 'function') {
      // 调用插件的install方法，并传入Vue实例
      plugin.install.apply(plugin, args)
    } else if (typeof plugin === 'function') {
      plugin.apply(null, args)
    }
    // 在已经安装的插件数组中，放进去
    installedPlugins.push(plugin)
    return this
  }
}


```

**总结**:

> Vue.use方法主要做了如下的事：
>
> 1. 检查插件是否安装，如果安装了就不再安装
> 2. 如果没有没有安装，那么调用插件的install方法，并传入Vue实例

我们知道了Vue.use做了什么之后。我们看看那些我们常见的插件，是如何利用这个use方法的。

### **Element中的install**

```
const install = function(Vue, opts = {}) {
  locale.use(opts.locale);
  locale.i18n(opts.i18n);
	// components是ElementUI的组件数组，里面有Dialog、Input之类的组件
 // 往Vue上面挂载组件
  components.forEach(component => {
    Vue.component(component.name, component);
  });

  Vue.use(Loading.directive);
// 自定义一些参数
  Vue.prototype.$ELEMENT = {
    size: opts.size || '',
    zIndex: opts.zIndex || 2000
  };
// 在Vue原型上注册一些方法，这就是为什么我们可以直接使用this.$alert、this.$loading的原因，值就是这么来的。
  Vue.prototype.$loading = Loading.service;
  Vue.prototype.$msgbox = MessageBox;
  Vue.prototype.$alert = MessageBox.alert;
  Vue.prototype.$confirm = MessageBox.confirm;
  Vue.prototype.$prompt = MessageBox.prompt;
  Vue.prototype.$notify = Notification;
  Vue.prototype.$message = Message;

};

```

同样的方法，我们来看看Vue-Router的install又做了什么。

### **Vue-Router中的install**

 我们先把这个install方法的部分拆解出来，只关注其最最核心的逻辑

> 如果不想读源码，可以直接看源码后面的文字简单总结

```
import View from './components/view'
import Link from './components/link'

export let _Vue

export function install (Vue) {
  _Vue = Vue

  const isDef = v => v !== undefined

  const registerInstance = (vm, callVal) => {
    let i = vm.$options._parentVnode
    if (isDef(i) && isDef(i = i.data) && isDef(i = i.registerRouteInstance)) {
      i(vm, callVal)
    }
  }
  Vue.mixin({
    beforeCreate () {
      // 如果该组件是根组件
      if (isDef(this.$options.router)) {
	      //  设置根组件叫_routerRoot
        this._routerRoot = this
        // 根组件的_router属性为，new Vue传进去的router
        // $options是在mains.js中，new Vue里的参数，在这里我们传入的参数，
        this._router = this.$options.router
        this._router.init(this)
        // 通过defineReactive方法，来把this._router.history.current变成响应式的，这个方法的底层就是object.defineProperty
        Vue.util.defineReactive(this, '_route', this._router.history.current)
      } else {
        // 如果该组件不是根组件，那么递归往上找，知道找到根组件的。
        // 因为Vue渲染组件是先渲染根组件，然后渲染根组件的子组件啊，然后再渲染孙子组件。
        // 结果就是每一个组件都有this._routerRoot属性，该属性指向了根组件。
        this._routerRoot = (this.$parent && this.$parent._routerRoot) || this
      }
      registerInstance(this, this)
    },
    destroyed () {
      registerInstance(this)
    }
  })
// 把自身$router代理为this._routerRoot（根组件的）的_router
// 根组件的_router,就是new Vue传入的 router
// 这样就实现了，每一个Vue组件都有$router、$route属性
  Object.defineProperty(Vue.prototype, '$router', {
    get () { return this._routerRoot._router }
  })
// 同理，这样就是把自身的$route，代理到根组件传入的route
  Object.defineProperty(Vue.prototype, '$route', {
    get () { return this._routerRoot._route }
  })
	// 注册 <router-view>组件
  Vue.component('RouterView', View)
	// 注册<router-link>组件
  Vue.component('RouterLink', Link)

  const strats = Vue.config.optionMergeStrategies
  // use the same hook merging strategy for route hooks
  strats.beforeRouteEnter = strats.beforeRouteLeave = strats.beforeRouteUpdate = strats.created
}


```

> 总结：**vue-router的install方法主要帮我们做了如下事情：**
>
> 1. 通过minxi混入的方式，如果自身是根组件，就把根组件的_router属性映射为new Vue传入的router实例(this.$options.router)。
> 2. 如果自身不是根组件，那么层层往上找，直到找到根组件，并用_routerRoot标记出根组件
> 3. 为每一个组件代理`$router`、`$route`属性，这样每一个组件都可以去到`$router`、`$route`
> 4. 注册``、``组件

看到这里，你应该明白了，为什么vueRouter需要install才能使用了吧。

**底层一点的理由就是，vueRouter需要在install方法，对Vue实例做一些自定义化的操作：比如在vue.prototype中添加`$router、$route`属性、注册``组件**

### 为什么axios不需要安装，可以开箱即用？

其实理由也很简单，跟上面需要install的相反的。因为axios是基于Promise封装的库，是完全独立于Vue的，根本不需要挂载在Vue上也能实现发送请求。

而因为VueRouter需要为我们提供`$router、$routers`之类的属性，要依赖与Vue或者操作Vue实例才能实现。

**Vue.use实际上就是Vue实例与插件的一座桥梁。**

------

## 如何自己编写一个插件？

我这里打算分享一下自己以前做的项目里，把axios改写成一个类似插件的思路。

要写其他插件的思路也相似的。

```
// api.js
import login from './login'; // login页面所有的aixos请求封装在此
import home from './home'; // home页面的所有请求封装在此
import detail from './detail'; // 详细页面的请求封装在此

const apiList = {
  ...login,
  ...home,
  ...detail,
};

const install = (Vue) => {
  if (install.installed) return;
  install.installed = true;

  /* 定义属性到Vue原型中
  这样每一个组件就可以通过this.$api.xxx(data) 去发送请求
  */
  Object.defineProperties(Vue.prototype, {
    $api: {
      get() {
        return apiList;
      },
    },
  });
};
// 导出一个对象，里面有install方法。install方法里就把$api代理到Vue中
export default {
  install,
};


```

然后在`mains.j`s中，就可以这样写了

```
import apis from './apis';
Vue.use(apis);
new Vue(参数);
```





https://juejin.im/post/5d8464a76fb9a06b3260ad30