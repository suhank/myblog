# ECharts 坐标轴标签、tooltip文本格式化

在ECharts折线图中（其他系列图标同理），我们需要对x轴坐标标签、坐图形上的文本标签、提示框组件文本等进行格式化，选项参数设置如下：

## **x轴坐标标签格式化：** 

```js
xAxis: {
  axisLabel: {
    show: true, // 默认为true 
    interval: 0, // 设置x轴文本标签全部显示 
    rotate: 45, //标签旋转角度，对于长文本标签设置旋转可避免文本重叠 
    formatter: function(data) {
      return data + "是x轴坐标值";
    }
  }
}


xAxis: [
    {
        axisLabel: {
            show: true, // 默认为true
            interval: 0, // 设置x轴文本标签全部显示
            rotate: 45, //标签旋转角度，对于长文本标签设置旋转可避免文本重叠
            formatter: function (data) {
                return data + "是x轴坐标值";
            }
        },
        type: 'category',
        boundaryGap: false,
        data: dateArray
    }
],
```

## **提示框（tooltip）组件文本格式化：** 

```js
tooltip: {
  show: true, // 是否显示提示框组件 
  trigger: 'axis', //坐标轴触发，用在柱状图，折线图等会使用类目轴的图表中使用 
  formatter: function(params) {
    return params[0].name + "：" +params[0].data; // 由于可能有多个series，因此params[0].name和.data表示第一个series的该坐标点 xAxis值和yAxis值 
  }
}
```

```js
tooltip: {
  trigger: 'axis',
  formatter: function(params) {
    var len = params.length;
    //格式化提示框
    var str = params[0].name + "<br>";
    str = str + params[0].seriesName + " : " + params[0].value + "%<br>";
    if (len >= 2) {
      str = str + params[1].seriesName + " : " + params[1].value + "%<br>";
    }
    if (len >= 3) {
      str = str + params[2].seriesName + " : " + params[2].value + "%";
    }
    return str; // 由于可能有多个series，因此params[0].name和.data表示第一个series的该坐标点 xAxis值和yAxis值
  }
},
```



## series图形上的文本标签格式化： 

（比如折线图中，每个数据点的信息提示文本） 

```js
series: [
  ……
  type: "line",
  label: { // label选项在 ECharts 2.x 中放置于itemStyle.normal下 
    normal: {
      show: true, // 是否显示标签 
      position: "top", // 标签的位置，默认top 
      formatter: function(params) {
        return params.value + "单位"; //params.value是yAxis值 
      }
    }
  }
]
```



```js
 series: [{
   name: '总提袋率',
   type: 'line',
   data: allValueArray,
   markLine: {
     data: [{
       type: 'average',
       name: '平均值',
       itemStyle: {
         normal: {
           label: {
             formatter: function(data) {
               return data.value + "%";
             }
           }
         }
       }
     }]
   }
 }]
```

https://blog.csdn.net/LavanSum/article/details/72898234