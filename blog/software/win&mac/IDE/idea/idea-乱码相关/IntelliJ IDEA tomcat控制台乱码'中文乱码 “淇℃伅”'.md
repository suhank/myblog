# IntelliJ IDEA tomcat控制台乱码'中文乱码 “淇℃伅”'

打开到tomcat安装目录下的conf/文件夹 修改logging.properties文件，

找到 **java.util.logging.ConsoleHandler.encoding = utf-8**

更改为 **java.util.logging.ConsoleHandler.encoding = GBK**





https://blog.csdn.net/bluesea_wangys/article/details/87711050