# spring 获取 实现某接口的所有实例bean

首先，获取 applicationContext，通过ApplicationAware自动注入

```java
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
 
/**
 * @ClassName: SpringBeanUtil
 * @Description: TODO(spring功能类，用于获取bean)
 * @author zhoushun
 * @date 2012-11-27 下午04:22:36
 * 
 */
@Component("springBeanUtil")
public class SpringBeanUtil implements ApplicationContextAware {
	protected final static Log logger = LogFactory.getLog(SpringBeanUtil.class);
 
	private static ApplicationContext ctx = null;
 
	private static Map<String, Properties> propMap = new HashMap<String, Properties>(
			0);
 
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		SpringBeanUtil.ctx = ctx;
	}
 
    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
 
    public  static <T> T getBean(String prop) {
        Object obj = ctx.getBean(prop);
        if (logger.isDebugEnabled()) {
            logger.debug("property=[" + prop + "],object=[" + obj + "]");
        }
        return (T)obj;
    }
 
	public static Properties getProperties(String filepath) {
		if (propMap.containsKey(filepath))
			return propMap.get(filepath);
 
		Resource resource = ctx.getResource(filepath);
		Properties prop = new Properties();
		try {
			prop.load(resource.getInputStream());
			propMap.put(filepath, prop);
			return prop;
		} catch (IOException e) {
			logger
					.error("can not find the resource file:[" + filepath + "]",
							e);
			return null;
		}
	}
}

```



获取某接口的所有实例bean

```java
//key位 bean name，value为实例
Map<String, Interface> result = SpringBeanUtil
								.getApplicationContext().getBeansOfType(Interface.class);


//返回 bean name 的String 数组
String[] result = SpringBeanUtil
				.getApplicationContext().getBeanNamesForType(IPrizeInvoke.class);

```





https://blog.csdn.net/z69183787/article/details/54347871