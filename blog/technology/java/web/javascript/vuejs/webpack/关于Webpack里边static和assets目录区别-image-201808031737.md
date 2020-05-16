[TOC]



# 关于Webpack里边static和assets目录区别

## 说明

assests放置的是组件的资源，static放置的是非组件的资源。

比如说即static下的文件资源作为src路径传入组件中，文件的path是可以被解析了，而assets则不行。

比如你写一个音乐播放器，类似的播放键和上一曲下一曲这些图标就应该作为组件资源放在assests里面，而不同音乐选集的封面这些是应该作为文件资源放在static下。

## 结论

static放经常更改的图，比如商品图之类的。

assets放很少修改的比如logo、小图标等。

static一般使用绝对路径，/static/...

```
var products = [{
  img: '/static/img/products/1.png',
  name: 'Product 1'
}, {
  img: '/static/img/products/2.png',
  name: 'Product 2'
}, {
  img: '/static/img/products/3.png',
  name: 'Product 3'
}, {
  img: '/static/img/products/4.png',
  name: 'Product 4'
}]
```

assets一般使用相对路径

```
var products = [{
  img: require('@/assets/products/1.png'),
  name: 'Product 1'
}, {
  img: require('@/assets/products/2.png'),
  name: 'Product 2'
}, {
  img: require('@/assets/products/3.png'),
  name: 'Product 3'
}, {
  img: require('@/assets/products/4.png'),
  name: 'Product 4'
}] 
```





https://blog.csdn.net/weixin_42283462/article/details/80621689