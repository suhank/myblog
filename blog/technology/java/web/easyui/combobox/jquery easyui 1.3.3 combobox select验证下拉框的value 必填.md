# jquery easyui 1.3.3 combobox select验证下拉框的value 必填

 

在使用jquery easyui 1.3.3的时候，发现combox下来框只验证text不验证value,满足不了需求，现进行扩展！

```js
<select id="cc" class="easyui-combobox">     
    <option value="">请选择</option>     
    <option value="1">bitem1</option>     
    <option value="2">ditem2</option>       
</select>  
```

扩展验证代码：

```js
/** 
 * 扩展combox验证，easyui原始只验证select text的值，不支持value验证 
 */  
$.extend($.fn.validatebox.defaults.rules, {  
    comboxValidate : {  
        validator : function(value, param,missingMessage) {  
            if($('#'+param).combobox('getValue')!='' && $('#'+param).combobox('getValue')!=null){  
                return true;  
            }  
            return false;  
        },  
        message : "{1}"  
    }  
});  
```

使用方法：

```js
<select class="easyui-combobox" id="cc" validType="comboxValidate['cc','请选择状态']">     
    <option value="">请选择</option>     
    <option value="1">bitem1</option>     
    <option value="2">ditem2</option>       
</select>    
```

注： validType="comboxValidate['cc','请选择状态']" ： 第一个参数（CC）表示要验证的组件ID，第二个参数表示验证的提示信息 class="easyui-combobox"：标示这是一个easyui combox元素 以上二个配置缺一不可





https://blog.csdn.net/lht0211/article/details/44492501