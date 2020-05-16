# webpack run dev后并没有生成dist目录，但是浏览器里却读取了dist里的build.js？

问题对人有帮助，内容完整，我也想知道答案0问题没有实际价值，缺少关键内容，没有改进余地

最近刚学vue + webpack
用vue init webpack-simple my-project 新建了一个vue + webpack的目录
npm install后就npm run dev了
浏览器自动启动后source里怎么会有dist/build.js？？？
本地里也没有生成这个目录啊！！！
npm run build就有dist了
问题：

1. 这个run dev 和 run build有什么区别？
2. 本地没有dist目录，但是浏览器却读取了dist目录的内容，这个东西要怎么理解？

## 回答

run dev 运行的命令是:

```
"dev": "cross-env NODE_ENV=development webpack-dev-server --open --hot",
```

这里是用 [https://github.com/webpack/we...](https://github.com/webpack/webpack-dev-server) 运行的页面.

webpack-dev-server 的介绍如下:

```
It uses webpack-dev-middleware under the hood, which provides fast in-memory 
access to the webpack assets.

它使用引擎盖下的WebPACK DEV中间件，提供快速内存。
访问WebPACK资产。
```



webpack-dev-middleware 的介绍如下:

```
No files are written to disk, it handle the files in memory
```





https://segmentfault.com/q/1010000009393240/a-1020000009393882