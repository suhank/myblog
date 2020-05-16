# vue子组件传参给父组件

[关于父组件传参给子组件，可以看我另一篇文章](http://www.cnblogs.com/Mrrabbit/p/8474338.html)

 

教程开始：

我们要实现的效果是：在子组件的Input框输入，父组件中实时更新显示。(也就是把子组件中的数据传给父组件)

## 一、子组件代码

template部分

```
<template>
  <section>
    <input v-model="message"/>
  </section>
</template>
```

js部分

```
<script>
export default {
  data(){
    return {
      message:'请输入'
    }
  },
//通过watch来监听message是否改变
  watch:{
    'message':function(){
      this.$emit('getMessage',this.message);//主要是通过$emit方法来实现传参的方式，第一个参数是自定义事件名称，第二个则是要传的数据
    }
  }
}
</script>
```



其实不一定要用wacth来监听数据变化，直接给input加个输入事件，也是可以的。

 

## 二、父组件代码

template部分



```
<template>
  <div id="app">
    <!--getMessage是子组件那边定义的 自定义事件-->
    <test  @getMessage="getVal"></test>
    <div>
      子组件输入的值:{{chindVal}}
    </div>
  </div>
</template>
```



js部分



```
<script>
import test from './components/header'
export default {
  data(){
    return {
      chindVal:'',
    }
  },
  components:{
    test
  },
  methods:{
    getVal(msg){//msg就是传过来的数据了  这只是个形参  名字可以随意
      this.chindVal=msg;//然后将数据赋值给chindVal
    }
  }
}
</script>
```



 

## 总结：

1.子组件传参给父组件主要是通过$emit方法来实现的。

2.在子组件中使用$emit方法，一般它接受两个参数，第一个是自定义事件(这个事件在父组件中需要用到)，第二个参数就是需要传的数据了。

3.而在父组件里，在调用的标签上引用子组件定义的那个事件，然后事件绑定一个函数。在函数里面进行赋值即可。





http://www.cnblogs.com/Mrrabbit/p/8482528.html