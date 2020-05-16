[TOC]



# 工作流学习——Activiti流程实例、任务管理四步曲

2015年06月26日 10:15:15 [fightingKing](https://me.csdn.net/zwk626542417) 阅读数 70305

# 一、前言

​    上一篇文章中我们将Activiti流程定义管理的知识进行了介绍，这篇文章我们继续Activiti流程实例和任务管理的学习。

# 二、正文

 

## **流程实例（ProcessInstance** ）

​    流程实例（ProcessInstance）和上一篇文章中流程定义（ProcessDefinition）的关系，与类和实例对象的关系有点像，ProcessDefinition是整个流程步骤的说明而ProcessInstance就是指流程定义从开始到结束的那个最大的执行路线。 

## **执行对象（Execution**）

​    提到ProcessInstance同时又会出现另一个名词，那就是执行对象（Execution），Execution是按照ProcessDefinition的规则执行的当前的路线，

​    如果ProcessDefinition只有一个执行路线的话，那么Execution和ProcessInstance就是完全一样了如果ProcessDefinition中有多个执行路线的话，Execution和ProcessInstance可能是同一个也可能不是同一个。所以得出结论：*一个流程中**P**rocessInstance有且只能有一个，而Execution可以存在多个。* 

## **任务（Task ）**

​    任务（Task）应该比较好理解，就是当流程执行到某步骤或某环节时生产的任务信息。

​    在上篇文章中我们将如何画流程图、如何部署流程定义已经介绍，流程定义和流程实例的关系刚才已经介绍了，所有现在我们就该开始启动流程实例了：

## 代码

### **启动流程实例**

```
/**
 * 启动流程实例
 */
@Test
public void startProcessInstance() {
	// 流程定义的key
	String processDefinitionKey = "HelloWorld";
	ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行的流程实例和执行对象相关的Service
			.startProcessInstanceByKey(processDefinitionKey);// 使用流程定义的key启动流程实例，key对应HelloWorld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
	System.out.println("流程实例ID:" + pi.getId());
	System.out.println("流程定义ID:" + pi.getProcessDefinitionId());
}
```

运行结果：

​    流程实例ID:501

​    流程定义ID:HelloWorld:2:404

 

说明： 

​    1)*在**数据库的act_ru_execution正在执行的执行对象表**中插入一条记录*

​    *2)在数据库的act_hi_procinst程实例的历史表中插入一条记录*

​    *3)**在数据库的**act_hi_actinst活动节点的历史表**中插入一条记录*

​    *4)**我们图中节点都是任务节点，所以**同时也会在act_ru_task流程实例的历史表添加一条记录*

​    *5)**在数据库的**act_hi_taskinst任务历史表**中也插入一条记录。*

 

 



### **查询历史流程实例**

​    

​    流程实例启动以后，我们也可以对某个流程实例一共一共执行了多少次流程进行查询，因为我们本例中现在刚进行了一个流程，所以目前只能查出一个流程：



```java
/**
 * 查询历史流程实例
 */
@Test
public void findHistoryProcessInstance(){
	String processInstanceId="501";
	HistoricProcessInstance hpi = processEngine.getHistoryService()
			.createHistoricProcessInstanceQuery()
			.processInstanceId(processInstanceId)
			.singleResult();
	System.out.println(hpi.getId() +"    "+hpi.getProcessDefinitionId()+"   "+ hpi.getStartTime()+"   "+hpi.getDurationInMillis());
}
```

运行结果：

​    501    HelloWorld:2:404   Fri Jun 26 09:34:51 CST 2015   null



### **查询当前的个人任务**



​    在上面讲流程启动后，因为该节点是任务节点所以在任务表中插入了任务的记录，现在我们就通过办理人将任务进行下查询：



```
/**
 * 查询当前的个人任务
 */
@Test
public void findMyPersonTask() {
	String assignee = "张三"; // TODO
	List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的service
			.createTaskQuery()// 创建任务查询对象
			// 查询条件
			.taskAssignee(assignee)// 指定个人任务查询，指定办理人
			// .taskCandidateGroup("")//组任务的办理人查询
			// .processDefinitionId("")//使用流程定义ID查询
			// .processInstanceId("")//使用流程实例ID查询
			// .executionId(executionId)//使用执行对象ID查询
			/** 排序 */
			.orderByTaskCreateTime().asc()// 使用创建时间的升序排列
			// 返回结果集
			// .singleResult() //返回唯一的结果集
			// .count()//返回结果集的数量
			// .listPage(firstResult, maxResults)//分页查询
			.list();// 返回列表
	if (list != null && list.size() > 0) {
		for (Task task : list) {
			System.out.println("任务ID：" + task.getId());
			System.out.println("任务名称:" + task.getName());
			System.out.println("任务的创建时间:" + task.getCreateTime());
			System.out.println("任务的办理人:" + task.getAssignee());
			System.out.println("流程实例ID:" + task.getProcessInstanceId());
			System.out.println("执行对象ID:" + task.getExecutionId());
			System.out.println("流程定义ID:" + task.getProcessDefinitionId());
			System.out
					.println("##################################################");
		}
```

