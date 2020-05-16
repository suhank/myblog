[TOC]



# Activiti基础操作 

- Activiti基础操作
  - 一、数据库创建与部署
    - [1、数据库创建](https://www.zybuluo.com/ruoli/note/469131#1数据库创建)
    - [2、部署流程图](https://www.zybuluo.com/ruoli/note/469131#2部署流程图)
  - 二、流程定义操作
    - [1、查询流程定义](https://www.zybuluo.com/ruoli/note/469131#1查询流程定义)
    - [2、删除询流程定义](https://www.zybuluo.com/ruoli/note/469131#2删除询流程定义)
    - [3、查看流程图](https://www.zybuluo.com/ruoli/note/469131#3查看流程图)
  - 三、流程扭转操作
    - [1、启动流程实例](https://www.zybuluo.com/ruoli/note/469131#1启动流程实例)
    - [2、查询个人任务](https://www.zybuluo.com/ruoli/note/469131#2查询个人任务)
    - [3、查询流程实例状态](https://www.zybuluo.com/ruoli/note/469131#3查询流程实例状态)
    - [4、删除所有流程数据](https://www.zybuluo.com/ruoli/note/469131#4删除所有流程数据)
  - 四、历史数据操作
    - [1、查询历史流程实例](https://www.zybuluo.com/ruoli/note/469131#1查询历史流程实例)
    - [2、查询个人历史任务](https://www.zybuluo.com/ruoli/note/469131#2查询个人历史任务)
    - [3、查询历史流程变量](https://www.zybuluo.com/ruoli/note/469131#3查询历史流程变量)



## 一、数据库创建与部署



### 1、数据库创建

以下两种方式可以创建Activiti数据库，初始化必须表结构，示例代码如下：



```
package activiti.Act_1_数据库创建与部署;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
public class Act_1_创建表结构 {
    /**
     * 方式一：依赖配置文件
     */
    @Test
    public void createTable() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource(
                        "activiti.cfg.xml")  
                .buildProcessEngine();
        System.out.println("processEngine:" + processEngine);
    }
    /**
     * 方式二：不依赖配置文件
     */
    @Test  
    public void createTable2(){  
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();  
        //连接数据库的配置  
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");  
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8");  
        processEngineConfiguration.setJdbcUsername("root");  
        processEngineConfiguration.setJdbcPassword("root");  
        /** 
            public static final String DB_SCHEMA_UPDATE_FALSE = "false";//不能自动创建表，需要表存在 
            public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";//先删除表再创建表 
            public static final String DB_SCHEMA_UPDATE_TRUE = "true";//如果表不存在，自动创建表 
         */  
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);  
        //工作流的核心对象，ProcessEnginee对象  
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();  
        System.out.println("processEngine:"+processEngine);  
    }  
}
```

依赖配置文件如下：



```
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemalocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd">
<bean class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration" id="processEngineConfiguration">
        <!-- 连接数据的配置 -->
    <property name="jdbcDriver" value="com.mysql.jdbc.Driver"></property>
    <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/activiti_wfe?useUnicode=true&characterEncoding=utf8"></property>
    <property name="jdbcUsername" value="root"></property>
    <property name="jdbcPassword" value="root"></property>
    <!-- 没有表创建表 -->
    <property name="databaseSchemaUpdate" value="true"></property>
</bean>
</beans>
```



### 2、部署流程图

画好流程图后，可以使用以下两种方式部署流程定义，代码如下：



```
    package activiti.Act_1_数据库创建与部署;
    import java.io.InputStream;
    import java.util.zip.ZipInputStream;
    import org.activiti.engine.ProcessEngine;
    import org.activiti.engine.ProcessEngines;
    import org.activiti.engine.repository.Deployment;
    import org.junit.Test;
    public class Act_2_部署流程定义 {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        /**
         * 方式一：
         * 部署流程定义*/
        @Test
        public void deploymentProcessDefinition(){
            Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
                            .createDeployment()//创建一个部署对象
                            .name("测试部署名称")//添加部署的名称
                            .addClasspathResource("diagrams/TestAct.bpmn")//从classpath的资源中加载，一次只能加载一个文件
                            .addClasspathResource("diagrams/TestAct.png")//从classpath的资源中加载，一次只能加载一个文件
                            .deploy();//完成部署
            System.out.println("部署ID："+deployment.getId());//1
            System.out.println("部署名称："+deployment.getName());//helloworld入门程序  
        }
        /**
         * 方式二：
         * 部署流程定义（从zip）*/
        @Test
        public void deploymentProcessDefinition_zip(){
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip");
            ZipInputStream zipInputStream = new ZipInputStream(in);
            Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
                            .createDeployment()//创建一个部署对象
                            .name("流程定义")//添加部署的名称
                            .addZipInputStream(zipInputStream)//指定zip格式的文件完成部署
                            .deploy();//完成部署
            System.out.println("部署ID："+deployment.getId());//
            System.out.println("部署名称："+deployment.getName());//
        }
    }
```



## 二、流程定义操作



### 1、查询流程定义



```
package activiti.Act_2_流程定义操作;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
public class Act_1_查询流程定义 {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /**
     * 查询流程定义
     */
    @Test
    public void findProcessDefinition(){
        List<ProcessDefinition> list = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
                        .createProcessDefinitionQuery()//创建一个流程定义的查询
                        /**指定查询条件,where条件*/
//                      .deploymentId(deploymentId)//使用部署对象ID查询
//                      .processDefinitionId(processDefinitionId)//使用流程定义ID查询
//                      .processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
//                      .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询
                        /**排序*/
                        .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
//                      .orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列
                        /**返回的结果集*/
                        .list();//返回一个集合列表，封装流程定义
//                      .singleResult();//返回惟一结果集
//                      .count();//返回结果集数量
//                      .listPage(firstResult, maxResults);//分页查询
        if(list!=null && list.size()>0){
            for(ProcessDefinition pd:list){
                System.out.println("流程定义ID:"+pd.getId());//流程定义的key+版本+随机生成数
                System.out.println("流程定义的名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值
                System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值
                System.out.println("流程定义的版本:"+pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1
                System.out.println("资源名称bpmn文件:"+pd.getResourceName());
                System.out.println("资源名称png文件:"+pd.getDiagramResourceName());
                System.out.println("部署对象ID："+pd.getDeploymentId());
                System.out.println("###################");
            }
        }           
    }
    /**
     * 
     * 查询最新版本的流程定义
     * 
     * */
    @Test
    public void findLastVersionProcessDefinition(){
        List<ProcessDefinition> list = processEngine.getRepositoryService()//
                        .createProcessDefinitionQuery()//
                        .orderByProcessDefinitionVersion().asc()//使用流程定义的版本升序排列
                        .list();
        /**
         * Map<String,ProcessDefinition>
              map集合的key：流程定义的key
              map集合的value：流程定义的对象
              map集合的特点：当map集合key值相同的情况下，后一次的值将替换前一次的值
         */
        Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
        if(list!=null && list.size()>0){
            for(ProcessDefinition pd:list){
                map.put(pd.getKey(), pd);
            }
        }
        List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());
        if(pdList!=null && pdList.size()>0){
            for(ProcessDefinition pd:pdList){
                System.out.println("流程定义ID:"+pd.getId());//流程定义的key+版本+随机生成数
                System.out.println("流程定义的名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值
                System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值
                System.out.println("流程定义的版本:"+pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1
                System.out.println("资源名称bpmn文件:"+pd.getResourceName());
                System.out.println("资源名称png文件:"+pd.getDiagramResourceName());
                System.out.println("部署对象ID："+pd.getDeploymentId());
                System.out.println("#########################################################");
            }
        }   
    }
}
```



### 2、删除询流程定义



```
package activiti.Act_2_流程定义操作;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
public class Act_2_删除流程定义 {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /**删除流程定义*/
    @Test
    public void deleteProcessDefinition(){
        //使用部署ID，完成删除
        String deploymentId = "1";
        /**
         * 不带级联的删除
         *    只能删除没有启动的流程，如果流程启动，就会抛出异常
         */
//      processEngine.getRepositoryService()//
//                      .deleteDeployment(deploymentId);
        /**
         * 级联删除，删除流程部署信息，流程定义信息，正在执行和历史关联的流程环节
         *    不管流程是否启动，都能可以删除
         */
        processEngine.getRepositoryService()//
                        .deleteDeployment(deploymentId, true);
        System.out.println("删除成功！");
    }
    /**
     * 删除流程定义（删除key相同的所有不同版本的流程定义）
     * */
    @Test
    public void deleteProcessDefinitionByKey(){
        //流程定义的key
        String processDefinitionKey = "testAct";
        //先使用流程定义的key查询流程定义，查询出所有的版本
        List<ProcessDefinition> list = processEngine.getRepositoryService()//
                        .createProcessDefinitionQuery()//
                        .processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
                        .list();
        //遍历，获取每个流程定义的部署ID
        if(list!=null && list.size()>0){
            for(ProcessDefinition pd:list){
                //获取部署ID
                String deploymentId = pd.getDeploymentId();
                processEngine.getRepositoryService()//
                            .deleteDeployment(deploymentId, true);
            }
        }
    }
}
```



### 3、查看流程图



```
package activiti.Act_2_流程定义操作;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
public class Act_3_查看流程图 {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /**查看流程图
     * @throws IOException */
    @Test
    public void viewPic() throws IOException{
        /**将生成图片放到文件夹下*/
        String deploymentId = "301";
        //获取图片资源名称
        List<String> list = processEngine.getRepositoryService()//
                        .getDeploymentResourceNames(deploymentId);
        //定义图片资源的名称
        String resourceName = "";
        if(list!=null && list.size()>0){
            for(String name:list){
                if(name.indexOf(".png")>=0){
                    resourceName = name;
                }
            }
        }
        //获取图片的输入流
        InputStream in = processEngine.getRepositoryService()//
                        .getResourceAsStream(deploymentId, resourceName);
        //将图片生成到D盘的目录下
        File file = new File("D:/"+resourceName);
        //将输入流的图片写到D盘下
        FileUtils.copyInputStreamToFile(in, file);
    }
}
```



## 三、流程扭转操作



### 1、启动流程实例



```
package activiti.Act_3_流程扭转操作;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
public class Act_1_启动流程实例 {
    /**启动流程实例*/
    @Test
    public void startProcessInstance(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //流程定义的key
        String processDefinitionKey = "processTest";
        List<String> users=new ArrayList<String>();
        users.add("a1");
        users.add("b1");
        Map<String,Object> parameter=new HashMap<String, Object>();
        parameter.put("users", users);
        ProcessInstance pi = processEngine.getRuntimeService()
        //与正在执行的流程实例和执行对象相关的Service
                        .startProcessInstanceByKey(processDefinitionKey,parameter);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
        System.out.println("流程实例ID:"+pi.getId());//流程实例ID    101
        System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   helloworld:1:4
    }
}
```



### 2、查询个人任务



```
package activiti.Act_3_流程扭转操作;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.task.Task;
import org.junit.Test;
public class Act_2_查询个人任务 {
    /**查询当前人的个人任务*/
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    @Test
    public void findMyPersonalTask(){
        String assignee = "admin";
        List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
                        .createTaskQuery()//创建任务查询对象
                        .taskAssignee(assignee).active()//指定个人任务查询，指定办理人，此为私有任务
                        .includeTaskLocalVariables()
                        .includeProcessVariables()
                        .list();
        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
                System.out.println("Task中获取参数："+task.getProcessVariables());
                System.out.println("Task中获取Local参数："+task.getTaskLocalVariables());
                System.out.println("getTaskService中获取参数："+processEngine.getTaskService().getVariables(task.getId()));
                System.out.println("流程处理人："+task.getAssignee());
                System.out.println("########################################################");
            }
        }
    }
}
```



### 3、查询流程实例状态



```
package activiti.Act_3_流程扭转操作;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
/**
 * 一个流程中，执行对象可以存在多个，但是流程实例只能有一个，一个执行对象可以对应多个任务
 * @author Administrator
 *
 */
public class Act_4_查询流程实例状态 {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /**
     * 查询流程状态（判断流程正在执行，还是结束）
     * */
    @Test
    public void isProcessEnd(){
        String processInstanceId = "2801";
        ProcessInstance pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
                        .createProcessInstanceQuery()//创建流程实例查询
                        .processInstanceId(processInstanceId)//使用流程实例ID查询
                        .singleResult();
        if(pi==null){
            System.out.println("流程已经结束");
        }
        else{
            System.out.println("流程正在执行");
        }
    }
}
```



### 4、删除所有流程数据



```
package activiti.Act_3_流程扭转操作;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
public class Act_7_删除所有流程数据 {
    @Test
    public void  removeIns(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine(); 
//      //删除正在运行的流程
        List<ProcessInstance>  instanceList=processEngine.getRuntimeService()
                                                         .createProcessInstanceQuery().list();
        System.out.println("开始删除正在运行的流程数据");
        for (ProcessInstance processInstance : instanceList) {
            processEngine.getRuntimeService().deleteProcessInstance(processInstance.getProcessInstanceId(), "");
            processEngine.getHistoryService().deleteHistoricProcessInstance(processInstance.getProcessInstanceId());
            System.out.println("已删除："+instanceList.indexOf(processInstance)+"/"+instanceList.size());
        }
        System.out.println("正在运行的流程删除完毕");
        //删除历史流程
        try{
        System.out.println("开始删除历史流程数据");
        SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        Date date = sdf.parse("2016-07-01 23:20:00");//删除指定日期之前的历史数据
        List<HistoricProcessInstance> list=processEngine.getHistoryService().createHistoricProcessInstanceQuery()
                                                        .startedBefore(date).list();
            for (HistoricProcessInstance processInstance : list){
                System.out.println(list.indexOf(processInstance));
                processEngine.getHistoryService().deleteHistoricProcessInstance(processInstance.getId());
                System.out.println("已删除："+list.indexOf(processInstance)+"/"+list.size());
            }
        System.out.println("历史流程删除完毕"); 
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



## 四、历史数据操作



### 1、查询历史流程实例



```
package activiti.Act_4_历史数据操作;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.junit.Test;
public class Act_1_查询历史流程实例 {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /**查询历史流程实例（后面讲）*/
    @Test
    public void findHistoryProcessInstance(){
        String processInstanceId = "401";
        HistoricProcessInstance hpi = processEngine.getHistoryService()//与历史数据（历史表）相关的Service
                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                        .processInstanceId(processInstanceId)//使用流程实例ID查询
                        .singleResult();
        System.out.println("流程实例ID："+hpi.getId()+"\n流程定义ID："+hpi.getProcessDefinitionId()+"\n流程开始时间："+hpi.getStartTime()+"\n流程结束时间："+hpi.getEndTime()+"\n流程持续毫秒数："+hpi.getDurationInMillis());
    }
}
```



### 2、查询个人历史任务



```
package activiti.Act_4_历史数据操作;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.Test;
public class Act_2_查询个人历史任务 {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /**查询历史任务（后面讲）*/
    @Test
    public void findHistoryTask(){
        String taskAssignee = "张三";
        List<HistoricTaskInstance> list = processEngine.getHistoryService()//与历史数据（历史表）相关的Service
                        .createHistoricTaskInstanceQuery()//创建历史任务实例查询
                        .taskAssignee(taskAssignee)//指定历史任务的办理人
                        .list();
        if(list!=null && list.size()>0){
            for(HistoricTaskInstance hti:list){
                System.out.println("TaskID："+hti.getId()+"\nTask名称："+hti.getName()+"\n流程实例ID："+hti.getProcessInstanceId()+"\n任务开始时间："+hti.getStartTime()+"\n任务结束时间："+hti.getEndTime()+"\n持续时间毫秒数："+hti.getDurationInMillis());
                System.out.println("################################");
            }
        }
    }
}
```



### 3、查询历史流程变量



```
package activiti.Act_4_历史数据操作;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;
public class Act_5_查询历史流程变量 {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    /**查询历史流程变量*/
    @Test
    public void findHistoryProcessVariables(){
        String processInstanceId = "2101";
        List<HistoricVariableInstance> list = processEngine.getHistoryService()
                        .createHistoricVariableInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .list();
        if(list!=null && list.size()>0){
            for(HistoricVariableInstance hvi:list){
                System.out.println(hvi.getId()+"   "+hvi.getProcessInstanceId()+"   "+hvi.getVariableName()+"   "+hvi.getVariableTypeName()+"    "+hvi.getValue());
                System.out.println("###############################################");
            }
        }
    }
}
```







 https://www.zybuluo.com/ruoli/note/469131#%E4%B8%80%E6%95%B0%E6%8D%AE%E5%BA%93%E5%88%9B%E5%BB%BA%E4%B8%8E%E9%83%A8%E7%BD%B2

https://www.zybuluo.com/ruoli/note/469131#%E4%B8%80%E6%95%B0%E6%8D%AE%E5%BA%93%E5%88%9B%E5%BB%BA%E4%B8%8E%E9%83%A8%E7%BD%B2https://www.zybuluo.com/ruoli/note/469131#一数据库创建与部署)