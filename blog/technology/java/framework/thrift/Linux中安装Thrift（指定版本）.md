# Linux中安装Thrift（指定版本）

2018年09月10日 18:19:49 [周小董](https://me.csdn.net/xc_zhou) 阅读数 428



 Thrift的安装步骤如下：

（1）下载thrift  （  <http://archive.apache.org/dist/thrift/>  ）

去下载thrift安装包，选择自己需要的 版本 （我安装的是 0.9.1）

下载     [thrift-0.9.1.tar.gz](http://archive.apache.org/dist/thrift/0.9.1/thrift-0.9.1.tar.gz)   （压缩包）

（2）解压thrift-0.9.1.tar.gz：  执行命令    **tar -zxvf thrift-0.9.1.tar.gz    （修改自己对应的版本号）**

x : 从 tar 包中把文件提取出来

z : 表示 tar 包是被 gzip 压缩过的，所以解压时需要用 gunzip 解压

v : 显示详细信息

f xxx.[tar.gz](https://www.baidu.com/s?wd=tar.gz&tn=SE_PcZhidaonwhc_ngpagmjz&rsv_dl=gh_pc_zhidao):  指定被处理的文件是 xxx.[tar.gz](https://www.baidu.com/s?wd=tar.gz&tn=SE_PcZhidaonwhc_ngpagmjz&rsv_dl=gh_pc_zhidao)

进入解压后的文件件，再执行以下步骤

（3）查看README和INSTALL文件（如果有的话），根据README和INSTALL文件，查看thrift安装说明以及thrift依赖的软件包 （一般没用，直接下一步吧）

（4）安装thrift依赖的其他软件包

使用 sudo apt-get install 命令进行安装

sudo apt-get install libboost-dev libboost-test-dev libboost-program-options-dev libevent-dev automake libtool flex bison pkg-config g++ libssl-dev

或通知yum安装  （我用的这个）

使用sudo yum install 命令进行安装

sudo yum install libboost-dev libboost-test-dev libboost-program-options-dev libevent-dev automake libtool flex bison pkg-config g++ libssl-dev

（5）开始安装thrift

 a)  运行软件根目录下的configure脚本

 $ ./configure

 b）使用make命令进行编译

$ make

c）使用make install命令进行安装

$ sudo make install

（6）测试thrift是否安装成功

输入 $ thrift -version命令，查看输出信息判断thrift是否安装成功，如果输出

Thrift version 0.9.1

则说明thrift安装成功，然后你可以利用thrift进行开发实现。





<https://blog.csdn.net/xc_zhou/article/details/82593854>