运行结果：

​    任务ID：504

​    任务名称:提交申请

​    任务的创建时间:Fri Jun 2609:34:51 CST 2015

​    任务的办理人:张三

​    流程实例ID:501

​    执行对象ID:501

​    流程定义ID:HelloWorld:2:404

​    \##################################################

 

说明：



​    1)因为是任务查询，所以从processEngine中应该得到TaskService

​    2)使用TaskService获取到任务查询对象TaskQuery

​    3)为查询对象添加查询过滤条件，使用taskAssignee指定任务的办理者（即查询指定用户的代办任务），同时可以添加分页排序等过滤条件

​    4)调用list方法执行查询，返回办理者为指定用户的任务列表

​    5)任务ID、名称、办理人、创建时间可以从act_ru_task表中查到。

​    6)在现在这种情况下，ProcessInstance相当于Execution

​    *7) 一个Task节点和Execution节点是1对1的情况，在task对象中使用Execution_来表示他们之间的关系*

​    *8)任务ID在数据库表act_ru_task中对应“ID_”列*

 



### **完成任务**



​    *查询完任务后，我们接下来将这个任务**id**为**504**的任务进行完成：*



```
/**
 * 完成我的任务
 */
@Test
public void compliteMyPersonTask() {
	// 任务ID
	String taskId = "504";
	processEngine.getTaskService().complete(taskId);
	;
	System.out.println("完成任务：任务ID:" + taskId);
}
```

运行结果：

​    完成任务：任务ID:504

说明：



​    1)是完成任务，所以从ProcessEngine得到的是TaskService。

​    2)当执行完这段代码，再以员工的身份去执行查询的时候，会发现这个时候已经没有数据了，因为正在执行的任务中没有数据。

​    3)对于执行完的任务，activiti将从act_ru_task表中删除该任务，下一个任务会被插入进来。

​    4)以”部门经理”的身份进行查询，可以查到结果。因为流程执行到部门经理审批这个节点了。

​    5)再执行办理任务代码，执行完以后以”部门经理”身份进行查询，没有结果。

​    6)重复第3和4步直到流程执行完。

 



### **查询历史任务**



​    *员工张三的任务已经完成，现在任务到部门经理李四了，如果我们现在仍然查询张三的任务的话，自然是查询不到了，只有查询李四才可以查到，不过我们可以通过流程实例**id**查询历史任务，查询历史任务可以将已经办理过的任务和现在正在执行的任务都查询出来：*



```java
/**
 * 查询历史任务
 */
@Test
public void findHistoryTask(){
	String processInstanceId="501";
	List<HistoricTaskInstance> list = processEngine.getHistoryService()//与历史数据（历史表）相关的service
			.createHistoricTaskInstanceQuery()//创建历史任务实例查询
			.processInstanceId(processInstanceId)
//				.taskAssignee(taskAssignee)//指定历史任务的办理人
			.list();
	if(list!=null && list.size()>0){
		for(HistoricTaskInstance hti:list){
			System.out.println(hti.getId()+"    "+hti.getName()+"    "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());
			System.out.println("################################");
		}
	}	
 
}
```

运行结果：



​    504    提交申请   501   Fri Jun 26 09:34:51 CST2015   Fri Jun 26 09:50:50 CST 2015   959867

​    \################################

​    602    审批【部门经理】   501   Fri Jun 26 09:50:51 CST2015   null   null

​    \################################

 



### **查询流程是否结束**



​    *我们还可以通过流程实例**id**查询某个流程现在的状态，是仍然在执行过程中呢，还是流程执行已经结束：*

 

```
/**
 * 查询流程状态（判断流程正在执行，还是结束）
 */
@Test
public void isProcessEnd(){
	String processInstanceId =  "501";
	ProcessInstance pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
			.createProcessInstanceQuery()//创建流程实例查询
			.processInstanceId(processInstanceId)//使用流程实例ID查询
			.singleResult();
	
	if(pi==null){
		System.out.println("流程已经结束");
	}
	else{
		System.out.println("流程没有结束");
	}
	
}
```



运行结果：



​    流程没有结束

 

# 三、总结

 

​    我们这篇文章主要是流程实例、执行对象、任务以及他们之间的关系进行了学习，同时我们还将启动和查询流程实例、判断流程实例是否执行结束、查看和办理任务以及查询历史任务都进行了介绍。





<https://blog.csdn.net/zwk626542417/article/details/46646565>