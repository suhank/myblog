# git将本地仓库上传到远程仓库 push

2013年04月27日 18:30:01

阅读数：11344

在已有的git库中搭建新库，并且将本地的git仓库，上传到远程服务器的git库中，从而开始一个新的项目
*首先*，在本地新建文件夹abc，进入到abc里面，然后**git init**。这样就在本地初始化了一个git项目abc。
*然后*，登录到远程的git服务器上，到gitrepo目录下面，**mkdir abc.git**。然后进入abc.git目录。**git  --bare init**。这样就在服务器端建立了一个空的git项目。
*之后*，在本地，进入到abc目录里面，增加远程仓库。**git remote -v** 显示项目目前的远程仓库，因为是新建项目，所以结果为空。**git remote add origin git://127.0.0.1/abc.git**这样就增加了远程仓库abc。
*最后*，**commit**提交本地代码，**git push origin master**这样就把本地的git库上传到了远程git服务器的git库中了

 

也可以不登陆远程直接本地操作

\1. git init

\2. git add .

\3. git commit -am "###"      -------以上3步只是本地提交

4.git remote add origin [git@xx.xx.xx.xx:repos/xxx/xxx/xxx.git](mailto:git@10.20.42.239:repos/am4.1/platform/packages/apps/SOS.git)

5.git push origin 本地分支:远程分支



https://blog.csdn.net/xdonx/article/details/8860310