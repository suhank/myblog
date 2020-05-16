[TOC]



# Activiti的全局事件机制及其监听处理

## 概述

Activiti在5.15以后的版本后，增加了统一的事件入口，不需要再像以前那样，监听流程的事件时，在流程定义的BPMN文件中为每个节点及流程增加以下的配置，以实现监听事件的做法，这种做法导致我们发布流程时，需要对bpmn文件进行设置，非常不方便，若调整其XML或Class类名或包名，都需要对BPMN文件重新修改并且发布，难度可想而知。

------

为了规避这种问题，我们重新引入统一监控机制，其思路来自Activiti的开发指导文件，如下：

<http://www.activiti.org/userguide/index.html#eventDispatcherConfiguration>



## 构建Activiti的事件分发器

统一事件处理，有利于为流程与业务的结合提供统一入口的处理，同进为后续的流程扩展提供了便利，包括任务人员的指派、会签的计算、流程回退的处理、流程日志等提供数据的切入口，所以通过构建我们的事件监听器就显得非常重要了。



### 定义的全局事件监听器的入口

```java
package com.redxun.bpm.activiti.listener;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.redxun.saweb.util.WebAppUtil;

/**
 * Activiti的全局事件监听器，即所有事件均需要在这里统一分发处理
 * @author csx
 * @copyright http://www.redxun.cn
 *
 */
public class GlobalEventListener implements ActivitiEventListener{
 /**
  * 日志处理器
  */
 public final static Log logger=LogFactory.getLog(GlobalEventListener.class);
 
 //事件及事件的处理器
 //private Map<String,EventHandler> handlers=new HashMap<String, EventHandler>();
 //更换为以下模式，可以防止Spring容器启动时，ProcessEngine尚未创建，而业务类中又使用了这个引用
 private Map<String,String> handlers=new HashMap<String, String>();
 
 @Override
 public void onEvent(ActivitiEvent event) {
  String eventType=event.getType().name();
  logger.debug("envent type is ========>" + eventType);
  //根据事件的类型ID,找到对应的事件处理器
  String eventHandlerBeanId=handlers.get(eventType);
  if(eventHandlerBeanId!=null){
   EventHandler handler=(EventHandler)WebAppUtil.getBean(eventHandlerBeanId);
   handler.handle(event);
  }
 }

 @Override
 public boolean isFailOnException() {
  return false;
 }

 public Map<String, String> getHandlers() {
  return handlers;
 }

 public void setHandlers(Map<String, String> handlers) {
  this.handlers = handlers;
 }

 
}
```



### 如何把全局监听加至Activiti的配置中去

```xml
<bean id="processEngineConfiguration" class="org.activiti.spring.SpringProcessEngineConfiguration">
   <property name="dataSource" ref="dataSource" />
   <property name="transactionManager" ref="transactionManager" />
   <property name="databaseSchemaUpdate" value="true" />
   <property name="jobExecutorActivate" value="false" />
    <property name="enableDatabaseEventLogging" value="false" />
    <property name="databaseType" value="${db.type}" />
    <property name="idGenerator" ref="actIdGenerator"/>
    <property name="eventListeners">
      <list>
        <ref bean="globalEventListener"/>
      </list>
    </property>
    <property name="activityFontName" value="黑体"/>
    <property name="labelFontName" value="黑体"/>
    <!-- 用于更改流程节点的执行行为 -->
    <property name="activityBehaviorFactory" ref="activityBehaviorFactoryExt"/>
  </bean>

<bean id="globalEventListener" class="com.redxun.bpm.activiti.listener.GlobalEventListener">
   <property name="handlers">
  <map>
   <entry key="TASK_CREATED" value="taskCreateListener"/>
   <entry key="TASK_COMPLETED" value="taskCompleteListener"/>
   <entry key="TASK_ASSIGNED" value="taskAssignedListener"/>
   <entry key="PROCESS_COMPLETED" value="processCompleteListener"/>
   <entry key="ACTIVITY_STARTED" value="activityStartedListener"/>
   <entry key="ACTIVITY_COMPLETED" value="activityCompletedListener"/>
   <entry key="ACTIVITY_SIGNALED" value="activitySignaledListener"/>
  </map>
 </property>
  </bean>
```



### 定义自己的事件处理器接口

```
/**
 *  Activiti的事件处理器
 * @author csx
 *
 */
public interface EventHandler {
 /**
  * 事件处理器
  * @param event
  */
 public void handle(ActivitiEvent event);
}
```



### 实现自己的任务监控处理

