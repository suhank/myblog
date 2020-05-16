[TOC]



# git 创建标签tag并推送tag到远程分支

------

### 在本地创建tag

-a :版本号

```cmd
$ git tag -a v1.0 1
```

回车后会跳出编辑窗口，提示写一些标签注释信息：

```cmd
#
# Write a message for tag:
#   v0.8
# Lines starting with '#' will be ignored.
this is version v1.0
123456
```

保存后退出，标签完成

也可以直接在命令后面添加注释信息，如下，效果和上面方法一样

```cmd
$ git tag -a v1.0 -m 'this is test version 1.0'1
```

##### 给忘记创建标签的历史提交创建标签

首先查看历史提交的各commit_id

```cmd
$ git log --oneline
a808270 (HEAD -> master, tag: v1.0, test/master) 又新增了一行
599f2ba rrrr
05ca835 22222
a5c6877 add an row
cceb71d add test
0e51a5c first row
12345678
```

比如我要对第四次提交创建标签，在命令后面加上commit_id即可。

```cmd
$ git tag -a v0.5 a5c6877 -m 'v0.5'1
```

### 查看标签

查看标签列表

```cmd
$ git tag1
```

查看某一个标签详细信息

```cmd
$ git show v1.0
12
```

### 将本地标签推送到远程分支

```cmd
$ git push origin master --tags1
```

### 删除远程分支标签

比如远程分支已有标签v0.8，我们可以直接推送空的同名标签到到远程分支，如下

```cmd
$ git push origin master :refs/tags/v0.81
```

即可删除远程标签

### 删除本地标签

```cmd
$ git tag -d v0.8
```





https://blog.csdn.net/wong_gilbert/article/details/79973642