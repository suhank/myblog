[TOC]



# spring注入一个实体bean的集合并指定顺序

2017年04月27日 20:07:51 [yingxian_Fei](https://me.csdn.net/yxtouch) 阅读数 4563

  

本文向一个bean中注入一个接口实体bean的集合，并指定各个实体bean在集合中的顺序，然后在被注入bean中按顺序调用集合中的bean实体中的方法。核心实现时在实体类上使用@Order注解来定制各个bean被注入的顺序实现。

### 1、定义实体类方法接口

如下demo定义了一个接口类，其中有一个doHandler的方法。

```java

package api.landsem.iot.v1.handler.impl.rpc;
 
public interface IRpcStatusHandler {
	/** 
	* @Title: doHandler 
	* @Description: Handler method for status update.
	* @param data
	* @param sign      
	*/  
	void doHandler(String status,String sign);
}
```



### 2、添加接口实现

如下为三个接口的实现类，注意在实现类上使用@Order注解来指定该实体bean被加载的顺序，注解中的值越小越优先被加载注入。

```java
package api.landsem.iot.v1.handler.impl.status;
 
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
 
import api.landsem.iot.v1.handler.IIotDeviceHandler;
import api.landsem.iot.v1.handler.IIotDeviceHandler.IOTDeviceStatus;
import api.landsem.iot.v1.handler.IIotDeviceHandler.IOTDeviceStatus.Status;
import api.landsem.iot.v1.handler.impl.rpc.IRpcStatusHandler;
 
/**
 * Update device status on device login.
 *
 */
@Component
@Order(1)
public class StatusUpdateHandler implements IRpcStatusHandler{	
	private static final Logger logger = Logger
			.getLogger(StatusUpdateHandler.class);	
	
	@Autowired
	private IIotDeviceHandler mIotDeviceHandler;
 
	@Override
	public void doHandler(String status, String sign) {
		logger.info("Call status update handler.");
	}
} 
```

```java
package api.landsem.iot.v1.handler.impl.status;
 
import org.apache.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
 
import api.landsem.iot.v1.handler.impl.rpc.IRpcStatusHandler;
 
/**
 * Send off line configuration message on device login.
 *
 */
@Component
@Order(2)
public class SendOfflineConfigHanler implements IRpcStatusHandler{
	private static final Logger logger = Logger
			.getLogger(SendOfflineConfigHanler.class);		
 
	@Override
	public void doHandler(String status, String sign) {
		logger.info("Call send offline configuration handler.");		
	}
} 
```



```java
package api.landsem.iot.v1.handler.impl.status;
 
import org.apache.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
 
import api.landsem.iot.v1.handler.impl.rpc.IRpcStatusHandler;
 
/**
 * Send off line navigation message on device login.
 */
@Component
@Order(3)
public class SendOfflineNavigationHandler implements IRpcStatusHandler{
	private static final Logger logger = Logger
			.getLogger(SendOfflineNavigationHandler.class);	
	
	@Override
	public void doHandler(String status, String sign) {
		logger.info("Call send offline navigation handler.");
		
	}
} 
```



### 3、使用 

#### （1）、直接注入到集合 

在需要使用的bean中，直接创建一个IRpcStatusHandler接口的集合，然后使用@Autowired注解即可，程序启动后会自动将实现了该接口的bean按照Order注解的顺序注入到集合中。

```java
    @Autowired
    public List<IRpcStatusHandler> mRpcStatusHandlers;
```



#### （2）、创建一个单独的集合bean

创建一个集合属性，然后使用@Autowired注入实现的bean，之后再将该集合属性作为一个bean返回并未bean指定名字。在其他bean中使用@Autowired注入时使@Qualifier注解指定bean的名字，实现加载集合bean。如下为java中的bean配置代码：

```html
package api.landsem.iot.v1.handler.impl.rpc.configuration;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
import api.landsem.iot.v1.handler.impl.rpc.IRpcMessageHandler;
import api.landsem.iot.v1.handler.impl.rpc.IRpcStatusHandler;
 
@Configuration
@ComponentScan("api.landsem.iot.v1.handler.impl.status")
public class RpcConfiguration {
    @Autowired
    private List<IRpcStatusHandler> mRpcStatusHandlers;
    
    /** 
    * @Title: getRpcStatusHandlers 
    * @Description: RPC status update hander bean list. 
    * @return      
    */  
    @Bean(name="rpcStatusUpdateHandlers")
    public List<IRpcStatusHandler> getRpcStatusHandlers() {
        return this.mRpcStatusHandlers;
    }
}
```

如下为在其他bean中使用集合bean对象的方法：

```html
	@Autowired
	@Qualifier(value = "rpcStatusUpdateHandlers")
	public List<IRpcStatusHandler> mRpcStatusHandlers;
```





<https://blog.csdn.net/smilefyx/article/details/70877313>