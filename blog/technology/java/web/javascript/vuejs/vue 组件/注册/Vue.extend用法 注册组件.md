[TOC]



# Vue.extend用法 注册组件

`Vue.extend` 是构造一个组件的语法器。

## 用法

Vue.extend ( options )，options 是对象；
使用基础Vue构造器，创建一个子类，参数是一个包含组件选项的对象，data选项是特例，它必须是函数。

## 1. 第一种用法--挂在到元素上

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vue@2.5.17/dist/vue.min.js"></script>
    <title>1. 第一种用法--挂在到元素上.html</title>
</head>
<body>

<div id="app">
</div>


<script type="text/javascript">
    let Profile = Vue.extend({
        template: '<p>{{firstName}} - {{lastName}} - {{alias}}</p>',
        data() {
            return {
                firstName: 'xu',
                lastName: 'zhu',
                alias: 'dong'
            }
        }
    });

    // 第一种用法
    // 创建 Profile实例,并挂载到一个元素上
    new Profile().$mount('#app')

</script>
</body>
</html>

```

输出如下： 

```
xu - zhu - dong
```

  

## 2. 第二种用法--将组件注册到`Vue.component` 全局方法里面

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vue@2.5.17/dist/vue.min.js"></script>
    <title>2. 第二种用法--将组件注册到Vue.component 全局方法里面.html</title>
</head>
<body>

<div id="app">
    <apple/>
</div>


<script type="text/javascript">
    let Profile = Vue.extend({
        template: '<p>{{firstName}} - {{lastName}} - {{alias}}</p>',
        data() {
            return {
                firstName: 'xu',
                lastName: 'zhu',
                alias: 'dong'
            }
        }
    });

    Vue.component('apple', Profile)
    
    new Vue({
        el: '#app'
    })
</script>
</body>
</html>

```

 

## 3. 第三种方法--将组件注册为局部组件

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/vue@2.5.17/dist/vue.min.js"></script>
    <title>3. 第三种方法--将组件注册为局部组件.html</title>
</head>
<body>

<div id="app">
    <apple/>
</div>


<script type="text/javascript">
    let Profile = Vue.extend({
        template: '<p>{{firstName}} - {{lastName}} - {{alias}}</p>',
        data() {
            return {
                firstName: 'xu',
                lastName: 'zhu',
                alias: 'dong'
            }
        }
    });

    new Vue({
        el: '#app',
        data: {},
        components: {
            apple: Profile
        }
    })
</script>
</body>
</html>

```



<https://www.cnblogs.com/xuzhudong/p/8631088.html>