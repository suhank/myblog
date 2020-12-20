# git工作流指南系列--gitFlow工作流

2019年11月15日

## GIT工作流

不同团队，不同公司，不同项目使用的工作流一定是不一样的。只有最适合的，没有最好的，软件工程没有银弹，同样的GIT工作流也没有银弹。

接下来我将用几篇文章跟大家一起讨论一下GIT常见几种工作流和使用场景。

### 常见的几种工作流

- 集中式工作流
- 功能分支工作流
- GitFlow工作流
- Forking工作流
- Pull Requests
- Github Flow工作流
- Gitlab Flow工作流

今天这篇文章先介绍一下大名鼎鼎也是颇受非议的一种工作流GitFlow工作流，为什么先讲解gitFlow，因为它是第一个考虑全面，合理功能完备的工作流，甚至于因为其流程的复杂度让很多人对其颇有微词。同时Github Flow和Gitlab Flow也都是在其基础上发展出来的。

## GitFlow工作流

当在团队开发中使用版本控制系统时，商定一个统一的工作流程是至关重要的。Git的确可以在各个方面做很多事情，然而，如果在你的团队中还没有能形成一个特定有效的工作流程，那么混乱就将是不可避免的。 git-flow就是当下一个非常流行的工作流。

### git-flow是什么？

