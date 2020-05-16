# vue2.0使用引入axios

发了vue2.0之axios使用详解(一)后，有朋友问如何在实际项目中使用，下面把我平常用的两种方法分享下，自己在实际项目中总结的方法，有不好的地方还请指正，共同提高，谢谢！



## 方法一:直接在.vue文件中引入使用

在.vue文件中使用

```javascript

<script>
  import axios from 'axios';
  export default {
    name: 'news',
    data () {
      return {
      }
    },
    methods: {
      getNewsFn(){
        axios.get(this.dataURL + '/getNews').then((news) => {
            this.news = news.data;
          }
        ).catch((err) => {
            console.log(err);
          }
        );
      }
    }
  }
</script>
      

```



## 方法二:注册为全局的函数

首先在main.js文件中引入

```js

import axios from '../node_modules/axios';
Vue.prototype.axios = axios;
new Vue({
  el: '#app',
  router,
  store,
  axios,
  echarts,
  template: '<App/>',
  components: { App }
});


```



其次在.vue文件中使用

```js
  defaultData(){
        let _this = this;
        _this.axios.get('http://' + _this.$store.state.defaultHttp + '?action_type=comp_news&comp_id=' + _this.$store.state.compValue + '&offset=0&len=' + _this.pageNum, {}, {
            headers: {}
        }).then(function (response) {
            
        }).catch(function (response) {
            console.log(response);
        });
    }  
```

https://blog.csdn.net/binginsist/article/details/71084970