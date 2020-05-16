# px2rpx 工具包使用介绍



github地址 :https://github.com/aOrz/px2rpx



## 可以使用命令行工具:CLI tool

```
$ npm install -g px2rpx
$ px2rpx -o build src/*.wxss
  Usage: px2rpx [options] <file...>

  Options:

    -h, --help                      output usage information
    -V, --version                   output the version number
    -u, --rpxUnit [value]           set `rpx` unit value (default: 75)
    -x, --threeVersion [value]      whether to generate @1x, @2x and @3x version stylesheet (default: false)
    -r, --rpxVersion [value]        whether to generate rpx version stylesheet (default: true)
    -b, --baseDpr [value]           set base device pixel ratio (default: 2)
    -p, --rpxPrecision [value]      set rpx value precision (default: 6)
    -o, --output [path]             the output file dirname
```



使用demo: **注意文件名一定要是css结尾的文件才能生效**

```
 px2rpx -b 1 -u 0.5  -o build index.css
```

