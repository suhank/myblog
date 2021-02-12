# 基于 Netty + Zoookeeper 实现零配置分布式RPC框架

2020年07月19日

## 1前言

- 学完Netty后总觉得不写点什么东西好像过意不去,于是就想去实现一个简易的RPC框架,但是见识到Dubbo的繁琐配置后,我知道无论再简陋我都希望它是零配置的, 就像Spring Cloud的Eureka/Nacos + Feign 那样.

## 2.简介

### 2.1 特征

- 零配置
- 容错处理
- 负载均衡
- 超时发送
- 服务器节点动态上下线
- 多种序列化方式
- 自定义协议栈
- 一致性hash算法

### 2.1 技术点

- Spring 项目必备框架,非常强大的对象管理能力
- Netty 基于NIO的高性能异步网络通讯框架, 处理服务节点的之间的数据传输问题
- Zookeeper 分布式服务框架, 用作服务注册和发现中心
- Curator 操作Zookeeper的客户端SDK
- Protostuff 高性能的序列化方式

## 3 快速上手

具体参考example包下的案例

### 3.1 服务注册

新建一个普通的SpringBoot项目ServerA, 然后导入服务注册依赖

```
  <!--   服务注册     -->
  <dependency>
        <groupId>com.burukeyou.BoomRpc</groupId>
        <artifactId>BoomRpc.register</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```

然后编写application.properties文件,配置服务发布的信息

```
#发布服务的端口
server.port=8090

# 发布服务的应用名
spring.application.name=ServerA

# 服务注册中心的地址,即Zookeeper的连接地址
boomRpc.register.address=192.168.1.19:2181
```

在SpringBoot程序入口类添加@EnableServerRegister注解开启服务注册

```
@EnableServerRegister
@SpringBootApplication
public class ServerA_Application {
    public static void main(String[] args) {
        SpringApplication.run(ServerA_Application.class,args);
    }
}
```

编写需要公开被调用的服务并用@BoomService注解标注, 并填写服务名与接口ProductService保持一致

```
public interface ProductService {

    List<String> getAllProductByUserId(String id);

    void buyOne(Integer productId);
}


@BoomService("ProductService")
public class ProductServiceImpl implements ProductService {

    @Override
    public List<String> getAllProductByUserId(String id) {
        return Arrays.asList("苹果","西瓜","饮料");
    }

    @Override
    public void buyOne(Integer productId) {
        System.out.println("购买商品:" + productId);
    }
}

```

连续启动3个此项目, 依次修改application.properties的server.port为8090,8091,8092 启动项目,服务发布成功,在Zookeer可观察到, 服务应用ServerA下有三个服务器节点提供者

![Netty + Zoookeeper，实现零配置分布式RPC框架](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/772394d272bc45489a8871e6f8ae7e4d)



### 3.2 服务调用

新建一个普通SpringBoot项目Client, 导入依赖

```
  <!--  服务调用   -->
 <dependency>
       <groupId>com.burukeyou.BoomRpc</groupId>
       <artifactId>BoomRpc.rpc</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
复制代码
```

编写application.properties配置文件

```
server.port=8080
spring.application.name=client
# 配置负载均衡策略: 轮训
boomRpc.rpc.loadblacne=roundRobin     
# 服务注册中心地址
boomRpc.register.address=192.168.1.19:2181
```

复制需要远端调用的服务接口,比如ServerA项目中编写的ProductService接口类, 并用

- 用@BoomRpc声明为需要远程服务调用的类, name属性设置远程服务调用的服务名,因为ProductService在ServerA项目中, 即在ServerA项目的配置文件spring.application.name属性值
- callback 是设置服务调用失败时回调的容错处理类, 需要继承Callback和ProductService接口

