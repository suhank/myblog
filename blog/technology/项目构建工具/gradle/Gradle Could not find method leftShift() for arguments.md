# Gradle Could not find method leftShift() for arguments

2019年03月27日 09:46:33 [袁骗鬼](https://me.csdn.net/qq_30596077) 阅读数 2492 

```
task hello << {
     println 'Hello world!' 
    }
```

其中 << 在gradle 在5.1 之后废弃了   

 可以查看gradle 版本号

```
gradle -v
```

更改为 

```
task hello  {
    doLast{
      println 'Hello world!'
    } 
}
```

正常显示  





<https://blog.csdn.net/qq_30596077/article/details/88837029>