[TOC]



# javascript array数组 es6数组删除指定元素

es6删除数组元素

## 方法一

```javascript
let arr = [{name: '黎明', id: 21111}, {name: '王小二', id: 1111}, {name: '大小二', id: 3222}]
let index = arr.findIndex(item => item.id === 32221111);
if (index !== -1) {
    arr.splice(index, 1)
}
console.log(arr)
```

## 方法二

```js
let arr = [{name: '黎明', id: 21111}, {name: '王小二', id: 1111}, {name: '大小二', id: 3222}]
arr = arr.filter(obj => obj.id !== 3222)
console.log(arr)
```

