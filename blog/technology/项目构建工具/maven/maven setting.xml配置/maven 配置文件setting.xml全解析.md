[TOC]



# maven 配置文件setting.xml全解析

互联网编程 2020-06-29 17:58:27

## Maven配置文件

maven的配置文件setting.xml存在于两个地方：

```
1.安装的地⽅：${M2_HOME}/conf/settings.xml，全局配置，对操作系统的所有使用者生效；
2.⽤户的⽬录：${user.home}/.m2/settings.xml，用户配置，只对当前操作系统的使用者生效。如果上述两者都存在，它们的内容将被合并，⽤户范围的settings.xml会覆盖全局的settings.xml
```

配置的优先级从高到低排序：

```
pom.xml>user settings.xml>global settings.xml。如果这些文件同时存在，在应用配置时，会合并它们的内容；如果有重复的配置，则优先级高的配置会覆盖优先级低的配置。
```

### 全局配置settings.xml配置详解

#### 声明规范

```
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0";
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance";
 xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">;
```

#### localRepository

```
<!--本地仓库。该值表示构建系统本地仓库的路径。其默认值为${user.home}/.m2/repository。 -->
<localRepository>usr/local/maven</localRepository>
```

#### interactiveMode

```
<!--Maven是否需要和⽤户交互以获得输⼊。如果Maven需要和⽤户交互以获得输⼊，则设置成true，反之则应为false。默认为true。 -->
<interactiveMode>true</interactiveMode>
```

#### offline

```
<!--表示Maven是否需要在离线模式下运⾏。如果构建系统需要在离线模式下运⾏，则为true，默认为false。-->
<!--当由于⽹络设置原因或者安全因素，构建服务器不能连接远程仓库的时候，该配置就⼗分有⽤。 -->
<offline>false</offline>
```

#### pluginGroups

```
<!--当我们使⽤某个插件，并且没有在命令⾏为其提供组织Id（groupId）的时候，Maven就会使⽤该列表。 -->
 <!--默认情况下该列表包含了org.apache.maven.plugins。 -->
 <pluginGroups>
     <pluginGroup>org.codehaus.mojo</pluginGroup>
 </pluginGroups>
```

#### proxies

```
<!--⽤来配置不同的代理，多代理profiles可以应对笔记本或移动设备的⼯作环境：通过简单的设置profile id   就可以很容易的更换整个代理配置。 -->
 <proxies>
     <!--代理元素包含配置代理时需要的信息-->
     <proxy>
         <!--代理的唯⼀定义符，⽤来区分不同的代理元素。 -->
         <id>myproxy</id>
         <!--该代理是否是激活的那个。true则激活代理。 -->
         <active>true</active>
         <!--代理的协议。-->
         <protocol>http://…</protocol>
         <!--代理的主机名。-->
         <host>proxy.somewhere.com</host>
         <!--代理的端口。-->
         <port>8080</port>
         <!--代理的用户名，用户名和密码表示代理服务器认证的登录名和密码。-->
         <username>proxyuser</username>
         <!--代理的密码，用户名和密码表示代理服务器认证的登录名和密码。-->
         <password>somepassword</password>
         <!--不该被代理的主机名列表。该列表的分隔符由代理服务器指定；例⼦中使⽤了竖线分隔符，使⽤逗号分        隔也很常⻅。 -->
         <nonProxyHosts>*.google.com|ibiblio.org</nonProxyHosts>
     </proxy>
 </proxies>
```

#### servers

```
<!--配置服务端的⼀些设置。⼀些设置如安全证书不应该和pom.xml⼀起分发。这种类型的信息应该存在于构建服务器上的settings.xml⽂件中。 -->
 <servers>
     <!--服务器元素包含配置服务器时需要的信息-->
     <server>
         <!--这是server的id（注意不是用户登录的id），该id与distributionManagement中repository           元素的id相匹配。 -->
         <id>server001</id>
         <!--鉴权用户名。鉴权用户名和鉴权密码表示服务器认证所需要的登录名和密码。-->
         <username>my_login</username>
         <!--鉴权密码 。鉴权用户名和鉴权密码表示服务器认证所需要的登录名和密码。密码加密功能已被添加到         2.1.0 +。详情请访问密码加密页面-->
         <password>my_password</password>
         <!--鉴权时使⽤的私钥位置。和前两个元素类似，私钥位置和私钥密码指定了⼀个私钥的路径（默认             是${user.home}/.ssh/id_dsa）以及如果需要的话，⼀个密钥 -->
         <privateKey>${usr.home}/.ssh/id_dsa</privateKey>
         <!--鉴权时使⽤的私钥密码。 -->
         <passphrase>some_passphrase</passphrase>
         <!--⽂件被创建时的权限。如果在部署的时候会创建⼀个仓库⽂件或者⽬录，这时候就可以使⽤权限            （permission）。这两个元素合法的值是⼀个三位数字，其对应了unix⽂件系统的权限，如664，或者          775。 -->
         <filePermissions>664</filePermissions>
         <!--⽬录被创建时的权限。 -->
         <directoryPermissions>775</directoryPermissions>
         <!--传输层额外的配置项 -->
         <configuration></configuration>
     </server>
 </servers>
```

