# axios修改默认baseUrl，动态修改域名或地址

## 问题？

需求就是当默认的host访问失败之后，需要换一个host，
我重新设置了axios.defaults.baseURL = '[http://baidu.com](https://link.segmentfault.com/?url=http%3A%2F%2Fbaidu.com)'，
但是没生效，还是用的默认的



## 答

我贴一段代码里面，如何配置axios的示例代码。
以下为封装的axios代码`requests.js`文件

```javascript
import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    
    //动态替换地址
    config.url = config.url.replace("https://xxxxxx.com/proxy/dahe/cccc", "http://localhost:9078/cccc");
    
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)
```





[axios可以修改默认baseUrl吗？或者有什么别的方案 - SegmentFault 思否](https://segmentfault.com/q/1010000037552405)