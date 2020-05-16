# 小程序重复点击事件

 

```
/**
 * 重复点击事件处理
 */
function repeatBtn(fn, gapTime) {
  if (gapTime == null || gapTime == undefined) {
    gapTime = 1500
  }

  let _lastTime = null

  // 返回新的函数
  return function () {
    let _nowTime = + new Date()
    if (_nowTime - _lastTime > gapTime || !_lastTime) {
      fn.apply(this, arguments)   //将this和参数传给原函数
      _lastTime = _nowTime
    }
  }
}
module.exports = {

  repeatBtn: repeatBtn,//重复点击事件处理
}
```

调用

```
  formSubmit: util.repeatBtn(function (e) {
},1000);
```





https://blog.csdn.net/u010598111/article/details/80318799