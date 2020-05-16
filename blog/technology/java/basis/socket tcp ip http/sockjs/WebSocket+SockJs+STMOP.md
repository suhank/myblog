[TOC]



# WebSocket+SockJs+STMOP

## 应用场景

websocket 是 Html5 新增加特性之一，目的是浏览器与服务端建立全双工的通信方式，解决 http 请求-响应带来过多的资源消耗，同时对特殊场景应用提供了全新的实现方式，比如聊天、股票交易、游戏等对对实时性要求较高的行业领域。

## 1.WebSocket

WebSocket 是发送和接收消息的底层API，WebSocket 协议提供了通过一个套接字实现全双工通信的功能。也能够实现 web 浏览器和 server 间的异步通信，全双工意味着 server 与浏览器间可以发送和接收消息。需要注意的是必须考虑浏览器是否支持，浏览器的支持情况如下：

#### 1.1 编写Handler类

**方法一：实现 WebSocketHandler 接口，WebSocketHandler 接口如下**

```
public interface WebSocketHandler {
    void afterConnectionEstablished(WebSocketSession session) throws Exception;
    void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception;
    void handleTransportError(WebSocketSession session, Throwable exception) throws Exception; 
    void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception; 
    boolean supportsPartialMessages();
}
```

**方法二：扩展 AbstractWebSocketHandler**

```
@Service
public class ChatHandler extends AbstractWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.sendMessage(new TextMessage("hello world."));
    }
}
```

该类中的方法我们都可以按需求重载

#### 1.2 拦截器的实现

```
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            attributes.put("username",userName);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
```

- beforeHandshake()方法，在调用 handler 前调用。常用来注册用户信息，绑定 WebSocketSession，在 handler 里根据用户信息获取WebSocketSession发送消息

#### 1.3 WebSocketConfig配置

```
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    @Autowired
    private ChatHandler chatHandler;
    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {  
    registry.addHandler(chatHandler,"/chat")
    .addInterceptors(webSocketHandshakeInterceptor);
    }
     @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192*4);
        container.setMaxBinaryMessageBufferSize(8192*4);
        return container;
    }

}
```

- 实现 WebSocketConfigurer 接口，重写 registerWebSocketHandlers 方法，这是一个核心实现方法，配置 websocket 入口，允许访问的域、注册 Handler、SockJs 支持和拦截器。
- registry.addHandler()注册和路由的功能，当客户端发起 websocket 连接，把 /path 交给对应的 handler 处理，而不实现具体的业务逻辑，可以理解为收集和任务分发中心。
- addInterceptors，顾名思义就是为 handler 添加拦截器，可以在调用 handler 前后加入我们自己的逻辑代码。
- ServletServerContainerFactoryBean可以添加对WebSocket的一些配置

#### 1.4 客户端配置

```
var  wsServer = 'ws://127.0.0.1:8080/chat'; 
var  websocket = new WebSocket(wsServer); 
websocket.onopen = function (evt) { onOpen(evt) }; 
websocket.onclose = function (evt) { onClose(evt) }; 
websocket.onmessage = function (evt) { onMessage(evt) }; 
websocket.onerror = function (evt) { onError(evt) }; 
function onOpen(evt) { 
     console.log("Connected to WebSocket server."); 
} 
function onClose(evt) { 
     console.log("Disconnected"); 
} 
function onMessage(evt) { 
     console.log('Retrieved data from server: ' + evt.data); 
} 
function onError(evt) { 
     console.log('Error occured: ' + evt.data); 
}
websocket.send(“test”);//客户端向服务器发送消息
```