#### mirrors

```
<!--为仓库列表配置的下载镜像列表。 -->
 <mirrors>
     <mirror>
         <!--该镜像的唯一标识符。id用来区分不同的mirror元素。-->
         <id>planetmirror.com</id>
         <!--镜像名称-->
        <name>PlanetMirror Australia</name>
         <!--该镜像的URL。构建系统会优先考虑使⽤该URL，⽽⾮使⽤默认的服务器URL。 -->
         <url>http://downloads.planetmirror.com/pub/maven2</url>;
         <!--被镜像的服务器的id -->
         <mirrorOf>central</mirrorOf>
     </mirror>
 </mirrors>
```

#### profiles

```
<!--根据环境参数来调整构建配置的列表。settings.xml中的profile元素是pom.xml中profile元素的裁剪版本。如果⼀个settings中的profile被激活，它的值会覆盖任何其它定义在POM中或者profile.xml中的带有相同id的profile。 -->
 <profiles>
     <profile>
         <id>test</id>
```

#### activation

```
<!--⾃动触发profile的条件逻辑。Activation是profile的开启钥匙。如POM中的profile⼀样，            profile的⼒量来⾃于它能够在某些特定的环境中⾃动使⽤某些特定的值；这些环境通过activation元素          指定。activation元素并不是激活profile的唯⼀⽅式。settings.xml⽂件中的activeProfile元素          可以包含profile的id。profile也可以通过在命令⾏，使⽤-P标记和逗号分隔的列表来显式的激活            （如，-P test）。 -->
<activation>
    <!--profile默认是否激活的标识 -->
    <activeByDefault>false</activeByDefault>
    <!--activation有⼀个内建的java版本检测，如果检测到jdk版本与期待的⼀样，profile被激                  活。 -->
    <jdk>1.7</jdk>
    <!--当匹配的操作系统属性被检测到，profile被激活。os元素可以定义⼀些操作系统相关的属性。 -->
    <os>
        <!--激活profile的操作系统的名字 -->
        <name>Windows XP</name>
        <!--激活profile的操作系统所属家族(如 'windows') -->
        <family>Windows</family>
        <!--激活profile的操作系统体系结构 -->
        <arch>x86</arch>
        <!--激活profile的操作系统版本 -->
        <version>5.1.2600</version>
    </os>
    <!--如果Maven检测到某⼀个属性（其值可以在POM中通过${名称}引⽤），其拥有对应的名称和值，Profile       就会被激活。如果值字段是空的，那么存在属性名称字段就会激活profile，否则按区分⼤⼩写⽅式匹配属性值        字段 -->
    <property>
        <!--激活profile的属性的名称 -->
        <name>mavenVersion</name>
        <!--激活profile的属性的值 -->
        <value>2.0.3</value>
    </property>
    <!--提供⼀个⽂件名，通过检测该⽂件的存在或不存在来激活profile。missing检查⽂件是否存在，如果不存     在则激活profile。另⼀⽅⾯，exists则会检查⽂件是否存在，如果存在则激活profile。 -->
    <file>
        <!--如果指定的⽂件存在，则激活profile。 -->
        <exists>/usr/local/hudson/hudson-home/jobs/maven-guide-zh-to-                        production/workspace/</exists>
        <!--如果指定的⽂件不存在，则激活profile。 -->
        <missing>/usr/local/hudson/hudson-home/jobs/maven-guide-zh-to-                        production/workspace/</missing>
    </file>
</activation>
```

#### properties

