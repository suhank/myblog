[TOC]



# vue this.$router.push()传参

2018-01-30 19:34:57



## 1  params 传参

注意⚠️：params传参 ，路径不能使用path 只能使用name,不然获取不到传的数据

```js
this.$router.push({name: 'dispatch', params: {paicheNo: obj.paicheNo}})
```

取数据：

```js
this.$route.params.paicheNo
```

## 2 query传参

```js
this.$router.push({path: '/transport/dispatch', query: {paicheNo: obj.paicheNo}})
```

取数据：

```js
this.$route.query.paicheNo
```





<https://blog.csdn.net/e87e09e11/article/details/79209764>