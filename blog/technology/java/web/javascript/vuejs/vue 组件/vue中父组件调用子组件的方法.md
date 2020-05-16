# vue中父组件调用子组件的方法

 

**子组件 ：**

```
<template>
  <div class=“son”>
  </div>
</template>

<script>
  export default {
    data(){
      return {
      }
    },
    methods:{
      fn() {
        alert('this is son fn')
      }
    }
  }
</script>
```

**父组件 ：**

```
父组件： 在子组件中加上ref即可通过this.$refs.ref.method调用
<template>
  <div @click="say">
    <son ref="sonBox"></son>
  </div>
</template>

<script>
  import son from './son.vue';
  export default {
    data(){
      return {
      }
    },
    components: {      
      son
    },
    methods:{
      say() {
        console.log(this.$refs.c1) //返回的是一子组件vue对象，所以可以直接调用其方法
        this.$refs.sonBox.fn(); 
      }
    }
  }
</script>
```

当触发父组件的click事件后，就会触发子组件的fn方法





https://blog.csdn.net/haochangdi123/article/details/80831619