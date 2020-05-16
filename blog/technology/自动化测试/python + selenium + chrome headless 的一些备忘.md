[TOC]



# python + selenium + chrome headless 的一些备忘

更新于 2018-01-31  约 6 分钟

> 使用python3.6在Ubuntu中进行了一项使用Chrome headless浏览器的工作, 在此记录下遇到的问题以及解决方法.

### 入门?

参考 [unning-selenium-with-headless-chrome](https://intoli.com/blog/running-selenium-with-headless-chrome/)

### Ubuntu中如何安装chrome浏览器, 以及chromedriver?

参考 [Installing ChromeDriver on Ubuntu](https://developers.supportbee.com/blog/setting-up-cucumber-to-run-with-Chrome-on-Linux/)

### selenium启动浏览器时常用的属性

```
from selenium.webdriver.chrome.options import Options
chrome_options = Options()
chrome_options.add_argument('window-size=1920x3000') #指定浏览器分辨率
chrome_options.add_argument('--disable-gpu') #谷歌文档提到需要加上这个属性来规避bug
chrome_options.add_argument('--hide-scrollbars') #隐藏滚动条, 应对一些特殊页面
chrome_options.add_argument('blink-settings=imagesEnabled=false') #不加载图片, 提升速度
chrome_options.add_argument('--headless') #浏览器不提供可视化页面. linux下如果系统不支持可视化不加这条会启动失败
chrome_options.binary_location = r'/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary' #手动指定使用的浏览器位置
```

### selenium如何连接到已经开启的浏览器?

需要在打开浏览器后, 获取浏览器的`command_executor url`, 以及`session_id`

```
opener.command_executor._url, opener.session_id #opener为webdriver对象
```

之后通过`remote`方式链接

```
from selenium import webdriver
opener = webdriver.Remote(command_executor=_url,desired_capabilities={}) #_url为上面的_url
opener.close() #这时会打开一个全新的浏览器对象, 先把新的关掉
opener.session_id = session_id #session_id为上面的session_id
```

之后对`opener`的任何操作都会反映在之前的浏览器上.

### selenium 的 desired_capabilities 如何传递`--headless`这样的浏览器参数

```
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
capabilities = DesiredCapabilities.CHROME
capabilities.setdefault('chromeOptions', {'args':['--headless', '--disable-gpu']})
```

### selenium 使用 crontab等环境启动时提示`chromedriver not in PATH`

初始化的时候, 传入chromedriver绝对路径

```
opener = webdriver.Chrome(r'/usr/local/bin/chromedriver', chrome_options=chrome_options)
```

### selenium使用cookies

- 获得cookies
  `opener.get_cookies()`
- 写入cookies
  `opener.add_cookie(cookie) #需要先访问该网站产生cookies后再进行覆写`

### selenium 等待页面所有异步函数完成

```
opener.implicitly_wait(30) #30是最长等待时间
```

### selenium 打开新标签页

偏向使用js函数来执行

```
opener.execute_script('''window.open("http://baidu.com","_blank");''')
```

### selenium 获得页面的网络请求信息

有些时候页面在你点击后会异步进行请求, 完成一些操作, 这时可能就会生成输出数据的url, 只要抓到这个url就可以跳过token验证等安全监测, 直接获得数据.

```
script =  "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;"
performances = opener.execute_script(script)
```

script里是js代码, 一般用来进行性能检查, 网络请求状况, 使用selenium执行这段js就可以获得所有的请求信息.

おわり.

阅读 9.1k更新于 2018-01-31





https://segmentfault.com/a/1190000013067705