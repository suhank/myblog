# activiti 任务的签收与委托

2017年06月24日 17:35:39 [zhanjr](https://me.csdn.net/Zhanjr) 阅读数 1827

 

Activiti 中关于 Task 的数据库表中有两个直接翻译含义相近的字段 OWNER_ 与 ASSIGNEE_。

## ASSIGNEE_（受理人）

任务的受理人，即执行该任务的用户。

1、当流程模型 xml 中指定了受理人时，Task 会直接填入该用户；

2、当没有指定或仅仅指定了候选人或候选组的时候，该字段为空。

当该字段为空时，可以使用签收功能指定受理人，即从候选人或候选组中选择用户签收该任务。

通过 TaskService 的 claim 方法可以实现流程的签收：

```
    void claim(String taskId,String userId)
```

通过 TaskService 的 setAssignee 方法同样可以实现流程的认领。

```
    taskService.setAssignee(String taskId, String userId);
```

两个方法的区别在于执行 claim 方法时会检查该任务是否已被签收，如果已被签收，则会抛出 ActivitiTaskAlreadyClaimedException 异常，其他方面两个方法效果一致。

## OWNER_（委托人，任务的所属人）

当受理人因故委托其他人来操作当前任务的时候，受理人就成为了委托人，而被委托人成为新的受理人。

通过 TaskService 的 delegateTask 方法可以实现流程的签收：

```
    /**
     * @param taskId 需要被委托的任务id
     * @param userId 需要设为新的受理人的用户id
     */
    void delegateTask(String taskId,String userId)
```





<https://blog.csdn.net/Zhanjr/article/details/73692984>