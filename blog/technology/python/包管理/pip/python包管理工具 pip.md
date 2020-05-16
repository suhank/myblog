 

[TOC]



# python包管理工具 pip

## 1 简介

pip是一个很方便的工具, 可以方便安装, 列出, 卸载python的模块/库/包等

应该尽量使用pip，不要继续使用easy_install.

`pip` 是一个[Python](http://lib.csdn.net/base/python)包管理工具，主要是用于安装 `PyPI` 上的软件包，可以替代 `easy_install` 工具。

- GitHub: <https://github.com/pypa/pip>
- Doc: <https://pip.pypa.io/en/latest/>

```sh
以下是pip全部命令参数 :
Usage:                                                                         
  pip <command> [options]                                                      
                                                                               
Commands:                                                                      
  install                     Install packages.                                
  uninstall                   Uninstall packages.                              
  freeze                      Output installed packages in requirements format.
  list                        List installed packages.                         
  show                        Show information about installed packages.       
  search                      Search PyPI for packages.                        
  wheel                       Build wheels from your requirements.             
  help                        Show help for commands.                          
                                                                               
General Options:                                                               
  -h, --help                  Show help.                                       
  --isolated                  Run pip in an isolated mode, ignoring            
                              environment variables and user configuration.    
  -v, --verbose               Give more output. Option is additive, and can be 
                              used up to 3 times.                              
  -V, --version               Show version and exit.                           
  -q, --quiet                 Give less output.                                
  --log <path>                Path to a verbose appending log.                 
  --proxy <proxy>             Specify a proxy in the form                      
                              [user:passwd@]proxy.server:port.                 
  --retries <retries>         Maximum number of retries each connection should 
                              attempt (default 5 times).                       
  --timeout <sec>             Set the socket timeout (default 15 seconds).     
  --exists-action <action>    Default action when a path already exists:       
                              (s)witch, (i)gnore, (w)ipe, (b)ackup.            
  --trusted-host <hostname>   Mark this host as trusted, even though it does   
                              not have valid or any HTTPS.                     
  --cert <path>               Path to alternate CA bundle.                     
  --client-cert <path>        Path to SSL client certificate, a single file    
                              containing the private key and the certificate   
                              in PEM format.                                   
  --cache-dir <dir>           Store the cache data in <dir>.                   
  --no-cache-dir              Disable the cache.                               
  --disable-pip-version-check                                                  
                              Don't periodically check PyPI to determine       
                              whether a new version of pip is available for    
                              download. Implied with --no-index.               
```



## 2 pip 安装卸载

### 2.1 脚本安装pip

```
$ curl -O https://bootstrap.pypa.io/get-pip.py
$ python get-pip.py
```

### 2.2 使用包管理软件安装

```
$ sudo yum install python-pip
$ sudo apt-get install python-pip
```

centos 7 安装 pip

如果新系统尚未安装工具pip，可通过以下三步来快速完成pip的安装

​         \1.  yum -y install epel-release

​         \2.  yum install python-pip

​         \3.  pip install --upgrade pip

三步解决安装pip工具的问题。

### 2.3 更新pip

```
$ pip install -U pip
```

### 2.4 卸载

卸载pip也是很简单的，只需要一句命令即可

```
sudo pip uninstall pip
```

然后会提示你是否确认卸载，输入 `y`回车即可

卸载完成之后可以使用 `pip --version` 再次检测，发现pip已经不存在

## 3 pip基本使用

### 3.1 安装PyPI软件

```
$ pip install SomePackage

  [...]
  Successfully installed SomePackage
```

### 3.2 查看具体安装文件

```
$ pip show --files SomePackage

  Name: SomePackage
  Version: 1.0
  Location: /my/env/lib/pythonx.x/site-packages
  Files:
   ../somepackage/__init__.py
   [...]
```

### 3.3 查看哪些软件需要更新

```
$ pip list --outdated

  SomePackage (Current: 1.0 Latest: 2.0)
```

### 3.4 升级软件包

> ```
> $ pip install --upgrade SomePackage
>
>   [...]
>   Found existing installation: SomePackage 1.0
>   Uninstalling SomePackage:
>     Successfully uninstalled SomePackage
>   Running setup.py install for SomePackage
>   Successfully installed SomePackage
>
> ```

### 3.5 卸载软件包

```sh
$ pip uninstall SomePackage

  Uninstalling SomePackage:
    /my/env/lib/pythonx.x/site-packages/somepackage
  Proceed (y/n)? y
  Successfully uninstalled SomePackage

```



## 4 pip简明手册

### 4.1 安装具体版本软件

```sh
$ pip install SomePackage            # latest version
$ pip install SomePackage==1.0.4     # specific version
$ pip install 'SomePackage>=1.0.4'     # minimum version
```

### 4.2 Requirements文件安装依赖软件

`Requirements文件` 一般记录的是依赖软件列表，通过pip可以一次性安装依赖软件包:

```sh
$ pip freeze > requirements.txt

$ pip install -r requirements.txt
```

### 4.3 列出已经安装的python包

```sh
# 查看已经安装的包
$ pip list
# 查看过期的安装包
$ pip list --outdated
ipython (Current: 1.2.0 Latest: 2.3.0)
```



### 4.4 查看软件包信息

```sh
$ pip show pip
---
Name: pip
Version: 1.4.1
Location: /Library/Python/2.7/site-packages/pip-1.4.1-py2.7.egg
Requires:

$ pip show pyopencl
---
Name: pyopencl
Version: 2014.1
Location: /Library/Python/2.7/site-packages
Requires: pytools, pytest, decorator
```



### 4.5 搜寻

```sh
$ pip search pycuda

pycuda                    - Python wrapper for Nvidia CUDA
pyfft                     - FFT library for PyCuda and PyOpenCL
cudatree                  - Random Forests for the GPU using PyCUDA
reikna                    - GPGPU algorithms for PyCUDA and PyOpenCL
compyte                   - A common set of compute primitives for PyCUDA and PyOpenCL (to be created)


```

### 4.6 配置文件

配置文件: `$HOME/.pip/pip.conf`, 举例:

```
[global]
timeout = 60
index-url = http://download.zope.org/ppix

[install]
ignore-installed = true
no-dependencies = yes
```



### 4.7 命令行自动补全

对于bash:

```
$ pip completion --bash >> ~/.profile
```

对于zsh:

```
$ pip completion --zsh >> ~/.zprofile
```

加载此配置文件后，则pip命令支持自动补全功能.





http://blog.csdn.net/weiwangchao_/article/details/72466406