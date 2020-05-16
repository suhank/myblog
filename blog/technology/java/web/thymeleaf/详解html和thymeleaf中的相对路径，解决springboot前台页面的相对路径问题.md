[TOC]



# 详解html和thymeleaf中的相对路径，解决springboot前台页面的相对路径问题

## 一.问题

最近在使用springboot框架，众所周知，该框架可以直接以jar的方式运行，在该方式运行的情况下，默认contextPath是/。在前台页面引用的时候，我是这样写相对路径的:

后台RequestMapping为/test/page1,前台访问路径就是"/test/page1",自动就会跳转到http://localhost:8080/test/page1路径，

这样自然是没有什么问题的，但是当工程以war包的形式部署到tomcat下时，在访问时需要加上项目名，比如project1,这时的contextPath就是/project1，那么在这种情况下，以前的相对路径写法"/test1/page1"就完全不能用了，因为它会自动跳转到http://localhost:8080/test/page1地址，很显然，这里缺少了/project1,正确路径是http://localhost:8080/project1/test/page1

那么怎么办呢?

## 二.解决过程

毫无疑问，这种情况是十分严重的，如果等项目完成了才发现路径都是错的，那无疑要花费巨大的精力去改，上网上查了一些资料，总结如下:

### 相对路径:

html页面中相对路径有两种：

第一种：/test/page1,这是相对于服务器根路径而言的，以之前的例子为例，使用结果就是直接从8080以后开始替换，如http://localhost:8080/test/page1

第二种：test/page2，这是相对于当前路径而言的，比如当前路径为http://localhost:8080/test/page1，那么替换以后就是

http://localhost:8080/test/test/page，在这种情况下也有对应的语法，../表示上级目录，./表示当前目录，如test/page2就相当于./test/page2，如果写成../test/page2,那么替换后的路径就是http://localhost:8080/test/page了。

### 绝对路径:

绝对路径就是直接http://localhost:8080/test/page1，十分简单，但是也相当于写死了

### 总结:

方案1，直接使用绝对路径。但是这样十分不好维护，写起来也麻烦，直接pass。

方案2,   使用相对路径第一种写法，部署的时候设置contextPath为/,这样自然没有问题，可以将springboot以jar方式部署，默认就是这种情况，如果一定要用tomcat部署，那么只能把war包解压出来的工程文件全部转移到tomcat的ROOT文件夹下，将ROOT文件夹内原有文件清空或转移，这样也可以实现根目录访问。这种方案一定程度上可以解决问题，但是对部署的方式限制太大了，只部署一个工程还可以，多了就没有办法了，因此也不是长久之计。

那么还有没有别的办法呢?有！，基本思路是利用thymeleaf动态生成html页面的特点，在相对路径前动态添加一个项目名，不就解决了?其实用jsp可以轻松做到，但是由于我的页面不是jsp页面，所以也不能使用这种方法。于是又进行了一番尝试，参考了很多资料，终于找到了解决方案!

方案3:直接利用thymeleaf的th:src或者th:href属性改变标签的链接路径，如

```
@{/js/{path}/myJs.js(path=${contextPath})}
```

对于thymeleaf来说，有四种相对URL：

> 页面相对 test/page1.html  同普通html相对路径第二种，替换末尾的路径
>
> 上下文相对 /test/page1.html 自动添加上下文路径在相对路径之前!实际生成路径/project1/test/page1.html
>
> 服务器相对 ~/test/page1.html 同普通html相对路径第一种，适合访问同一服务器不同上下文路径时使用，如同一个tomcat上的project2

​        协议相对  //code.jquery.com/jquery-2.0.3.min.js 跨域访问使用

另外附上带有变量的thymeleaf语法

```
@{/js/{path}/myJs.js(path=${contextPath},param=${contextPath})}
```

()内完成对变量或者参数的赋值，比如contextPath变量值为/app,那么最终生成的URL为http://localhost:11111/app/js//app/myJs.js?param=/app,

如果语句为

```
@{/js/{path}/myJs.js(path=${contextPath})}
```

那么会生成

http://localhost:11111/app/js//app/myJs.js

需要注意的是，thymeleaf将会以完成参数赋值后的结果来判断是哪种相对类型。

## 三.解决方案

```html
<script src="../static/js/myJs.js" th:src="@{/js/myJs.js}"></script>
```

利用thymeleaf提供的相对上下文动态路径，轻松解决问题，静态路径可以用来描述当前页面文件与其他页面文件在当前工程的位置关系，这样即使在未联网的情况直接打开html文件，也可以找到所引用的js文件和css文件等其他文件，充分发挥thymeleaf的威力!

## 四.感想

有些较为基础的知识还是必须多了解，这样碰到问题才能快速找到解决方案，如果压根不知道thymeleaf的作用，连查都不知道查什么好，一开始想的方案是在相对路径前自动追加一个项目名，初步想了利用配置文件从后端传值的等方法，也想了利用tomcat的URL与工程文件的映射来解决，但是感觉都不怎么优雅。后来想到thymeleaf是生成动态模板的，也许有我不知道的特性，果然，真的是十分方便，看来还是要对thymeleaf加深了解才行。





https://blog.csdn.net/qq_35603331/article/details/76255125