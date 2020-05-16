[TOC]



# mpvue 使用 echarts 小程序组件 npm方式导入 推荐

## 微信小程序引入echarts图表组件，折线图柱状图啥的

```
八月份微信小程序支持了npm，所以就可以直接用npm的方式引入图表组件。
参考的github链接为：https://github.com/lert707/mpvue-echarts 
```

------

### 第一步，初始化一个mpvue项目

### 第二步，引入mpvue-echarts

mpvue-echarts 支持

```
npm install mpvue-echarts  --save 
```

echarts包自定义下载

```
接着引入import * as echarts from "../../static/echarts.min.js";
一开始我直接把echarts所有的组件引进去了，直接超过了小程序项目大小的限制(2M)
解决：http://echarts.baidu.com/builder.html
直接在线定制你需要的组件，然后就生成了echarts.min.js 
```



### 第三步，就可以直接在index.vue文件中使用了，就不一一举例了，下面是折线图的代码

```vue
<template>
  <div class="container">
    <div class="wrap">
      <mpvue-echarts :echarts="echarts" :onInit="onInit" />
    </div>
  </div>
</template>

<script>
import * as echarts from '../../../../static/ec-canvas/echarts'
import mpvueEcharts from 'mpvue-echarts'

export default {
  data () {
    return {
      echarts,
      onInit: initChart，
      resdata： []  //定义一个数组，用来动态传递图表数据
    }
  },
  components: {
    mpvueEcharts
  },
  methods: {
   // 这里只是做一个测试，方法里可以调用接口，获取到的数据赋值给resdata
    test() {
      const res = [8000, 12000, 10000, 16000, 14000, 18000, 16000];
      resdata = res;
    },
    initChart (canvas, width, height) {
	 const chart = echarts.init(canvas, null, {
	    width: width,
	    height: height
	  })
	  canvas.setChart(chart)
	  var option = {
	    backgroundColor: '#fff',
	    color: ['#37A2DA', '#67E0E3', '#9FE6B8'],
	    tooltip: {
	      trigger: 'axis'
	    },
	    legend: {
	      data: []
	    },
	    grid: {
	      containLabel: true
	    },
	    xAxis: {
	      type: 'category',
	      boundaryGap: false,
	      axisLine: {
	        lineStyle: {
	          color: '#ccc'
	        }
	      },
	      data: ['0:00', '4:00', '8:00', '12:00', '16:00', '20:00', '24:00']
	    },
	    yAxis: {
	      x: 'center',
	      type: 'value',
	      min: 8000, // 坐标轴最小值
	      max: 20000,
	      scale: true,
	      // 坐标轴的颜色
	      axisLine: {
	        lineStyle: {
	          color: '#ccc'
	        }
	      }
	    },
	    series: [{
	      name: '',
	      type: 'line',
	      smooth: false, // true是曲线，false是直线
	      data: this.resdata,
	      // 设置折线区域颜色
	      areaStyle: {normal:{}},
	      itemStyle: {
	        normal: {
	          color: new echarts.graphic.LinearGradient(
	            0, 0, 0, 1,
	            [
	              {offset: 0, color: 'skyblue'}
	            ]
	          )
	        }
	      }
	    }]
	  }
	  chart.setOption(option)
	  return chart
	}
  },
  onShow() {
    this.test()
  }
}
</script>

<style scoped>
  .wrap {
    width: 110%;
    height: 300px;
    margin-left: -20px;
    margin-top: -20px;
    position: relative;
    z-index: -1;
  }
</style>

```

------

以上就是mpvue引入echarts图表组件的方法啦，是不是有了npm后就很简单了呢。

## 注意点

 如果你想在手机微信上也预览效果的话，npm run dev之后，在dist/common/vendor.js，这个文件超出了小程序的大小限制，所以你得使用npm run build，让webpack压缩这个vendor.js文件，打包压缩完后，去手机上预览吧。





https://blog.csdn.net/lert707/article/details/82862755