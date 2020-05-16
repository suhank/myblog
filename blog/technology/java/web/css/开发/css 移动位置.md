# css 移动位置

```css
/*尽量少用position 兼容问题*/
float:right;
margin-right:100px;
margin-top:100px;
/*同一列*/
margin-left:-140rpx;


或者 
padding-left
修复位置
flex-direction: column; 
```



小程序中如果背景是图片,图片不是响应式的.就不要用rpx,而是使用px