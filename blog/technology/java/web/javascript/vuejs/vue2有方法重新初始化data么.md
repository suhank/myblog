# vue2有方法重新初始化data么



重新调用data方法，即可重置

```
Object.assign(this.$data, this.$options.data())
```



 [vm.$options](https://cn.vuejs.org/v2/api/index.html#vm-options)

- **类型**：`Object`

- **只读**

- **详细**：

  用于当前 Vue 实例的初始化选项。需要在选项中包含自定义属性时会有用处：

  ```
  new Vue({
    customOption: 'foo',
    created: function () {
      console.log(this.$options.customOption) // => 'foo'
    }
  })
  ```







https://segmentfault.com/q/1010000007235324

https://cn.vuejs.org/v2/api/index.html