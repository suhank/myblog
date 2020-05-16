# javascript input的onblur与onchange的区别

onblur：只要input失去焦点就会触发 onblur事件。不管input框里面的值是否改变，都会触发事件。

onchange：只有当input框里面的值发生变化才会执行，这里加了值判断 。

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />  
<script type="text/javascript">
function init(){
    function testChange(){
        this.value=this.value.toUpperCase();
        document.getElementById("onchangeDiv").innerHTML=Number(document.getElementById("onchangeDiv").innerHTML)+1;
    }
    function testblur(){
        this.value=this.value.toUpperCase();
        document.getElementById("onblurDiv").innerHTML=Number(document.getElementById("onblurDiv").innerHTML)+1;
    }
    var input1=document.getElementById("input1");
    var input2=document.getElementById("input2");
    input1.onchange=testChange;
    input2.onblur=testblur;
}
window.onload=init;
</script> 
</head>  
<body>  
<p>触发onchange <span id="onchangeDiv">0</span>次</p>
<p>触发onblur <span id="onblurDiv">0</span>次</p>
<input type="text" id="input1" value="test change"/><br/>
<input type="text" id="input2" value="test blur"/>
</body>  
</html>
```





http://www.manongjc.com/article/66.html