```
@BoomRpc(name = "ServerA",callback = ProductServiceCallback.class)//
public interface ProductService {

    List<String> getAllProductByUserId(String id);

    void buyOne(Integer productId);
}

// 容错处理类
public class ProductServiceCallback extends Callback implements ProductService {

    @Override
    public List<String> getAllProductByUserId(String id) {
        Throwable throwable = getThrowable(); // 获得失败异常信息
        System.err.println("getAllProductByUserId服务调用失败： "+throwable.getMessage());
        return Arrays.asList("获取商品失败");
    }

    @Override
    public void buyOne(Integer productId) {
        Throwable throwable = getThrowable();
        System.err.println("buyOne服务调用失败： "+throwable.getMessage());
    }
}
```

在Cleint应用程序启动类添加开启服务调用功能注解@EnableBoomRpc, 而provider属性填写需要远端调用的接口的所在的包的位置 (即之前编写的ProductService所在包的位置)

```
@EnableBoomRpc(provider = {"burukeyou.client.rpc"})
@SpringBootApplication
public class Client01Application {
    public static void main(String[] args) {
        SpringApplication.run(Client01Application.class,args);
    }
}
```

编写Controller测试远程调用效果

```
@RestController
public class TestController {


    @Autowired
    private ProductService productService;

	
    @RequestMapping("/b")
    public void testProductServiceBuyOne(){
        productService.buyOne(47289384);
    }

    @RequestMapping("/c")
    public List<String> testProductServiceGetAll(){
        return productService.getAllProductByUserId("5324534");
    }

}
```

### 3.2 测试效果

#### 3.2.1 测试服务远程调用

启动Client程序,并调用接口http://localhost:8080/c ,发现与期待结果一致远程调用了ServerA的实现类

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678c737e7bc2)

此次请求被负载均衡到了 ServerA的 8090节点

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678c7c7923fd)



#### 3.2.2 测试负载均衡策略

连续发送三个 http://localhost:8080/c 请求, 由于使用轮训策略, 服务器节点依次被调用

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678c7beb25b7)



#### 3.2.3 测试服务节点动态上下线

此时若再启动一个ServerA项目端口为8093, 测Client应用能动态感知到更新本地服务提供者缓存,保证数据一致性.

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678c7c8f30ad)

这时再发请求发现请求被负载到了新的服务器节点

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678c7c97dad0)

同里把8093节点停掉也能动态感知到, 之后如何请求也不会被负载到8093上

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678ca89634f3)



#### 3.2.4 测试服务降级

如果把所有服务提供者Server停掉, 则Client远程调用将会失败,但配置了降级策略会回调到callback类进行处理, 如果不配置callbakc属性默认会抛出异常

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678cb95aa27e)

再请求http://localhost:8080/c接口返回,发现代码输出与ProductServiceCallback类被回调了

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678cbc860926)

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678cbc935376)



#### 3.2.5 测试请求超时处理

为了模拟请求超时,把ServerRequesthandler类的睡眠代码 Thread.sleep(30000);解开注释

之后再次发送http://localhost:8080/c请求, 会请求超时被降级处理类回调



![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678cbf1d5548)

每次请求发送的超时时间默认是2秒,默认会重试3次发送, 在第4次发现重试次数用完就会抛出异常被降级处理类ProductServiceCallback处理回调显示

![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678cbf84787a)



## 4 RPC实现原理

- 所谓RPC就是远程服务调用, 从效果上看就是调用远程方法时跟调用本地方法一样, 本质就是通过动态代理生成代理对象,然后由代理对象去进行远程调用.
- 所以实现RPC无非就两点, 1-如何远程调用其他服务的方法 2-如何像调用本地方法一样调用远程方法

抛开所有不谈,只要能远程调用其他服务上的方法就算是实现了RPC,再说白就是服务于服务之间能进行通讯,所以只能我们之间能进行通讯我就能进行远程调用

