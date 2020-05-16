# jquery显示隐藏div的几种方法



1、

```
$("#demo").attr("style","display:none;");//隐藏div

$("#demo").attr("style","display:block;");//显示div
```

2、

```
$("#demo").css("display","none");//隐藏div

$("#demo").css("display","block");//显示div
```

3、

```
$("#demo").hide();//隐藏div

$("#demo").show();//显示div
```

4、

```
$("#demo").toggle(//动态显示和隐藏
    function () {
        $(this).attr("style","display:none;");//隐藏div
    },

    function () {
        $(this).attr("style","display:block;");//显示div
    }
);

<div id="demo"></div>
```



注：

$("#demo").show()表示display:block, 
$("#demo").hide()表示display:none; 

1和2中的display:none可以换成visibility:hidden，display:block可以换成visibility:visible.两者的区别是前者隐藏后不占空间，而后者隐藏后会占空间

 

例：

```html
<script type="text/javascript">
    function showDiv1(){
      //$("#test1").attr("style","display:block");
      //$("#test1").show();  
      $("#test1").css("display","block");
    }
    function showDiv2(){
      //$("#test2").attr("style","visibility:visible");
      $("#test2").css("visibility","visible");
    }
 
 
    function hiddenDiv1(){
      $("#test1").hide();
    }
 
    function hiddenDiv2(){
      $("#test2").attr("style","visibility:hidden");
    }
 
  </script>
</head>
<body>
  <div id="test1" style="display:none">aaaaaa</div>  <%--隐藏的div--%>
  <div  id="test2" style="visibility: hidden">bbbbb</div>  <%--隐藏的div--%>
 
  <button onclick="showDiv1()">显示1</button>
  <button onclick="showDiv2()">显示2</button>
  <button onclick="hiddenDiv1()">隐藏1</button>
  <button onclick="hiddenDiv2()">隐藏2</button>
</body>

```





https://blog.csdn.net/skh2015java/article/details/52790121