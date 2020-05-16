# easyui combobox设置不可编辑

JSP：

```Html
<div>
	<select id="myCombobox" class="easyui-combobox" name="dept" style="width:100px; height:25px">
		<option value="2017/7">2017年7月</option>
		<option value="2017/8">2017年8月</option>
		<option value="2017/9">2017年9月</option>
		<option value="2017/10">2017年10月</option>
		<option value="2017/11">2017年11月</option>
		<option value="2017/12">2017年12月</option>
	</select>
</div>

<script type="text/javascript">
	$(function(){
	    $('#myCombobox').combobox({    
	     required:true,    
	     multiple:false, //多选
	     editable:false  //是否可编辑
	     });  
	})
</script>
```

获取值

```
var comboboxValue = $('#myCombobox').combobox('getValue');
```

