# easyui textbox combobox 设置只读不可编辑状态

输入框 textbox

$("#xx").textbox('setValue','value');  //设置输入框的值

$('#xx').textbox('textbox').attr('readonly',true);  //设置输入框为禁用

 

下拉框相关 combobox

$("#xx").combobox({disabled: true});      //设置下拉款为禁用

$("#xx").combobox('setValue',xlid);  //设置下拉款的默认值  xlid是你下拉款的id属性

$("#xx").combobox('getValue');      //获取下拉款id值

$("#xx").combobox('getText');      //获取下拉款name值







https://www.cnblogs.com/hailexuexi/p/6963078.html