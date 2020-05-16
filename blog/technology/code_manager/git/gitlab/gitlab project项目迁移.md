

[TOC]



# gitlab project项目迁移

## 一、gitlab迁移需求

- 由于集团要求，需要把子公司gitlab仓库迁移到集团的gitlab仓库
- 子公司gitlab地址：git.aaa.com，集团gitlab地址：git.bbb.net

## 二、gitlab迁移步骤

- **1、查看目前子公司gitlab地址**

  ```
  luoxuejun-d1:360-fang yanmin$ git remote -v
  origin    git@git.aaa.com:360fang/360-fang.git (fetch)
  origin    git@git.aaa.com:360fang/360-fang.git (push) 
  ```

- **2、在集团gitlab上新建fang项目，生成gitlab地址：git@git.bbb:360-fyd/fang.git**

- **3、设置把本地gitlab地址替换成为集团gitlab地址**

  ```
  luoxuejun-d1:360-fang yanmin$ git remote set-url origin git@git.bbb.net:360-fyd/fang.git
  luoxuejun-d1:360-fang yanmin$ git remote -v
  origin  git@git.bbb.net:360-fyd/fang.git (fetch)
  origin  git@git.bbb.net:360-fyd/fang.git (push) 
  ```

- **4、查看分支情况**

  ```
  luoxuejun-d1:360-fang yanmin$ git branch -a
  master
  * v1.0.0
  v1.1.1
  remotes/origin/HEAD -> origin/master
  remotes/origin/develop
  remotes/origin/master
  remotes/origin/newtrust
  remotes/origin/trust
  remotes/origin/v1.0.0
  remotes/origin/v1.0.1
  remotes/origin/v1.1.1 
  ```

  > master、v1.0.0、v1.1.1代表本地分支、remotes/origin/develop等代表远程分支

- **5、把本地指定分支，推送到集团远程代码仓库**

  ```
  luoxuejun-d1:360-fang yanmin$ git push origin master:master 
  ```

- **6、把远程分支推送到远程**

  - A、先checkout远程分支到本地

    ```
    luoxuejun-d1:360-fang yanmin$ git checkout -b develop origin/develop 
    ```

  - B、在push本地分支到远程仓库

    ```
    luoxuejun-d1:360-fang yanmin$ git push origin develop:develop
    ```





<https://blog.csdn.net/lcyaiym/article/details/77678467>