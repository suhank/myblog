# maven多模块项目，子模块依赖父模块不写版本号时编译器报错

2017-12-07 10:11:06

 

出现版本号无法继承

解决办法：





在父模块的pom文件中给依赖加上依赖管理标签。

```html
    <dependencyManagement>
        <dependencies>
 
        </dependencies>
    </dependencyManagement>
```

即可解决版本无法继承的问题。









如果还有问题则在

子模块的dependencies上也要加上dependencyManagement



<https://blog.csdn.net/u014643282/article/details/78738015>