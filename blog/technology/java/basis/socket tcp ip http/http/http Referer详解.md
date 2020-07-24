[TOC]



# http Referer详解

### 什么是HTTP [Referer](http://ourapache.com/archives/tag/referer)

简言之，HTTP Referer是header的一部分，当浏览器向web服务器发送请求的时候，一般会带上Referer，告诉服务器我是从哪个页面链接过来的，服务器 籍此可以获得一些信息用于处理。比如从我主页上链接到一个朋友那里，他的服务器就能够从HTTP Referer中统计出每天有多少用户点击我主页上的链接访问他的网站。

Referer其实应该是英文单词Referrer，不过拼错的人太多了，所以编写标准的人也就将错就错了。

### 我的问题

我刚刚把feed阅读器改变为[Gregarius](http://gregarius.net/)，但他不像我以前用的liferea，访问新浪博客的时候，无法显示其中的图片，提示“此图片仅限于新浪博客用户交流与沟通”，我知道，这就是HTTP Referer导致的。

由于[我上网客户端配置的特殊性](http://www.ourapache.com/wp-admin/294)，首先怀疑是squid的问题，但通过实验排除了，不过同时发现了一个[Squid和Tor、Privoxy协同使用的隐私泄露问题](http://wiki.noreply.org/noreply/TheOnionRouter/SquidProxy)，留待以后研究。

### Gregarius能处理这个问题么？

[答案是否定的](http://forums.gregarius.net/comments.php?DiscussionID=448)，因为Gregarius只是负责输出html代码，而对图像的访问是有客户端浏览器向服务器请求的。

不过，安装个firefox扩展也许能解决问题，文中推荐的”Send Referrer”我没有找到，但发现另外一个可用的：”[RefControl](https://addons.mozilla.org/en-US/firefox/addon/953)“，可以根据访问网站的不同，控制使用不同的Referer。

但是我不喜欢用Firefox扩展来解决问题，因为我觉得他效率太低，所以我用更好的方式——Privoxy。

### Privoxy真棒

在Privoxy的default.action中添加两行：

> {+hide-referrer{forge}}
> .album.sina.com.cn

这样Gregarius中新浪博客的图片就出来了吧？+hide-referrer是Privoxy的一个过滤器，设置访问时对HTTP Referer的处理方式，后面的forge代表用访问地址当作Refere的，还可以换成block，代表取消Referer，或者直接把需要用的 Referer网址写在这里。

用Privoxy比用Firefox简单的多，赶紧换吧。

### From https to http

我还发现，从一个https页面上的链接访问到一个非加密的http页面的时候，在http页面上是检查不到HTTP Referer的，比如当我点击自己的https页面下面的w3c xhtml验证图标（网址为<http://validator.w3.org/check?uri=referer>），从来都无法完成校验，提示：

> No Referer header found!

原来，在[http协议的rfc文档](http://www.ietf.org/rfc/rfc2616.txt)中有定义：

> 15.1.3 Encoding Sensitive Information in URI’s
>
> …
>
> Clients SHOULD NOT include a Referer header field in a (non-secure)
> HTTP request if the referring page was transferred with a secure
> protocol.

这样是出于安全的考虑，访问非加密页时，如果来源是加密页，客户端不发送Referer，[IE一直都是这样实现的](http://support.microsoft.com/kb/178066)，[Firefox浏览器也不例外](http://kb.mozillazine.org/Network.http.sendSecureXSiteReferrer)。但这并不影响从加密页到加密页的访问。

### Firefox中关于Referer的设置

都在里，有两个键值：

- network.http.sendRefererHeader (default=2) 设置Referer的发送方式，0为完全不发送，1为只在点击链接时发送，在访问页面中的图像什么的时候不发送，2为始终发送。参见[Privacy Tip #3: Block Referer Headers in Firefox](http://cafe.elharo.com/privacy/privacy-tip-3-block-referer-headers-in-firefox/)
- network.http.sendSecureXSiteReferrer (default=true) 设置从一个加密页访问到另外一个加密页的时候是否发送Referer，true为发送，false为不发送。

### 利用Referer防止图片盗链

虽然Referer并不可靠，但用来防止图片盗链还是足够的，毕竟不是每个人都会修改客户端的配置。实现一般都是通过apache的配置文件，首先设置允许访问的地址，标记下来：

> \# 只允许来自domain.com的访问，图片可能就放置在domain.com网站的页面上
> SetEnvIfNoCase Referer “^http://www.domain.com/” local_ref
> \# 直接通过地址访问
> SetEnvIf Referer “^$” local_ref

然后再规定被标记了的访问才被允许：

> <FilesMatch “.(gif|jpg)”>
> Order Allow,Deny
> Allow from env=local_ref
> </FilesMatch>

或者

> <Directory /web/images>
> Order Deny,Allow
> Deny from all
> Allow from env=local_ref
> </Directory>

这方面的文章网上很多，参考：

- [Apache 下防止盗链的解决办法](http://leftleg.hzpub.com/read.php?405)
- [Apache的环境变量设置](http://blog.51766.com/page/zsc?entry=1144852732034)
- [配置 Apache 实现禁止图片盗链](http://blog.soueasy.net/post/80.htm)

### 不要使用Rerferer的地方

不要把Rerferer用在身份验证或者其他非常重要的检查上，因为Rerferer非常容易在客户端被改变，不管是通过上面介绍的Firefox扩展，或者是Privoxy，甚至是libcurl的调用，所以Rerferer数据非常之不可信。

如果你想限制用户必须从某个入口页面访问的话，与其使用Referer，不如使用session，在入口页面写入session，然后在其他页面检查，如果用户没有访问过入口页面，那么对应的session就不存在，参见[这里的讨论](http://www.thescripts.com/forum/thread3090.html)。不过和上面说的一样，也不要过于相信这种方式的“验证”结果。

个人感觉现在Rerferer除了用在防盗链，其他用途最多的就是访问统计，比如统计用户都是从哪里的链接访问过来的等等。





https://blog.csdn.net/fishmai/article/details/52388840