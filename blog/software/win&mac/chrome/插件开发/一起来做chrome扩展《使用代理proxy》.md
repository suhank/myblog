[TOC]



# 一起来做chrome扩展《使用代理proxy》

在这么一个高墙林立的地方，不会翻墙肯定是不行的，所以这次就来看看chrome extension如何简单的控制chrome代理的。

# 方法

```
chrome.proxy.settings.set({value: {}, scope: 'regular'},function() {});
```

这是文档里的方法，很简单，当然要控制代理，还得加一些东西，比如value对应的值

```
var config = {
	mode: "pac_script",
	pacScript: {
		data: pac
	}
};
```

config.mode表明使用的是pac_script，什么是pac_script，可以看看 [![img](https://f.ydr.me/zh.wikipedia.org)维基百科：代理自动配置](http://zh.wikipedia.org/wiki/代理自动配置)，可以把它简单的理解为一个js函数:FindProxyForURL(url, host)，也就是pacScript.data对应的值。它虽然是一个js函数，但是是以字符串的形式传给它的。具体情况如下：

```
var pac = "var FindProxyForURL = function(url, host){"+
	"if(shExpMatch(url, '*amazon\.com*')){"+
		"return 'PROXY 192.168.0.1:9000';"+
	"}"+
	"return 'DIRECT'"+
"}";
```

很好理解的代码，如果匹配到amazon.com就使用代理192.18.0.1:9000，如果没有匹配到，直接返回，等于是什么都不做。shExpMatch函数用来匹配url或者host，匹配的方式和DOS的通配符相似

如果有多个匹配，直接写或||就可以了，如要匹配amazon.com和google.com

```
var pac = "var FindProxyForURL = function(url, host){"+
	"if(shExpMatch(url, '*amazon\.com*')||"+
		"shsExpMatch(url, '*google\.com*')"+
	"){"+
		"return 'PROXY 192.168.0.1:9000';"+
	"}"+
	"return 'DIRECT'"+
"}";
```

这样，把它们结合一下，写成一个方法setProxy()

```
function setProxy(){
	var pac = "var FindProxyForURL = function(url, host){"+
		"if(shExpMatch(url, '*amazon\.com*')||"+
			"shsExpMatch(url, '*google\.com*')"+
		"){"+
			"return 'PROXY 192.168.0.1:9000';"+
		"}"+
		"return 'DIRECT'"+
	"}";

	var config = {
		mode: "pac_script",
		pacScript: {
			data: pac
		}
	}

	chrome.proxy.settings.set({value: config, scope: 'regular'}, function(){});
}
```

在background中调用一下setProxy()方法，就启用了代理配置，如果打开amazon.com或是google.com，就会使用代理去打开，如果是其它网站，就不使用代理。

# 如何切换代理

代理已经可以正常使用了，那如果有多个代理，怎么切换呢？其实很简单，结合上一篇 [![img](https://f.ydr.me/frontenddev.org)《本地存储localStorage》](http://frontenddev.org/link/chrome-extension-to-do-the-local-storage-localstorage.html) 闭着眼睛也能想到，使用localStorage来存储代理信息不就行了，把IP和端口转给setProxy，加执行一下setProxy方法，就直接切换了代理。所以加上参数或使用全局变量都可以解决这个问题。

引用

[http://fuweiyi.com/chrome%E6%89%A9%E5%B1%95/2015/02/05/a-chrome-extension-proxy.html](http://fuweiyi.com/chrome扩展/2015/02/05/a-chrome-extension-proxy.html)



https://f-e-d.club/topic/together-to-make-chrome-extension-use-a-proxy-proxy.article