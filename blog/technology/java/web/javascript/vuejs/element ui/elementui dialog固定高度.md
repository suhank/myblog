

# elementui dialog固定高度



今天用dialog里面显示echarts图，由于里面包含了tab页面，第一个tab页面显示一个图标，第二个tab页面显示2个图标，上下的位置，这样导致切换tab的时候，dialog的高度会发生变化，官网里面只有设置宽度，可用下面方法解决

在dialog里写一个div ，div的大小设置为相对视窗的大小就行了

```html
<el-dialog
      class="dialog"
      :visible.sync="dialogVisible"
      top="1%"
      :title="dialogTitle"
      @close="handleClose"
      width="50%"
      
    >
 
    <div class="el-dialog-div">
 
          //省略其他内容
   </div >
 
</el-dialog>
 
 
 
<style lang="scss" scoped>
 .el-dialog-div{
    height: 60vh;
     overflow: auto;
    }
 
</style>
```



原文链接：https://blog.csdn.net/CarryBest/article/details/89396354