# easyui idField 写错导致进入页面默认datagrid选择最后一行

easyui idField写错或者写成不存在的字段会 导致进入页面默认datagrid选择最后一行

```js
var dg = $('#dg').datagrid({
        method: "get",
        url: '${ctx}/pagger/list4Ajax/notePlayTaskList',
        fit: true,
        fitColumns: false,
        border: false,
        idField: 'id',  //必须写具体的字段.或者不写
        striped: true,
        pagination: true,
        rownumbers: true,
        pageNumber: 1,
        pageSize: 50,
        pageList: [50, 100, 150, 200, 500, 1000],
        singleSelect: true,
        remoteSort: false,
        columns: [[
            {field: 'sys_no', hidden: true} 
            , {
                field: 'create_pin',
                title: '<spring:message code="lable.createPin"></spring:message>',
                width: 130,
                sortable: true,
                filter: "livebox",
                halign: 'center'
            }
            , {
                field: 'update_pin',
                title: '<spring:message code="lable.updatePin"></spring:message>',
                width: 130,
                sortable: true,
                filter: "livebox",
                halign: 'center'
            }
        ]],
        onSelect: function (rowIndex, rowData) {
            console.log("onSelect执行了...") 
        }, 
        enableHeaderClickMenu: true,
        enableHeaderContextMenu: true,
        enableRowContextMenu: false,
        toolbar: '#tb'

    });
```

如上 idField: 'id', 没有id字段.  就会导致默认datagrid选择最后一行





idField指明哪一个字段是标识字段。