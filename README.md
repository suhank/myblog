# myblog

博客,记录学习技术和平时遇到的问题.


github地址：https://github.com/nowisjerry/myblog

gitee地址：https://gitee.com/jerry_ye/myblog.git


文章结构目录:

```
.
|-- blog
|   |-- software
|   |   |-- mac
|   |   |   |-- Alfred
|   |   |   |   `-- workflow备份
|   |   |   |-- Homebrew
|   |   |   |-- SecureCRT
|   |   |   |-- iterm2
|   |   |   |-- note
|   |   |   |-- qq
|   |   |   `-- 终端
|   |   |       `-- zsh
|   |   |-- win
|   |   |   |-- Araxis Merge
|   |   |   |-- cmder
|   |   |   `-- notepad++
|   |   `-- win&mac
|   |       |-- IDE
|   |       |   |-- eclipse
|   |       |   |-- idea
|   |       |   |   |-- debug-调试
|   |       |   |   |-- mac
|   |       |   |   |-- 快捷键相关
|   |       |   |   `-- 插件
|   |       |   `-- webstorm
|   |       |-- Microsoft visio
|   |       |-- Sublime Text
|   |       |-- chrome
|   |       |-- postman
|   |       |-- typora
|   |       |-- vmware
|   |       |   |-- VMware Workstation
|   |       |   `-- VMware vSphere
|   |       `-- vscode
|   |-- system
|   |   |-- macos
|   |   |   |-- note
|   |   |   `-- 开发
|   |   `-- windows
|   |       |-- dos
|   |       |-- note
|   |       |-- 宽带
|   |       |-- 系统
|   |       `-- 系统安装相关
|   |-- technology
|   |   |-- MQ
|   |   |   |-- kafka
|   |   |   |-- note
|   |   |   |-- rabbitMQ
|   |   |   |   |-- rabbitmq-demo-3.3.4
|   |   |   |   `-- 安装
|   |   |   `-- rocketMQ
|   |   |       `-- 官方文档
|   |   |           |-- acl
|   |   |           |-- client
|   |   |           |   `-- java
|   |   |           |-- dledger
|   |   |           `-- msg_trace
|   |   |-- algorithm_算法
|   |   |   |-- hash哈希
|   |   |   |-- 排列组合
|   |   |   `-- 查找算法
|   |   |       `-- 树
|   |   |-- api文档工具
|   |   |   `-- Swagger
|   |   |-- bigdata
|   |   |   |-- elasticsearch
|   |   |   |   |-- java客户端
|   |   |   |   |-- 优化
|   |   |   |   |-- 分页page遍历
|   |   |   |   |-- 图形化客户端和工具
|   |   |   |   |-- 安装
|   |   |   |   `-- 查询搜索
|   |   |   |-- hadoop
|   |   |   |   |-- hadoop 1.x 版本
|   |   |   |   |-- mapreduce
|   |   |   |   `-- 安装
|   |   |   |       `-- Hadoop安装教程_单机_伪分布式配置_CentOS6.4_Hadoop2.6.0_给力星_files
|   |   |   |-- hbase
|   |   |   |-- kibana
|   |   |   |-- storm
|   |   |   |-- zookeeper
|   |   |   |   |-- java客户端
|   |   |   |   |   |-- curator
|   |   |   |   |   `-- zkClient
|   |   |   |   |-- 会话超时
|   |   |   |   `-- 运维
|   |   |   |       `-- 安装
|   |   |   `-- 统一资源管理与调度平台（系统）
|   |   |-- code_manager
|   |   |   |-- git
|   |   |   |   |-- commond-命令
|   |   |   |   |-- github
|   |   |   |   |-- gitlab
|   |   |   |   `-- 经常使用
|   |   |   `-- svn
|   |   |-- cpu
|   |   |-- database
|   |   |   |-- NoSQL
|   |   |   |   |-- cache-缓存
|   |   |   |   |   |-- Ehcache
|   |   |   |   |   |   `-- 三种 EhCache 的集群方案 深入探讨在集群环境中使用 EhCache 缓存系统_files
|   |   |   |   |   |-- memcached
|   |   |   |   |   |   |-- install
|   |   |   |   |   |   |   `-- Memcached和Memcache安装（64位win7）_files
|   |   |   |   |   |   `-- note
|   |   |   |   |   |       |-- MemCache详细解读_files
|   |   |   |   |   |       |-- Memcache---集群方案_files
|   |   |   |   |   |       `-- memcached 可以设置数据永不过期吗？_files
|   |   |   |   |   |-- redis
|   |   |   |   |   |   |-- install
|   |   |   |   |   |   |-- redisclient客户端
|   |   |   |   |   |   |   |-- lettuce
|   |   |   |   |   |   |   |-- redisson
|   |   |   |   |   |   |   `-- spring redis template
|   |   |   |   |   |   |-- 事务
|   |   |   |   |   |   `-- 分布式锁
|   |   |   |   |   `-- spring-cache
|   |   |   |   `-- mongoDB
|   |   |   |-- database_connection_pool
|   |   |   |-- database_resourse
|   |   |   |-- sql
|   |   |   |   |-- h2
|   |   |   |   |-- mysql
|   |   |   |   |   |-- client
|   |   |   |   |   |-- 存储过程
|   |   |   |   |   |-- 开发
|   |   |   |   |   |   |-- mysql 不存在则插入,存在则更新或忽略
|   |   |   |   |   |   |-- 事务和锁
|   |   |   |   |   |   |   |-- 死锁
|   |   |   |   |   |   |   `-- 间隙锁
|   |   |   |   |   |   |-- 函数方法
|   |   |   |   |   |   |   `-- find_in_set
|   |   |   |   |   |   |-- 多表关联join
|   |   |   |   |   |   `-- 数据类型
|   |   |   |   |   |-- 性能优化
|   |   |   |   |   |   |-- 分库分表相关
|   |   |   |   |   |   `-- 索引相关
|   |   |   |   |   `-- 运维
|   |   |   |   |       `-- 导入导出数据
|   |   |   |   |-- note
|   |   |   |   |-- oracle
|   |   |   |   |   |-- 开发
|   |   |   |   |   `-- 运维
|   |   |   |   |-- sql解析器
|   |   |   |   |   `-- Jsqlparser
|   |   |   |   `-- 优化
|   |   |   |-- 分布式事务
|   |   |   |   `-- TCC
|   |   |   |-- 分布式数据库
|   |   |   |   `-- TiDB
|   |   |   `-- 数据库中间件
|   |   |       |-- Sharding-JDBC
|   |   |       |   |-- shardingjdbc 2.x 官方文档 使用指南
|   |   |       |   `-- 源码分析
|   |   |       `-- mycat
|   |   |-- docker
|   |   |   |-- dockerfile
|   |   |   |   |-- demo
|   |   |   |   `-- note
|   |   |   |-- docker命令command
|   |   |   |-- docker安装install
|   |   |   |   `-- 集群安装
|   |   |   |-- docker日志相关
|   |   |   |-- docker网络相关
|   |   |   `-- 运维相关
|   |   |-- java
|   |   |   |-- basis
|   |   |   |   |-- IO
|   |   |   |   |   `-- 文件字节-file-byte
|   |   |   |   |-- JDBC
|   |   |   |   |   `-- 批量执行sql
|   |   |   |   |-- JVM
|   |   |   |   |   |-- gc垃圾收集器
|   |   |   |   |   `-- 软引用和弱引用
|   |   |   |   |-- NIO
|   |   |   |   |   |-- netty
|   |   |   |   |   |   |-- ByteBuf
|   |   |   |   |   |   `-- netty-socketio
|   |   |   |   |   `-- note
|   |   |   |   |-- annotation_注解
|   |   |   |   |-- date_时间
|   |   |   |   |   `-- date日期时间工具
|   |   |   |   |-- enum_枚举
|   |   |   |   |   `-- switch case
|   |   |   |   |-- exception_异常
|   |   |   |   |-- id_gen唯一id生成器
|   |   |   |   |-- idempotent幂等
|   |   |   |   |-- java8
|   |   |   |   |   |-- lambda
|   |   |   |   |   |-- null问题
|   |   |   |   |   |-- streams
|   |   |   |   |   |   `-- list转map
|   |   |   |   |   `-- 新的api结构定义
|   |   |   |   |       `-- java8 方法引用 双冒号
|   |   |   |   |           `-- java8 利用Lambda获取属性名称
|   |   |   |   |-- json
|   |   |   |   |   |-- fastjson
|   |   |   |   |   |-- gson
|   |   |   |   |   |-- jackson
|   |   |   |   |   `-- note
|   |   |   |   |-- note
|   |   |   |   |   `-- java命令行
|   |   |   |   |-- reflect_反射
|   |   |   |   |-- serialization序列化
|   |   |   |   |-- socket tcp ip http
|   |   |   |   |   |-- sockjs
|   |   |   |   |   `-- websocket
|   |   |   |   |-- spi_服务提供发现机制
|   |   |   |   |-- string
|   |   |   |   |-- thread
|   |   |   |   |   |-- ThreadLocal
|   |   |   |   |   |-- note
|   |   |   |   |   |-- queue队列
|   |   |   |   |   |   |-- Disruptor
|   |   |   |   |   |   `-- 队列系列介绍
|   |   |   |   |   |-- threadpool_线程池
|   |   |   |   |   |   |-- jdk
|   |   |   |   |   |   `-- spring
|   |   |   |   |   `-- 并发
|   |   |   |   |       |-- java-jdk-lock-锁
|   |   |   |   |       |   |-- synchronized
|   |   |   |   |       |   |-- 可重入锁
|   |   |   |   |       |   |-- 读写锁
|   |   |   |   |       |   `-- 锁的基础原理
|   |   |   |   |       |-- volatile
|   |   |   |   |       `-- 多线程操作 fork join
|   |   |   |   |-- timer定时器
|   |   |   |   |-- tree
|   |   |   |   |-- utils
|   |   |   |   |   `-- web
|   |   |   |   |-- xml
|   |   |   |   |-- 二维码-QrCode
|   |   |   |   |-- 正则
|   |   |   |   |-- 泛型
|   |   |   |   |-- 编码
|   |   |   |   |   `-- base64
|   |   |   |   |-- 语法
|   |   |   |   |-- 进制单位
|   |   |   |   `-- 集合-容器
|   |   |   |       |-- Guava
|   |   |   |       |   |-- EventBus-事件总线
|   |   |   |       |   `-- cache
|   |   |   |       |-- apache commons
|   |   |   |       `-- jdk
|   |   |   |           |-- 开发
|   |   |   |           `-- 结构性能
|   |   |   |-- design mode
|   |   |   |   |-- 创建型模式
|   |   |   |   |-- 结构型模式
|   |   |   |   `-- 行为型模式
|   |   |   |-- framework
|   |   |   |   |-- authorization-登录认证-授权
|   |   |   |   |   |-- OAuth 2.0
|   |   |   |   |   `-- shiro
|   |   |   |   |-- cat_监控工具
|   |   |   |   |-- dubbo
|   |   |   |   |-- jersey
|   |   |   |   |-- persistence持久层框架
|   |   |   |   |   |-- hibernate
|   |   |   |   |   |-- mybatis
|   |   |   |   |   |   |-- mybatis-plugin-interceptor-拦截器
|   |   |   |   |   |   |-- mybatis增强工具
|   |   |   |   |   |   |   `-- MybatisPlus
|   |   |   |   |   |   |-- 开发
|   |   |   |   |   |   `-- 源码相关
|   |   |   |   |   `-- springjdbc
|   |   |   |   |-- retryer-重试
|   |   |   |   |   |-- guava-retryer-重试
|   |   |   |   |   `-- spring-retry-重试
|   |   |   |   |-- spring
|   |   |   |   |   |-- spring 拦截器（Interceptor）和过滤器（Filter）
|   |   |   |   |   |-- spring-aop
|   |   |   |   |   |   `-- 动态代理
|   |   |   |   |   |       |-- cglib
|   |   |   |   |   |       `-- jdk
|   |   |   |   |   |-- spring-boot
|   |   |   |   |   |   |-- actuator
|   |   |   |   |   |   |-- profiles
|   |   |   |   |   |   |-- servlet
|   |   |   |   |   |   |-- springboot启动解析
|   |   |   |   |   |   |-- starter
|   |   |   |   |   |   `-- 配置相关
|   |   |   |   |   |-- spring-enable-importSelector
|   |   |   |   |   |-- spring-ioc
|   |   |   |   |   |-- spring_el
|   |   |   |   |   |-- spring_i18n_国际化
|   |   |   |   |   |-- spring_test
|   |   |   |   |   |   `-- spring-boot
|   |   |   |   |   |-- spring_transaction_事务
|   |   |   |   |   |-- spring_util_内部工具类
|   |   |   |   |   |-- spring_父子容器
|   |   |   |   |   |-- springcloud
|   |   |   |   |   |   |-- config
|   |   |   |   |   |   |-- feign
|   |   |   |   |   |   |   |-- feign独立使用
|   |   |   |   |   |   |   `-- springcloud_feign使用_也可以使用在非cloud项目
|   |   |   |   |   |   |-- hystrix
|   |   |   |   |   |   |-- sidecar
|   |   |   |   |   |   `-- zuul
|   |   |   |   |   |-- springmvc
|   |   |   |   |   |   |-- 异步接口
|   |   |   |   |   |   |   `-- @Async
|   |   |   |   |   |   `-- 源码相关
|   |   |   |   |   |-- spring源码
|   |   |   |   |   |   |-- ComponentScan
|   |   |   |   |   |   `-- ioc
|   |   |   |   |   `-- 开发
|   |   |   |   |       |-- conditional条件注解
|   |   |   |   |       |-- spring 获取 实现某接口的所有实例bean
|   |   |   |   |       |-- 依赖
|   |   |   |   |       `-- 配置相关
|   |   |   |   |-- thrift
|   |   |   |   |-- 工作流
|   |   |   |   |   |-- activiti
|   |   |   |   |   |   |-- 任务
|   |   |   |   |   |   |-- 变量
|   |   |   |   |   |   `-- 网关
|   |   |   |   |   `-- camunda
|   |   |   |   |-- 文件文档在线预览
|   |   |   |   `-- 规则引擎
|   |   |   |       |-- drools
|   |   |   |       `-- easy-rules
|   |   |   |-- http远程通讯
|   |   |   |   |-- Hessian
|   |   |   |   |-- HttpClient
|   |   |   |   |-- RestTemplate
|   |   |   |   `-- okhttp
|   |   |   |-- javadoc
|   |   |   |-- job
|   |   |   |   `-- quartz
|   |   |   |       `-- spring_quartz
|   |   |   |-- junit-test
|   |   |   |   `-- mock框架
|   |   |   |-- log
|   |   |   |   |-- Log4j2
|   |   |   |   |-- log4j
|   |   |   |   |-- log4jdbc
|   |   |   |   |-- logback
|   |   |   |   `-- note
|   |   |   |-- note
|   |   |   |-- pdf
|   |   |   |-- web
|   |   |   |   |-- CORS_跨域
|   |   |   |   |-- JWT
|   |   |   |   |-- chart_图形报表
|   |   |   |   |   `-- ECharts
|   |   |   |   |-- css
|   |   |   |   |   |-- 原理
|   |   |   |   |   |-- 开发
|   |   |   |   |   |   |-- CSS模块化
|   |   |   |   |   |   |-- border 边框
|   |   |   |   |   |   |-- 伪类和伪元素
|   |   |   |   |   |   |-- 位置相关
|   |   |   |   |   |   |-- 动画
|   |   |   |   |   |   |-- 命名规范
|   |   |   |   |   |   |-- 图片
|   |   |   |   |   |   |-- 文字段落换行
|   |   |   |   |   |   `-- 选择符 Selectors 选择器
|   |   |   |   |   `-- 预处理器
|   |   |   |   |-- easyui
|   |   |   |   |   |-- combobox
|   |   |   |   |   `-- datagrid
|   |   |   |   |-- freemarker
|   |   |   |   |-- html
|   |   |   |   |-- javascript
|   |   |   |   |   |-- TypeScript
|   |   |   |   |   |-- jquery
|   |   |   |   |   |   |-- ajax
|   |   |   |   |   |   `-- 选择器
|   |   |   |   |   |-- nodejs
|   |   |   |   |   |   |-- db操作
|   |   |   |   |   |   |-- npm镜像
|   |   |   |   |   |   |-- 包管理
|   |   |   |   |   |   `-- 安装
|   |   |   |   |   |-- orgi
|   |   |   |   |   |   |-- ECMAScript
|   |   |   |   |   |   |   `-- es6
|   |   |   |   |   |   |       `-- spread运算符与rest参数
|   |   |   |   |   |   |-- array数组对象
|   |   |   |   |   |   |-- json
|   |   |   |   |   |   |-- url地址跳转参数相关
|   |   |   |   |   |   |-- web_api
|   |   |   |   |   |   |-- 事件
|   |   |   |   |   |   |-- 原理
|   |   |   |   |   |   |-- 官方api文档
|   |   |   |   |   |   |   |-- Object.defineProperty
|   |   |   |   |   |   |   `-- 计时器
|   |   |   |   |   |   |-- 工具
|   |   |   |   |   |   |-- 异步
|   |   |   |   |   |   |   `-- async-await
|   |   |   |   |   |   |-- 文件相关
|   |   |   |   |   |   |-- 日期时间相关
|   |   |   |   |   |   |-- 模块相关
|   |   |   |   |   |   |-- 生命周期
|   |   |   |   |   |   |-- 编码
|   |   |   |   |   |   `-- 闭包
|   |   |   |   |   |-- vuejs
|   |   |   |   |   |   |-- axios
|   |   |   |   |   |   |-- element ui
|   |   |   |   |   |   |-- router
|   |   |   |   |   |   |-- vuex
|   |   |   |   |   |   |-- webpack
|   |   |   |   |   |   |-- 原理
|   |   |   |   |   |   |-- 开发
|   |   |   |   |   |   |-- 插件
|   |   |   |   |   |   |-- 教程
|   |   |   |   |   |   `-- 组件
|   |   |   |   |   |-- weixin
|   |   |   |   |   |   |-- mpvue
|   |   |   |   |   |   |   `-- echarts
|   |   |   |   |   |   `-- 原生
|   |   |   |   |   |       |-- picker
|   |   |   |   |   |       `-- 表格
|   |   |   |   |   `-- 工具包
|   |   |   |   |-- note
|   |   |   |   |-- servlet_jsp
|   |   |   |   |-- session_cookie
|   |   |   |   |-- thymeleaf
|   |   |   |   |-- url
|   |   |   |   |-- velocity
|   |   |   |   `-- web浏览器相关
|   |   |   `-- 系统架构
|   |   |-- jenkinsfile
|   |   |-- kubernetes
|   |   |-- linux
|   |   |   |-- command
|   |   |   |   |-- 查看 搜索 文本 相关
|   |   |   |   `-- 网络
|   |   |   |-- install
|   |   |   |-- note
|   |   |   |   |-- vim
|   |   |   |   |-- 查看版本
|   |   |   |   |-- 环境变量相关
|   |   |   |   |-- 监控
|   |   |   |   `-- 防火墙
|   |   |   |-- shell
|   |   |   |   `-- bash
|   |   |   |-- 内核
|   |   |   `-- 运维
|   |   |       `-- 监控调试排查问题工具
|   |   |           `-- arthas-java诊断利器
|   |   |-- lua
|   |   |-- markdown
|   |   |-- note
|   |   |   `-- 环境
|   |   |-- ognl
|   |   |-- performance
|   |   |   |-- jmeter
|   |   |   |-- loadrunner
|   |   |   `-- note
|   |   |-- php
|   |   |   |-- 安装部署
|   |   |   |   `-- phpstrom
|   |   |   `-- 开发
|   |   |-- python
|   |   |   |-- json相关
|   |   |   |-- 包管理
|   |   |   |   |-- pip
|   |   |   |   `-- pipenv-官方推荐以后都用这个
|   |   |   |-- 工具
|   |   |   |-- 开发
|   |   |   `-- 运维
|   |   |-- thinker
|   |   |   |-- api接口规范
|   |   |   |-- 前端
|   |   |   |-- 开源项目
|   |   |   |-- 微服务
|   |   |   |-- 简历
|   |   |   `-- 面试
|   |   |-- web container
|   |   |   |-- nginx
|   |   |   |   |-- 安装
|   |   |   |   |-- 负载均衡
|   |   |   |   `-- 配置相关
|   |   |   `-- tomcat
|   |   |-- yaml
|   |   |-- 业务
|   |   |-- 分布式理论
|   |   |-- 操作系统 计算机原理 相关
|   |   |   `-- 硬盘原理
|   |   |-- 数学
|   |   |-- 数据
|   |   |-- 生活科学
|   |   |   `-- 电工
|   |   |-- 网络
|   |   |   `-- DNS
|   |   |-- 购房相关
|   |   `-- 项目构建工具
|   |       |-- gradle
|   |       `-- maven
|   |           |-- nexus
|   |           `-- note
|   |               |-- commond-命令
|   |               |-- deploy 上传jar相关
|   |               |-- 依赖管理
|   |               |-- 打包jar
|   |               `-- 版本管理
|   `-- 电子导购
|-- src
|   |-- main
|   |   |-- java
|   |   |   `-- com
|   |   |       `-- practice
|   |   |           `-- tool
|   |   `-- resources
|   `-- test
|       `-- java
|           `-- com
|               `-- practice
`-- target
    |-- classes
    |   `-- com
    |       `-- practice
    |           `-- base64
    `-- generated-sources
        `-- annotations

517 directories

```