它并不是什么新的技术，它只是一套git使用方案，按照这套方案使用git将会获得较好的版本控制体验。 git-flow只是封装了一些git命令，让你在使用的时候可以更加的方便，即使不使用git-flow你依然可以通过git命令的组合实现。 [gitflow开源项目地址](https://github.com/petervanderdoes/gitflow-avh)

##### gitflow经典流程图



![img](git工作流指南系列--gitFlow工作流.assets/16e694ca26d43458)



##### 分支类型

- feature分支： 用于功能开发
- develop分支： 用于聚合feature分支开发的功能
- release分支： 用于测试发版
- master 分支： 打上版本TAG长期稳定支持，任何一个tag都可以稳定发布
- hotfix 分支： 用于修复线上BUG

##### 流程介绍

- 不同feature在不同feature分支上单独开发(或测试)。
- 确定版本号和此版本将要发布的功能后，将相应featustre分支统一向develop分支合并，然后拉出新的release预发布分支。
- release分支作为持续集成关注的分支，修复bug。
- 待release分支测试验收通过后，统一向master分支和develop分支合并，并在master分支打tag。
- 根据tag发布apk版本。

若线上发现严重bug，需走hotfix流程。

- 基于master分支拉出hotfix分支修复线上问题。
- bug修复完成统一向master和develop分支合并。
- master分支打上新的tag，发布新版本。

### 安装

安装方式按照不同的系统有不同的方法，详细安装方式可以参考如下链接

[github.com/petervander…](https://github.com/petervanderdoes/gitflow-avh/wiki/Installation)



idea插件:https://plugins.jetbrains.com/plugin/7315-git-flow-integration

### 实践

#### 初始化

你可以从远程仓库clone项目或者本地新建项目

初始化命令

```
git flow init
```

该命令会指引你去修改不同分支的前缀，建议没有特别的需求使用默认前缀即可

```
$ git flow init
Initialized empty Git repository in /Users/tobi/acme-website/.git/
Branch name for production releases: [master] 
Branch name for "next release" development: [develop] 

How to name your supporting branch prefixes?
Feature branches? [feature/] 
Release branches? [release/] 
Hotfix branches? [hotfix/] 

```

如果你不需要修改默认的分支前缀（大多数情况）那么也可以使用如下命令进行初始化来跳过询问过程

```
git flow init -d
```

#### feature分支

当我们初始化完成之后，就可以开始一个新功能的开发，开发新功能需要在feature分支上进行，下面就让我们创建一个新的叫做rss-feed的feature分支。

```
git flow feature start rss-feed
```

这个功能可能需要多人协作才能完成，所以我们需要把它发布到远端（如果是本地创建的项目，请先与远端仓库建立联系）

```
git flow feature public rss-feed
```

这条指令在远程仓库新建了一个feature/rss-feed的分支，并将本地ree-feed分支track上述分支，push本地分支代码。

> 该命令只能执行一次，当远程仓库已经有了相应分支，在执行该命令将会报错，这个时候只要执行push命令就可以了。

经过一段时间的努力，我们跟同事一起协作开发完成了rss-feed分支上的功能，我们需要把这个feature分支合并到develop分支(可能还有别的feature分支，一起合并到develop)。

```
git flow feature finish rss-feed
```

*该命令做了以下几件事*

- 切换到develop分支
- 将feature/rss-feed分支merge到develop分支
- 删除本地feature/rss-feed分支

> git flow进行merge操作或者tag操作的时候，会让打开vim编辑器让你填写merge信息或者tag信息（tag信息必须填写，否则无法打tag）

> 如果merge过程发生了冲突，则在第二步merge时终止流程，即不会再删除本地分支。但当前已处于develop分支，待本地冲突解决并commit后，重新执行git flow feature finish <feature_name>即可完成finish流程。

**同步远程仓库**

- push本地develop分支
- 删除远端仓库feature/rss-feed分支

```
git push origin develop
git push origin :feature/rss-feed
```

**finish指令的附加参数**

- -r mege前先进行rebase操作
- -F merge操作完成后删除远程和本地feature分支
- -k 保留feature分支

#### 关于feature分支的讨论

不知道大家有没有这个疑问？feature分支的逻辑功能颗粒度应当是怎样的？是一个可拆分的大任务？需要多人协作？还是一个不可拆分的逻辑单元，只能由一个人独立完成？

我的看法是，每个人都应该有自己的feature分支，feature分支应当是不可拆分的完整逻辑功能，不应当多人协作；如果能够拆分那就拆成两个不同的feature分支。

**这么做的理由是：**

- 如果多人使用同一个feature，势必会导致publish的冲突，因为只能publish一次。
- finish操作也只能有一个人完成
- feature分支需要时不时的进行pull/push操作
- 每个分支还必须有一个管理者，整个项目还需要一个最终管理者进行release操作，导致组织结构复杂，而软件工程开发本身是一个扁平化的结构，扁平化代表了高效率。

**由此引发的讨论：**

- 既然feature分支只是自己在用，是否有必要将feature分支publish到远程仓库呢？欢迎大家留言交流。
- 前端如何进行任务分配？模块化，微服务等概念如何在前端落地？一个项目如何能够拆分成互不干涉，完全解耦的几个部分？如果不能，那么多人协作的过程中势必会产生冲突和功能重复，代码冗余，如何避免呢？欢迎大家不吝赐教。

#### release分支

当项目组的各个成员都完成了自己在本次版本中feature分支的功能开发并合并到develop分支，项目的管理员应当开始release操作。

基于develop分支拉出release分支进行测试，发现bug项目组成员拉取release分支后直接在release分支上进行修改，并提交到远程仓库。

下面假设我们这一次发版的版本号为v2.0，开始一个release

```
git flow release start v2.0
```

此命令会基于本地的develop分支创建一个release/v2.0分支，并切换到这个分支上。

为了让其他协同人员也能看到此分支，需要将其发布出去。

```
git flow release publish v2.0
```

测试完成后就可以发布正式版了

```
git flow release finish v2.0
```

**这一步操作具体流程：**

- 切换到master分支
- 执行git fetch检查更新： 如果远程仓库有更新，会停下来并让你先执行git pull命令（如果有冲突解决冲突并提交，这可能是唯一一种直接操作master分支的情形了）, 确保本地master是最新的。
- 如果没有更新，将release/v2.0分支合并到本地master: 会让你填写merge信息，vim的形式（如何操作vim）
- 将release/v2.0分支合并到本地develop
- 删除本地release/v2.0分支

> 建议在使用`release finish`命令之前使用git pull更新develop和master代码，特别是master

> 如果本地还有未finish的release分支，将不允许使用start指令开启新的release分支，这一点是对并行发布的一个限制。

同步到远程仓库

```
git push origin develop
git push origin master
git push origin v2.0
```

或者

```
git push origin --all
git push origin --tags
```

#### hotfix分支

当我们的线上版本出现BUG需要紧急修复的时候，流程如下：

假设修复bug的版本号为v2.0-patch

```
git flow hotfix start v2.0-patch
```

> hotfix并没有public命令，因为BUG一般是比较小且不可分割的逻辑单元，通常是单人在单个工作周期内完成，也不需要跟其他人协作。

本地完成修复，并测试通过commit之后就可以执行finish命令

```
git flow hotfix finish v2.0-patch
```

该命令执行结果：

```
Summary of actions:
- Latest objects have been fetched from 'origin'
- Hotfix branch has been merged into 'master'
- The hotfix was tagged 'v2.0-patch'
- Hotfix branch has been back-merged into 'develop'
- Hotfix branch 'hotfix/v2.0-patch' has been deleted

```

- git fetch 检查本地与远端是否up-to-date
- 将v2.0-patch分支合并到master分支
- 生成「v2.0-pathc」标签
- 将v2.0-patch分支合并到develop分支
- 删除本地v2.0-patch分支

## 相关参考资料

[GIT工作流指南](https://github.com/xirong/my-git/blob/master/git-workflow-tutorial.md)

[GitFlow使用最强指北](https://juejin.im/post/6844903917399048199)

[什么是版本控制](https://www.git-tower.com/learn/git/ebook/cn/command-line/basics/what-is-version-control)

## 版本控制工具的使用基本原则

### 精准的提交

每次提交都是一个小儿完整的功能或者一个BUG的修复。不应该出现多个功能点一块提交或者多个BUG一起修复的情况。如果一旦发现提交的代码有问题，可以方便的会滚到改动之前的正确状态，不会影响到其他协作者开发进程。

### 频繁的提交

尽可能频繁的提交你的改动到远程仓库，这样，可以避免将来合并代码的时候产生大量的冲突以至于难以解决。同时，也可以让其他同事比较快的共享你的改动。

### 不要提交不完整的功能

如果你正在开发的新功能比较庞大，那么可以讲这个功能尽可能拆分为几个逻辑模块，并且要保证分次提交的逻辑模块不会影响到整个系统的正确性。如果你只是因为临时的一些事情需要切到别的分支或者是临时需要中断开发（比如说下班）,那么应该使用Stash储藏功能来保存你的更改。

### 提交前进行测试

不要想当然的认为自己的代码是正确的，提交之前应该经过充分的测试才能提交，即使是提交到本地仓库，也应该进行测试，因为这些代码在未来会被推送到远程共享给你的同事。

### 高质量的提交注释

每次提交都应该包含完整的注释。团队成员应当遵循统一的提交规则，一般应当明确的体现出提交的类型以及具体的事情，例如 feat: add message list;

### 遵循统一的流程规范

Git 可以支持很多不同的工作流程：长期分支、功能分支、合并以及 rebase、git-flow 等等。选择什么样的开发流程要取决如下一些因素：项目开发的类型，部署模式和（可能是最重要的）开发团队成员的个人习惯。不管怎样，选择什么样的流程都需要得到所有开发成员的一致认可，并且一直遵循它。


作者：zifeiyu
链接：https://juejin.cn/post/6844903997589946382
来源：掘金
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

