[TOC]



# vuejs webpack模板里import路径中@符号是什么意思

## 问

用vuejs的[webpack模板](https://github.com/vuejs-templates/webpack/blob/master/template/src/router/index.js)生成的项目中，router/index.js里面有一句：

```
import Hello from '@/components/Hello'
```

这里路径前面的“@”符号表示什么意思？





## 答

这是`webpack`的路径别名，相关定义在这里：

```
resolve: {
    // 自动补全的扩展名
    extensions: ['.js', '.vue', '.json'],
    // 默认路径代理
    // 例如 import Vue from 'vue'，会自动到 'vue/dist/vue.common.js'中寻找
    alias: {
        '@': resolve('src'),
        '@config': resolve('config'),
        'vue$': 'vue/dist/vue.common.js'
    }
}
```

@ 等价于 /src 这个目录，免得你写麻烦又易错的相对路径

参见本人知乎专栏文章：[学习 Vue 你需要知道的 webpack 知识（一）](https://zhuanlan.zhihu.com/p/25829687)



https://segmentfault.com/q/1010000008881292/a-1020000008883630