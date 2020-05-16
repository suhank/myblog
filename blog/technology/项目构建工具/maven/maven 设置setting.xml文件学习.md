[TOC]



# maven 设置setting.xml文件学习

官网地址:http://maven.apache.org/ref/3.5.3/maven-settings/settings.html

# 快速预览

maven的配置文件为settings.xml，在下面路径中可以找到这个文件，分别为：
    ------ $M2_HOME/conf/settings.xml：全局设置，在maven的安装目录下；
    ------ ${user.home}/.m2/settings.xml：用户设置，需要用户手动添加，可以将安装目录下的settings.xml文件拷贝过来修改。
    两个文件的关系为：如果两个文件同时存在，文件内容将被融合，相同设置将以用户设置的settings.xml为准。
    该文件一共有10个配置项，文件结构为：

```
<settings xmlns="<http://maven.apache.org/SETTINGS/1.0.0>"
          xmlns:xsi="<http://www.w3.org/2001/XMLSchema-instance>"
          xsi:schemaLocation="<http://maven.apache.org/SETTINGS/1.0.0>
                        <http://maven.apache.org/xsd/settings-1.0.0.xsd>">
    <localRepository/>
    <interactiveMode/>
    <usePluginRegistry/>
    <offline/>
    <pluginGroups/>
    <servers/>
    <mirrors/>
    <proxies/>
    <profiles/>
    <activeProfiles/>
</settings>
```


 下面对每一个配置项做一个简要的讲解，帮助理解每一项的含义和配置方式，以便后面做更深入的学习：

# 简单属性

 ```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"  
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
        xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0  
                http://maven.apache.org/xsd/settings-1.0.0.xsd">  
    <localRepository>${user.home}/.m2/repository</localRepository>  
    <interactiveMode>true</interactiveMode>  
    <usePluginRegistry>false</usePluginRegistry>  
    <offline>false</offline>  
    ...  
 </settings>  
 ```

localRepository：本地仓库路径，默认值 ${user.home}/.m2/repository；
interactiveMode：值为true/false，true表示mave可以使用用户输入，默认true；
usePluginRegistry：值为true/false，true表示maven使用${user.home}/.m2/plugin-registry.xml管理插件版本，默认为false；
offline：值为true/false，true表示构建系统在离线模式下执行，默认为false；

# pluginGroups

每个pluginGroup元素都包含一个groupId，当你在命令行中没有提供插件的groupid时，将会使用该列表。这个列表自动包含org.apache.maven.plugins和org.codehaus.mojo。

```
<pluginGroups>  
    <pluginGroup>org.mortbay.jetty</pluginGroup>  
</pluginGroups>  
```

例如：在做了上面的配置后，直接执行如下简写形式的命令即可

mvn jetty:run

# servers

POM中的repositories和distributionManagement元素为下载和部署定义的仓库。一些设置如服务器的用户名和密码不应该和pom.xml一起分发。这种类型的信息应该存在于构建服务器上的settings.xml文件中。

```xml
<servers>  
    <server>  
        <id>server001</id>  
        <username>my_login</username>  
        <password>my_password</password>  
        <privateKey>${user.home}/.ssh/id_dsa</privateKey>  
        <passphrase>some_passphrase</passphrase>  
        <filePermissions>664</filePermissions>  
        <directoryPermissions>775</directoryPermissions>  
        <configuration></configuration>  
    </server>  
</servers>  
```

id：服务器的id，和repository/mirror中配置的id项匹配；
username，password：服务器的认证信息；
privateKey，passphrase：指定一个路径到一个私有key（默认为${user.home}/.ssh/id_dsa）和一个passphrase；
filePermissions，directoryPermissions：设置文件和文件夹访问权限，对应unix文件权限值，如：664，后者775.
注意：如果你使用一个已有key登录服务器，你必须忽略\<password>项，否则，key将会被忽略。

# Mirrors

```
<mirrors>  
    <mirror>  
        <id>planetmirror.com</id>  
        <name>PlanetMirror Australia</name>  
        <url>http://downloads.planetmirror.com/pub/maven2</url>  
        <mirrorOf>central</mirrorOf>  
    </mirror>  
</mirrors>  
```

- id，name：镜像的唯一标识和用户友好的名称；该id用于区分镜像元素，并在连接镜像时从<servers>部分选择相应的凭据。


- url：此镜像的基本网址。 构建系统将使用此URL来连接到存储库，而不是原始存储库URL。


- mirrorof：

  这是镜像的存储库的ID。 例如，要指向Maven中央存储库（https://repo.maven.apache.org/maven2/）的镜像，请将此元素设置为中央。 更高级的映射，如repo1，repo2或*，！inhouse也是可能的。 这不能与镜像ID匹配。

  使用镜像的仓库的id，可以使用下面匹配属性：
           ------*：匹配所有仓库id；
           ------external:*：匹配所有仓库id，除了那些使用localhost或者基于仓库的文件的仓库；
           ------多个仓库id之间用逗号分隔；
           ------!repol：表示匹配所有仓库，除了repol。
           注意：如果配置了多个仓库，首先匹配id精确匹配的镜像，否则maven使用第一个匹配的镜像。

# proxies

代理服务器设置。

