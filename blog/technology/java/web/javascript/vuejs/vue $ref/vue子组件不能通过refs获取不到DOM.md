# vue子组件不能通过refs获取不到DOM

2018.04.29 16:09:16字数 297阅读 3,236

> vue提供一个对象\$refs可以获取DOM，一般在加载组件时候就需要获取DOM，此时可以在created/mounted钩子函数中`this.$refs.xxx`。注意，切记，`this.$refs.xxx`一定要放到`this.$nextTick`的方法内执行，或者在`setTimeout`中执行，延迟时间一般20ms就可以啦

**那么问题来了，为什么在组件的子组件内的created/mounted中添加`this.$nextTick`内执行`this.$refs.xxx`却始终获取不到DOM呢？**

> 其实原因很简单，因为子组件内的虚拟dom是通过父组件通信过来的数据产生的，大概意思就是父组件给子组件一个data列表数据，子组件通过`v-for`将data数据遍历出来，然后你在created/mounted中通过`this.$refs.xxx`来获取这个列表dom，很难获取到，因为这个created/mounted执行的时候，子组件内还没有数据，所以`this.$refs.xxx`无法获取dom，应该应该用watch来监听data，当data接受到父组件传来的数据，再在`this.$nextTick`内获取`this.$refs.xxx`

```javascript
watch:{
  data(){
    this.$nextTick(_=>{
      this.$refs.xxx
    })
  }
}
```





<https://www.jianshu.com/p/7a2174ab06a3>