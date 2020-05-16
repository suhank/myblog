

[TOC]

# vue router 怎么样才能获取到当前的路由路径呢

## 问题

就是那个路径 比如主页是 "/" 这个东西
我用的nuxt.js
就是要获取

```
 <nuxt-link to="/"></nuxt-link>
```

这个to 里面的路径 





## 答:

```
this.$router.path
```







https://segmentfault.com/q/1010000009289159