```
<proxy>  
    <id>optional</id>  
    <active>true</active>  
    <protocol>http</protocol>  
    <username>proxyuser</username>  
    <password>proxypass</password>  
    <host>proxy.host.net</host>  
    <port>80</port>  
    <nonProxyHosts>local.net|some.host.com</nonProxyHosts>  
</proxy>  
```

  id：可选，为代理服务器设置一个名称；
  active：true/false，默认true；
  protocol：代理服务器使用的协议类型；
  username：用户名；
  password：密码；
  host：主机名，或者ip；
  port：端口号；
  nonProxyHosts：不使用代理服务器的主机类型，多个主机之间用'|'分隔，可使用通配符，如：*.somewhere.com。

# profiles

这里的profile元素是pom.xml的profile元素的一个裁剪版本，它包含activation、repositories、pluginRepositories和properties元素。

如果一个在settings中的profile是激活的，它的值将覆盖在一个POM或者profiles.xml文件中的任何相同id的profiles。

## Activation

通过Activation来指定profile生效的环境，具体见下：

```
<profiles>  
    <profile>  
        <id>test</id>  
        <activation>  
            <activeByDefault>false</activeByDefault>  
            <jdk>1.5</jdk>  
            <os>  
                <name>Windows XP</name>  
                <family>Windows</family>  
                <arch>x86</arch>  
                <version>5.1.2600</version>  
            </os>  
            <property>  
                <name>mavenVersion</name>  
                <value>2.0.3</value>  
            </property>  
            <file>  
                <exists>${basedir}/file2.properties</exists>  
                <missing>${basedir}/file1.properties</missing>  
            </file>  
        </activation>  
        ...  
    </profile>  
</profiles>  
```

1. activeByDefault：是否自动激活；
   jdk：jdk版本，必须达到该版本后才能执行激活；
   os：操作系统环境信息；
   property：当maven探测到一个name=value值对时，profile才被激活；
   file：文件exists或者missing可以激活该profile。

## Properties

  properties中的值可以在一个POM中通过${x}来使用，x比哦是一个property，以下形式在settiongs.xml文件中都可以使用：
   --env.X；表示使用一个环境变量，如：${env.PATH}表示使用PATH环境变量；
   --project.x：标识在POM中的对应元素的值，如：<project><version>1.0</version></project>可以通过${project.version}引用；
   --settings.x：在settins.xml中包含的对应元素的值，如：<settings><offline>false</offline></settings>可以通过${settings.offline}引用；
   --Java System Properties：所有java.lang.System.getProperties()获取的属性都是可用的，如：${java.home}；
   --x：在<properties/> 中或者一个扩展文件中设置的属性，如：${someVar}；
   配置方式为：

```
<profiles>  
    <profile>  
        ...  
        <properties>  
            <user.install>${user.home}/our-project</user.install>  
        </properties>  
        ...  
    </profile>  
</profiles> 
```

属性${user.install}就可以在POM中使用了。

## Repositories

仓库。仓库是Maven用来填充构建系统本地仓库所使用的一组远程项目。而Maven是从本地仓库中使用其插件和依赖。不同的远程仓库可能含有不同的项目，而在某个激活的profile下，可能定义了一些仓库来搜索需要的发布版或快照版构件。

```xml
<repositories>  
    <repository>  
        <id>codehausSnapshots</id>  
        <name>Codehaus Snapshots</name>  
        <releases>  
            <enabled>false</enabled>  
            <updatePolicy>always</updatePolicy>  
            <checksumPolicy>warn</checksumPolicy>  
        </releases>  
        <snapshots>  
            <enabled>true</enabled>  
            <updatePolicy>never</updatePolicy>  
            <checksumPolicy>fail</checksumPolicy>  
        </snapshots>  
        <url>http://snapshots.maven.codehaus.org/maven2</url>  
        <layout>default</layout>  
    </repository>  
</repositories>  
```

releases、snapshots：不同的版本策略，对应发布版本和快照版本；

enabled：true/false，对应类型是否激活；

updatePolicy：更新策略，更新snapshot包的频率，属性有四个值always(实时更新) daily（每天更新一次） interval:xxx（隔xxx分钟更新一次）  never（从不更新） 默认为daily 

checksumPolicy：maven部署文件到仓库时，也会部署对应的校验和文件，你可以设置：ignore，fail或者warn用于当校验和文件不存在或者检验失败时的处理策略；
layout：上面提到的仓库大部分都遵循共同的布局，可以配置：default（默认）或者legacy（遗留）；

## pluginRepositories

插件仓库。仓库是两种主要构件的家。第一种构件被用作其它构件的依赖。这是中央仓库中存储大部分构件类型。另外一种构件类型是插件。Maven插件是一种特殊类型的构件。由于这个原因，插件仓库独立于其它仓库。pluginRepositories元素的结构和repositories元素的结构类似。每个pluginRepository元素指定一个Maven可以用来寻找新插件的远程地址。

# activeProfiles

```
   <activeProfiles>  
       <activeProfile>env-test</activeProfile>  
   </activeProfiles>  
```

   activeProfile中间定义activeProfile的id，在这里定义的activeProfile总是被激活，不关心环境设置，如果配置的id的profile没有发现，将没有任何事发生。



https://blog.csdn.net/tomato__/article/details/13025187