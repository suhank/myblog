[TOC]



# activiti6.0版本流程撤回、跳转、回退等操作

2018-02-02 18:07:55

## 思路

如题，实现思路：

1、获取当前任务所在的节点

2、获取所在节点的流出方向

3、记录所在节点的流出方向，并将所在节点的流出方向清空

4、获取目标节点

5、创建新的方向

6、将新的方向set到所在节点的流出方向

7、完成当前任务

8、还原所在节点的流出方向

## 网上原代码

```java
public void revoke(String objId) throws Exception {
		
		Task task = taskService.createTaskQuery().processInstanceBusinessKey(objId).singleResult();
		if(task==null) {
			throw new ServiceException("流程未启动或已执行完成，无法撤回");
		}
		
		LoginUser loginUser = SessionContext.getLoginUser();
		List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
				.processInstanceBusinessKey(objId)
				.orderByTaskCreateTime()
				.asc()
				.list();
		String myTaskId = null;
		HistoricTaskInstance myTask = null;
		for(HistoricTaskInstance hti : htiList) {
			if(loginUser.getUsername().equals(hti.getAssignee())) {
				myTaskId = hti.getId();
				myTask = hti;
				break;
			}
		}
		if(null==myTaskId) {
			throw new ServiceException("该任务非当前用户提交，无法撤回");
		}
		
		String processDefinitionId = myTask.getProcessDefinitionId();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		
		//变量
//		Map<String, VariableInstance> variables = runtimeService.getVariableInstances(currentTask.getExecutionId());
		String myActivityId = null;
		List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
				.executionId(myTask.getExecutionId()).finished().list();
		for(HistoricActivityInstance hai : haiList) {
			if(myTaskId.equals(hai.getTaskId())) {
				myActivityId = hai.getActivityId();
				break;
			}
		}
		FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);
		
		
		Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
		String activityId = execution.getActivityId();
		logger.warn("------->> activityId:" + activityId);
		FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
		
		//记录原活动方向
		List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
		oriSequenceFlows.addAll(flowNode.getOutgoingFlows());
		
		//清理活动方向
		flowNode.getOutgoingFlows().clear();
		//建立新方向
		List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
		SequenceFlow newSequenceFlow = new SequenceFlow();
		newSequenceFlow.setId("newSequenceFlowId");
		newSequenceFlow.setSourceFlowElement(flowNode);
		newSequenceFlow.setTargetFlowElement(myFlowNode);
		newSequenceFlowList.add(newSequenceFlow);
		flowNode.setOutgoingFlows(newSequenceFlowList);
		
		Authentication.setAuthenticatedUserId(loginUser.getUsername());
		taskService.addComment(task.getId(), task.getProcessInstanceId(), "撤回");
		
		Map<String,Object> currentVariables = new HashMap<String,Object>();
		currentVariables.put("applier", loginUser.getUsername());
		//完成任务
		taskService.complete(task.getId(),currentVariables);
		//恢复原方向
		flowNode.setOutgoingFlows(oriSequenceFlows);
	}
```

## my代码

```java
    /**
     * 撤销 参考:https://blog.csdn.net/lianjie_c/article/details/79242009
     */
    public void revoke(String processInstanceId) {
        //获取待处理的任务
        Task todoTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
        if(todoTask==null) {
            throw new BusinessException("流程未启动或已执行完成，无法撤回");
        }


        //获取最后一次完成的任务,也就是用来撤销的任务
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .orderByTaskCreateTime()
                .asc()
                .list();
        HistoricTaskInstance myRevokeTask = historicTaskInstanceList.get(historicTaskInstanceList.size() - 1);
        String myTaskId = myRevokeTask.getId();
        if (!Objects.equals(myRevokeTask.getAssignee(),getUserNo())) {
            throw new BusinessException("该任务非当前用户提交，无法撤回");
        }

        String processDefinitionId = myRevokeTask.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        String myActivityId = null;
        List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
                .executionId(myRevokeTask.getExecutionId()).finished().list();
        for (HistoricActivityInstance hai : haiList) {
            if (myTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }
        //获取所在节点的流出方向
        FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);
        Execution execution = runtimeService.createExecutionQuery().executionId(myRevokeTask.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        logger.warn("------->> activityId:" + activityId);
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);

        //记录所在节点的流出方向，
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(flowNode.getOutgoingFlows());

        //记录所在节点的流出方向，并将所在节点的流出方向清空
        flowNode.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(flowNode);
        newSequenceFlow.setTargetFlowElement(myFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        flowNode.setOutgoingFlows(newSequenceFlowList);


        //完成任务
        claim(todoTask.getId(), getUserNo());
        TaskResult taskResult = new TaskResult();
        taskResult.setName("撤回");
        taskResult.setValue(MstConstants.YN.NO);
        complete(todoTask.getId(), todoTask.getProcessInstanceId(), null, taskResult, null);

        //恢复原方向
        flowNode.setOutgoingFlows(oriSequenceFlows);
    }
```





参考: <https://segmentfault.com/a/1190000013952695>

<https://blog.csdn.net/lianjie_c/article/details/79242009>