- 比如服务A 要调用服务B的方法, 既然A要调用B的方法,所以它们得进行数据的传输, 而A和B又不在一台机器上, 两个应用进行数据传输的方式千千万通过Netty或者Http都可以 ,只要能保证通讯即可, 然后才可以进行数据的传输.
- 当它们可以进行数据的传输后, 服务A只需要把要调用哪个类的哪个方法以及方法参数值是什么传递过去给服务B即可, 所以服务B有了上面3个信息之后通过反射技术就可以在本地执行. 执行完之后就可以把结果数据发送回给服务A即可
- 比如服务B收到服务A发来的消息本地调用核心实现如下

```java
 private Object handleRequest(RpcRequest request)  {
 		//获得要调用哪个类
        String className = request.getClassName();
        className = className.substring(className.lastIndexOf(".")+1);
        // 获得要调用的这个类的哪个方法
        String methodName = request.getMethodName();
        // 获得要调用的这个类的这个方法的参数类型
        Class<?>[] parameterTypes = request.getParameterTypes();
        // 获得要调用的这个类的这个方法的参数类型的具体值
        Object[] parameters = request.getParameters();
        // 获得这个类的具体实现类的class对象
        Object obj = ServerRegisterBoot.impClassMap.get(className);
        Class<?> clazz = obj.getClass();
        // 根据这个类的具体实现类的class对象获取到方法对象
        Method method = clazz.getMethod(methodName, parameterTypes);
        // 执行方法对象的到结果
        return  method.invoke(obj, parameters);
    }
```

- 执行完之后只要我们通过Netty就可以把结果数据发送回去即可
- 就是两个服务之间进行通讯, 然后把你要调用哪个接口的哪个方法传输过去即可,另一端收到执行后把结果数据发送回去就没了. 两个服务间通讯有很多中方式走TPC也好走Http也罢, 只要你能通讯就能远程调用.

另一个关键就是如何像调用本地方法一样调用远程方法, 不可能说你每次要远程调用就重新写一大堆的代码,什么建立通讯连接,发送请求数据, 处理请求数据,把请求数据返回吧

- 这个基于动态代理就非常好实现
- 比如下面代码, 理论上3步骤ProductService接口的抽象get方法调用是没效果的, 但是通过动态代理+ Spring 技术, 我们可以用动态代理实现ProductService接口的实现类对象(代理对象)并注入到Spriing容器中, 此时Spring容器中就存在ProductService接口的实现类, 然后可以通过 @Autowired方式从Spring上下文中进行依赖注入, 然后就可以执行getAllProductByUserId方法了, 因为Java多态的特性然后实际就会去调用代理对象的方法,所以只需要实现代理对象的方法去实际远程调用即可.

```java
	public interface ProductService { 
	    List<String> get(String id);
	}

    @Autowired
    private ProductService productService; // 2

	productService.getAllProductByUserId(id); // 3
```

动态代理的远程调用核心实现如下:

- 封装请求对象
- 获得接受该请求的服务提供地址列表
- 从服务提供地址列表进行负载均衡选择一台
- 通过Netty把请求发过去即可

```java
 @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1 - 封装请求对象,用于发送到另一段
        //就是要告诉另一端你要调用哪个类的哪个方法
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);

        // 2- 获得服务serverName的服务提供者列表
        List<String> providerList = RpcCacheHolder.SERVER_PROVIDERS.get(serverName);
        if (providerList == null || providerList.size() < 1 ){
        	// 另一段压根没提供服务的机器存在,肯定无法远程调用直接降级处理
            return Callbacker.Builder(rpcRequest)
                             .IfNotCallback(() -> {throw new RuntimeException(serverName+"服务不存在，调用失败");})
                             .orElseSet(new RuntimeException(serverName+"服务不存在，调用失败"));
        }

        // 4. 负载均衡,具体要把请求发送到哪个机器处理
        LoadBalanceContext loadBalanceContext = RpcCacheHolder.APPLICATION_CONTEXT.getBean(LoadBalanceContext.class);
        String serverIp = loadBalanceContext.executeLoadBalance(providerList);
        System.out.println("负载均衡: 调用服务" + serverName +"的" +  serverIp + " 服务器节点");

        //
        String[] host = serverIp.split(":");
        RpcClient rpcClient = new RpcClient(host[0].trim(), Integer.parseInt(host[1].trim()),serverName);

        RpcResponse rpcResponse = null;
        try {
        	// 5 - 最后把请求消息通过Netty发送到另一端即可, 等待接受另一端的响应
            rpcResponse = rpcClient.sendRequest(rpcRequest);
        } catch (Exception e) {
            // 如果因为某些原因发送失败,直接降级处理
            return Callbacker.Builder(rpcRequest).IfNotCallback(e::printStackTrace).orElseSet(e);
        }

		// 最后把实际远程调用的那个方法的返回值返回即可
        return rpcResponse != null ? rpcResponse.getResult() : null;
    }
```

