# vue2.0 给data对象新增属性，并触发视图更新 $set()

如下代码，给 student对象新增 age 属性

```
data () {
    return {
        student: {
            name: '',
            sex: ''
        }
    }
} 
```

众所周知，直接给student赋值操作，虽然可以新增属性，**但是不会触发视图更新**

```
mounted () {
    this.student.age = 24
} 
```

原因是：**受 ES5 的限制，Vue.js 不能检测到对象属性的添加或删除。因为 Vue.js 在初始化实例时将属性转为 getter/setter，所以属性必须在 data 对象上才能让 Vue.js 转换它，才能让它是响应的。**

要处理这种情况，我们可以使用**$set()**方法，既可以新增属性,又可以触发视图更新。

**但是，值得注意的是，网上一些资料写的$set()用法存在一些问题**，导致在新接触这个方法的时候会走一些弯路！

错误写法：**this.$set(key,value)**（ps: 可能是vue1.0的写法）

```
mounted () {
    this.$set(this.student.age, 24)
} 
```

正确写法：**this.$set(this.data,”key”,value’)**

```
mounted () {
    this.$set(this.student,"age", 24)
}
```





https://blog.csdn.net/panyang01/article/details/76665448