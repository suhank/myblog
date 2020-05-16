

# echarts 修改图例legend颜色

2018年03月21日 18:15:58

阅读数：1996

legend的颜色是在option里面直接定义的color数组，有几个图例就在color里面写几个颜色值。

```js
var option = {
    color:['#4472C5','#ED7C30','#80FF80','#FF8096','#800080'],
    legend: {
        data:['临停车','月租车','免费车','储值车','军警车'],
        left:'center',
        bottom:'10%',
        itemWidth:10,//图例的宽度
        itemHeight:10,//图例的高度
        textStyle:{//图例文字的样式
            color:'#ccc',
            fontSize:16
        }
    },
    ...
}
```





https://blog.csdn.net/sinat_36422236/article/details/79643830