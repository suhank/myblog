## easyui 为EasyUI的DataGrid某单元格单独赋值示例

EasyUI并没有提供官方的方法为DataGrid的单元格赋值的方法，这点不得不吐槽一下，这么个简单的功能折腾了我好几天，试过N多种方法，都不是很好，都有各式各样的问题。比如你想使用updateRow，不好意思，updateRow后，这行就不能使用grid.datagrid('getChanges', 'updated')来获取这列的数据了。下面是我最后的解决方法，代码如下：



```js
var EasyUIDataGrid = {
 //设置列的值(适用于所有列)
    setFieldValue: function (fieldName, value, index, grid) {
        if (grid == undefined || grid == '') {
            grid = $('#editgrid');
        }
        if (index == undefined || index == '') {
            index = this.GeteditIndex(grid);
            if (index == undefined) {
                index = 0;
            }
        }
        var row = grid.datagrid('getRows')[index];
        if (row != null) {
            var editor = grid.datagrid('getEditor', { index: index, field: fieldName });
            if (editor != null) {
                this.setValueToEditor(editor, value);
            }
            else {
                var view = $('.datagrid-view');
                for (var i = 0; i < view.length; i++) {
                    if ($(view[i]).children(grid.selector).length  > 0) {
                        var view = $(view[i]).children('.datagrid-view2');
                        var td = $(view).find('.datagrid-body td[field="' + fieldName + '"]')[index]
                        var div = $(td).find('div')[0];
                        $(div).text(value);
                    }
                }
                row[fieldName] = value;
            }
            grid.datagrid('clearSelections');
        }
    },
    //设置datagrid的编辑器的值
    setValueToEditor: function (editor, value) {
        switch (editor.type) {
            case "combobox":
                editor.target.combobox("setValue", value);
                break;
            case "combotree":
                editor.target.combotree("setValue", value);
                break;
            case "textbox":
                editor.target.textbox("setValue", value);
                break;
            case "numberbox":
                editor.target.numberbox("setValue", value);
                break;
            case "datebox":
                editor.target.datebox("setValue", value);
                break;
            case "datetimebox":
                editor.target.datebox("setValue", value);
                break;
            default:
                editor.html = value;
                break;
        }
    }
}

 
```

 从上面代码段可以看出来，我的意路是，如果该列有编辑器，那非常好办，直接获取该单元格的编辑器，为该编辑器赋值即可。但如果这列没有编辑器呢？看下面两段关键的代码 。

```js
for (var i = 0; i < view.length; i++) {
    if ($(view[i]).children(grid.selector).length  > 0) {
        var view = $(view[i]).children('.datagrid-view2');
        var td = $(view).find('.datagrid-body td[field="' + fieldName + '"]')[index]
        var div = $(td).find('div')[0];
        $(div).text(value);
    }
}
```

这段代码是datagrid经解析后的dom，我们直接操作dom显示值,有for循环是因为需要判断一个页面中存在多个datagrid的情况，可能不同的版本解析后的dom有所区别，这就需要大家自己慢慢再调试了。。但仅仅这样处理还不够，因为仅是这样处理,我们使用getChanges方法是取不到这个单元格的值的，所以还得为这行数据再赋值一次，就是:**row[fieldName] = value** 这句关键的代码了。



 可能你会想到，那为什么我们不直接写row[fieldName] = value就好了。因为直接写row[fieldName] = value，getChanges方法是可以取到值的，但界面上却不能及时的将值显示出来，必须强制刷新这行数据才能显示，而刷新又会丢失掉该行中另外一些编辑器的值，所以，把显示和真正赋值两者分开，就较好的解决这个问题了。

 



http://www.lmwlove.com/ac/ID1166