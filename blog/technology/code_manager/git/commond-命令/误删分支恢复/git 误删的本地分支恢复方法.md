# git 误删的本地分支恢复方法

原创伴得白马啸西风 最后发布于2017-12-06 14:13:07 阅读数 9664  收藏
展开

1.   git  log  -g 找回之前提交的commit,并记下commit_id
2.   git  branch  newbranch  commit_id
3.   切换到newbranch分支，检查文件是否存在。


 



原文链接：https://blog.csdn.net/zhouxiangyu666666/article/details/78730089