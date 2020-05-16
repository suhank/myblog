# 使用object.defineProperty()方法监听属性变化

### 问题提出

定义一个对象，改变这个对象的属性，或者说是读取这个对象属性以及属性值的时候，我们自己定义的对象，一般不会知道这个对象什么时候被赋值，什么时候被改变。

```js
var object = {
    name: 'mapbar_front'
};
console.log(object.name);//这个时候我们不知道这个对象的name属性被读取。
object.name = '中国';//这个时候我们也不知道这个对象的name属性被重新赋值。 
```

所以基于上面的示例，我们需要使用一种方法，来实现对象的属性的监听。

### Object.defineProperty(obj, prop, descriptor)

JS原生对象中，有这样的一个方法，就能够实现对象属性的监听。就是通过defineProperty(obj,prop,descriptor)的第三个参数来实现的。

使用示例如下：

```js
var obj = new Object();
var value;
Object.defineProperty(obj,'name',{
    get: function () {
        console.log('get it');
        return value;//必须return一个值，作为name属性的值
    },
    set: function (newvalue) {
        console.log('set it');
        value = newvalue;//同步把value的值进行更新
    }
});
console.log(obj);
console.log(obj.name);//get it
obj.name = 1234;//set it
console.log(obj.name);//get it 
```

### 封装一个监听属性值变动的方法

使用defineProperty方法封装一个监听属性变动的函数。

```js
var object = {
    name: 'liwudi',
    age: 34
}

function changeIt(object) {
    function descripterFun(value) {
        return {
            enumerable: true, // 可枚举
            configurable: false, // 不能再define
            get: function () {
                console.log('get it:' + value);
                return value;
            },
            set: function (newvalue) {
                console.log('set it:' + newvalue);
                value = newvalue;
            }
        }
    }

    for (var i in object) {
        Object.defineProperty(object, i, descripterFun(object[i]))
    }
}


console.log("原始数据:" + JSON.stringify(object));


changeIt(object);

console.log("修改数据开始");
object.name = '我是中国人';
console.log("修改数据结束");

console.log("修改之后数据:" + JSON.stringify(object));

```

### 使用递归，解决深层遍历问题

上面的方式，虽然给一个对象的属性添加上了getter和setter，但是对于深层对象而言，这是不够的。

```Js
function observe(obj){
    if(!obj || typeof obj != 'object'){
        return
    }
    for(var i in obj){
        definePro(obj, i, obj[i]);
    }
}
function definePro(obj, key, value){
    observe(value);
    object.defineProperty(obj, key, { 
        get: function(){
            return value;
        },
        set: function(newval){
            console.log('检测变化',newval);
            value = newval;
        }
    })
}
```



https://blog.csdn.net/mapbar_front/article/details/80472395