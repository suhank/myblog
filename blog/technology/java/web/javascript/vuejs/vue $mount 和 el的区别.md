# vue $mount 和 el的区别

两者在使用效果上没有任何区别，都是为了将实例化后的vue挂载到指定的dom元素中。

如果在实例化vue的时候指定el，则该vue将会渲染在此el对应的dom中，反之，若没有指定el，则vue实例会处于一种“未挂载”的状态，此时可以通过$mount来手动执行挂载。

注：如果$mount没有提供参数，模板将被渲染为文档之外的的元素，并且你必须使用原生DOM API把它插入文档中。

例如：

```js
var MyComponent = Vue.extend({
  template: '<div>Hello!</div>'
})

// 创建并挂载到 #app (会替换 #app)
new MyComponent().$mount('#app')

// 同上
new MyComponent({ el: '#app' })

// 或者，在文档之外渲染并且随后挂载
var component = new MyComponent().$mount()
document.getElementById('app').appendChild(component.$el)
```



<https://www.cnblogs.com/Jimc/p/10265272.html>