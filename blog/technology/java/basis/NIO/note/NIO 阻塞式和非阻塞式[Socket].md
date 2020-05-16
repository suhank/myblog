# NIO 阻塞式和非阻塞式[Socket]

# 1、相关类

## ⑴ ServerSocketChannel

​    服务套接字通道

⒈ ServerSocketChannel open() {} 
    获取通道

⒉ ServerSocketChannel bind(SocketAddress local) {} 
    绑定端口。创建InetSocketAddress(int port)【SocketAddress抽象类的子类】，传输端口

⒊ SocketChannel accept() {} 
    获取套接字通道【用于和客户端通道进行数据的传输】

## ⑵ SocketChannel

​    套接字通道

⒈ int write(ByteBuffer src) {} 
    将数据传输给客户端的数据通道。通过SocketChannel来调用

⒉ int read(ByteBuffer dst) {} 
    读取客户端通道发送的数据。通过SocketChannel来调用

## ⑶ ByteBuffer

​    字节缓冲区

byte[] array() {} 
    获取字节数组(数据)

# 2、阻塞式

## (0) 概念

​    当一个线程调用 read() 或 write()时，该线程被阻塞，直到有一些数据被读取或写入，该线程在此期间不能执行其他任务。因此，在完成网络通信进行 IO 操作时，由于线程会阻塞，所以服务器端必须为每个客户端都提供一个独立的线程进行处理，当服务器端需要处理大量客户端时，性能急剧下降

## ⑴ 服务端

```
    ServerSocketChannel serverSocketChannel = null;
    SocketChannel socketChannel = null;
    try {
        serverSocketChannel = ServerSocketChannel.open(); // 获取服务端数据传输通道
        serverSocketChannel.bind(new InetSocketAddress(???)); // 绑定端口
        socketChannel = serverSocketChannel.accept(); // 获取接收客户端数据传输的通道

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int len;
        while (-1 != (len = socketChannel.read(byteBuffer))) {
            byteBuffer.flip();
            System.out.println(new String(byteBuffer.array(), 0, len)); // 将ByteBuffer缓冲区中的数据转换为byte数组
            byteBuffer.clear();
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (null != socketChannel) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != serverSocketChannel) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
1234567891011121314151617181920212223242526272829303132
```

## ⑵ 客户端

```
    SocketChannel socketChannel = null;
    try {
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("???.???.???.???", ??)); // 连接服务端
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("你好，我是客户端".getBytes());
        byteBuffer.flip(); // 一定要转换为读取数据模式！
        socketChannel.write(byteBuffer);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (null != socketChannel) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
12345678910111213141516171819
```

# 3、非阻塞式

## (0) 选择器

​    Selector 
    是SelectableChannel的多路复用器，Selector可以同时监控多个SelectableChannel的IO状况。即：利用Selector可以通过一个线程来管理多个Channel 
    Selector是非阻塞IO的核心

​    ⒈ 创建选择器 
        Selector selector = Selector.open();

​    ⒉ 通道注册选择器【用于监听指定的通道事件】 
        register(Selector sel, int ops) {} 
        通过ServerSocketChannel或SocketChannel来调用 
        第一个参数为选择器；第二个参数为要监听的通道的key的状态，可以选择4种【在SelectionKey抽象类中有4个常量】 
            SelectionKey.OP_READ = 1 << 0 = 1 读 
            SelectionKey.OP_WRITE = 1 << 2 = 4 写 
            SelectionKey.OP_CONNECT = 1 << 3 = 8 连接 
            SelectionKey.OP_ACCEPT = 1 << 4 = 16 接收

⒊ 选择相应通道上的准备好进行I/O操作的key的集合 
        int select() 
        通过选择器对象来调用 
        返回准备好I/O操作的key的个数

⒋ 获取准备好进行I/O操作的key的集合

```
  Set<SelectionKey> selectedKeys()
1
```

​        通过选择器来调用

⒌ boolean isReadable() 
        key对应的通道是否准备好进行读操作

​        boolean isWritable() 
            key对应的通道是否准备好进行写操作

​        boolean isConnectable() 
            key对应的通道是否已经完成了还是没有完成

​        boolean isAcceptable() 
            key对应的通道是否准备好去接收一个新的套接字连接

⒍ 切换套接字通道为非阻塞模式 
        configureBlocking(boolean block) {} 
            参数为false时，即非阻塞模式；true为阻塞式 
            通过ServerSocketChannel或SocketChannel来调用

## ⑴ 概念

​    当线程从某通道进行读写数据时，若没有数据可用时，该线程可以进行其他任务。线程通常将非阻塞 IO 的空闲时间用于在其他通道上执行 IO 操作，所以单独的线程可以管理多个输入和输出通道。因此，NIO 可以让服务器端使用一个或有限几个线程来同时处理连接到服务器端的所有客户端，提高了效率

## ⑵ 服务端

```
    ServerSocketChannel serverSocketChannel = null;
    SocketChannel socketChannel = null;
    try {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(???));
        serverSocketChannel.configureBlocking(false); // 切换为非阻塞模式

        Selector selector = Selector.open(); // 获取选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // 监听【接受事件】

        while (0 < selector.select()) { // 选择相应通道上的准备好进行I/O操作的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            SelectionKey key;
            while (iterator.hasNext()) {
                key = iterator.next();
                if (key.isAcceptable()) {
                    socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false); // 切换为非阻塞模式
                    socketChannel.register(selector, SelectionKey.OP_READ); // 重新设置通道监听为读状态
                } else if (key.isReadable()) {
                    socketChannel = (SocketChannel) key.channel(); // 获取键所创建的通道，并强转为套接字通道
                    int len;
                    while (0 < (len = socketChannel.read(byteBuffer))) {
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                }
                iterator.remove(); // 移除key【取消选择器的监听】，否则会出报NPE！
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (null != socketChannel) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != serverSocketChannel) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
```

## ⑶ 客户端

```
    SocketChannel socketChannel = null;
    Scanner scanner = null;
    try {
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("???.???.???.???", ???));
        socketChannel.configureBlocking(false); // 切换为非阻塞模式

        scanner = new Scanner(System.in);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (scanner.hasNext()) {
            String next = scanner.next();
            byteBuffer.put(next.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (null != scanner) {
            scanner.close();
        }
        if (null != socketChannel) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
```



https://blog.csdn.net/adsl624153/article/details/78191527