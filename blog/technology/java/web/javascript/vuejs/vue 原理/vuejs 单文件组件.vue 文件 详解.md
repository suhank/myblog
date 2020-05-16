[TOC]



# vuejs 单文件组件.vue 文件 详解

## 前言

　　vuejs 自定义了一种.vue文件，可以把html, css, js 写到一个文件中，从而实现了对一个组件的封装， 一个.vue 文件就是一个单独的组件。由于.vue文件是自定义的，浏览器不认识，所以需要对该文件进行解析。 在webpack构建中，需要安装vue-loader 对.vue文件进行解析。在 sumlime 编辑器中，我们 书写.vue 文件，可以安装vue syntax highlight 插件，增加对文件的支持。

　　用vue-cli 新建一个vue项目，看一下.vue文件长什么样？ 在新建项目的过程中，命令行中会询问你几个问题，当询问是否安装vue-router 时，这里先选择否。项目完成后，我们看到src  目录下有一个componet 目录，里面有一个 Hello.vue 文件，内容如下，这里对template 里面的内容做了一些删减

```vue
<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <h2>Essential Links</h2>
  </div>
</template>

<script>
export default {
  name: 'hello',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App'
    }
  }
}
</script>

<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
```

　　可以看到，在 .vue 文件中， template 中都是html 代码，它定义了在页面中显示的内容，由于里面还有变量，也可以说定义了一个模版；script中都是js 代码，它定义这个组件中所需要的数据和及其操作，style 里面是css 样式，定义这个组件的样式，scoped 表明这里写的css 样式只适用于该组件，可以限定样式的作用域。

## **script 标签中 export defalut 后面的对象的理解。**

　　在不使用.vue 单文件时，我们是通过 Vue 构造函数创建一个 Vue 根实例来启动vuejs 项目，Vue 构造函数接受一个对象，这个对象有一些配置属性 el, data, component, template 等，从而对整个应用提供支持。

```
new Vue({
  el: '#app',
  data: {
        msg: "hello Vue"     
  }  
})
```

　　在.vue文件中，export default 后面的对象 就相当于 new Vue() 构造函数中的接受的对象，它们都是定义组件所需要的数据（data）, 以及操作数 据的方法等， 更为全面的一个 export default 对象，有methods, data, computed, 这时可以看到, 这个对象和new Vue() 构造函数中接受的对象是一模一样的。但要注意data 的书写方式不同。在 .vue 组件, data 必须是一个函数，它return（返回一个对象），这个返回的对象的数据，供组件实现。

　   把项目中自带的hello.vue 内容清空，我们自己写一个组件来体验一下这种相同。

```vue
<template>
  <div class="hello">
    <input type="txet" placeholder="请输入文字" v-model="msg" @keypress.enter="enter">
    <p>{{upper}}</p>
  </div>
</template>

<script>
export default {
  data () {
    return {
      msg: 'hello'
    }
  },
  methods:{
    enter () {
      alert(this.msg);
    }
  },
  computed: {
    upper () {
      return this.msg.toUpperCase();
    }
  }
}
</script>

<style scoped>
  input {
    width: 200px;
    height: 20px;
  }
  p {
    color: red;
  }
</style>
```

　　页面中有一个input输入框，当进行输入的时候，输入框下面的内容会进行同步显示，只不过它是大写，当输入完成后，按enter键就会弹出我们输入的内容。获取用户输入的内容，我们用的是v-model 指令，这个指令将用户输入的内容绑定到变量上，并且它响应式的，我们的变量值会随着用户输入的变化而变化，也就是说我们始终获取的都是用户最新的输入。下面大写的显示，用的是computed属性，弹窗则是给绑定了一个keypress事件，通过描述，你会发现，它简直就是一个vue实例，实际上它就是个vue实例。每一个vue组件都是一个vue实例，更容易明白 export default 后面的对象了。

## **父子组件之间的通信**

　　每一个.vue 文件就是一个 组件，组件和组件相互组合，就成了一个应用，这就涉及到的组件和组件之间的通信，最常用的就是父子之间的通信。在vue 中， 在一个组件中通过 import 引入另一个组件，这个组件就是父组件，被引入的组件就是子组件。

　　在我们这个vue-cli 项目中，src 文件夹下有一个App.vue 文件，它的script标签中，import Hello from './components/Hello'，那么 App.vue 就是父组件，components 文件夹下的Hello.vue 就是子组件。父组件通过props 向子组件传递数据，子组件通过自定义事件向父组件传递数据。

　　父组件向子组件传值, 它主要是通过元素的属性进行的. 在App.vue 的template中,有一个 <hello></hello>, 这就是我们引入的子组件.  给其添加属性如 mes-father="message from father";  父组件将数据传递进去,子组件需要接收才能使用. 怎样接收呢?

　　在Hello.vue 中, export default 后面的对象中,添加一个字段props, 它是一个数组, 专门用来接收父组件传递过来的数据. props: ["mesFather"], 这里定义了mesFather 字符串, 和父组件中定义的元素的属性一一对应. 但是我们在父组件,就是在 <hello /> 元素中定义的属性是mes-father， 没有一一对应啊?  这主要是因为，在html 元素中大小写是不敏感的。 如果我们写成<hello mesFather="message from father"></hello>， 里面的mesFather  就会转化成mesfather, 相当于我们向子组件传递了一个mesfather数据， 如果在js 文件中，我们定义 props: ["mesFather"]，我们是接受不到数据的，因为js 是区分大小写的， 只能写成props: ["mesfather"].  但是在js 文件中，像这种两个单词拼成的数据，我们习惯用驼峰命名法，所以vue 做了一个转化，如果在组件中属性是 - 表示，它 自动会转化成驼峰式。  传进来的数据是mes-father, 转化成mesFather, 我们在js 里面写mesFather, 一一对应，子组件可以接受到组件。 props 属性是和data， methods 属性并列的，属同一级别。 props 属性里面定义的变量，在 子组件中的template 中可以直接使用。

　　App.vue 的template 更改如下：

```
<template>
  <div id="app">
    <img src="./assets/logo.png">
    <hello mes-father="message from father"></hello>
  </div>
</template>
```

　　Hello.vue组件，这里还是把项目中自带的Hello.vue 清空，自己写，变成如下内容

```
<template>
  <div class="hello">
    <p>{{mesFather}}</p>
  </div>
</template>

<script>
export default {
  props:['mesFather']
}
</script>
```

　　这时，在页面中看到 message from father 字样，父元素向子元素传递数据成功。

　　子组件向父组件传递数据，需要用到自定义事件。 例如，我们在Hello.vue ，写入一个input, 接收用户输入，我们想把用户输入的数据传给父组件。这时，input 需要先绑定一个keypress 事件，获取用户的输入，同时还要发射自定义事件，如valueUp, 父组件只要监听这个自定义事件，就可以知道子组件要向他传递数据了。子组件在发射自定义事件时，还可以携带参数，父组件在监听该事件时，还可以接受参数，参数，就是要传递的数据。

　　在 Hello.vue template中，添加一个input输入框，给它一个v-model 获取用户的输入，再添加keypress的事件，用于发射事件，传输数据。script 中添加data，定义变量来获取用户的输入，添加methods 来处理keypress事件的处理函数enter, 整个Hello.vue 文件如下



```
<template>
  <div class="hello">
    <!-- 添加一个input输入框 添加keypress事件-->
    <input type="text" v-model="inputValue" @keypress.enter="enter">
    <p>{{mesFather}}</p>
  </div>
</template>

<script>
export default {
  props:['mesFather'],

  // 添加data, 用户输入绑定到inputValue变量，从而获取用户输入
  data: function () {
    return {
      inputValue: ''  
    }
  },
  methods: {
    enter () {
      this.$emit("valueUp", this.inputValue) 
      //子组件发射自定义事件valueUp, 并携带要传递给父组件的值，
      // 如果要传递给父组件很多值，这些值要作为参数依次列出 如 this.$emit('valueUp', this.inputValue, this.mesFather); 
    }
  }
}
</script>
```



　　在App.vue 中， template中hello 组件绑定一个自定义事件，@valueUp =“receive”, 用于监听子组件发射的事件，再写一个 p 元素，用于展示子组件传递过来的数据，<p>子组件传递过来的数据 {{ childMes }}</p>

相应地，在scrpit中，data 中，定义一个变量childMes, 并在 methods 中，定义一个事件处理函数reciever。整个App.vue修改如下：



```
<template>
  <div id="app">
    <img src="./assets/logo.png">

    <!-- 添加自定义事件valueUp -->
    <hello mes-father="message from father" @valueUp="recieve"></hello>

    <!-- p元素，用于展示子组件传递过来的数据 -->
    <p>子组件传递过来的数据 {{childMes}}</p>
  </div>
</template>

<script>
import Hello from './components/Hello'

export default {
  name: 'app',
  components: {
    Hello
  },
  // 添加data
  data: function () {
    return {
      childMes:''
    }
  },

  // 添加methods，自定义事件valueUp的事件处理函数recieve
  methods: {
    recieve: function(mes) { // recieve 事件需要设置参数，这些参数就是子组件传递过来的数据，因此，参数的个数，也要和子元素传递过来的一致。
      this.childMes = mes;
    }
  }
}
</script>
```



　　这时在input中输入内容，然后按enter键，就以看到子组件传递过来的数据，子组件向父组件传递数据成功。 

　　当在input输入框中输入数据，并按enter键时，它会触发keypress.enter事件，从而调用事件处理函数enter， 在enter 中， 我们发射了一个事件valueUp， 并携带了一个参数，由于在<hello @valueUp=”recieve”></hello> 组件中， 我们绑定valueUp 事件，所以父组件在时刻监听valueUp 事件， 当子组件发射value 事件时，父组件立刻捕获到，并立即调用它的回调函数receive, 在receive 中，我们获取到子组件传递过来的数据，并赋值了data 中的变量childMes, 由于data 数据发生变化，从而触发dom更新，页面中就显示子组件传递过来的内容。

　　其实在子组件中， props 最好的写法是props 验证，我们在子组件Hello.vue中写 props:['mesFather'], 只是表达出，它接受一个参数mesFather, 如果写成props 验证，不仅能表达出它需要什么参数，还能表达参数类型，并且如有错误，vue 会做出警告。现在把props 改成props 验证的写法, Hello.vue 中的js中的props修改如下：



```
props： {
      'mesFather': {
          type: String,
          default: 'from father',
          required:true
      }
  }
```



　　如果是组件与组件之间的通信非常复杂，不光是父子组件，还有兄弟组件，那就需要用到状态管理，vuex





https://www.cnblogs.com/SamWeb/p/6391373.html