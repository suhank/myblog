[TOC]



# vue父组件传参给子组件

其实组件之间传参有很多种方法：

1.通过本地存储

2.使用vuex状态管理

今天记录一下第三种方法

 

## 子组件

2.打开项目，在components文件夹下新建一个vue文件，我这边以header.vue为例（这个是子组件）：

template部分

```
<template>
  <section class="chind">
    <div>{{userName}}</div>
  </section>
</template>
```

js部分

```
<script>
export default {
  props:['userName']//主要是通过props来接受父组件传过来的值
}
</script>
```



## 父组件

3.找到App.vue这个文件，如果使用vue-cli创建的项目，里面会自动生成这个文件的（这是父组件）

template部分

```
<template>
  <section class="chind">
    <!--注意下面的userName和子组件props里面的参数要一致-->
    <test :userName="'兔子先生'"></test>
  </section>
</template>
```

解释：\<test :userName="兔子先生">\</test> 兔子先生就是需要传给子组件的值，你也可以把它变成动态的，在当前vue文件的data里面定义即可。

js部分

```
<script>
//引入子组件 名称可以随意 我这边是test
import test from './components/header'
export default {
  components:{
    test
  }
}
</script>
```

 

## 总结：

  1父组件给子组件传参主要是通过props来实现的。

  2在父组件的test标签里面定义一个属性，并且将要传的值 赋值给这个属性。

  3而在子组件里，直接通过props 即可获取你在父组件上的test标签里面定义的属性了。然后可以直接在子组件的页面上使用，无需在data中定义。



http://www.cnblogs.com/Mrrabbit/p/8474338.html