## 5 服务注册中心数据结构设计

- 类似Nacos的服务注册方式,服务注册中心是以一台应用为维度作为一个服务, 提供相同服务的机器就会加入到这个服务的集群,统一对外提供服务
- 每个应用在配置文件的spring.application.name属性配置它要加入哪个服务集群
- 比如:
- ![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678d02d4037a)

## 6 服务器节点动态上下线实现

- 首先需要感知服务器节点的状态是在服务调用端的, 因为它需要知道哪些服务是可用的, 以及这个服务下有哪些服务提供者, 然后才可以进行远程服务调用.
- 每个应用启动时把 “ /应用名/当前服务器IP : 端口” 这样的节点在Zookeeper创建即可, 比如“/ServerA/192.168.1.7:8090”, 然后一定要把节点设置为临时节点. 这个当该节点挂掉的时候注册中心Zookeeper能够感知到然后自动把该节点删除. 服务注册代码如下:

```
 public String register(String nodePath, String nodeData) {
        isConnenct();

        String path = null;
        try {
            path =  zkClient.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL) //设置为临时节点
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE) //设置权限
                    .forPath(nodePath, nodeData.getBytes());
        }catch (KeeperException.NodeExistsException e){
            logger.error("NodeExistsException ----服务注册失败，该服务器节点 {} 已经注册,请修改",e.getPath());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
```

- 同时启动后扫描我们要订阅哪些服务即哪些服务是需要进行远程调用的,这个通过@BoomRpc这个注解去标记, 它的name属性就是配置它需要调用哪个远程服务的服务名,所以只需要在启动的时候扫描被标记了@BoomRpc注解的接口获取到name属性值再做一个去重就可以只要当前要订阅哪些服务, 之后就可以用curator的api去订阅这些服务.
- 扫描@BoomRpc注解代码实现如下:

```
Reflections reflections = new Reflections("@BoomRpc注解所在的包");
Set<Class<?>> rpcClazz = reflections.getTypesAnnotatedWith(BoomRpc.class, true);
for (Class<?> e : rpcClazz) {
    BoomRpc annotation = e.getAnnotation(BoomRpc.class);
    String serverName = "".equals(annotation.name()) ? annotation.value() : annotation.name();
 //把要订阅的服务serverName添加到Set集合即可
 RpcCacheHolder.SUBSCRIBE_SERVICE.add(serverName);
}
```

然后就可以用curator的api去订阅这些服务.代码实现如下

- 其实就是给该服务添加一个监听器,当有服务器上线或者下线都能感知到,同时更新本地服务提供者缓存

