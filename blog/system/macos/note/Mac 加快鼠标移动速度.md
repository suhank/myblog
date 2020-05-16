# Mac 加快鼠标移动速度

0.122018.05.21 14:43:03字数 120阅读 6,389

这段时间用Mac感觉鼠标移动速度太慢，设置->也调整到最高，又不想装软件，网上搜了下方法如下：

1、查看当前鼠标速度值

> defaults read -g com.apple.mouse.scaling

2、修改鼠标移动值

> defaults write -g com.apple.mouse.scaling 7.5

（数值你可以调 7---9，自己感觉速度，10 以上太快了，不建议）

 4、重启电脑 OK 享受MM完美速度







https://www.jianshu.com/p/5f012adcb358