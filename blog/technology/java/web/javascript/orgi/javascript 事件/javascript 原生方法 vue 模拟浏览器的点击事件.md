[TOC]



# javascript 原生方法 vue 模拟浏览器的点击事件

2018-10-29 11:00:18

## 1. 创建自定事件并监听

> 可看看 MDN 的 Evnet， CustomEvent 对象

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>simulate click</title>
</head>
<body>
  <input type="button" value="clickMe" id="demo_click">
  <script>
      
      const btn = document.getElementById('demo_click');
      btn.onclick = function () { //
        alert('click complete!');
      };
      // 1. 创建自定义事件
      const event = new Event('build');  // 自定义事件
       
      document.addEventListener('build', function (e) {
        console.log('dispatch');
      }, false); // dom 监听事件
      document.dispatchEvent(event); // dom触发事件
  </script>
</body>
</html>
 
```

## 模拟 浏览器的鼠标点击事件

```html
  <!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>simulate click</title>
</head>
<body>
  <input type="button" value="clickMe" id="demo_click">
  <script>
      
      const btn = document.getElementById('demo_click');
      btn.onclick = function () { //
        alert('click complete!');
      };
      simulateClick();
    // 2. 模拟 浏览器的鼠标点击事件
    // 2.1 可以触发 onclick 事件（先触发）
    // 2.2 可以触发 addEventListener 事件（后触发）
    // 2.3 jQuery 的事件绑定底层就是 addEventListener ,
      function simulateClick() { // 模拟 浏览器的鼠标点击事件
        const event = new MouseEvent('click', {
          view: window,
          bubbles: true,
          cancelable: true
        });
        btn.dispatchEvent(event);      
      }
  </script>
</body>
</html>
```





## vue

```html
<template>
  <section>
    <a ref="a" :href="url" target="_blank" title=""></a>
  </section>
</template>

<script>

  export default {
    data() {
      return {
        url: 'https://xxxxxx.com/xxx/xxx/xx'
      }
    },
    components: {},
    mounted() {
      this.$refs['a'].click()
    },
    methods: {}
  }

</script>

<style>
</style>


```





<https://blog.csdn.net/palmer_kai/article/details/83502688>