[TOC]



# git 批量删除tag

git 批量删除tag

## 本地

```
git tag | grep "v" |xargs git tag -d
```

其中grep "v"应该是你自己想要的匹配

## 远程

```
git show-ref --tag | grep "v1.0"| awk '{print $2}'|xargs git push origin --delete
```

其中,grep "v1.0"应该是你想要的匹配







<https://www.jianshu.com/p/83ea11828c8e>