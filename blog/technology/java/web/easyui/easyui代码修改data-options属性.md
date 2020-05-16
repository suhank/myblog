# easyui代码修改data-options属性

  

记录一下easyui代码修改data-options属性方法。

例如常用的验证必填。

$("#e_Suttle2").textbox({ required: false });

要注意，是什么控件就转换什么控件。

例如

$("#e_Suttle2").combobox({ required: false });

$("#e_Suttle2").datetimebox({ required: false });

再就是，设置的时候直接用json方式传参，部使用string，注意，传json对象。





https://blog.csdn.net/lanwilliam/article/details/79819765