```
  public static  CuratorFramework zkClient = null;
  private static ReentrantLock updateProviderLock = new ReentrantLock();
  // 服务发现
  public List<String> discover(String serverName){
        isConnenct();

        List<String> serverList = null;
        try {
             PathChildrenCache childrenCache = new PathChildrenCache(zkClient, "/" + serverName,true);
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            // 订阅服务
            addListener(childrenCache,serverName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverList;
    }


 private void addListener(PathChildrenCache childrenCache,String serverName){
        childrenCache.getListenable().addListener((curatorFramework, event) -> {
            // 创建子节点
            if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                String path = event.getData().getPath();
                String host = path.substring(path.lastIndexOf("/") + 1, path.length());
                System.out.println("服务器上线:" + path);

                try {
                	// 更新本地服务提供者缓存
                	//SERVER_PROVIDERS就是一个 Map<String, List<String>>集合
                    updateProviderLock.lock();
                    List<String> list = RpcCacheHolder.SERVER_PROVIDERS.getOrDefault(serverName,new ArrayList<>());
                    list.add(host);
                    RpcCacheHolder.SERVER_PROVIDERS.put(serverName,list);
                } finally {
                    updateProviderLock.unlock();
                }
            }
            // 删除子节点
            else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                String path = event.getData().getPath();
                String host = path.substring(path.lastIndexOf("/") + 1, path.length());
                System.out.println("服务器下线:" + event.getData().getPath());

                try {
                    updateProviderLock.lock();
                    List<String> list = RpcCacheHolder.SERVER_PROVIDERS.get(serverName);
                    list.remove(host);
                    RpcCacheHolder.SERVER_PROVIDERS.put(serverName,list);
                } finally {
                    updateProviderLock.unlock();
                }
            }
        });
    }
```

## 7 自定义通信协议设计

### 7.1 自定义数据包

- 所谓协议就是一个二进制数据包 然后每个Bit位都代表一定的意义
- 现在两个服务之间要进行通讯, 如果事先设计好传输信息的协议可以避免很多问题



![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678cfe68c44f)



- 魔数相当于一串密码, 凡事客户端发来的数据包携带的前面4个字节的数据跟我们设置魔数不相同直接不处理, 防止一些恶意的数据请求
- 序列化算法, 通过规范好实际传输数据用什么序列化后, 服务端接受到数据包之后就可以根据序列化算法字段进行对应的反序列化
- 反序列化后的类型,就是接受到数据后要把实际传输数据反序列化成什么对象类型
- 数据包长度, 实际传输数据的长度
- 实际传输数据,就是真正要传输到另一端的数据, 比如A要远程调用B, A就要把请求对象发送给B, 而B就要把响应对象返回给A

### 7.1 请求对象设计

- 请求对象目的要告诉对方你要调用它的哪个类的哪个方法

```
public class RpcRequest extends RpcProtocol  {
    private String requestId;// 请求id
    private String className; //调用类名
    private String methodName; //调用方法名
    private Class<?>[] parameterTypes; // 方法参数类型
    private Object[] parameters;//方法参数
}
```

- 请求id如果是基于长连接传输设计, 即多个请求共用一个连接通道就会无法保证请求的有序性, 这样就不知哪个请求是谁发的那接收到一堆响应后又不知道要教给谁处理了,如果每次给请求生成全局唯一ID, 就可以保证每一个请求的唯一性有序性
- 不过这里实现是基于一个请求一个连接后就断开的方法处理暂时不会有这个问题

### 7.2 响应对象设计

- 响应对象就是请求后返回的数据

```
public class RpcResponse extends RpcProtocol {
    private String requestId; //对应的请求id, 这个响应对象对应哪个请求
    private Exception exception; //请求异常信息
    private Object result; // 实际远程方法调用返回的数据

```

## 8负载均衡

### 8.1 基于策略设计模式动态注入负载均衡策略

- 类图设计如下:
- LoadBalanceStrategy 是负载均衡抽象类
- RandomStrategy 是随机负载均衡的具体实现类
- RoundRobinStrategy 是 轮训负载均衡的具体实现类
- LoadBalanceContext 是拥有调度负载均衡能力的上下文



![在这里插入图片描述](基于 Netty + Zoookeeper 实现零配置分布式RPC框架.assets/1736678d05c98859)

一般都可以在配置文件中配置使用哪种负载策略,然后用Spring读取动态生成对应的负载均衡实现类然后设置到LoadBalanceContext上下文种, 最后再把LoadBalanceContext注入到Spring容器中即可. 之后通过LoadBalanceContext执行负载策略.代码实现



