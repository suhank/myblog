[TOC]



# easyui combobox插入一条数据到选项中

## 需求:

1.combobox通过url远程加载数据 ,需要在选项的第一条插入一个"-请选择-" 

2.不要再后台获取数据时添加 ,要在前台js中实现

## 思路:

combobox里面有一个loadFilter回调函数，这个函数就是过滤后台返回的数据并显示，那么那就可以获取到后台返回的数据（后台返回的数据是一个数组），并在这个数组前面加一条数据"-请选择-"；然后return就行了；

```js
$("#你的id").combobox({
	loadFilter:function(data){
	//这里你想怎么改变data数据就怎么改变（增删改，都可以），只要格式正确
	return data;
}});
```



## 案例代码:

data-options中写

```html
<select class="easyui-combobox" id="txtSearchTagType" name="tagType"
        style="width:128px;"
        url="${ctx}/sysEnum/getEnumData?enumType=TagType"
        data-options="valueField:'enumValue', textField:'enumName',multiple:false,editable:false,
        loadFilter:function(data){
           //添加'--无--' 选项
           var sysEnum={};
           sysEnum.enumName='<spring:message code="select.nulloption"></spring:message>';
           sysEnum.enumValue='';
           data.splice(0,0,sysEnum)//在数组0位置插入obj,不删除原来的元素
          return data;
        }
"></select>
```

js中写:

```Js
$('#cc').combobox({  
     url:'combobox_data.json',  
     valueField:'id',  
     textField:'text',  
     loadFilter:function(data){  
        var obj={};  
        obj.id='';  
        obj.text='-请选择-'  
        data.splice(0,0,obj)//在数组0位置插入obj,不删除原来的元素  
        return data;  
      }  
 });  
```

 zhu掬水留香 ,我在知道提问得到他的回复,查看官网api才注意到loadFilter事件,记录一下以供遇到同样问题的小伙伴参考







https://blog.csdn.net/qq_34545192/article/details/72899592