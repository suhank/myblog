

[TOC]



# vue父组件触发子组件事件

## 1. 父组件中获取子组件方法

### $children

```html
<template>
    <div>
        <v-header></v-header>
        <v-content></v-content>
        <v-footer></v-footer>
    </div>
</template>
<script>
    import vHeader from './Header';
    import vContent from './Content';
    import vFooter from './Footer';
 
    export default {
        components:{vHeader,vContent,vFooter},
        created(){
            console.log(this.$children)
            //输出结果[VueComponent,VueComponent,VueComponent],此时可以通过下标获取响应组件，如获取vHeader为this.$children[0].
        }
    }
</script>
```

### $refs

```
<template>
    <div>
        <v-header ref='header'></v-header>
        <v-content ref='content'></v-content>
        <v-footer ref='footer'></v-footer>
    </div>
</template>
<script>
    import vHeader from './Header';
    import vContent from './Content';
    import vFooter from './Footer';
 
    export default {
        components:{vHeader,vContent,vFooter},
        created(){
            console.log(this.$refs);
            //输出结果：{header:VueComponent,content:VueComponent,footer:VueComponent},此时可以通过对象key进行获取响应组件，如vHeader组件获取为this.$refs.header
        }
    }
</script>
```

## 2. 子组件中定义父组件所要触发事件

### methods直接定义

```html
<script>
    export default {
        methods:{
            childAction(val='hello world'){
                console.log(val)
            }
            //此时在父组件，可以通过获取相应子组件，使用对象key值childAction对其进行调用,当前函数形参非必须
        }
    }
</script>
```

### $on

```html
<script>
    export default {
        mounted(){
            this.$on('bridge',(val)=>{
                this.childAction(val);
            });
            ///此时通过$on进行监听中间桥接函数bridge对目的方法childAction进行触发
        },
        methods:{
            childAction(val='hello world'){
                console.log(val)
            }
 
        }
    }
</script>
```

------

## 3. 父组件调用子组件方法

### 父组件Father.vue

```html
<template>
    <div>
        <v-header ref='header'></v-header>
        <v-content ref='content'></v-content>
        <v-footer ref='footer'></v-footer>
        <button @click='emitChild1'>ref与on触发</button>
        <button @click='emitChild2'>ref直接触发</button>
        <button @click='emitChild3'>children与on触发</button>
        <button @click='emitChild4'>children直接触发</button>
    </div>
</template>
<script>
    import vHeader from './Header';
    import vContent from './Content';
    import vFooter from './Footer';
 
    export default {
        components:{vHeader,vContent,vFooter},
        methods:{
            emitChild1(){
                this.$refs.footer.$emit('bridge','你好吗!');
                //打印：  你好吗
                this.$refs.footer.$emit('bridge');
                //打印：hello world
            },
            emitChild2(){
                this.$refs.footer.childAction('你好吗!');
                //打印：  你好吗
                this.$refs.footer.childAction();
                //打印：hello world
            },
            emitChild3(){
                this.$children[2].$emit('bridge','你好吗!');
                //打印：  你好吗
                this.$children[2].$emit('bridge');
                //打印：hello world
            },
            emitChild4(){
                this.$children[2].childAction('你好吗!');
                //打印：  你好吗
                this.$children[2].childAction();
                //打印：hello world
            },
        }
    }
</script>
```

### 子组件Footer.vue

```html
<template>
    <footer>This is footer-component</footer>
</template>
<script>
    export default {
        mounted(){
            this.$on('bridge',(val)=>{
                this.childAction(val);
            });
 
        },
        methods:{
            childAction(val='hello world'){
                console.log(val)
            }
 
        }
    }
</script>
```





https://www.cnblogs.com/zhanghao-repository/p/9037225.html