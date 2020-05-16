 

[TOC]



# vue.js 使用axios实现下载功能

2018年01月04日 21:32:09 [seanxwq](https://me.csdn.net/seanxwq) 阅读数：19684



本文主要来源于知乎一个回答，这里红色部分做了自己的处理，虽然自己的少，可是很有用的几句代码哦

只好回答一下**axios如何拦截get请求并下载文件**的了。

## Ajax无法下载文件的原因

浏览器的GET(frame、a)和POST(form)请求具有如下特点：

- response会交由浏览器处理
- response内容可以为二进制文件、字符串等

Ajax请求具有如下特点：

- response会交由Javascript处理
- response内容仅可以为字符串

因此，Ajax本身无法触发浏览器的下载功能。

## Axios拦截请求并实现下载

为了下载文件，我们通常会采用以下步骤：

- 发送请求
- 获得response
- 通过response判断返回是否为文件
- 如果是文件则在页面中插入frame
- 利用frame实现浏览器的get下载

我们可以为axios添加一个拦截器：



```javascript
import axios from 'axios'
 
// download url
const downloadUrl = url => {
  let iframe = document.createElement('iframe')
  iframe.style.display = 'none'
  iframe.src = url
  iframe.onload = function () {
    document.body.removeChild(iframe)
  }
  document.body.appendChild(iframe)
}
 
// Add a response interceptor
axios.interceptors.response.use(c=> {
  // 处理excel文件
  if (res.headers && (res.headers['content-type'] === 'application/x-msdownload' || res.headers['content-type'] === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')) {
    downloadUrl(res.request.responseURL)
    
    res.data='';
    res.headers['content-type'] = 'text/json'
    return res;
  }
  ...
  return res;
}, error => {
  // Do something with response error
  return Promise.reject(error.response.data || error.message)
})
export default axios

```



之后我们就可以通过axios中的get请求下载文件了。



主要代码来自：https://www.zhihu.com/question/263323250/answer/267842980



## 推荐这个一种方法

由于兼容性等问题，其实导出直接用链接更方便一些，兼容性也好，参数不是很多的话放在请求路径后面也是ok的，具体如下：

```js
//导出
exportOrderList() {
  let param = new URLSearchParams();
  param.append('strParamData', JSON.stringify(data));
  param.append('fileName', "标签列表");
  param.append('sessionId', sessionStorage.getItem('sessionId'));
  let url = "api/queryListExport?" + param.toString();
  window.location.href = url;
},
```

关键就是这句哦：

```html
window.location.href = url;
```

这样也不需要像上面加拦截器什么的了哦，方便好用





https://blog.csdn.net/seanxwq/article/details/78975661