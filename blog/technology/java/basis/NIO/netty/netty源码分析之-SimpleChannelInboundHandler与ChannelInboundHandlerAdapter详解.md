# netty源码分析之-SimpleChannelInboundHandler与ChannelInboundHandlerAdapter详解

> 每一个Handler都一定会处理出站或者入站（也可能两者都处理）数据，例如对于入站的Handler可能会继承SimpleChannelInboundHandler或者ChannelInboundHandlerAdapter，而SimpleChannelInboundHandler又是继承于ChannelInboundHandlerAdapter，最大的区别在于SimpleChannelInboundHandler会对没有外界引用的资源进行一定的清理，并且入站的消息可以通过泛型来规定。

对于两者关系：

```java
public abstract class SimpleChannelInboundHandler<I> extends ChannelInboundHandlerAdapter
```

对于ChannelInboundHandlerAdapter的实现，会实现ChannelInboundHandler中的所有方法：

```java
public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler
```

但是我们可能只会重写一些我们感兴趣的方法来处理数据，这里使用的是适配器模式

对于SimpleChannelInboundHandler中：

```java
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (acceptInboundMessage(msg)) {
                @SuppressWarnings("unchecked")
                I imsg = (I) msg;
                channelRead0(ctx, imsg);
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
            if (autoRelease && release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }
    protected abstract void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception; 
```

因此我们继承SimpleChannelInboundHandler后，处理入站的数据我们只需要重新实现channelRead0方法，当channelRead真正被调用的时候我们的逻辑才会被处理。**这里使用的是模板模式，让主要的处理逻辑保持不变，让变化的步骤通过接口实现来完成**

值得注意的是对于SimpleChannelInboundHandler入站的数据，当被读取之后可能会执行ReferenceCountUtil.release(msg)释放资源。底层是实现ReferenceCounted，当新的对象初始化的时候计数为1，retain()方法实现其他地方的引用计数加1，release()方法实现应用减一，当计数减少到0的时候会被显示清除，再次访问被清除的对象会出现访问冲突。因此，当我们实现自己的Handler的时候如果希望将客户端发送过来的数据发送到客户端，可能在上述finally中已经释放了资源（writeAndFlush是异步处理），所以会出现异常情况。

但是当我们实现的是ChannelInboundHandler类的时候，重写channelRead方法时，需要释放ByteBuf相关的内存，可以使用Netty提供了一个工具方法，ReferenceCountUtil.release()



https://blog.csdn.net/u011262847/article/details/78713881