# npm 全局包目录查看和修改


Node.js通过npm的-g命令可以将包保存在全局，让所有项目共享，但默认情况下，会保存在/usr/lib/node_modules目录下，造成根目录空间不足，实际生产中，需要设置到合适位置下。

查看全局包位置：

```sh
# 可以看到实际的位置
npm root -g
```

修改全局包位置

```sh
npm config set prefix '目标目录'
```

查看修改结果

```sh
npm config get prefix
# 或者用npm root -g命令也可
```

另，
已安装的可以通过卸载后重新intall的方式

卸载命令

```
npm uninstall -g xxx
```



npm update moduleName：更新node模块

npm rebuild moduleName

npm view moudleName dependencies：查看包的依赖关系

npm view moduleName repository.url：查看包的源文件地址

npm view moduleName engines：查看包所依赖的Node的版本

npm outdated：检查包是否已经过时，此命令会列出所有已经过时的包，可以及时进行包的更新





<https://www.iteye.com/blog/xwhuang-2309963>