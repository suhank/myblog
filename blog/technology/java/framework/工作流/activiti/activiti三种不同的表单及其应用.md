[TOC]



# activiti三种不同的表单及其应用 

## 开篇语

这个恐怕是初次接触工作流最多的话题之一了，当然这个不是针对Activiti来说的，每个工作流引擎都会支持多种方式的表单。目前大家讨论到的大概有三种。

1. 动态表单
2. 外置表单
3. 普通表单

具体选择哪种方式只能读者根据自己项目的实际需求结合现有技术或者架构、平台选择！！！

## 1.动态表单

这是程序员最喜欢的方式，同时也是客户最讨厌的……因为表单完全没有布局，所有的表单元素都是顺序输出显示在页面。

此方式需要在流程定义文件(bpmn20.xml)中用activiti:formProperty属性定义，可以在开始事件（Start Event）和Task上设置，而且支持变量自动替换，语法就是UEL。

```javascript
<startevent
id="startevent1"
name="Start">

  <extensionelements>

    <activiti:formproperty
id="name"
name="Name"
type="string"></activiti:formproperty>

  </extensionelements>

</startevent>

<usertask
id="usertask1"
name="First Step">

  <extensionelements>

    <activiti:formproperty
id="setInFirstStep"
name="SetInFirstStep"
type="date"></activiti:formproperty>

  </extensionelements>

</usertask>
```

注意：这种方式表单的内容都是以key和value的形式数据保存在引擎表中！！！



## 2.外置表单

这种方式常用于基于工作流平台开发的方式，代码写的很少，开发人员只要把表单内容写好保存到.form文件中即可，然后配置每个节点需要的表单名称（form key），实际运行时通过引擎提供的API读取Task对应的form内容输出到页面。

此种方式对于在经常添加新流程的需求比较适用，可以快速发布新流程，把流程设计出来之后再设计表单之后两者关联就可以使用了。例如公司内部各种简单的审批流程，没有业务逻辑处理，仅仅是多级审批是否通过等等情况，当流程需要一些特殊处理时可以借助Listener或者Delegate方式实现。Activiti Explorer就是使用的这种方式，表单信息都配置在流程定义文件中。

代码片段：

```javascript
<process
id="FormKey"
name="FormKey">

    <startevent
id="startevent1"
name="Start"
activiti:formkey="diagrams/form/start.form"></startevent>

    …

</process>
```

## 3.普通表单

这个是最灵活的一种方式，常用于业务比较复杂的系统中，或者业务比较固定不变的需求中，例如ERP系统。普通表单的特点是把表单的内容存放在一个页面（jsp、jsf、html等）文件中，存放方式也有两种（一体式、分离式）：

1.一体式：把整个流程涉及到的表单放在一个文件然后根据处理的任务名称匹配显示，kft-activiti-demo的普通表单模式就是一体式的做法，把表单内容封装在一个div里面，div的ID以节点的名称命名，点击“办理”按钮时用对话框的方式把div的内容显示给用户。

2.分离式：对于非Ajax应用来说比较常用，每个任务对应一个页面文件，点击办理的时候根据任务的ID动态指定表单页面。

和以上两种方式比较有两点区别：

a、表单：和第二种外置表单类似，但是表单的显示、表单字段值填充均由开发人员写代码实现。

b、数据表：数据表单独设计而不是和前两种一样把数据以key、value形式保存在引擎表中。

## 4.从业务数据和流程关联比较

1、动态表单：引擎已经自动绑定在一起了，不需要额外配置。

2、外置表单：和业务关联是可选的，提供的例子中是没有和业务关联的，如果需要关联只需要在提交StartForm的时候设置businessKey即可。

3、普通表单：这个应该是必须和业务关联，否则就是无头苍蝇了……

原文发布于微信公众号 - Linyb极客之路（gh_c420b2cf6b47）

原文发表时间：2018-05-06

本文参与[腾讯云自媒体分享计划](https://cloud.tencent.com/developer/support-plan)，欢迎正在阅读的你也加入，一起分享。

发表于 2018-07-26

[其他](https://cloud.tencent.com/developer/tag/125?entry=article)





<https://cloud.tencent.com/developer/article/1165539>