**注意：**
其中 wsServer = ‘[ws://127.0.0.1:8080/chat](https://link.jianshu.com/?t=ws://127.0.0.1:8080/chat)’中的地址要根据自己的实际情况来定，一般形式为：ws://域名:端口/应用路径/WebSocket 配置的 path。“应用路径”是应用部署在 tomcat 中的文件夹路径，“WebSocket 配置的 path”是配置文件中这条配置项配置的路径。

后台输出：

```
- Connection established
- getId:1
- getLocalAddress:/127.0.0.1:8080
- getUri:/chat
```

#### 1.5 Bad Code

- 1006
  nginx配置添加

```
  proxy_connect_timeout 500s;
```

- 1009
  内容长度超限，将MaxTextMessageBufferSize增大

## 2. SockJs

为了应对许多浏览器不支持WebSocket协议的问题，设计了备选`SockJs`。

SockJS 是 WebSocket 技术的一种模拟。SockJS 会 尽可能对应 WebSocket API，但如果 WebSocket 技术不可用的话，就会选择另外的通信方式协议。

#### 2.1 WebSocketConfig配置

```
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    @Autowired
    private ChatHandler chatHandler;
    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        
    // withSockJS() 方法声明我们想要使用 SockJS 功能，如果WebSocket不可用的话，会使用 SockJS； 
       registry.addHandler(chatHandler,"/chat").addInterceptors(webSocketHandshakeInterceptor).withSockJS();
    }
}
```

#### 客户端配置

具体做法是 依赖于 JavaScript 模块加载器（如 require.js or curl.js）　还是简单使用 script 标签加载 JavaScript 库。最简单的方法是 使用 script 标签从 SockJS CDN 中进行加载，如下所示：

```
//加载sockjs
<script src="http://cdn.sockjs.org/sockjs-0.3.min.js"></script>
var url = '/chat';
var sock = new SockJS(url);
//.....
```

对以上代码分析

- SockJS 所处理的 URL 是 “http://“ 或 “https://“ 模式，而不是 “ws://“ or “wss://“；
- 其他的函数如 onopen, onmessage, and onclose ，SockJS 客户端与 WebSocket 一样，在此代码省略

后台输出：

```
- Connection established
- getId: qtfwdtti**（注意：此处和直接利用websocket API有区别）**
- getLocalAddress:/127.0.0.1:8080
- getUri: /chat/668/qtfwdtti/websocket**（注意：此处和直接利用websocket API有区别）**
```

## 3.STOMP

SockJS 为 WebSocket 提供了 备选方案。但无论哪种场景，对于实际应用来说，这种通信形式层级过低。下面看一下如何 在 WebSocket 之上使用 STOMP协议，来为浏览器 和 server 间的 通信增加适当的消息语义。（STOMP—— Simple Text Oriented Message Protocol——面向消息的简单文本协议）

#### 3.1 WebSocket、SockJs、STOMP三者关系

简而言之，WebSocket 是底层协议，SockJS 是WebSocket 的备选方案，也是 底层协议，而 STOMP 是基于 WebSocket（SockJS） 的上层协议

1. 假设HTTP协议并不存在，只能使用TCP套接字来编写web应用，你可能认为这是一件疯狂的事情。
2. 不过幸好，我们有HTTP协议，它解决了 web 浏览器发起请求以及 web 服务器响应请求的细节。
3. 直接使用 WebSocket（SockJS） 就很类似于 使用 TCP 套接字来编写 web 应用；因为没有高层协议，因此就需要我们定义应用间所发送消息的语义，还需要确保 连接的两端都能遵循这些语义。
4. **同HTTP在TCP套接字上添加请求-响应模型层一样，STOMP在 WebSocket之上提供了一个基于帧的线路格式层，用来定义消息语义。**

#### 3.2 STOMP

STOMP帧由命令，一个或多个头信息以及负载所组成。如下就是发送数据的一个STOMP帧：

```
SEND
destination:/app/room-message
content-length:20

{\"message\":\"Hello!\"}
```

**对以上代码分析：**

1. SEND：STOMP命令，表明会发送一些内容；
2. destination：头信息，用来表示消息发送到哪里；
3. content-length：头信息，用来表示 负载内容的 大小；
4. 空行；
5. 帧内容（负载）内容

#### 3.3 WebSockConfig配置

```
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //定义了一个客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息
        config.enableSimpleBroker("/topic");
        //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //添加一个/chat端点，客户端就可以通过这个端点来进行连接；withSockJS作用是添加SockJS支持
        registry.addEndpoint("/gs-guide-websocket")).withSockJS();
    }

}
```

**对以上代码分析：**

1. EnableWebSocketMessageBroker 注解表明： 这个配置类不仅配置了 WebSocket，还配置了基于代理的 STOMP 消息；
2. 它复写了 registerStompEndpoints() 方法：添加一个服务端点，来接收客户端的连接。将 “/gs-guide-websocket
3. ” 路径注册为 STOMP 端点。这个路径与之前发送和接收消息的目的路径有所不同， 这是一个端点，客户端在订阅或发布消息到目的地址前，要连接该端点，即用户发送请求 ：url=’/127.0.0.1:8080/gs-guide-websocket
4. ’ 与 STOMP server 进行连接，之后再转发到订阅url；
5. 它复写了 configureMessageBroker() 方法：配置了一个 简单的消息代理，通俗一点讲就是设置消息连接请求的各种规范信息。
6. 发送应用程序的消息将会带有 “/app” 前缀。

#### 3.4 Controller

```
@Controller
public class GreetingController {


    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendToUser("/topic/greetings")
    //@SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }

}
```

**对以上代码分析**

1. @MessageMapping 标识客户端发来消息的请求地址，前面我们全局配置中制定了服务端接收的地址以“/app”开头，所以客户端发送消息的请求连接是：/app/hello；
2. @SendToUser可以将消息只返回给发送者
3. @SendTo会将消息广播给所有订阅`/hello`这个路径的用户。

#### 3.5 客户端代码

```
function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}
```

#### 3.6 获取用户信息，定向推送

虽然STMOP的协议很好的实现了订阅，返回的模式，但是没法定向的从服务端推送消息个某个用户，要解决这个问题就需要获取用户的信息，使得我们可以对其推送。

##### 3.6.1 MyHandsHandler

```
public class MyHandsHandler extends DefaultHandshakeHandler {


    public MyHandsHandler() {
    }

    public MyHandsHandler(RequestUpgradeStrategy requestUpgradeStrategy) {
        super(requestUpgradeStrategy);
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String id = null;
        if (uri != null) {
            String query = uri.getPath();
            String[] pairs = query.split("/");
            id = pairs[3];
        }
        MyPrincipal principal = new MyPrincipal();
        principal.setName(id);
        System.out.println(id);
        return principal;
    }

    class MyPrincipal implements Principal {
        private String name;

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyPrincipal that = (MyPrincipal) o;

            return name != null ? name.equals(that.name) : that.name == null;

        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }
}
```

继承DefaultHandshakeHandler，重写其determineUser方法，根据需要自定义Principal的返回值，其name就是用来标记返回对象的id。更进一步可以用一个List来维护用户的登陆状态等。

##### 3.6.2 注册MyHandsHandler

```
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //定义了一个客户端订阅地址的前缀信息，也就是客户端接收服务端发送消息的前缀信息
        config.enableSimpleBroker("/topic");
        //定义了服务端接收地址的前缀，也即客户端给服务端发消息的地址前缀
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //添加一个/chat端点，客户端就可以通过这个端点来进行连接；withSockJS作用是添加SockJS支持
        registry.addEndpoint("/gs-guide-websocket").setHandshakeHandler(new MyHandsHandler()).withSockJS();
    }

}
```

##### 3.6.3 Controller

```
@Controller
public class GreetingController {


    @Autowired
    private SimpMessagingTemplate template;


    @RequestMapping("/xxx")
    public String greetingx(String id) throws Exception {
        template.convertAndSendToUser(id, "/topic/greetings", new Greeting("Hello, " + id + "!"));
        return "success";
    }
}
```

**以上代码分析**

1. 通过依赖注入`SimpMessagingTemplate`我们可以在服务端的任何地方发送消息
2. template.convertAndSendToUser(id, "/topic/greetings", new Greeting("Hello, " + id + "!"))可以将消息发送给我们指定id的用户，此处的id就是Principal中的name值

#### 3.7 其他Annotation说明

##### 3.7.1@DestinationVariable

```
@MessageMapping("/hello/{roomId}")
    public void roomMessage(HelloMessage message, @DestinationVariable String roomId){
        String dest = "/topic/" + roomId + "/" + "greetings";
        this.template.convertAndSend(dest, message);
    }
```

1. @DestinationVariable 用以取出请求地址中的房间 id 参数 roomId；

参考文献：

1. [http://blog.csdn.net/pacosonswjtu/article/details/51914567](https://link.jianshu.com/?t=http://blog.csdn.net/pacosonswjtu/article/details/51914567)
2. [http://tech.lede.com/2017/03/08/qa/websocket+spring/](https://link.jianshu.com/?t=http://tech.lede.com/2017/03/08/qa/websocket+spring/)



https://www.jianshu.com/p/4ef5004a1c81