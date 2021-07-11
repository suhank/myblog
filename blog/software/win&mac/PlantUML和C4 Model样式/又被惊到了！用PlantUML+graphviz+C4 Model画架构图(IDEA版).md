# [又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版)](https://my.oschina.net/u/4622859/blog/4520852)

2020/07/07 22:08

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/457c4b36-2d5e-46d6-ad3f-9195c57c50fc.jpg)

**一、前言**

在平常的工作中画一些架构图，流程图应该是再正常不过了，画图的工具也是根据每个人的喜好进行选择，有的选择Visio，有的选择PowerDesigner，还有直接用在线的processOn等。这些工具各有优劣，都能实现画图的目标。但也有一些不足，比如：如何跟团队其他成员进行共享？目前看只能拷贝源文件或复制链接，如何进行版本控制？因为画图也是一个不断迭代的过程，也会有回到之前历史版本的情况。那么今天要跟大家介绍的这个画图软件，就是基于代码进行绘制，也就是现在所说的**Diagrams as code**，能够和代码一样放在像Git这样的版本控制系统中，团队成员签出代码的同时就签出了这些架构图，架构图类似上图所示，是不是很赞！



这个图是采用PlantUML+graphviz+C4 Model绘制的，下面就介绍一下如何在IDEA里进行集成。

**二、环境集成**

**1、前置条件**

必须安装JDK和IDEA。

我的版本是JDK8和IDEA 2019.3.2(Ultimate Edition)

**2、安装PlantUML插件**

在IDEA的File——>Settings...——>Plugins，在插件这里搜索PlanUML，选择PlanUML integration进行安装，安装完后重启IDEA使插件生效。

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/01eb2df4-1b68-4f68-8988-aa45370a0e4c.png)

**3、安装****graphviz绘图工具**

如果不安装graphviz，那么使用plantUML语法编写的代码不能生成图形，会显示如下报错信息：

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/902ba83b-b5cf-410f-8ba2-1f0a7308df81.png)

①下载https://graphviz.gitlab.io/_pages/Download/windows/graphviz-2.38.zip

②解压到一个文件夹下，比如D:\software\graphviz-2.38

③设置环境变量GRAPHVIZ_DOT

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/6aac07eb-299b-4cbb-8cfe-54a750b77c55.png)

**4、IDEA中配置graphviz**

在IDEA的plantUML中设置graphviz的执行文件dot.exe

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/6d5ab68c-97df-4432-9b72-cd733831d225.png)

创建一个test.puml文件，验证是否配置成功，内容如下：

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/cc66901a-add2-488c-8b81-87589995d342.png)

从右侧的内容可以看出，PlantUML和graphviz安装成功了。到这一步，可以使用PlantUML来绘制一些图形了，比如：时序图，组件图等，如下所示：

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/cdc57197-4da1-42af-9064-da61a7288f72.png)

这样的图形也能满足我们的需求，只是看起来不够美观，下面就与C4 Model进行集成。

**5、集成C4 Model**

要集成C4 Model需要将`C4.puml`、`C4_Context.puml`、 `C4_Container.puml` 和`C4_Component.puml`文件拷贝到代码里，存放到一个能够找到的路径下。这几个文件在官方的代码库里，地址为

```
https://github.com/RicardoNiepel/C4-PlantUML.git
```

下面添加一个样例代码，验证是否OK。**注意：这几个文件中使用了域名，国内访问不了，因为已经下载到本地，改成相对路径即可。**



```
@startuml Basic Sample!include ../c4/C4_Container.pumlPerson(admin, "Administrator")System_Boundary(c1, "Sample System") {    Container(web_app, "Web Application", "C#, ASP.NET Core 2.1 MVC", "Allows users to compare multiple Twitter timelines")}System(twitter, "Twitter")
Rel(admin, web_app, "Uses", "HTTPS")Rel(web_app, twitter, "Gets tweets from", "HTTPS")@enduml
```

最后生产的图形如下，和开始的架构图的样式一样，通过这种方式绘制的架构图，是不是很专业啊。

![img](又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版).assets/39d1a3e7-e822-4d06-9953-4d5321152dbc.png)



本文分享自微信公众号 - DevOps探索者（devopsagile）。
如有侵权，请联系 support@oschina.cn 删除。
本文参与“[OSC源创计划](https://www.oschina.net/sharing-plan)”，欢迎正在阅读的你也加入，一起分享。





[又被惊到了！用PlantUML+graphviz+C4 Model画架构图(IDEA版) - osc_47338985的个人空间 - OSCHINA - 中文开源技术交流社区](https://my.oschina.net/u/4622859/blog/4520852)