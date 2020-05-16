[TOC]



# Activiti流程变量详解 



## 1、什么事流程变量？

在流程执行或者任务执行的过程中，用于设置和获取变量，使用流程变量在流程传递的过程中传递业务参数。 
对应的表： 
act_ru_variable：正在执行的流程变量表 
act_hi_varinst：流程变量历史表

## 2、setVariable和setVariableLocal的区别

### setVariable

设置流程变量的时候，流程变量名称相同的时候，后一次的值替换前一次的值，此设置为全局设置，每一个Task都可以获取到。

### setVariableLocal

针对特定Task环节设置的参数，获取时必须传入TaskID，当此环节完结后，就只能在历史中查询，好处是多个Task可以设置同名的参数而不影响，他们独立存在。 
总结来说，setVariable作用域为整个流程实例，一此设置多次使用，setVariableLocal作用域为Task本身，一此设置一次使用。

### 参考示例代码

```
/**
* 设置流程变量
*/
@Test
public void variablesTest(){
    TaskService taskService = processEngine.getTaskService();
    String taskId = "5503";
    //私有变量：与任务ID绑定，任务私有变量，必须要当前的taskId才可以获取到
    taskService.setVariableLocal(taskId, "请假天数", 5);
    //全局变量：可在当前流程所有Task可获取
    taskService.setVariable(taskId, "请假日期", "2016/07/29");
    //获取私有变量
    String days =taskService.getVariableLocal(taskId, "请假天数").toString();
    //获取全局变量
    String time =taskService.getVariable(taskId, "请假日期").toString();
    System.out.println("请假天数:"+days);//请假天数:5
    System.out.println("请假日期:"+time);//请假日期:2016/07/29
}
```

## 3、注意事项

在同一Task环节，getVariableLocal 和 getVariable 都可获取到本环节的所有参数（交叉获取），参考代码如下：

```
    @Test
    public void variablesTest(){
        TaskService taskService = processEngine.getTaskService();
        String taskId = "5503";
        //可通过getVariableLocal的形式获取到setVariable的全局变量
        taskService.setVariable(taskId, "请假天数", 1);
        String days1 =taskService.getVariableLocal(taskId, "请假天数").toString();
        System.out.println("请假天数:"+days1);//请假天数:1
        //可通过getVariable的形式获取到setVariableLocal的私有变量
        taskService.setVariableLocal(taskId, "请假天数", 2);
        String days2 =taskService.getVariable(taskId, "请假天数").toString();
        System.out.println("请假天数:"+days2);//请假天数:2
    }
```

如果同一Task环节，setVariable 和 setVariableLocal 同时设置了相同的参数名，则后面设置的值会覆盖前面的，代码如下：

```
    TaskService taskService = processEngine.getTaskService();
    String taskId = "5503";
    taskService.setVariable(taskId, "请假天数", 5);
    taskService.setVariableLocal(taskId, "请假天数", 10);
    String days1 =taskService.getVariableLocal(taskId, "请假天数").toString();
    String days2 =taskService.getVariable(taskId, "请假天数").toString();
    System.out.println("请假天数:"+days1+"-"+days2); 
    //返回结果：请假天数:10-10
```



## 4、其他设置流程参数的方式及作用域

启动流程时设置，为全局变量，任意环节都可以获取。 
Task完成时可设置，为全局变量，任意环节都可以获取。



https://www.zybuluo.com/ruoli/note/473448#%E5%8F%82%E8%80%83%E7%A4%BA%E4%BE%8B%E4%BB%A3%E7%A0%81