# log4j2的重新加载及不同级别日志输出到不同日志文件

log4j2的配置文件格式可以是JSON,或者XML。 

一般是在classpath路径中查找log4j2.json，或者log4j2.xml， 

或者也可以通过系统参数来设置配置文件，比如（一般这个用在命令行启动的应用）： 

```
-Dlog4j.configurationFile=path/to/log4j2.xml  
```

在应用启动时，除了让log4j2在classpath和系统参数中查找配置文件，进行初始化以外，我们还可以在代码中随时重新加载log4j2的配置文件，进行重新配置。 

例如，考虑下如下的常见场景：WEB应用，我们不希望把log4j2.xml打包到自己的jar文件中（这样修改log4j2的配置就麻烦了），也不希望把log4j2.xml放到WEB-INF/classes下面（不希望用户随便操作WEB-INF下的文件），那我们可以把log4j2.xml和其他项目中用到的配置文件，放到一个集中的地方，比如TOMCAT/bin/config下，这时，改如果初始化log4j2呢？我们可以提供一个InitServlet，例如： 

```java
...  
public void init() throws ServletException {  
    String configRoot = this.getInitParameter("configRoot");  
    String log4j2Config = configRoot + File.separator + this.getInitParameter("log4j2Config");  
    File file = new File(log4j2Config);  
    try {  
        LoggerContext context =(LoggerContext)LogManager.getContext(false);  
        context.setConfigLocation(file.toURI());  
          
        //重新初始化Log4j2的配置上下文  
        context.reconfigure();  
    } catch (MalformedURLException e) {  
        e.printStackTrace();  
    }  
      
    //todo: 这里拿到的logger，已经是按新配置文件初始化的了  
    logger = LogManager.getLogger(DefaultInitServlet.class);  
}  
```

相应的，只要在web.xml配置这个InitServlet，并提供configRoot和log4j2Config两个路径即可（也可以不要配置configRoot，而是通过System.getProperty("user.dir")来获取应用的运行目录，对tomcat而言，这个目录就是tomcat/bin，其他如命令行的应用，就是bat/sh的所在目录） 

web.xml 

```xml
<servlet> 
    <servlet-name>InitServlet</servlet-name> 
    <servlet-class>test.InitServlet</servlet-class> 
    <load-on-startup>0</load-on-startup> 
    <init-param> 
        <!-- 配置文件根目录 --> 
        <param-name>configRoot</param-name> 
        <param-value>d://config</param-value> 
    </init-param> 
    <init-param> 
        <!-- log4j2配置文件相对路径  --> 
        <param-name>log4j2Config</param-name> 
        <param-value>log4j2/log4j2.xml</param-value> 
    </init-param> 
< /servlet> 
```

好了，讲过如何重新初始化log4j2，再讲下如何把不同级别的日志，输出到不同的日志文件中。这个在网上，包括官网上，都没有一个是说清楚的。 

比如，希望trace/debug级别的日志输出到debug.log，而info级别的日志输出到info.log，其他如warn/error/fatal级别的日志都输出到error.log，这样分开输出是有好处的。我们按照如下的log42j.xml的配置，即可实现这样的输出。 

```xml
<?xml version="1.0" encoding="UTF-8"?>  
  
<configuration debug="off" monitorInterval="1800">  
    <Properties>  
        <Property name="log-path">d://logs</Property>  
    </Properties>  
  
    <Appenders>  
        <Console name="Console" target="SYSTEM_OUT">  
            <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36}.%M()/%L  - %msg%xEx%n"/>  
        </Console>  
  
        <File name="app_debug" fileName="${log-path}/app/debug.log" append="false">  
            <Filters>  
                <ThresholdFilter level="info" onMatch="DENY" onMismatch="NEUTRAL"/>  
                <ThresholdFilter level="debug" onMatch="ACCEPT" onMismatch="NEUTRAL"/>  
            </Filters>  
            <PatternLayout pattern="%d{yyyy.MM.dd HH:mm:ss z} %-5level %class{36}.%M()/%L - %msg%xEx%n"/>  
        </File>  
        <File name="app_info" fileName="${log-path}/app/info.log" append="false">  
            <Filters>  
                <ThresholdFilter level="warn" onMatch="DENY" onMismatch="NEUTRAL"/>  
                <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>  
            </Filters>  
  
            <PatternLayout pattern="%d{yyyy.MM.dd HH:mm:ss z} %-5level %class{36}.%M()/%L - %msg%xEx%n"/>  
        </File>  
        <File name="app_error" fileName="${log-path}/app/error.log" append="false">  
            <Filters>  
                <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>  
            </Filters>  
            <PatternLayout pattern="%d{yyyy.MM.dd HH:mm:ss z} %-5level %class{36}.%M()/%L - %msg%xEx%n"/>  
        </File>  
    </Appenders>  
    <Loggers>  
        <Logger name="com.test.app" level="trace" additivity="false">  
            <appender-ref ref="Console"/>  
            <appender-ref ref="app_debug"/>  
            <appender-ref ref="app_info"/>  
            <appender-ref ref="app_error"/>  
        </Logger>  
    </Loggers>  
</configuration> 
```

主要是要理解ThresholdFilter的onMatch/onMismatch的三个选项值：ACCEPT/DENY/NEUTRAL，其实，根据字面意思，也很好理解。 

重要的是，如果有多个ThresholdFilter，那么Filters是必须的，同时在Filters中，首先要过滤不符合的日志级别，把不需要的首先DENY掉，然后再ACCEPT需要的日志级别，这个次序不能搞颠倒。 





https://blog.csdn.net/zmx729618/article/details/53389111