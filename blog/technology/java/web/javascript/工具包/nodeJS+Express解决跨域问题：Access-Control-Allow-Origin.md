# NodeJS+Express解决跨域问题：Access-Control-Allow-Origin 

今天在玩`vue-resource`时，后台使用`nodejs`来提供数据，由于需要跨域，在网上也找到了解决方法。

`vue-resource`代码(其实就是ajax技术)：

```js
this.$http.get({url:"http://localhost:3000/getdata"})
.then(function (data) {
    console.log(data)
},function (error) {});
    
```

`nodejs`部分：

```js
var express = require('express');
var app = express();
//设置跨域访问
app.all('*', function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    res.header("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
    res.header("X-Powered-By",' 3.2.1')
    res.header("Content-Type", "application/json;charset=utf-8");
    next();
});

app.get('/getdata', function(req, res) {
    res.send({id:req.params.id, name: req.params.password});
});

app.listen(3000);
console.log('Listening on port 3000...');
```





https://segmentfault.com/a/1190000005714840