```
@Configuration
public class RpcBeanConfiguration {

    private final BoomRpcProperties properties;

    public RpcBeanConfiguration(BoomRpcProperties properties) {
        this.properties = properties;
    }
	
	// Spring启动时执行这段代码,把LoadBalanceContext注入到容器中
    @Bean
    public LoadBalanceContext loadBalanceContext(){
        LoadBalanceContext loadBalanceContext = new LoadBalanceContext();
        if ("roundRobin".equalsIgnoreCase(properties.getLoadblacne().trim())){
            loadBalanceContext.setLoadBalanceStrategy(new RoundRobinStrategy());
        }else if("random".equalsIgnoreCase(properties.getLoadblacne().trim())){
            loadBalanceContext.setLoadBalanceStrategy(new RandomStrategy());
        }else if("hash".equalsIgnoreCase(properties.getLoadblacne().trim())){
            // todo
        }else {
            loadBalanceContext.setLoadBalanceStrategy(new RandomStrategy());
        }
        return loadBalanceContext;
    }
}
```

### 8.2 缓存负载均衡之一致性hash算法

在有些时候我们希望相同的请求能够负载的负载到同一个服务器节点上, 这个在分布式缓存有非常广泛应用. 比如通过第一次查询缓存发现缓存不存在, 通过缓存key将缓存负载均衡的缓存到一台节点上,下一次读取缓存key时可以直接负载均衡到之前相同的服务器节点 .这就可以使用到hash算法,因为每个相同的key的hash值是一致的.

但是传统hash算法存在弊端, 传统hash算法为:

- hash(key) % / 机器数 = 余数 （服务器序号）
- 这就会造成一个问题, 当前集群机器数变化时, 会导致之前缓存的负载均衡大面积失效, 甚至造成缓存雪崩

这时一致性hash算法就在一定程度上解决了这个问题, 它只会影响小部分的缓存负载失效 它原理就是:

- 事先将服务提供者列表hash映射到哈希环上(节点)，当一个请求到来对， 请求的key进行hash映射到环上, 然后顺时针寻找找到的第一个节点就作为负载后的节点
- 一般一致性hash算法会存在hash偏斜现象, 比如事先将服务提供者列表全部hash映射到哈希环上(节点)的这些节点 并不均匀的分割hash环，这样在实际负载的时候就违背了负载均衡的理念, 解决办法是给每个节点新构N个建虚拟节点，让他们在逻辑上均衡的分割Hash环.

代码实现:

```
public class ConsistentHashStrategy   {

    /** 哈希环
     *      存放虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
    */
     private  SortedMap<Integer, String> hashRing = new TreeMap<>();

    /**
         * 虚拟节点的数目
     */
    private static final int VIRTUAL_NODES = 100;

	// 将服务提供者列表映射成hash环
    public  <T> ConsistentHashStrategy initHashRing(List<T> list){
        for (T ip : list) {
            // 为每个真实节点生成VIRTUAL_NODES个虚拟节点
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodeName = ip + "&&VN" + i; // 虚拟节点名称中保存了真实节点，这样就可以找回到真实节点
                int hash = getHash(virtualNodeName);
                hashRing.put(hash, virtualNodeName);
            }
        }
        return this;
    }

	// 根据请求key从hash环上进行负载
    public String getServer(String key){
        int hash = getHash(key);
        System.out.println(key+ "：  key的"+hash);
        // 顺时针寻找找到的第一个节点
        SortedMap<Integer, String> sortedMap = hashRing.tailMap(hash);

        String targetNode;
        if (sortedMap.isEmpty()){
            // 如果sortedMap为null表示 hash寻找到了哈希环的尾部， 直接寻找到hash环第一个节点即可
            targetNode = hashRing.get(hashRing.firstKey());
        }else {
            targetNode = sortedMap.get(sortedMap.firstKey());
        }

        // 最后把找到的虚拟节点名称截取一下就可以获得真实节点名称
        return targetNode != null && !"".equals(targetNode) ? targetNode.substring(0,targetNode.indexOf("&&")) : null;
    }


    // FNV1_32_HASH算法
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
}
```

