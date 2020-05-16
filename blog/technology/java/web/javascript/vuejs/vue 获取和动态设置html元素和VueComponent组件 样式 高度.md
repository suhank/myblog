[TOC]



# vue 获取和动态设置html元素和VueComponent组件 样式 高度

## 1.获取HTML元素高度

```html
<div v-for="data in list">
    <div ref="abc">{{data.id}}</div>
</div>
```

```js
mounted(){
    console.log(this.$refs.abc[0].clientHeight);//获取第一个div元素的高度
    this.$refs.abc[0].style.height = 100 +'px';//动态设置HTML元素高度
}
```

注意：

​     1.此处用到v-for循环，this.\$refs.abc返回的是个HTMLElement数组
​     2.this.\$refs在DOM元素挂载完成后才可以调用
​     3.不可以通过this.$refs.abc[0].clientHeight = 100 +'px'设置高度，因为clientHeight属性是只读的，不允许修改。
​     4.注意加'px'单位 

## 2.获取VueComponent标签生成的元素的高度

```html
<Row v-for="(data,idx) in list" :key="idx">
   <Col ref="leftCol">
      <p>{{data.id}}</p>
   </Col>
   <Col ref="rightCol">
      <p>{{data.id}}</p>
   </Col>
</Row>
```

```js
mounted(){
    for(let i = 0; i < this.list.length; i++){
      console.log(this.$refs.leftCol[i].$el.clientHeight);//获取左边列元素的高度
      console.log(this.$refs.rightCol[i].$el.clientHeight);//获取右边列元素的高度
      this.$refs.leftCol[0].$el.style.height = 100 +'px';//动态设置VueComponent元素高度    
    };
}
```

注意：

​	this.\$refs.leftCol返回的是个VueComponent数组，this.$refs.leftCol[i]返回的是个VueComponent元素，而不是HTMLElement







https://blog.csdn.net/baidu_39355821/article/details/80223652