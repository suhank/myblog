[TOC]



# Error during WebSocket handshake: Unexpected response code: 403

## 问:

I have implemented **WebSockets** with **Spring Boot Application** and have the below error message when trying to test the ws connection with the chrome extension '**Smart websocket client**'. However, I have no problem when run the Spring Boot Application locally.

```java
WebSocket connection to 'ws://192.168.X.XYZ:8080/test' failed: 
Error during WebSocket handshake: Unexpected response code: 403
```

The only difference which I see is in the Request headers:

In the one it works - Origin:[http://192.168.X.XYZ:8080](http://192.168.x.xyz:8080/)

In the one it does not work - Origin:chrome-extension://omalebghpgejjiaoknljcfmglgbpocdp

What I did in the **WebSocketConfig** class is below:

```java
@Override
public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(myHandler(), "/test").setAllowedOrigins("http://192.168.X.XYZ:8080");
}
```

and still does **not** work.

Could you please advise what the reason for that error might be and how to fix it?

Thank you in advance.





## 答:

setAllowedOrigins("*"); fixed the issue. Thanks! 

```java
//注册处理拦截器,拦截url为socketServer的请求
registry.addHandler(socketHandler, "/socketServer")
		.addInterceptors(new WebSocketInterceptor())
        .setAllowedOrigins("*");//setAllowedOrigins("*") 解决Error during WebSocket handshake: Unexpected response code: 403
```

