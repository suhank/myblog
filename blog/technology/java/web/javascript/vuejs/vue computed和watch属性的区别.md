# vue computed和watch属性的区别

代码中虽然可以在{{}}中进行一些计算，但是当计算比较复杂时，写在模板中不是那么的友好，这时候就可以使用watch观察和computed计算、methods方法 属性将复杂的逻辑计算从模板中拆出来。这几者的区别有：

- computed属性只在依赖的数据发生变化时，才会重新计算，否则当多次调用computed属性时，调用的其实是缓存；而methods和watch则每调用一次就计算一次；
- 以官方例子（[获取全名](https://link.jianshu.com/?t=https://cn.vuejs.org/v2/guide/computed.html#%E8%AE%A1%E7%AE%97%E5%B1%9E%E6%80%A7-vs-Methods)）为例，computed的写法更加简洁，代码量更少；





https://www.jianshu.com/p/2e0bc028ed36