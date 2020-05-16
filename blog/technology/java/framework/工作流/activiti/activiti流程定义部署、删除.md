[TOC]



# activiti流程定义部署、删除

## 1、部署流程定义

部署流程定义也可以认为是增加流程定义。

首先创建流程引擎对象（公用的方法）

private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine(); 

### 方法1：

```java
    /**
     * 1.发布流程
     * 会在三张表中产生数据：
     * act_ge_bytearray 产生两条数据
     * act_re_deployment 产生一条数据
     * act_re_procdef 产生一条数据
     */
    public void deploy() throws Exception {
        // 获取仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 创建发布配置对象
        DeploymentBuilder builder = repositoryService.createDeployment();
        // 设置发布信息
        builder
                .name("请假流程")// 添加部署规则的显示别名
                .addClasspathResource("diagrams/Leave2.bpmn")// 添加规则文件
                .addClasspathResource("diagrams/Leave2.png");// 添加规则图片  不添加会自动产生一个图片不推荐
        // 完成发布
        builder.deploy();
    }
```

### 方法2：

```java
    public void deployZIP() throws Exception {
        // 获取仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 创建发布配置对象
        DeploymentBuilder builder = repositoryService.createDeployment();
        // 获得上传文件的输入流程
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/diagrams.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        // 设置发布信息
        builder
                .name("请假流程")// 添加部署规则的显示别名
                .addZipInputStream(zipInputStream );
        // 完成发布
        builder.deploy();
    }
```

 说明：

　　1)     首先获得默认的流程引擎，在创建时会自动加载classpath下得activiti.cfg.xml

　　2)     通过流程引擎获取了一个RepositoryService对象->仓库服务对象

　　3)     由仓库的服务对象产生一个部署对象配置对象，用来封装部署环境的相关配置。

　　4)     这是一个链式编程，在部署配置对象中设置显示名，上传规则文件相对classpath的地址。

　　5)     部署，也是往数据库中存储流程定义的过程。

　　6)     这一步在数据库中将操作三张表：

　　　　a)     act_re_deployment

  　　　　 存放流程定义的显示名和部署时间，每部署一次增加一条记录

　　　　b)     act_re_procdef

  　　　　 存放流程定义的属性信息，部署每个新的流程定义都会在这张表中增加一条记录。

　　　　c)     act_ge_bytearray

  　　　　 存储流程定义相关的部署信息。即流程定义文档的存放地。每部署一次就会增加两条记录，一条是关于bpmn规则文件的，一条是图片的（如果部署时只指定了bpmn一个文件，activiti会在部署时解析bpmn文件内容自动生成流程图）。两个文件不是很大，都是以二进制形式存储在数据库中。

 

## **2、删除流程**

　　删除部署到activiti中的流程定义。

```java
    public void delDeployment() throws Exception {
        // 获取仓库服务对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 删除发布信息
        String deploymentId = "1";
        // 普通删除，如果当前规则下有正在执行的流程，则抛异常
        repositoryService.deleteDeployment(deploymentId);
        // 级联删除,会删除和当前规则相关的所有信息，包括历史
        repositoryService.deleteDeployment(deploymentId, true);
    }
```

 说明：

　　1)     因为删除的是流程定义，而流程定义的部署是属于仓库服务的，所以应该先得到RepositoryService

　　2)     如果该流程定义下没有正在运行的流程，则可以用普通删除。如果是有关联的信息，用级联删除。一般情况下用普通删除就可以。由于级联删除涉及的数据比较多，一般只开放给超级管理员使用。

 

## 3、查看流程定义

```java
    /**
     * 查看流程定义
     * 流程定义 ProcessDefinition
     * id : {key}:{version}:{随机值}
     * name ： 对应流程文件process节点的name属性
     * key ： 对应流程文件process节点的id属性
     * version ： 发布时自动生成的。如果是第一发布的流程，veresion默认从1开始；如果当前流程引擎中已存在相同key的流程，则找到当前key对应的最高版本号，在最高版本号上加1
     */public void queryProcessDefinition() throws Exception {
        // 获取仓库服务对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取流程定义查询对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        // 配置查询对象
        processDefinitionQuery
                //添加过滤条件
//     .processDefinitionName(processDefinitionName)
//     .processDefinitionId(processDefinitionId)
//     .processDefinitionKey(processDefinitionKey)
                //分页条件
//     .listPage(firstResult, maxResults)
                //排序条件
                .orderByProcessDefinitionVersion().desc();
        /**
         * 执行查询
         * list : 执行后返回一个集合
         * singelResult 执行后，首先检测结果长度是否为1，如果为一则返回第一条数据；如果不唯一，抛出异常
         * count： 统计符合条件的结果数量
         */
        List<ProcessDefinition> pds = processDefinitionQuery.list();
        // 遍历集合，查看内容
        for (ProcessDefinition pd : pds) {
            System.out.print("id:" + pd.getId() +",");
            System.out.print("name:" + pd.getName() +",");
            System.out.print("key:" + pd.getKey() +",");
            System.out.println("version:" + pd.getVersion());
        }
    }
```

 说明：

　　1)     因为流程定义的信息存放在仓库中，所以应该获取RepositoryService。

　　2)     创建流程定义查询对象，可以在ProcessDefinitionQuery上设置查询过滤参数

　　3)     调用ProcessDefinitionQuery对象的list方法，执行查询，获得符合条件的流程定义列表

　　4)     由运行结果可以看出：

　　　　a)     Key和Name的值为：bpmn文件process节点的id和name的属性值

　　　　b)     key属性被用来区别不同的流程定义。

　　　　c)     带有特定key的流程定义第一次部署时，version为1。之后每次部署都会在当前最高版本号上加1

　　　　d)     Id的值的生成规则为:{processDefinitionKey}:{processDefinitionVersion}:{generated-id}, 这里的generated-id是一个自动生成的唯一的数字

　　　　e)     重复部署一次，deploymentId的值以一定的形式变化

　　　　f)      流程定义(ProcessDefinition)在数据库中没有相应的表对应，只是从act_ge_bytearray表中取出相应的bpmn和png图片，并进行解析。





<https://www.cnblogs.com/cxyj/p/3877181.html>