# Spring 事件驱动模型

Spring 事件驱动模型包括三个概念：事件、事件监听器、事件发布者。

#### 事件 ApplicationEvent

```java
public abstract class ApplicationEvent extends EventObject {
    private final long timestamp;
    // protected transient Object  source; 父类中定义
    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }
}
```

自定义事件时只需要继承ApplicationEvent

#### 事件监听器 ApplicationListener

```java
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

    void onApplicationEvent(E event);
}
```

ApplicationListener 接口限定 ApplicationEvent的子类作为接口中方法的参数，所以每一个监听器都是针对某一具体的事件进行监听。

#### 事件发布者 ApplicationEventPublisher

```java
@FunctionalInterface
public interface ApplicationEventPublisher {

    default void publishEvent(ApplicationEvent event){
        publishEvent((Object) event);
    }

    void publishEvent(Object event);
}
```

ApplicationContext 实现了ApplicationEventPublisher，所以发布事件时可直接通过ApplicationContext

#### 特点

- 一个事件可被多个监听器监听处理
- 如果事件发布方法存在事务，那么事件发布和事件监听方法处于同一事务中，属于同步处理。
- 可通过onApplicationEvent添加@Async注解变为异步处理

#### 注解 @EventListener

可在监听方法上使用@EventListener注解代替实现ApplicationListener接口, 并可在方法上添加@Order注解实现监听器处理的顺序

@TransactionalEventListener 可隔离事件发布和监听的事务

#### 实例

```java
@Data
public class HomeWorkEvent extends ApplicationEvent {
    
    
    /**
     * 作业内容
     */
    private String content;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public HomeWorkEvent(Object source, String content) {
        super(source);
        this.content = content;
    }
}


/**
 * 定义作业的监听者学生
 * @author xup
 * @date 2019/12/28 21:57
 */
@Component
public class StudentListener implements ApplicationListener<HomeWorkEvent> {
    
    @Override
    public void onApplicationEvent(HomeWorkEvent event) {
        System.out.println("StudentListener接收到老师布置的作业：" + event.getContent());
    }
}


/**
 * 作业发布者--教师
 * @author xup
 * @date 2019/12/28 21:59
 */
@Component
public class TeacherPublisher {
    @Autowired
    private ApplicationContext applicationContext;
    
    public void publishHomeWork(String content){
        HomeWorkEvent homeWorkEvent = new HomeWorkEvent(this, content);
        applicationContext.publishEvent(homeWorkEvent);
    }
}


/**
 * 定义作业的监听者学生
 * @author xup
 * @date 2019/12/28 21:57
 */
@Component
public class StudentListener2 {
    
    @EventListener
    @Order(2)
    public void receiveHomeWork(HomeWorkEvent event) {
        System.out.println("StudentListener2接收到老师布置的作业2：" + event.getContent());
    }
    
    @EventListener(classes = {HomeWorkEvent.class})
    @Order(1)
    public void receiveHomeWork(Object event) {
        System.out.println("StudentListener2接收到老师布置的作业1：" + ((HomeWorkEvent)event).getContent());
    }
}


@RunWith(value= SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TeacherPublisherTest {
    
    @Autowired
    private TeacherPublisher teacherPublisher;
    
    @Test
    public void publish(){
        teacherPublisher.publishHomeWork("背诵课文。。");
    }
    
}
```

输出

```
StudentListener2接收到老师布置的作业1：背诵课文。。
StudentListener2接收到老师布置的作业2：背诵课文。。
StudentListener接收到老师布置的作业：背诵课文。。
```



https://juejin.im/post/6844904033589673992#heading-4