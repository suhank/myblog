# spring aop中ProceedingJoinPoint获取目标方法，参数，注解

 

```java

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cy.pj.common.annotation.RequiredLog;
import com.cy.pj.common.util.IPUtils;
import com.cy.pj.sys.entity.SysLog;
import com.cy.pj.sys.service.SysLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Aspect 描述的类为切面类，此类中实现：
 * 1)切入点(Pointcut)的定义
 * 2)通知(advice)的定义(扩展功能)
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {
    /**
     * @Pointcut 注解用于描述或定义一个切入点
     * 切入点的定义需要遵循spring中指定的表达式规范
     * 例如:("bean(sysMenuServiceImpl)")为切入点表达式
     * 的一种定义方式。
     */
    //bean(bean名称或一个表达式)
    //@Pointcut("bean(sysMenuServiceImpl)")
    @Pointcut("@annotation(com.cy.pj.common.annotation.RequiredLog)")
    public void logPointCut() {
    }

    /**
     * @param jp 连接点(此连接点只应用@Around描述的方法)
     * @return
     * @throws Throwable
     * @Around 注解描述的方法为一个环绕通知方法，
     * 在此方法中可以添加扩展业务逻辑，可以调用下一个
     * 切面对象或目标方法
     */
    @Around("logPointCut()")
    public Object aroundAdvice(ProceedingJoinPoint jp)
            throws Throwable {
        long start = System.currentTimeMillis();
        log.info("start:" + start);
        Object result = jp.proceed();//调用下一个切面或目标方法
        long end = System.currentTimeMillis();
        log.info("end:" + end);
        //记录日志(用户行为信息)
        saveLog(jp, (end - start));
        return result;
    }

    @Autowired
    private SysLogService sysLogService;

    //日志记录
    private void saveLog(ProceedingJoinPoint jp, long time) throws Throwable {
        //1.获取用户行为日志(ip,username,operation,method,params,time,createdTime)
        //获取类的字节码对象，通过字节码对象获取方法信息
        Class<?> targetCls = jp.getTarget().getClass();
        //获取方法签名(通过此签名获取目标方法信息)
        MethodSignature ms = (MethodSignature) jp.getSignature();
        //获取目标方法上的注解指定的操作名称
        Method targetMethod =
                targetCls.getDeclaredMethod(
                        ms.getName(),
                        ms.getParameterTypes());
        RequiredLog requiredLog =
                targetMethod.getAnnotation(RequiredLog.class);
        String operation = requiredLog.value();
        System.out.println("targetMethod=" + targetMethod);
        //获取目标方法名(目标类型+方法名)
        String targetClsName = targetCls.getName();
        String targetObjectMethodName = targetClsName + "." + ms.getName();
        //获取请求参数
        String targetMethodParams = Arrays.toString(jp.getArgs());
        //2.封装用户行为日志(SysLog)
        SysLog entity = new SysLog();
        entity.setIp(IPUtils.getIpAddr());
        entity.setUsername("admin");
        entity.setOperation(operation);
        entity.setMethod(targetObjectMethodName);
        entity.setParams(targetMethodParams);
        entity.setTime(time);
        entity.setCreatedTime(new Date());
        //3.调用业务层对象方法(saveObject)将日志写入到数据库
        sysLogService.saveObject(entity);
    }
}


```





https://blog.csdn.net/u013259361/article/details/102913329?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase