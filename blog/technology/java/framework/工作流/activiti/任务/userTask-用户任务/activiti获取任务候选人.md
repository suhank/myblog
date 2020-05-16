# activiti获取任务候选人

IdentityLink是任务候选人的对象。来自于taskService。

```java
 
 	/**
     * 
     *@User   :Test
     *@date   :2014-6-27 上午09:38:36
     *@return :Set
     *@userFor :获得任务中的办理候选人
     */
	private Set getTaskCandidate(String taskId) {
		Set users = new HashSet();
        //主要存储任务节点与参与者的相关信息
		List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(taskId);
		if (identityLinkList != null && identityLinkList.size() > 0) {
			for (Iterator iterator = identityLinkList.iterator(); iterator
					.hasNext();) {
				IdentityLink identityLink = (IdentityLink) iterator.next();
				if (identityLink.getUserId() != null) {
					User user = getUser(identityLink.getUserId());
					if (user != null)
						users.add(user);
				}
				if (identityLink.getGroupId() != null) {
					// 根据组获得对应人员
					List userList = identityService.createUserQuery()
							.memberOfGroup(identityLink.getGroupId()).list();
					if (userList != null && userList.size() > 0)
						users.addAll(userList);
				}
			}
 
		}
		return users;
	}
 
	private User getUser(String userId) {
		User user = (User) identityService.createUserQuery().userId(userId)
				.singleResult();
		return user;
	}
```





<https://blog.csdn.net/lan12334321234/article/details/70048918>