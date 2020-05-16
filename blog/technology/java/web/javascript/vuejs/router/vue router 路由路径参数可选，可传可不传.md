# vue router 路由路径参数可选，可传可不传

2018年06月08日 11:19:54 [晏紫苏_cc](https://me.csdn.net/yanzisu_congcong) 阅读数：2587

 

由于项目需求会遇到进入某个页面获取模默认信息，但有时需要传递一个id获取对应的信息，为了兼容同一个页面的路由的参数，可传可不传，可以针对路由做以下处理:

```javascript
{
    path: '/index/:id?', //获取参数：this.$route.params.id
    name: 'index',
    component: Index
}

```





https://blog.csdn.net/yanzisu_congcong/article/details/80620463