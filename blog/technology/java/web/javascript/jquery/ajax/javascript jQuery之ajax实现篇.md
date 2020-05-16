[TOC]



# javascript jQuery之ajax实现篇

jQuery的ajax方法非常好用，这么好的东西，你想拥有一个属于自己的ajax么？接下来，我们来自己做一个简单的ajax吧。

## 实现功能

由于jq中的ajax方法是用了内置的deferred模块，是Promise模式的一种实现，而我们这里没有讲过，所以我们就不使用这一模式啦。

我们只定义一个ajax方法，他可以简单的get，post，jsonp请求就可以啦~~



```
var ajax = function () {
    
  //  做一些初始化，定义一些私有函数等
 
  return function () {
    // ajax主体代码
  }
    
}()


ajax({
  url: myUrl,
  type: 'get',
  dataType: 'json',
  timeout: 1000,
  success: function (data, status) {
    console.log(data)
  },
  fail: function (err, status) {
    console.log(err)
  }
})
```



我们的ajax方法最后实现的功能如上所示，非常类似于jq。那我们还在等什么，开始吧。

## 整体思路

我们的ajax方法需要传递一个对象进去，这个对象中我们可以定义一些我们希望的属性，我们就必须初始一下各种属性



```
//默认请求参数
  var _options = {
    url: null,  // 请求连接
    type: 'GET',  // 请求类型
    data: null,  // post时请求体
    dataType: 'text',  // 返回请求的类型，有text/json两种
    jsonp: 'callback',  // jsonp请求的标志，一般不改动
    jsonpCallback: 'jsonpCallback',  //jsonp请求的函数名
    async: true,   // 是否异步
    cache: true,   // 是否缓存
    timeout:null,  // 设置请求超时
    contentType: 'application/x-www-form-urlencoded',
    success: null,  // 请求成功回调函数
    fail: null   // 请求失败回调
  }
```



以上我们定义了一大串请求有关的数据，接下来我们就开始ajax主体函数的书写，现在的ajax方法是这样了

```
var ajax = function () {

  //默认请求参数
  var _options = {
    url: null,
    type: 'GET',
    data: null,
    dataType: 'text',
    jsonp: 'callback',
    jsonpCallback: 'jsonpCallback',
    async: true,
    cache: true,
    timeout:null,
    contentType: 'application/x-www-form-urlencoded',
    success: null,
    fail: null
  }
  // ...
  return function (options) {
     // ...
  }
}()
```



我们可以想一下，ajax方法传递一个对象进来，我们是不是需要把我们设置的这个对象上的属性来覆盖掉初始化_options上面的那些属性呢，肯定需要。那下面我们先写一个简单的继承，如下：

```
var ajax = function () {

  //默认请求参数
  var _options = {
    url: null,
    type: 'GET',
    data: null,
    dataType: 'text',
    jsonp: 'callback',
    jsonpCallback: 'jsonpCallback',
    async: true,
    cache: true,
    timeout:null,
    contentType: 'application/x-www-form-urlencoded',
    success: null,
    fail: null
  }
  //  内部使用的继承方法
  var _extend = function(target,options) {
    if( typeof target !== 'object' || typeof options !== 'object' ) {
      return;
    }
    var copy ,clone, name;
    for( name in options ) {
      if(options.hasOwnProperty(name) && !target.hasOwnProperty(name)) {
        target[name] = options[name];
      }
    }
    return target;
  };


  // ...
  return function (options) {

    // 没有传参或者没有url，抛出错误
    if( !options || !options.url ) {
      throw('参数错误！');
    }

    // 继承操作
    options.type = options.type.toUpperCase();
    _extend(options,_options);
     // ...
  }
}()
```



这个继承方法，我们是把初始化的_options继承到了options，为什么呢？因为我们这个_options对象不在ajax方法内部，我们需要使用它，但我们不能改变他，如果改变了他，下一次使用ajax方法将会崩溃。因此，我们紧紧是把配置的options对象没有的属性设置为初始值。

下面，我们就要发送请求了么？等等！好像jsonp请求不是xhr请求啊，他好像是将请求url当做script标签的src值插入到页面body中去实现的，哦，对了，我们先把jsonp请求处理一下再开始建立xhr请求的代码吧。



