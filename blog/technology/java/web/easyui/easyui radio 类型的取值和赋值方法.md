[TOC]



# easyui radio 类型的取值和赋值方法

## 1.HTML 文件

```html

    <tr id="client_check1">
        <th>委托人证件类型:</th>
        <td><input id="certType" type="radio" name="certType"
            class="easyui-validatebox" checked="checked" value="身份证"><label>身份证</label></input>
            <input id="certType" type="radio" name="certType"
            class="easyui-validatebox" value="护照"><label>护照</label></input>
        </td>
    </tr>
    
    
```

## 2.JS的取值和赋值方法

```js
    //取值方法
    function checkRadio(){
        alert($("input[name='certType'][checked]").val());
    }

    赋值方法
    //将委托人的信息进行赋值
    if (data.certType == "身份证") {
        $("input[name='certType'][value='身份证']").attr("checked",true); 
    }else if(data.certType == "护照"){
        $("input[name='certType'][value='护照']").attr("checked",true); 
    }
    
    
    radio 取值：
    JS代码
    $("input[name='radioName'][checked]").val(); 
    
    radio 赋值, 选中值为2的radio：
    JS代码
    $("input[name='radioName'][value=2]").attr("checked",true);
```



https://www.cnblogs.com/mr-wuxiansheng/p/6399232.html