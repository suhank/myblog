# Debian更换软件源



 2019-03-10 01:08:30

中国科技大学镜像网
地址
https://mirrors.ustc.edu.cn/debian/

\#备份一下软件源

```
sudo cp /etc/apt/sources.list /etc/apt/sources.list_bak
```

一般情况下，将 /etc/apt/sources.list 文件中 Debian 默认的源地址 http://deb.debian.org/ 替换为 [http://mirrors.ustc.edu.cn](http://mirrors.ustc.edu.cn/) 即可。

```
sudo sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list
```

当然也可以直接编辑 /etc/apt/sources.list 文件（需要使用 sudo）。
用以下命令打开配置文件

```
sudo vi /etc/apt/sources.list
```

加入如下内容即可

```
deb http://mirrors.ustc.edu.cn/debian stable main contrib non-free
# deb-src http://mirrors.ustc.edu.cn/debian stable main contrib non-free
deb http://mirrors.ustc.edu.cn/debian stable-updates main contrib non-free
# deb-src http://mirrors.ustc.edu.cn/debian stable-updates main contrib non-free

# deb http://mirrors.ustc.edu.cn/debian stable-proposed-updates main contrib non-free
# deb-src http://mirrors.ustc.edu.cn/debian stable-proposed-updates main contrib non-free
```

敲击i键进入插入模式，组合键ctrl+shift+v将复制内容粘贴至源文件中，敲击两次esc键进入命令模式，输入引号内键“：wq!"保存并退出
更改完 sources.list 文件后请运行 `sudo apt-get update` 更新索引以生效。

更多镜像
阿里云：http://mirrors.aliyun.com/
搜狐：http://mirrors.sohu.com/
网易：http://mirrors.163.com/







https://blog.csdn.net/jomesm/article/details/88374156