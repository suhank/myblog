# ApplicationContextAware及InitializingBean及bean注入执行顺序

2018-05-20 01:30:25

 

1、 spring先检查注解注入的bean，并将它们实例化

2、 然后spring初始化bean的顺序是按照xml中配置的顺序依次执行构造

3、 如果某个类实现了ApplicationContextAware接口，会在类初始化完成后调用setApplicationContext（）方法进行操作

4、 如果某个类实现了InitializingBean接口，会在类初始化完成后，并在setApplicationContext（）方法执行完毕后，调用afterPropertiesSet（）方法进行操作







版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。

本文链接：<https://blog.csdn.net/liyantianmin/article/details/80379515>