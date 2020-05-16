# git clone 指定分支操作

##  前言

近期服务器迁移，而且原来本地开发是在同一个目录中切换不同的分支，感觉有点挫，于是打算一个文件目录对应一个分支，这样不会有太大的文件差异。

## 方式一

记录下来本次操作，可能以后还会用到。

git初始化一般是这样。

git init 

git clone .git地址

之后重点来了，因为clone下来的一般为master分支，有可能不是想拉下来的分支。可以使用以下的方法

```
git branch -r #查看远程分支
git branch -a #查看所有分支
git  checkout origin/xxx  选择远端xxx分支
git branch xxx  创建本地xxx分支
git checkout xxx  选择新创建的分支就可以了。
```

－－－－－－－－－－－－－－－

## 方式二 简单的方法

当然还有更简单的方法。

直接指定clone某个分支即可：

```
git clone -b xxx .git地址

xxx分支名称
```







https://blog.csdn.net/yun__yang/article/details/74466059