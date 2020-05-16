# vue中限制input只能输入数字-示例

## 方法1  

```javascript
new Vue({
    el:'#demo',
    data:{
        oldNum:0    
    },
    computed:{
        inpNum:{
            get:function(){
                return this.oldNum;
            },
            set:function(newValue){
                this.oldNum=newValue.replace(/[^\d]/g,'');
            }
        }
    }
})
```

## 方法二：

```javascript
<input type='text' @input="handleInput" :value="val"/>
 
handleInput(e){
	this.val=e.target.value.replace(/[^\d]/g,'');
    this.$forceUpdate()
}
```





https://blog.csdn.net/zpcqdkf/article/details/80427737