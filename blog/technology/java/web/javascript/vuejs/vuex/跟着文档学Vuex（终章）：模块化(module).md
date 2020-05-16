[TOC]



# 跟着文档学Vuex（终章）：模块化(module)

2017.09.25 15:27* 字数 914 阅读 3918评论 3喜欢 8

### 一、为什么需要模块化

前面我们讲到的例子都在一个状态树里进行，当一个项目比较大时，所有的状态都集中在一起会得到一个比较大的对象，进而显得臃肿，难以维护。为了解决这个问题，Vuex允许我们将store分割成模块（module），每个module有自己的state，mutation，action，getter，甚至还可以往下嵌套模块，下面我们看一个典型的模块化例子

```
const moduleA = {
  state: {....},
  mutations: {....},
  actions: {....},
  getters: {....}
}

const moduleB = {
  state: {....},
  mutations: {....},
  actions: {....},
  getters: {....}
}

const store = new Vuex.Store({
  modules: {
    a: moduleA,
    b: moduleB
  }
})

store.state.a // moduleA的状态
store.state.b // moduleB的状态
```

### 二、模块的局部状态

模块内部的mutation和getter，接收的第一参数（state）是模块的局部状态对象,rootState

```
const moduleA = {
  state: { count: 0},
  mutations: {
    increment (state) {
      // state是模块的局部状态，也就是上面的state
      state.count++
    }
  },
  getters: {
    doubleCount (state, getters, rootState) {
      // 参数 state为当前局部状态，rootState为根节点状态
      return state.count * 2
    }
  },
  actions: {
    incremtnIfOddRootSum ( { state, commit, rootState } ) {
      // 参数 state为当前局部状态，rootState为根节点状态
      if ((state.cont + rootState.count) % 2 === 1) {
        commit('increment')
      }
    }
  }
}
```

### 三、命名空间（这里一定要看，不然有些时候会被坑）

上面所有的例子中，模块内部的action、mutation、getter是注册在全局命名空间的，如果你在moduleA和moduleB里分别声明了命名相同的action或者mutation或者getter（叫some），当你使用store.commit('some')，A和B模块会同时响应。所以，如果你希望你的模块更加自包含和提高可重用性，你可以添加namespaced: true的方式，使其成为命名空间模块。当模块被注册后，它的所有getter，action，mutation都会自动根据模块注册的路径调用整个命名，例如：

```
const store = new Vuex.Store({
  modules: {
    account: {
      namespaced: true,
      state: {...}, // 模块内的状态已经是嵌套的，namespaced不会有影响
      getters: {      // 每一条注释为调用方法
        isAdmin () { ... } // getters['account/isAdmin']
      },
      actions: {
        login () {...} // dispatch('account/login')
     },
      mutations: {
        login () {...} // commit('account/login')
      },
      modules: {     // 继承父模块的命名空间
        myPage : {
          state: {...},
          getters: {
            profile () {...}     // getters['account/profile']
          }
        },
        posts: {    // 进一步嵌套命名空间
          namespaced: true,
          getters: {
            popular () {...}    // getters['account/posts/popular']
          }
        }
      }
    }
  }
})
```

启用了命名空间的getter和action会收到局部化的getter，dispatch和commit。你在使用模块内容时不需要再同一模块内添加空间名前缀，更改namespaced属性后不需要修改模块内的代码。

### 四、在命名空间模块内访问全局内容（Global Assets）

如果你希望使用全局state和getter，roorState和rootGetter会作为第三和第四参数传入getter，也会通过context对象的属性传入action
若需要在全局命名空间内分发action或者提交mutation，将{ root: true }作为第三参数传给dispatch或commit即可。

```
modules: {
  foo: {
    namespaced: true,
    getters: {
      // 在这个被命名的模块里，getters被局部化了
      // 你可以使用getter的第四个参数来调用 'rootGetters'
      someGetter (state, getters, rootSate, rootGetters) {
        getters.someOtherGetter    // -> 局部的getter， ‘foo/someOtherGetter’
        rootGetters.someOtherGetter // -> 全局getter, 'someOtherGetter'
      }
    },
    actions: {
      // 在这个模块里，dispatch和commit也被局部化了
      // 他们可以接受root属性以访问跟dispatch和commit
      smoeActino ({dispatch, commit, getters, rootGetters }) {
        getters.someGetter    // 'foo/someGetter'
        rootGetters.someGetter    // 'someGetter'
        dispatch('someOtherAction')      // 'foo/someOtherAction'
        dispatch('someOtherAction', null, {root: true})    // => ‘someOtherAction’
        commit('someMutation')    // 'foo/someMutation'
        commit('someMutation', null, { root: true })    // someMutation
      }
    }
  }
}
```

### 五、带命名空间的绑定函数

前面说过，带了命名空间后，调用时必须要写上命名空间，但是这样就比较繁琐，尤其涉及到多层嵌套时（当然开发中别嵌套太多，会晕。。）
下面我们看下一般写法

```
computed: {
  ...mapState({
    a: state => state.some.nested.module.a,
    b: state => state.some.nested.module.b
  }),
  methods: {
    ...mapActions([
      'some/nested/module/foo',
       'some/nested/module/bar'
    ])
  }
}
```

对于这种情况，你可以将模块的命名空间作为第一个参数传递给上述函数，这样所有的绑定会自动将该模块作为上下文。简化写就是

```
computed: {
  ...mapStates('some/nested/module', {
    a: state => state.a,
    b: state => state.b
  })
},
methods: {
  ...mapActions('some/nested/module',[
    'foo',
    'bar'
  ])
}
```

### 六、模块重用

有时我们可能创建一个模块的多个实例，例如：

- 创建多个store，他们共用一个模块
- 在一个store中多次注册同一个模块

如果我们使用一个纯对象来声明模块的状态，那么这个状态对象会通过引用被共享，导致数据互相污染。
实际上Vue组件内data是同样的问题，因此解决办法也是一样的，使用一个函数来声明模块状态（2.3.0+支持）

```
const MyModule = {
  state () {
    return {
      foo: 'far'
    }
  }
}
```

### 七、总结

到这里模块化（module）的内容就已经讲完了，本次主要讲解了module出现的原因，使用方法，全局和局部namespaced模块命名空间，局部访问全局内容，map函数带有命名空间的绑定函数和模块的重用。

### 引用

> [https://vuex.vuejs.org](https://link.jianshu.com/?t=https://vuex.vuejs.org) Vuex官方文档