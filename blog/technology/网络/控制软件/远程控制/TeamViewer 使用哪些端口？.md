[TOC]



# TeamViewer 使用哪些端口？

TeamViewer 无需任何特殊的防火墙配置即可轻松连接到远程计算机。在绝大多数情况下，只要可以上网，TeamViewer 始终可用。TeamViewer 与互联网对外连接时，通常不会受到防火墙屏蔽。

但是，在某些情况下，例如在具有严格安全政策的公司中，防火墙可能会阻止所有未知的外网连接，在这种情况下，您需要配置防火墙，允许 TeamViewer 通过防火墙与外部连接。

## TeamViewer 的端口

以下是 TeamViewer 需要使用的端口：

### TCP/UDP 端口 5938

TeamViewer 倾向于通过 **端口5938** 进行对外的 TCP 和 UDP 连接 — 这是TeamViewer的主要端口，并且 TeamViewer 在使用此端口时具有最佳性能。您的防火墙应至少允许使用此端口。

### TCP 端口 443

如果 TeamViewer 无法通过端口 5938 进行连接，接下来会尝试通过 TCP **端口 443** 进行连接。

但是，Android、iOS、Windows Mobile 和 BlackBerry 移动端上运行的TeamViewer不使用端口 443。

**注意**: 我们在管理控制台中创建的自定义模块也使用端口 443。如果您要通过群组策略部署自定义模块，则需要确保您要部署的计算机上的端口 443 打开。端口 443 还有其他用途，包括 TeamViewer 更新检查。

### TCP 端口 80

如果 TeamViewer 无法通过端口 5938 或 443 进行连接，则会尝试通过 TCP **端口 80** 进行连接。由于会产生了额外的开销（Overhead），并且如果连接断开也不会自动重新连接，通过此端口的连接速度比端口 5938 或 443 慢，可靠性也较低。因此，端口 80 仅作为最后备用选择。

Android、Windows Mobile 和 BlackBerry 上运行的TeamViewer不使用端口 80。但是，如果需要，我们的 iOS 应用程序可使用端口 80。

## Android和 Windows Mobile 

我们在 Android 和 Windows Mobile上运行的TeamViewer只能通过端口 5938 与外部连接。如果移动设备上的 TeamViewer 无法连接并提示您“检查网络连接”，可能是因为您的移动数据提供商或 WiFi 路由器/防火墙屏蔽了此端口。

## 目标 IP 地址

TeamViewer 软件可连接到我们遍布世界各地的主服务器。这些服务器使用多个不同的 IP 地址范围，这些地址范围也会经常发生变化。因此，我们无法提供我们的服务器 IP 列表。但是，我们的所有 IP 地址均有PTR 记录，并解析为 ***.teamviewer.com** 。您可以用它来确定可通过您的防火墙或代理服务器的目标 IP 地址。

虽然可以如此操作，但从安全的角度来看，这样完全没有必要 — TeamViewer 只能通过防火墙发起外部数据连接，因此只需防火墙屏蔽所有传入连接并仅允许通过端口 5938 进行对外连接即可，无论目标 IP 地址如何变化。

## 每个操作系统使用的端口一览表

|                | TCP/UDP 端口 5938 | TCP 端口 443 | TCP 端口 80 |
| -------------- | ----------------- | ------------ | ----------- |
| Windows        | x                 | x            | x           |
| macOS          | x                 | x            | x           |
| Linux          | x                 | x            | x           |
| ChromeOS       | x                 | x            | x           |
| iOS            | x                 |              | x           |
| Android        | x                 |              |             |
| Windows Mobile | x                 |              |             |





[https://community.teamviewer.com/t5/TeamViewer-Knowledge-Base-ZH/TeamViewer-%E4%BD%BF%E7%94%A8%E5%93%AA%E4%BA%9B%E7%AB%AF%E5%8F%A3/ta-p/33711#toc-hId-2043613849](https://community.teamviewer.com/t5/TeamViewer-Knowledge-Base-ZH/TeamViewer-使用哪些端口/ta-p/33711#toc-hId-2043613849)