```java
package com.redxun.bpm.activiti.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmDestNode;
import com.redxun.bpm.core.entity.BpmNodeJump;
import com.redxun.bpm.core.entity.BpmRuPath;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.config.BpmEventConfig;
import com.redxun.bpm.core.entity.config.UserTaskConfig;
import com.redxun.bpm.core.identity.service.BpmIdentityCalService;
import com.redxun.bpm.core.identity.service.IdentityTypeService;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmNodeJumpManager;
import com.redxun.bpm.core.manager.BpmNodeSetManager;
import com.redxun.bpm.core.manager.BpmRuPathManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.bpm.enums.TaskEventType;
import com.redxun.core.script.GroovyEngine;
import com.redxun.org.api.model.IdentityInfo;
import com.redxun.saweb.context.ContextUtil;
/**
 * 任务创建监听器
 * 主要用来执行人员分配，事件执行等
 * @author csx
 *
 */
public class TaskCreateListener implements EventHandler{
 
 private static Log logger=LogFactory.getLog(TaskCreateListener.class);
 
 @Resource
 private IdentityTypeService identityTypeService;
 
 @Resource BpmIdentityCalService bpmIdentityCalService;
 
 @Resource BpmNodeSetManager bpmNodeSetManager;
 
 @Resource GroovyEngine groovyEngine;
 
 @Resource
 BpmTaskManager bpmTaskManager;
 
 @Resource
 BpmRuPathManager bpmRuPathManager;
 
 @Resource BpmInstManager bpmInstManager;
 
 @Resource
 private BpmNodeJumpManager bpmNodeJumpManager;
 

 public void executeScript(TaskEntity taskEntity){
  String solId=(String) taskEntity.getVariable("solId");
  //处理事件
  UserTaskConfig userTaskConfig=bpmNodeSetManager.getTaskConfig(solId, taskEntity.getTaskDefinitionKey());
  if(userTaskConfig.getEvents().size()>0){
   BpmEventConfig bpmEventConfig=null;
   for(BpmEventConfig eventConfig:userTaskConfig.getEvents()){
    if(TaskEventType.TASK_CREATED.name().equals(eventConfig.getEventKey())){
     bpmEventConfig=eventConfig;
     break;
    }
   }
   //执行脚本
   if(bpmEventConfig!=null && StringUtils.isNotEmpty(bpmEventConfig.getScript())){
    logger.debug("===================execute the script in task create listener:"+bpmEventConfig.getScript());
    Map<String,Object> vars=taskEntity.getVariables();
    //把任务实体变量放置进来
    vars.put("taskEntity", taskEntity);
    vars.put("taskId", taskEntity.getId());
    groovyEngine.executeScripts(bpmEventConfig.getScript(),vars);
   }
  }
 }
 
 @Override
 public void handle(ActivitiEvent event) {
  ActivitiEntityEventImpl eventImpl=(ActivitiEntityEventImpl)event;
  TaskEntity taskEntity=(TaskEntity)eventImpl.getEntity();

  logger.debug("create task is "+taskEntity.getName()+" key is:"+taskEntity.getTaskDefinitionKey());
  logger.debug("enter the task create listener ---->" + event.getType().name());
  
  //执行任务的标题处理
  String processSubject=(String)taskEntity.getVariable("processSubject");
  String solId=(String)taskEntity.getVariable("solId");
  taskEntity.setDescription(processSubject);
  taskEntity.setSolId(solId);
  taskEntity.setTenantId(ContextUtil.getCurrentTenantId());
  
  //记录跳转的信息
  createNodeJump(taskEntity);
  
  //执行事件的处理
  executeScript(taskEntity);
  //是否已经对任务进行了人员分配
  boolean isAssigned=false;
  //检查是否为会签任务，若是，则先从变量中获得执行人员
  String multiInstance=(String)taskEntity.getExecution().getActivity().getProperty("multiInstance");
  //是否为回退的处理,并且回退的节点不是会签节点，则
  BpmRuPath backRuPath=ProcessHandleHelper.getBackPath();
  if(backRuPath!=null && StringUtils.isEmpty(multiInstance)){
   if("userTask".equals(backRuPath.getNodeType())){
    taskEntity.setAssignee(backRuPath.getAssignee());
    isAssigned=true;
   }else{//查找其子结点上的执行人员 
    BpmRuPath nodePath= bpmRuPathManager.getByParentIdNodeId(backRuPath.getPathId(),taskEntity.getTaskDefinitionKey());
    if(nodePath!=null && StringUtils.isNotEmpty(nodePath.getAssignee())){
     taskEntity.setAssignee(nodePath.getAssignee());
     isAssigned=true;
    }
   }
  }
  //已经分配，则不从配置数据中获取人员数据
  if(isAssigned){
   publishAssignEvent(taskEntity);
   return;
  }
  
  
  if(StringUtils.isNotEmpty(multiInstance)){
   Integer loopCounter=(Integer)taskEntity.getExecution().getVariable("loopCounter");
   String signUserIds=(String)taskEntity.getExecution().getVariable("signUserIds_"+taskEntity.getTaskDefinitionKey());
   
   //优先从变量中取
   String assignee=getUserIds(signUserIds,loopCounter);
   if(StringUtils.isNotEmpty(assignee)){
    isAssigned=true;
    taskEntity.setAssignee(assignee);
    taskEntity.setOwner(assignee);
    Date expiretime=(Date)taskEntity.getExecution().getVariable("expiretime_"+taskEntity.getTaskDefinitionKey());
    Integer priority=(Integer)taskEntity.getExecution().getVariable("priority_"+taskEntity.getTaskDefinitionKey());
    taskEntity.setDueDate(expiretime);
    taskEntity.setPriority(priority);
   }
  }
  //已经分配，则不从配置数据中获取人员数据
  if(isAssigned){
   publishAssignEvent(taskEntity);
   return;
  }
  

  //从线程中获得人员列表映射（即从页面中传过来的人员配置）
  //优先使用页面中的人员配置
  IExecutionCmd processNextCmd=ProcessHandleHelper.getProcessCmd();
  if(processNextCmd!=null){
   BpmDestNode bpmDestNode=processNextCmd.getNodeUserMap().get(taskEntity.getTaskDefinitionKey());
   
   if(bpmDestNode!=null && StringUtils.isNotEmpty(bpmDestNode.getUserIds())){
    String[]uIds=bpmDestNode.getUserIds().split(",");
    isAssigned=true;
    if(uIds.length==1){
     taskEntity.setAssignee(uIds[0]);
     taskEntity.setOwner(uIds[0]);
    }else{
     taskEntity.addCandidateUsers(Arrays.asList(uIds));
    }
    taskEntity.setPriority(bpmDestNode.getPriority());
    taskEntity.setDueDate(bpmDestNode.getExpireTime());
   }
  }
  //已经分配，则不从配置数据中获取人员数据
  if(isAssigned){
   publishAssignEvent(taskEntity);
   return;
  }
  
  //取得人员配置的信息列表
  Collection<IdentityInfo> idInfoList=bpmIdentityCalService.calNodeUsersOrGroups(taskEntity.getProcessDefinitionId(), taskEntity.getTaskDefinitionKey(),taskEntity.getVariables());
  
  if(idInfoList.size()==1){
   IdentityInfo identityInfo=idInfoList.iterator().next();
   if(IdentityInfo.IDENTIFY_TYPE_USER.equals(identityInfo.getIdentityType())){
    taskEntity.setAssignee(identityInfo.getIdentityInfoId());
    taskEntity.setOwner(identityInfo.getIdentityInfoId());
   }else{
    taskEntity.addCandidateGroup(identityInfo.getIdentityInfoId());
   }
   isAssigned=true;
  }else{
   if(idInfoList.size()>0){
    isAssigned=true;
   }
   for(IdentityInfo info:idInfoList){
    
    if(IdentityInfo.IDENTIFY_TYPE_USER.equals(info.getIdentityType())){
     taskEntity.addCandidateUser(info.getIdentityInfoId());
    }else{
     taskEntity.addCandidateGroup(info.getIdentityInfoId());
    }
   }
  }
  
  if(isAssigned){
   publishAssignEvent(taskEntity);
  }
  
  
    
 }
 
 /**
  * 发布任务分配事件
  * @param taskEntity
  */
 public void publishAssignEvent(TaskEntity taskEntity){
   if (StringUtils.isNotEmpty(taskEntity.getAssignee())) {
       Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
               ActivitiEventBuilder.createEntityEvent(ActivitiEventType.TASK_ASSIGNED, taskEntity));
    }
 }
 
 private String getUserIds(String userIds,Integer index){
  String[] uIds=userIds.split("[,]");
  if(index<uIds.length){
   return uIds[index];
  }
  return null;
 }
 
 private void createNodeJump(TaskEntity taskEntity){

  BpmNodeJump nodeJump=new BpmNodeJump();
  nodeJump.setActDefId(taskEntity.getProcessDefinitionId());
  nodeJump.setActInstId(taskEntity.getProcessInstanceId());
  nodeJump.setTaskId(taskEntity.getId());
  nodeJump.setCreateTime(taskEntity.getCreateTime());
  //获得任务的创建时间
  nodeJump.setNodeName(taskEntity.getName());
  nodeJump.setNodeId(taskEntity.getTaskDefinitionKey());
  nodeJump.setHandlerId(ContextUtil.getCurrentUserId());
  IExecutionCmd cmd=ProcessHandleHelper.getProcessCmd();
  nodeJump.setCheckStatus(BpmNodeJump.JUMP_TYPE_UNHANDLE);
  nodeJump.setRemark("无");
  if(cmd instanceof ProcessStartCmd){
   bpmNodeJumpManager.create(nodeJump);
  }else{
   bpmNodeJumpManager.create(nodeJump);
  }
 }
 
}
```

【说明】
我们通过在监听这个事件，完成了很多activiti没有处理的数据，如创建执行路径，为后续的任务回退进行做准备，进行任务的人员分配处理等。





<https://my.oschina.net/man1900/blog/690054>