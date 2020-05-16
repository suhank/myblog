# maven常用命令



1. mvn compile 编译源代码
2. mvn test-compile 编译测试代码
3. mvn test 运行测试
4. mvn package 打包，根据pom.xml打成war或jar
  如果pom.xml中设置 war，则此命令相当于mvn war:war
  如果pom.xml中设置 jar，则此命令相当于mvn jar:jar
5. mvn -Dtest package	打包但不测试。完整命令为：mvn -D maven.test.skip=true package
6. mvn install	在本地Repository中安装jar
7. mvn clean	清除产生的项目
8. mvn eclipse:eclipse 生成eclipse项目
  10.mvn idea:idea	生成idea项目
  11.mvn eclipse:clean	清除eclipse的一些系统设置
9. 

一般使用情况是这样，

1、通过cvs或svn下载代码到本机

2、执行mvn eclipse:eclipse生成ecllipse项目文件

3、然后导入到eclipse就行了

4、修改代码后执行mvn compile或mvn test检验



 



<http://rubyer.me/blog/613/>