## 9 容错处理设计

- 容错处理是指在远程服务调用失败时候进行的回调, 比如服务提供者不存在,或者请求超时,或者发送请求异常, 或者发送请求成功后但是服务调用异常把异常信息返回等等之后进行的一个回调的处理, 而不会因为某个服务的不可用导致整个服务挂掉.
- 实现上采用类似Feign的fallback机制,通过事先配置好回调处理类处理
- 进行回调处理只需要在发生异常情况是通过发送封装的请求对象就可以拿到它上面的远程调用注解@BoomRpc, 进而就可以拿到配置的回调类callback, 通过回调类反射获取到远程调用失败的方法对象, 在动态注入异常信息,然后在调用方法对象即可实现回调处理

一般是否需要处理回调只有一个逻辑即是否配置了callback类,没有则直接抛出异常,伪代码如下:

```
 //假设已经拿到@BoomRpc注解的callback属性的值
 if(callback != null) {
	// 调用callback的类对象进行回调处理
}else {
	// 直接抛出异常
}
```

为了方便调用写了一个链式调用链Callbacker处理: 调用方式如下:

```
   RpcResponse rpcResponse = null;
        try {
        	// 这是远程服务调用返回的响应对象
            rpcResponse = rpcClient.sendRequest(rpcRequest);
            //根据响应对象是否包含异常信息判断是否远程调用异常
            if (rpcResponse != null && rpcResponse.getException() != null){
                Exception e = rpcResponse.getException();
                //如果存在异常则交给容错处理类Callbacker进行处理 
                return Callbacker.Builder(rpcRequest).IfNotCallback(e::printStackTrace).orElseSet(e);
            }
        } catch (Exception e) {
            // 捕获异常，容错处理
            return Callbacker.Builder(rpcRequest).IfNotCallback(e::printStackTrace).orElseSet(e);
        }
```

具体容错处理类Callbacker实现:

```
public class Callbacker {

    private RpcRequest rpcRequest;

    private Class<?> callBackClass;
    private Class<?> callback;


    public static Callbacker Builder(RpcRequest rpcRequest){
        return new Callbacker(rpcRequest);
    }

	// 创建Callbacker的同时拿到 BoomRpc注解配置的callback类
    private  Callbacker(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
        try {
            System.out.println(rpcRequest);
            callBackClass =  Class.forName(rpcRequest.getClassName());
            callback = callBackClass.getAnnotation(BoomRpc.class).callback();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("1");
        }
    }

	// 如果不需要回调处理直接执行自定义的处理方法Process,一般都会直接传抛出异常
    public Callbacker IfNotCallback(Process process){
         if (!shouldCallback())
             process.doSomething();
        return this;
    }
	
	// 如果要进行回调处理, 直接把异常信息传入去处理即可
    public Object orElseSet(Throwable throwable) throws Exception {
        if (shouldCallback()){
        	// 创建callback的实例对象
            Object obj = callback.newInstance();
            //从callback上获得此次请求的方法的方法对象
            Method method = callback.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
            // 由于callback类都需要继承Callback抽象类拿到异常信息,这里就可以动态注入异常信息
            Method setThrowable = callback.getMethod("setThrowable", Throwable.class);
            setThrowable.invoke(obj,throwable);
            // 之后执行此次请求的方法的方法对象即可
            return method.invoke(obj,rpcRequest.getParameters());
        }else
            return null;
    }

	// 判断callback是否为默认值void.class来进行回调处理
    private boolean shouldCallback(){
        return callback != void.class;
    }


    public interface Process {
        void doSomething();
    }
}
```

完整代码地址 [BoomRpc](https://github.com/burukeYou/BoomRpc)



D:\backup\studio\AvailableCode\framework\rpc-远程服务调用\netty\BoomRpc



[基于 Netty + Zoookeeper 实现零配置分布式RPC框架 (juejin.cn)](https://juejin.cn/post/6854573210948763655)