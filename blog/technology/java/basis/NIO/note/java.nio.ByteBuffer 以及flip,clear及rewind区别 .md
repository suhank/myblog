# java.nio.ByteBuffer 以及flip,clear及rewind区别 

## **Buffer 类**

定义了一个可以线性存放primitive type数据的容器接口。Buffer主要包含了与类型（byte, char…）无关的功能。

值得注意的是**Buffer及其子类都不是线程安全的。**

每个Buffer都有以下的属性：

capacity
这个Buffer最多能放多少数据。capacity一般在buffer被创建的时候指定。

limit
在Buffer上进行的读写操作都不能越过这个下标。当写数据到buffer中时，limit一般和capacity相等，当读数据时，
limit代表buffer中有效数据的长度。

position
读/写操作的当前下标。当使用buffer的相对位置进行读/写操作时，读/写会从这个下标进行，并在操作完成后，
buffer会更新下标的值。

mark
一个临时存放的位置下标。调用mark()会将mark设为当前的position的值，以后调用reset()会将position属性设
置为mark的值。mark的值总是小于等于position的值，如果将position的值设的比mark小，当前的mark值会被抛弃掉。

这些属性总是满足以下条件：
0 <= mark <= position <= limit <= capacity

limit和position的值除了通过limit()和position()函数来设置，也可以通过下面这些函数来改变：

 

Buffer clear()

public final Buffer **clear**() {
position = 0; //设置为0
limit = capacity; //极限和容量相同
mark = -1; //取消标记
return this;
} 
把position设为0，把limit设为capacity，**一般在把数据写入Buffer前调用。**

 

Buffer flip()

public final Buffer **flip**() {
limit = position;
position = 0;
mark = -1;
return this;
} 
把limit设为当前position，把position设为0，**一般在从Buffer读出数据前调用。**

 

Buffer rewind()

public final Buffer **rewind**() {
position = 0;
mark = -1;
return this;
} 
把position设为0，limit不变，**一般在把数据重写入Buffer前调用。**

 

Buffer对象有可能是只读的，这时，任何对该对象的写操作都会触发一个ReadOnlyBufferException。
isReadOnly()方法可以用来判断一个Buffer是否只读

### 举例补充（摘自百度知道）：

ByteBuffer 的filp函数, 将缓冲区的终止位置limit设置为当前位置, 缓冲区的游标position(当前位置)重设为0. 
比如 我们有初始化一个ByteBuffer 后有 
ByteBuffer buffer = ByteBuffer.allocate(1024);
这是 终止位置limit在1024， 而起始位置position在 0
如果我们添加一个数据, 
buffer.putint(90);
这会使起始位置 position 移到4, 也就是说postion始终都在第一个可写字节的位置上. limit 则不会发生改变
而如果这时,我们调用
buffer.flip();
position转到0， limit转到 4 也就是原来的position 所在位置
这里的flip, 从另外一个角度上来说, 是在读数据时,操作的
然而, 如果我此时在写
buffer.putInt(90);
就会将原来的覆盖掉
如果我们在写, 这时就不行了, 就会重现问题了. 因为我们的limit是4, 我们写入数据不能超过这个limit,(当然还有capacity)
所以在写的时候,最好先清空buffer.clear();
如果真的不想清空, 也可以调用 
buffer.limit(newlimit);
设置一个较大的limit, 再写入 
当然不能超过capacity, 可以等于 capacity

 

## ByteBuffer 类

在Buffer的子类中，ByteBuffer是一个地位较为特殊的类，因为在java.io.channels中定义的各种channel的IO
操作基本上都是围绕ByteBuffer展开的。

ByteBuffer定义了4个static方法来做创建工作：

ByteBuffer allocate(int capacity) //创建一个指定capacity的ByteBuffer。
ByteBuffer allocateDirect(int capacity) //创建一个direct的ByteBuffer，这样的ByteBuffer在参与IO操作时性能会更好
ByteBuffer wrap(byte [] array)
ByteBuffer wrap(byte [] array, int offset, int length) //把一个byte数组或byte数组的一部分包装成ByteBuffer。

ByteBuffer定义了一系列get和put操作来从中读写byte数据，如下面几个：
byte get()
ByteBuffer get(byte [] dst)
byte get(int index)
ByteBuffer put(byte b)
ByteBuffer put(byte [] src)
ByteBuffer put(int index, byte b) 
这些操作可分为绝对定位和相对定为两种，相对定位的读写操作依靠position来定位Buffer中的位置，并在操
作完成后会更新position的值。在其它类型的buffer中，也定义了相同的函数来读写数据，唯一不同的就是一
些参数和返回值的类型。

除了读写byte类型数据的函数，ByteBuffer的一个特别之处是它还定义了读写其它primitive数据的方法，如：

int getInt() //从ByteBuffer中读出一个int值。
ByteBuffer putInt(int value) // 写入一个int值到ByteBuffer中。
读写其它类型的数据牵涉到字节序问题，ByteBuffer会按其字节序（大字节序或小字节序）写入或读出一个其它
类型的数据（int,long…）。字节序可以用order方法来取得和设置：
ByteOrder order() //返回ByteBuffer的字节序。
ByteBuffer order(ByteOrder bo) // 设置ByteBuffer的字节序。

ByteBuffer另一个特别的地方是可以在它的基础上得到其它类型的buffer。如：
CharBuffer asCharBuffer()
为当前的ByteBuffer创建一个CharBuffer的视图。在该视图buffer中的读写操作会按照ByteBuffer的字节
序作用到ByteBuffer中的数据上。

用这类方法创建出来的buffer会从ByteBuffer的position位置开始到limit位置结束，可以看作是这段数据
的视图。视图buffer的readOnly属性和direct属性与ByteBuffer的一致，而且也只有通过这种方法，才可
以得到其他数据类型的direct buffer。





https://my.oschina.net/u/2416019/blog/607290