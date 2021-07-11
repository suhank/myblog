### 使用VSCode+PlantUML+C4-Model快速画架构图

#### 关于C4-Model

最近在看C4-Model，它的理念很实用，架构图要明确面向人群，根据面向人群的不同，产出四幅图来描述一个系统或者一个架构。System Context --> Container --> Component --> Code 四个层次。



![层级](使用VSCode+PlantUML+C4-Model快速画架构图.assets/1)



具体每个层级的图形用来描述什么，可以参见这里：[www.infoq.cn/article/C4-…](https://www.infoq.cn/article/C4-architecture-model)

本文的关注点更小，不讨论这些道的层面，而是描述怎么做，也就是术的层面。

#### 安装vscode

其实直接使用plantUML也可以，不过plantUML的界面比较简陋，功能也比较弱，而vscode中有plantUML的插件，使用起来更顺手。

vscode的安装很简单，MAC下直接https://code.visualstudio.com/Download下载dmg文件安装到Application即可。

#### 安装vscode的plantUML&Graphviz插件

plantUML是一款可以通过文字、代码画UML图的工具，不需要考虑态度配色、位置等因素，方便快速。而vscode上对应的插件功能类似。

Graphviz是一款图形化预览插件，plantUML默认只能渲染出时序图，对于比较复杂的图形，则需要Graphviz插件协助渲染。

直接在vscode的marketplace中搜索安装，如下所示：



![file](使用VSCode+PlantUML+C4-Model快速画架构图.assets/1-20210620165856355)



PS: 运行plantUML需要Java环境，请自行安装jdk并配置环境变量。

如果未安装Graphviz，在渲染的时候会报错：

```
/opt/local/bin/dot File not exist.
复制代码
```

#### 配置C4-model对应的snippets

vscode有工作区的概念，也就是一个工作目录。通过File-->open... 打开一个目录，就会默认把这个目录作为一个工作区。

在工作区中会默认读取当前目录下的一个隐藏子目录 .vscode 来获取当前工作区的设置，在这里我们只自定义C4-Model的snippets(代码片段)，用于辅助画图时语句编写。

首先从github上拉取C4-PlantUML项目源码，如下

```
git clone https://github.com/RicardoNiepel/C4-PlantUML
复制代码
```

可以看到其代码结构：



![file](使用VSCode+PlantUML+C4-Model快速画架构图.assets/1-20210620165856352)



我们需要的snippets文件就在这里，直接复制这个目录到工作区即可，比如我的工作区：



![file](使用VSCode+PlantUML+C4-Model快速画架构图.assets/1-20210620165856329)



#### 实操画图

当画图可以用代码实现时，是不是觉得有点兴奋，普通的UML图形语法可以参考这里http://plantuml.com/zh/sitemap-language-specification

我这里只针对C4-Model来简单介绍下。

##### 常见元素

在C4-Model中有几个元素，都比较直观：

```
Person:人员
System:系统，包含DB、Application、WebPage等
System_ext:外部系统
System_Boundary:系统边界，在这个边界中的都是系统的组成部分，里面一般是Container级别的组件
Container:容器，不是Docker之类的容器，简单点可以直接理解为System的组成部分，比如DB、Application等
ContainerDb:DB
Container_Boundary:容器边界，在这个边界范围内的都是容器的组成部分，里面一般都是Component级别的数据
Component:组件，比如一个Controller，一个Service逻辑处理类，一个Domain等
Rel:连接线
复制代码
```

还需要额外强调下，普通的画图工具，文件后缀需要保存为.uml，而c4的，需要保存为.puml。先保存为对应的后缀，snippets才会在后续的编写过程中生效。

还有一些特殊的代码用来指定图的模式--注意后面要加上()：

##### LAYOUT_WITH_LEGEND()

在include之后加上这行代码，会在生成图形的右下角生成如下内容：



![file](使用VSCode+PlantUML+C4-Model快速画架构图.assets/1-20210620165856339)



##### LAYOUT_AS_SKETCH()

加上这个表示草稿，整个渲染生成的图形都是草稿样式，如下：



![file](使用VSCode+PlantUML+C4-Model快速画架构图.assets/1-20210620165856357)



##### LAYOUT_TOP_DOWN

排列方向，顾名思义为从上到下排列，注意不需要加括号

##### LAYOUT_LEFT_RIGHT

排列方向，顾名思义为从左到右排列，注意不需要加括号

##### 开始写对应的code

```
@startuml
!include /Users/alankim/gitspace/C4-PlantUML/C4_Container.puml

'LAYOUT_WITH_LEGEND()
LAYOUT_AS_SKETCH()

Person(user, "用户")

System_Boundary(item, "商品相关"){
    System(itemCenter, "商品中心", "")
    System(priceCenter, "价格中心", "")
    System(InventoryCenter,"销售库存","")
}

System_Ext(orderPlatform, "订单平台")
System_Ext(wmsStock,"wms库存")
System_Ext(buy,"导购平台")

Rel(wmsStock, InventoryCenter, "库存上抛")
Rel(buy,itemCenter,"商品信息查询")
Rel(user,orderPlatform,"提交订单")
Rel(buy,priceCenter,"价格查询")
Rel(orderPlatform,InventoryCenter,"扣减库存")
Rel(InventoryCenter, itemCenter, "查询商品sku")
Rel(priceCenter,itemCenter,"查询商品sku")
Rel(user,buy,"用户浏览")

@enduml
复制代码
```

这个图画的比较复杂，基本上把大部分组件都用上了，对应生成的图形如下：



![file](使用VSCode+PlantUML+C4-Model快速画架构图.assets/1-20210620165856311)



> 本文由博客一文多发平台 [OpenWrite](https://openwrite.cn/?from=article_bottom) 发布！





[使用VSCode+PlantUML+C4-Model快速画架构图 (juejin.cn)](https://juejin.cn/post/6844903975678902279#heading-4)