[TOC]



# activiti 之流程启动者

## 设置流程启动者

在启动一个流程时，我们会有将当前用户启动的流程保存起来，作为流程发起人（启动人、申请人、提交人）

而在保存这个流程启动者信息，api 没有明确规范该怎么存。所以这里我总结下我学到的保存流程启动者信息的方法。

- 咖啡兔博客中的做法 - [Activiti设置流程发起用户信息 - 咖啡兔 - HenryYan](http://www.kafeitu.me/activiti/2012/05/20/set-process-start-user.html)
  注： 如果单纯照博客这么做的话，确实能在 `ACT_HI_PROCINST` 的 `START_USER_ID_` 字段存放该值，但是我却没有取出来，一直是null。

- 同上，也用 ： `identityService.setAuthenticatedUserId(userId);` ，但是这里还要配合 流程定义里面的 开始事件 `startEvent` ,在开始事件中设置初始信息 。eg:

  ```
  <startEvent id="startevent1" name="Start" activiti:initiator="applyUserId"/>
  ```

  注： 这里applyUserId 是被存放在流程变量中的 , 而它的值就是 `identityService.setAuthenticatedUserId(userId)` 存入的userId。 因为是流程变量，所以可以用通用的获取流程变量的方法得到。

- 使用流程变量（Map） 。之前提过，在启动流程时有很多方法，同一个方法还有很多重载。如：

  ```
  startProcessInstanceByKey(String processDefinitionKey, Map<String,Object> variables);
  ```

  这里启动除了给流程定义的key，还给了一个map ,这个map 维护了了这个流程实例的变量。里面你能够存放很多东西，包括启动人信息。如：

  ```
  // 设置申请人,将之保存在流程变量中
  Map<String ,Object > variables = new HashMap<>();
  variables.put("applyUser","kk");
  ```

  至于得到流程变量的值，示例如下：

  ```
     Map<String,Object> vars = taskService.getVariables(task.getId());
      for (String variableName : vars.keySet()) {
          String val = (String) vars.get(variableName);
          System.out.println(variableName + " = " +val);
      }
  ```

- 保存在业务表单中。在业务表单维护一个冗余字段，用于保存发起人。咖啡兔的Demo源代码就那么干的。



## 查询流程启动者

发起流程时，配置activiti:initiator属性，并且在代码中：

```
Authentication.setAuthenticatedUserId(userId);
```

其中，userId对应流程发起人
查询某人发起的流程关键代码：

```
@Autowired
private HistoryService historyService;
List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().startedBy(userId).list();
```




原文链接：https://blog.csdn.net/u010784959/article/details/79259188

## 问题

请问下启动人 保存在流程变量中的key是“ applyUser”吗 还是“ applyUserId”？ 保存为“ applyUser”的话能在ACT_HI_PROCINST 的 START_USER_ID_中保存吗



### 回复 

```
<startEvent id="startevent1" name="Start" activiti:initiator="applyUserId"/>
```

这里applyUserId你可以随意起名称的，反正用的地方名称一样就可以，

 



<https://segmentfault.com/a/1190000000660671>