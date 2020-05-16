[TOC]



# Scrcpy – 用电脑控制 Android 手机[Win/macOS/Linux]

[Android](https://www.appinn.com/category/android/) [macOS](https://www.appinn.com/category/mac/) [Windows](https://www.appinn.com/category/windows/) [精选](https://www.appinn.com/category/featured/) 2019/08/16 [青小蛙](https://www.appinn.com/author/qingwa/) [50](https://www.appinn.com/scrcpy-remote-android-from-computer/#comments)



[Scrcpy](https://www.appinn.com/scrcpy-remote-android-from-computer/) 是一款可以用电脑显示并控制 Android 手机的开源工具，支持 USB、Wi-Fi 两种方式连接，以及 Windows、macOS、Linux 三种操作系统，无需在手机安装任何应用，无需 root，但需要 adb 工具。 

## Scrcpy – 远程显示/控制 Android 手机

心血来潮搜了下 Vysor 替代品（之前是因为 Vysor 收费加上免费版显示质量不好就弃了）。搜到了一个开源替代品 [Scrcpy](https://github.com/Genymobile/scrcpy)，发现小众还没有介绍过，我搬运点 [Github 项目](https://github.com/Genymobile/scrcpy)页面的介绍过来。

![Scrcpy - 用电脑控制 Android 手机[Win/macOS/Linux] 2](image-202002161854/ea6dd9baa7673340caf2bb4a06f3a567b3f7a2dd.jpeg)

它专注于

- **轻量** (原生, 仅显示设备屏幕)
- **性能** (30~60fps)
- **质量** (1920×1080 及以上)
- **低延时** ([35~70ms](https://github.com/Genymobile/scrcpy/pull/646))
- **启动速度快** (1秒左右出画面)
- **非侵入性** (不需要在手机安装任何东西)

## 要求

- Android 5.0 以上
- 启用 adb 调试（即 USB 调试）

 

好了，我就翻译到这儿了，还有更多详细信息（比如快捷键控制之类），网上也有些教程，要么青蛙自己补充吧，哈哈，我懒 。

等等，我还可以提一下几个重要的优点：

- 电脑和手机剪贴板能交互，以及传文件
- Windows/MacOS/Linux 支持
- 可以录屏
- 支持 Wifi 控制

## 安装方法

### Linux

安装方法：[手动安装说明](https://github.com/Genymobile/scrcpy/blob/master/BUILD.md)。不用担心，作为 Linux 用户，不难。

### Windows

直接下载：

- [`scrcpy-win32-v1.10.zip`](https://github.com/Genymobile/scrcpy/releases/download/v1.10/scrcpy-win32-v1.10.zip)
  *(SHA-256: f98b400b3764404b33b212e9762dd6f1593ddb766c1480fc2609c94768e4a8e1)*
- [`scrcpy-win64-v1.10.zip`](https://github.com/Genymobile/scrcpy/releases/download/v1.10/scrcpy-win64-v1.10.zip)
  *(SHA-256: 95de34575d873c7e95dfcfb5e74d0f6af4f70b2a5bc6fde0f48d1a05480e3a44)*

### macOS

需要先安装 [Homebrew](https://brew.sh/)，然后在终端中运行：

```
brew install scrcpy
brew cask install android-platform-tools
```

## 使用方法

```
scrcpy
```

![Scrcpy - 用电脑控制 Android 手机[Win/macOS/Linux] 3](image-202002161854/2019-08-166-22-59.jpg!o)

就好了，就能看到你的手机屏幕了：

![Scrcpy - 用电脑控制 Android 手机[Win/macOS/Linux] 4](image-202002161854/2019-08-166-22-53.jpg!o)

就可以通过手机控制了，非常简单易用。

看同学们需求，再更新 Wi-Fi 连接方式。

## Wi-Fi 连接方式

注意，要使用 Wi-Fi，仍然需要每次先用 USB 连接手机一次，实用性还不是特别高。

1. 将设备连接到与计算机相同的 Wi-Fi
2. 获取手机 IP 地址（在设置→关于手机→状态）
3. 启用 TCP/IP 上的 adb：*adb tcpip 5555*
4. 连接到您的设备：*adb connect 手机IP：5555*
5. 拔下手机
6. 像往常一样运行 scrcpy

要切换回USB模式：*adb usb*



## 快捷键

| Action                                  | Shortcut                      | Shortcut (macOS)             |
| --------------------------------------- | ----------------------------- | ---------------------------- |
| Switch fullscreen mode                  | `Ctrl`+`f`                    | `Cmd`+`f`                    |
| Resize window to 1:1 (pixel-perfect)    | `Ctrl`+`g`                    | `Cmd`+`g`                    |
| Resize window to remove black borders   | `Ctrl`+`x` \| *Double-click¹* | `Cmd`+`x` \| *Double-click¹* |
| Click on `HOME                          | `Ctrl`+`h` \| *Middle-click*  | `Ctrl`+`h` \| *Middle-click* |
| Click on `BACK`                         | `Ctrl`+`b` \| *Right-click²*  | `Cmd`+`b` \| *Right-click²*  |
| Click on `APP_SWITCH`                   | `Ctrl`+`s`                    | `Cmd`+`s`                    |
| Click on `MENU`                         | `Ctrl`+`m`                    | `Ctrl`+`m`                   |
| Click on `VOLUME_UP`                    | `Ctrl`+`↑` *(up)*             | `Cmd`+`↑` *(up)*             |
| Click on `VOLUME_DOWN`                  | `Ctrl`+`↓` *(down)*           | `Cmd`+`↓` *(down)*           |
| Click on `POWER`                        | `Ctrl`+`p`                    | `Cmd`+`p`                    |
| Power on                                | *Right-click²*                | *Right-click²*               |
| Turn device screen off (keep mirroring) | `Ctrl`+`o`                    | `Cmd`+`o`                    |
| Rotate device screen                    | `Ctrl`+`r`                    | `Cmd`+`r`                    |
| Expand notification panel               | `Ctrl`+`n`                    | `Cmd`+`n`                    |
| Collapse notification panel             | `Ctrl`+`Shift`+`n`            | `Cmd`+`Shift`+`n`            |
| Copy device clipboard to computer       | `Ctrl`+`c`                    | `Cmd`+`c`                    |
| Paste computer clipboard to device      | `Ctrl`+`v`                    | `Cmd`+`v`                    |
| Copy computer clipboard to device       | `Ctrl`+`Shift`+`v`            | `Cmd`+`Shift`+`v`            |
| Enable/disable FPS counter (on stdout)  | `Ctrl`+`i`                    | `Cmd`+`i`                    |

```
¹Double-click on black borders to remove them.
¹双击黑色边框将其删除。
²Right-click turns the screen on if it was off, presses BACK otherwise.
²如果已关闭，请右键单击以打开屏幕，否则按BACK。
```

https://www.appinn.com/scrcpy-remote-android-from-computer/