```
var ajax = function () {

  //默认请求参数
  var _options = {
    url: null,
    type: 'GET',
    data: null,
    dataType: 'text',
    jsonp: 'callback',
    jsonpCallback: 'jsonpCallback',
    async: true,
    cache: true,
    timeout:null,
    contentType: 'application/x-www-form-urlencoded',
    success: null,
    fail: null
  }
  //  内部使用的继承方法
  var _extend = function(target,options) {
    if( typeof target !== 'object' || typeof options !== 'object' ) {
      return;
    }
    var copy ,clone, name;
    for( name in options ) {
      if(options.hasOwnProperty(name) && !target.hasOwnProperty(name)) {
        target[name] = options[name];
      }
    }
    return target;
  };
  
  // jsonp处理函数
  function _sendJsonpRequest(url,callbackName,succCallback) {

    var script = document.createElement('script');

    script.type="text/javascript";
    script.src=url;

    document.body.appendChild(script);
    // 如果用户自己定义了回调函数，就用自己定义的，否则，调用success函数
    window[callbackName] = window[callbackName] || succCallback;

  }

  // ...
  return function (options) {

    // 没有传参或者没有url，抛出错误
    if( !options || !options.url ) {
      throw('参数错误！');
    }

    // 继承操作
    options.type = options.type.toUpperCase();
    _extend(options,_options);

    /*jsonp部分，直接返回*/
    if( options.dataType === 'jsonp' ) {
      var jsonpUrl = options.url.indexOf('?') > -1 ? options.url: options.url +
        '?' + options.jsonp+ '=' + options.jsonpCallback;

     return  _sendJsonpRequest(jsonpUrl,options.jsonpCallback,options.success);
      
    }
     // ...
  }
}()
```



我们定义了一个_sendJsonpRequest函数，这个函数接收三个参数，第一个是jsonpUrl，第二个是jsonp的回调函数名，第三个是成功回调函数，我们在这个函数内建立一个src为jsonpUrl的script元素插入到body中，同时，确定了回调函数（如果我们定义了jsonpCallback函数就调用它，如果没有就调用success回调，一般情况我们不去定义全局的jsonpCallback函数而传递success回调来完成jsonp请求）。

好，处理好jsonp请求后，我们开始处理xhr请求了。



```
var ajax = function () {

  //默认请求参数
  var _options = {
    url: null,
    type: 'GET',
    data: null,
    dataType: 'text',
    jsonp: 'callback',
    jsonpCallback: 'jsonpCallback',
    async: true,
    cache: true,
    timeout:null,
    contentType: 'application/x-www-form-urlencoded',
    success: null,
    fail: null
  }
  //  内部使用的继承方法
  var _extend = function(target,options) {
    if( typeof target !== 'object' || typeof options !== 'object' ) {
      return;
    }
    var copy ,clone, name;
    for( name in options ) {
      if(options.hasOwnProperty(name) && !target.hasOwnProperty(name)) {
        target[name] = options[name];
      }
    }
    return target;
  };
  
  // jsonp处理函数
  function _sendJsonpRequest(url,callbackName,succCallback) {

    var script = document.createElement('script');

    script.type="text/javascript";
    script.src=url;

    document.body.appendChild(script);
    // 如果用户自己定义了回调函数，就用自己定义的，否则，调用success函数
    window[callbackName] = window[callbackName] || succCallback;

  }
  
  // json转化为字符串
  var _param = function(data) {
    var str = '';
    if( !data || _empty(data)) {
      return str;
    }
    for(var key in data) {
      str += key + '='+ data[key]+'&'
    }
    str = str.slice(0,-1);
    return str;
  }
  //判断对象是否为空
  var _empty = function(obj) {
    for(var key in obj) {
      return false;
    }
    return true;
  }

  // ...
  return function (options) {

    // 没有传参或者没有url，抛出错误
    if( !options || !options.url ) {
      throw('参数错误！');
    }

    // 继承操作
    options.type = options.type.toUpperCase();
    _extend(options,_options);

    /*jsonp部分，直接返回*/
    if( options.dataType === 'jsonp' ) {
      var jsonpUrl = options.url.indexOf('?') > -1 ? options.url: options.url +
        '?' + options.jsonp+ '=' + options.jsonpCallback;

     return  _sendJsonpRequest(jsonpUrl,options.jsonpCallback,options.success);
      
    }
    
     //XMLHttpRequest传参无影响
    var xhr = new (window.XMLHttpRequest || ActiveXObject)('Microsoft.XMLHTTP');
    // get搜索字符串
    var search = '';

    // 将data序列化
    var param= _param(options.data)

    if( options.type === 'GET' ) {
      search = (options.url.indexOf('?') > -1 ? '&' : '?') + param;
      if(!options.cache) {
        search += '&radom='+Math.random();
      }
      
      param = null;
    }

     // ...
  }
}()
```



首先，兼容ie创建xhr对象，XMLHttpRequest构造函数传递参数是无影响，然后我们定义了两个辅助变量：search、param，前者用于get请求的查询字串，后者用于post请求的send内容，我们定义了一个_param方法来讲对象转换为send方法参数的模式，就如你看到的那样，下面我们做了get与post之间合理的search、param的赋值工作。接下来我们就可以发送请求书写最激动人心的内容了。

