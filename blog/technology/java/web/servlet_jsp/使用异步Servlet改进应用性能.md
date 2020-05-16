# 使用异步Servlet改进应用性能

[Nikita Salnikov Tarnovski](http://plumbr.eu/about-us)是[plumbr](http://plumbr.eu/)的高级开发者，也是一位应用性能调优的专家，他拥有多年的性能调优经验。近日，Tarnovski[撰文](http://plumbr.eu/blog/how-to-use-asynchronous-servlets-to-improve-performance)谈到了如何通过异步Servlet来改进常见的Java Web应用的性能问题。

众所周知，Servlet 3.0标准已经发布了很长一段时间，相较于之前的2.5版的标准，新标准增加了很多特性，比如说以注解形式配置Servlet、web.xml片段、异步处理支持、文件上传支持等。虽然说现在的很多Java Web项目并不会直接使用Servlet进行开发，而是通过如Spring MVC、Struts2等框架来实现，不过这些Java Web框架本质上还是基于传统的JSP与Servlet进行设计的，因此Servlet依然是最基础、最重要的标准和组件。在Servlet 3.0标准新增的诸多特性中，异步处理支持是令开发者最为关注的一个特性，本文就将详细对比传统的Servlet与异步Servlet在开发上、使用上、以及最终实现上的差别，分析异步Servlet为何会提升Java Web应用的性能。

本文主要介绍的是能够解决现代Web应用常见性能问题的一种性能优化技术。当今的应用已经不仅仅是被动地等待浏览器来发起请求，而是由应用自身发起通信。典型的示例有聊天应用、拍卖系统等等，实际情况是大多数时间与浏览器的连接都是空闲的，等待着某个事件来触发。

这种类型的应用自身存在着一个问题，特别是在高负载的情况下问题会变得更为严重。典型的症状有线程饥饿、影响用户交互等等。根据近一段时间的经验，我认为可以通过一种相对比较简单的方案来解决这个问题。在Servlet API 3.0实现成为主流后，解决方案就变得更加简单、标准化且优雅了。

在开始介绍解决方案前，我们应该更深入地理解问题的细节。还有什么比看源代码更直接的呢，下面就来看看下面这段代码：

```java
@WebServlet(urlPatterns = "/BlockingServlet")
public class BlockingServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
    try {
      long start = System.currentTimeMillis();
      Thread.sleep(2000);
      String name = Thread.currentThread().getName();
      long duration = System.currentTimeMillis() - start;
      response.getWriter().printf("Thread %s completed the task in %d ms.", name, duration);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
```

上面这个Servlet主要完成以下事情：

1. 请求到达，表示开始监控某些事件。
2. 线程被阻塞，直到事件发生为止。
3. 在接收到事件后，编辑响应然后将其发回给客户端。

为了简化，代码中将等待部分替换为一个Thread.sleep()调用。

现在，你可能会觉得这就是一个挺不错的Servlet。在很多情况下，你的理解都是正确的，上述代码并没有什么问题，不过当应用的负载变大后就不是这么回事了。

为了模拟负载，我通过[JMeter](http://jmeter.apache.org/)创建了一个简单的测试，我会启动2,000个线程，每个线程运行10次，每次都会向/BlockedServlet这个地址发出请求。将这个Servlet部署在Tomcat 7.0.42中然后运行测试，得到如下结果：

- 平均响应时间：19,324ms
- 最快响应时间：2,000ms
- 最慢响应时间：21,869ms
- 吞吐量：97个请求/秒

默认的Tomcat配置有200个工作线程，此外再加上模拟的工作由2,000ms的睡眠时间来表示，这就能比较好地解释最快与最慢的响应时间了，每个线程都会睡眠2秒钟。再加上上下文切换的代价，因此97个请求/秒的吞吐量基本上是符合我们的预期的。

对于绝大多数的应用来说，这个吞吐量还算是可以接受的。重点来看看最慢的响应时间与平均响应时间，问题就变得有些严重了。经过20秒而不是期待的2秒才能得到响应显然会让用户感到非常不爽。

下面我们来看看另外一种实现，利用Servlet API 3.0的异步支持：

```java
@WebServlet(asyncSupported = true, value = "/AsyncServlet")
public class AsyncServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Work.add(request.startAsync());
  }
}
public class Work implements ServletContextListener {
  private static final BlockingQueue queue = new LinkedBlockingQueue();

  private volatile Thread thread;

  public static void add(AsyncContext c) {
    queue.add(c);
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(2000);
            AsyncContext context;
            while ((context = queue.poll()) != null) {
              try {
                ServletResponse response = context.getResponse();
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.printf("Thread %s completed the task", Thread.currentThread().getName());
                out.flush();
              } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
              } finally {
                context.complete();
              }
            }
          } catch (InterruptedException e) {
            return;
          }
        }
      }
    });
    thread.start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    thread.interrupt();
  }
}
```

上面的代码看起来有点复杂，因此在开始分析这个解决方案的细节信息之前，我先来概述一下这个方案：速度上提升了75倍，吞吐量提升了20倍。看到这个结果，你肯定迫不及待地想知道这个示例是如何做到的吧。

这个Servlet本身是非常简单的。需要注意两点，首先是声明Servlet支持异步方法调用：

```
@WebServlet(asyncSupported = true, value = "/AsyncServlet")
```

其次，重要的部分实际上是隐藏在下面这行代码调用中的。

```
Work.add(request.startAsync());
```

整个请求处理都被委托给了Work类。请求上下文是通过AsyncContext实例来保存的，它持有容器提供的请求与响应对象。

现在来看看第2个，也是更加复杂的类，Work类实现了ServletContextListener接口。进来的请求会在该实现中排队等待通知，通知可能是上面提到的拍卖中的竞标价，或是所有请求都在等待的群组聊天中的下一条消息。

当通知到达时，我们这里依然是通过Thread.sleep()让线程睡眠2,000ms，队列中所有被阻塞的任务都是由一个工作线程来处理的，该线程负责编辑与发送响应。相对于阻塞成百上千个线程以等待外部通知，我们通过一种更加简单且干净的方式达成所愿，通过批处理在单独的线程中处理请求。

还是让结果来说话吧，测试配置与方才的示例一样，依然使用Tomcat 7.0.24的默认配置，测试结果如下所示：

- 平均响应时间：265ms
- 最快响应时间：6ms
- 最慢响应时间：2,058ms
- 吞吐量：1,965个请求/秒

虽然说这个示例很简单，不过对于实际项目来说通过这种方式依然能获得类似的结果。

在将所有的Servlet改写为异步Servlet前，请容许我多说几句。该解决方案非常适合于某些应用场景，比如说群组通知与拍卖价格通知等。不过，对于等待数据库查询完成的请求来说，这种方式就没有什么必要了。像往常一样，我必须得重申一下——请通过实验进行度量，而不是瞎猜。

对于那些不适合于这种解决方案的场景来说，我还是要说一下这种方式的好处。除了在吞吐量与延迟方面带来的显而易见的改进外，这种方式还可以在大负载的情况下优雅地避免可能出现的线程饥饿问题。

另一个重要的方面，这种异步处理请求的方式已经是标准化的了。它不依赖于你所使用的Servlet API 3.0，兼容于各种应用服务器，如Tomcat 7、JBoss 6或是Jetty 8等，在这些服务器上这种方式都可以正常使用。你不必再面对各种不同的Comet实现或是依赖于平台的解决方案了，比如说Weblogic FutureResponseServlet。

就如本文一开始所提的那样，现在的Java Web项目很少会直接使用Servlet API进行开发了，不过诸多的Web MVC框架都是基于Servlet与JSP标准实现的，那么在你的日常开发中，是否使用过出现多年的Servlet API 3.0，使用了它的哪些特性与API呢？



http://www.infoq.com/cn/news/2013/11/use-asynchronous-servlet-improve