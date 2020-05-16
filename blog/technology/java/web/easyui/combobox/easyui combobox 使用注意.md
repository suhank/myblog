# easyui combobox 使用注意

## data-options方式:(有些属性不生效)

```js
<select class="easyui-combobox" id="textCarrierNameCn"  name="carrierSysNo"
      style="width:128px;"
      url="${ctx}/mstCarrier/getCarrierList"
      data-options="valueField:'sysNo', textField:'carrierName',multiple:false,editable:false,required:true
         "></select>
```

## js方式:推荐

```js
function configCombobox() {
    $('#textCarrierNameCn').combobox({
        url: '${ctx}/mstCarrier/getCarrierList',
        valueField: 'sysNo',
        textField: 'carrierName',
        multiple: false, editable: false, required: true,
        onLoadSuccess: function () {
            //设置默认值只有js方式才能生效
            $('#textCarrierNameCn').combobox('setValue', '2');
        }
    });
}

function init() {
    //配置下拉选框
   configCombobox()
}

$(function(){
    init()
})
```

## 总结

以后都用JavaScript方式都可以生效html代码也比较清晰