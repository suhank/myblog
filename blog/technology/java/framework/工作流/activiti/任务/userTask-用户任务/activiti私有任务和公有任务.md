[TOC]



# Activiti私有任务和公有任务



主要介绍Activiti公有任务和私有任务的区别，如何设置公有任务，如何认领公有任务，如何将公有任务转换为私有任务



## 1、共同点

公有任务和私有任务的概念为UserTask所特有，需要人才参与。

## 2、私有任务

私有任务即有直接分配给指定用户的任务。只有一个用户可以成为任务的执行者。在activiti中，用户叫做执行者。 拥有执行者的用户任务 （即私有任务）对其他用户是不可见的。只能出现执行者的个人任务列表中。 
直接把用户任务分配给指定用户使用assignee属性，XML代码如下：

```
<usertask activiti:assignee="ruoli" id="theTask" name="my task" />
```

Assignee属性对应的值为一个用户的ID。 
直接分配给用户的任务可以通过TaskService像下面这样办理：

```
List<task> tasks =taskService.createTaskQuery().taskAssignee("sirius").list();
Task task = tasks.get(0);// 假设任务集合的第一条是要办理的任务
taskService.complete(task.getId());
```



## 3、公有任务

有的用户任务在指派时无法确定具体的办理者，这时任务也可以加入到人员的候选任务列表中，然后让这些人员选择性认领和办理任务。 
公有任务的分配可以分为指定候选用户和候选组两种。 

a) 把任务添加到一批用户的候选任务列表中，使用candidateUsers属性，XML内容如下：

```
<usertask activiti:candidateusers="sirius,kermit" id="theTask" name="myTask" />
```

candidateUsers属性内为用户的ID，多个用户ID之间使用（半角）逗 号间隔。 

b) 把任务添加到一个或多个候选组下，这时任务对组下的所有用户可 见，首先得保证每个组下面有用户，通过IdentityService对象创建用户 和组，然后把用户添加到对应的组下。 
然后配置组任务，使用candidateGroups属性，XML内容如下：

```
<usertask activiti:candidategroups="testGroup,developGroup" id="theTask" name="myTask" />
```

间接分配给用户的任务，可以通过TaskService像下面这样操作：

```java
List<task>tasks =taskService.createTaskQuery().taskCandidateUser("sirius").list();
Task task = tasks.get(0);// 假设任务集合的第一条是要办理的任务
String taskId = task.getId();
taskService.claim(taskId ,"sirius"); //认领任务，让用户成为任务的执行者，将公有任务转为私人任务
taskService.complete(taskId );
```





https://www.zybuluo.com/ruoli/note/473607