最终的代码如下

```js

var ajax = function () {

  //默认请求参数
  var _options = {
    url: null,
    type: 'GET',
    data: null,
    dataType: 'text',
    jsonp: 'callback',
    jsonpCallback: 'jsonpCallback',
    async: true,
    cache: true,
    timeout:null,
    contentType: 'application/x-www-form-urlencoded',
    success: null,
    fail: null
  }


  // json转化为字符串
  var _param = function(data) {
    var str = '';
    if( !data || _empty(data)) {
      return str;
    }
    for(var key in data) {
      str += key + '='+ data[key]+'&'
    }
    str = str.slice(0,-1);
    return str;
  }
  //判断对象是否为空
  var _empty = function(obj) {
    for(var key in obj) {
      return false;
    }
    return true;
  }

  var _extend = function(target,options) {
    if( typeof target !== 'object' || typeof options !== 'object' ) {
      return;
    }
    var copy ,clone, name;
    for( name in options ) {
      if(options.hasOwnProperty(name) && !target.hasOwnProperty(name)) {
        target[name] = options[name];
      }
    }
    return target;
  };

  // 自定义text转化json格式
  var parseJSON = function(text) {
    if(typeof text !== 'string') {
      return;
    }
    if( JSON && JSON.parse ) {
      return JSON.parse(text);
    }
    return (new Function('return '+text))();
  }

  // jsonp处理函数
  function _sendJsonpRequest(url,callbackName,succCallback) {

    var script = document.createElement('script');

    script.type="text/javascript";
    script.src=url;

    document.body.appendChild(script);
    // 如果用户自己定义了回调函数，就用自己定义的，否则，调用success函数
    window[callbackName] = window[callbackName] || succCallback;

  }


  return function (options) {

    // 没有传参或者没有url，抛出错误
    if( !options || !options.url ) {
      throw('参数错误！');
    }

    // 继承操作
    options.type = options.type.toUpperCase();
    _extend(options,_options);

    /*jsonp部分，直接返回*/
    if( options.dataType === 'jsonp' ) {
      var jsonpUrl = options.url.indexOf('?') > -1 ? options.url: options.url +
        '?' + options.jsonp+ '=' + options.jsonpCallback;

      _sendJsonpRequest(jsonpUrl,options.jsonpCallback,options.success);
      
      return;
    }

     //XMLHttpRequest传参无影响
    var xhr = new (window.XMLHttpRequest || ActiveXObject)('Microsoft.XMLHTTP');

    // get搜索字符串
    var search = '';

    // 将data序列化
    var param= _param(options.data)

    if( options.type === 'GET' ) {
      search = (options.url.indexOf('?') > -1 ? '&' : '?') + param;
      if(!options.cache) {
        search += '&radom='+Math.random();
      }
      
      param = null;
    }

    xhr.open( options.type, options.url + search, options.async );

    xhr.onreadystatechange = function() {
      if( xhr.readyState == 4 ) {
        if( xhr.status >= 200 && xhr.status < 300 || xhr.status == 304 ) {
          var text = xhr.responseText;
          // json格式转换
          if(options.dataType == 'json') {
              text = parseJSON(text)
          }

          if( typeof options.success === 'function') {

            options.success(text,xhr.status)
          }
          
        }else {

          if(typeof options.fail === 'function') {
            options.fail('获取失败', 500)
          }
          
        }
      }
    }

    xhr.setRequestHeader('content-type',options.contentType);
    // get请求时param时null
    xhr.send(param);

    // 如果设置了超时，就定义
    if(typeof options.timeout === 'number') {
      // ie9+
      if( xhr.timeout ) {
        xhr.timeout = options.timeout;
      }else {
        setTimeout(function() {
          xhr.abort();
        },options.timeout)
      }
    }
  }

}()


ajax({
  url: myUrl,
  type: 'get',
  dataType: 'json',
  timeout: 1000,
  success: function (data, status) {
    console.log(data)
  },
  fail: function (err, status) {
    console.log(err)
  }
})
```



可以看到，我们很熟悉的xhr代码，在这里，我们需要写一个解析返回字串形成json格式对象的方法parseJSON，类似于jq中的parseJSON方法，如上所示。

我们还需要设置超时代码，如果设置了请求超时，我们就如上定义。

注意：上面代码中，由于懒，设置请求头一行并没有判断是否在post请求下，你可以自己设置~~~。

## 结尾

一个简单的ajax方法就完成了，你是否也完成了呢？如果你懂deferred，你可以尝试着书写为deferred格式，很简单的~~~。

能力有限，水平一般，如有错误，请指正。



https://www.cnblogs.com/xujiazheng/p/6253461.html