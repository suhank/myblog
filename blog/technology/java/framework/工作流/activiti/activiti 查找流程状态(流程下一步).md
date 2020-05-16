[TOC]



# activiti 查找流程状态(流程下一步)

## 需求：

在显示用户所有提交的申请时，需要显示该流程发起时间、流程目前流动状态等信息。

## 思路：

​    1、第一想到的方式是在每个流程任务环节task.complete(id)之后，通过查找流程实例processInstance activity来获取下一流程环节名称，然后保存到流程变量中。该方法缺点：当前task若是最有一个环节，一单task.complete之后，流程已经结束，无法修改已经结束的流程的变量(目前我没有找到合适的代码方法)

​    2、仔细查看数据库，有个act_hi_actinst表，该表存放了流程实例的所有activity。查找相应API，得到有如下方法：

```java
historyService.createHistoricActivityInstanceQuery()//
	.processInstanceId(hvi.getProcessInstanceId())//
	.unfinished()//未完成的活动(任务)
	.singleResult()
```

如果查找结果为null，则该流程实例已经走完，如果不为空，则查出来的activity 就是流程实例的下一环节。到此，流程状态(流程下一步环节)顺利找到。



我把结果存放在MAP中，然后用JSON返回：

```java
HistoricActivityInstance hai=historyService.createHistoricActivityInstanceQuery()//
	.processInstanceId(hvi.getProcessInstanceId())//
	.unfinished()
	.singleResult();
if(hai!=null){
	map.put("piState", hai.getActivityName());// 流程状态
}else{
	map.put("piState", "完结");// 流程状态
}
```

<https://blog.51cto.com/pjwqh/1654178>