```
<!--对应profile的扩展属性列表。Maven属性和Ant中的属性⼀样，可以⽤来存放⼀些值。这些值可以在POM中的任何地⽅使⽤标记${X}来使⽤，这⾥X是指属性的名称。属性有五种不同的形式，并且都能在settings.xml⽂件中访问。 -->
<!--1. env.x: 在⼀个变量前加上"env."的前缀，会返回⼀个shell环境变量。例如,"env.PATH"指代          了$path环境变量（在Windows上是%PATH%）。 -->
<!--2. project.x：指代了POM中对应的元素值。 -->
<!--3. settings.x: 指代了settings.xml中对应元素的值。 -->
<!--4. Java System Properties: 所有可通过java.lang.System.getProperties()访问的属        性都能在POM中使⽤该形式访问，例如 ${java.home}。 -->
<!--5. x: 在<properties/>元素中，或者外部⽂件中设置，以${someVar}的形式使⽤。 -->
<properties>
    <!-- 如果这个profile被激活，那么属性${user.install}就可以被访问了 -->
    <user.install>usr/local/winner/jobs/maven-guide</user.install>
</properties>
```

#### repositories

```
<!--远程仓库列表，它是Maven⽤来填充构建系统本地仓库所使⽤的⼀组远程项⽬。 -->
<repositories>
    <repository>
        <id>codehausSnapshots</id>
        <name>Codehaus Snapshots</name>
        <!--如何处理远程仓库⾥发布版本的下载 -->
        <releases>
            <!--true或者false表示该仓库是否为下载某种类型构件（发布版，快照版）开启。 -->
            <enabled>false</enabled>
            <!--该元素指定更新发⽣的频率。Maven会⽐较本地POM和远程POM的时间戳。这⾥的选项                      是：always（⼀直），daily（默认，每⽇），interval：X（这⾥X是以分钟为单位的                      时间间隔），或者never（从不）。 -->
            <updatePolicy>always</updatePolicy>
            <!--当Maven验证构件校验⽂件失败时该怎么做:ignore（忽略），fail（失败），或者warn（警              告）。 -->
            <checksumPolicy>warn</checksumPolicy>
        </releases>
        <!--如何处理远程仓库⾥快照版本的下载。有了releases和snapshots这两组配置，POM就可以在每个单          独的仓库中，为每种类型的构件采取不同的策略。例如，可能有⼈会决定只开启对快照版本下载的⽀持。参          ⻅repositories/repository/releases元素 -->
        <snapshots>
            <enabled />
            <updatePolicy />
            <checksumPolicy />
        </snapshots>
        <!--远程仓库URL，按protocol://hostname/path形式 -->
        <url>http://snapshots.maven.codehaus.org/maven2</url>;
        <!--⽤于定位和排序构件的仓库布局类型，可以是default（默认）或者legacy（遗留）。Maven2为其         仓库提供了⼀个默认的布局；然⽽，Maven1.x有⼀种不同的布局。我们可以使⽤该元素指定布局是             default（默认）还是legacy（遗留）。 -->
        <layout>default</layout>
    </repository>
</repositories>
```

#### pluginRepositories

```
<!--仓库是两种主要构件的家。第一种构件被用作其它构件的依赖。这是中央仓库中存储的大部分构件类型。另外一种构件类型是插件。Maven插件是一种特殊类型的构件。由于这个原因，插件仓库独立于其它仓库。pluginRepositories元素的结构和repositories元素的结构类似。每个pluginRepository元素指定一个Maven可以用来寻找新插件的远程地址。-->
<pluginRepositories>
    <pluginRepository>
        <releases>
            <enabled />
            <updatePolicy />
            <checksumPolicy />
        </releases>
        <snapshots>
            <enabled />
            <updatePolicy />
            <checksumPolicy />
        </snapshots>
        <id />
        <name />
        <url />
        <layout />
    </pluginRepository>
</pluginRepositories>
```

#### activeProfiles

```
<!--⼿动激活profiles的列表，按照profile被应⽤的顺序定义activeProfile。 该元素包含了⼀组        activeProfile元素，每个activeProfile都含有⼀个profile id。任何在activeProfile中定义的           profile id，不论环境设置如何，其对应的 profile都会被激活。如果没有匹配的profile，则什么都        不会发⽣。例如，env-test是⼀个activeProfile，则在pom.xml（或者profile.xml）中对应id的            profile会被激活。如果运⾏过程中找不到这样⼀个profile，Maven则会像往常⼀样运⾏。 -->
        <activeProfiles>
            <activeProfile>env-test</activeProfile>
        </activeProfiles>
    </profile>
 </profiles>
</settings>
```





https://www.toutiao.com/i6843707005660561933/?tt_from=android_share&utm_campaign=client_share&timestamp=1593443907&app=news_article&utm_medium=toutiao_android&use_new_style=1&req_id=202006292318270100150450181937B5FE&group_id=6843707005660561933