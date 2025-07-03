<div align="center">
  <img style="width: 128px; height: 128px;" src="https://raw.githubusercontent.com/OpenListTeam/Logo/main/logo.svg" alt="logo" />

  <p><em>OpenList 是一个有韧性、长期治理、社区驱动的 AList 分支，旨在防御基于信任的开源攻击。</em></p>

  <img src="https://goreportcard.com/badge/github.com/OpenListTeam/OpenList/v3" alt="latest version" />
  <a href="https://github.com/OpenListTeam/OpenList/blob/main/LICENSE"><img src="https://img.shields.io/github/license/OpenListTeam/OpenList" alt="License" /></a>
  <a href="https://github.com/OpenListTeam/OpenList/actions?query=workflow%3ABuild"><img src="https://img.shields.io/github/actions/workflow/status/OpenListTeam/OpenList/build.yml?branch=main" alt="Build status" /></a>
  <a href="https://github.com/OpenListTeam/OpenList/releases"><img src="https://img.shields.io/github/release/OpenListTeam/OpenList" alt="latest version" /></a>

  <a href="https://github.com/OpenListTeam/OpenList/discussions"><img src="https://img.shields.io/github/discussions/OpenListTeam/OpenList?color=%23ED8936" alt="discussions" /></a>
  <a href="https://github.com/OpenListTeam/OpenList/releases"><img src="https://img.shields.io/github/downloads/OpenListTeam/OpenList/total?color=%239F7AEA&logo=github" alt="Downloads" /></a>
</div>

---

- [English](./README.md) | 中文 | [日本語](./README_ja.md) | [Dutch](./README_nl.md)

- [贡献指南](./CONTRIBUTING.md)
- [行为准则](./CODE_OF_CONDUCT.md)
- [许可证](./LICENSE)

## 功能

- [x] 多种存储
  - [x] 本地存储
  - [x] [阿里云盘](https://www.alipan.com)
  - [x] OneDrive / Sharepoint ([国际版](https://www.microsoft.com/en-us/microsoft-365/onedrive/online-cloud-storage), [中国](https://portal.partner.microsoftonline.cn), DE, US)
  - [x] [天翼云盘](https://cloud.189.cn)（个人、家庭）
  - [x] [GoogleDrive](https://drive.google.com)
  - [x] [123云盘](https://www.123pan.com)
  - [x] [FTP / SFTP](https://en.wikipedia.org/wiki/File_Transfer_Protocol)
  - [x] [PikPak](https://www.mypikpak.com)
  - [x] [S3](https://aws.amazon.com/s3)
  - [x] [Seafile](https://seafile.com)
  - [x] [又拍云对象存储](https://www.upyun.com/products/file-storage)
  - [x] [WebDAV](https://en.wikipedia.org/wiki/WebDAV)
  - [x] Teambition([中国](https://www.teambition.com), [国际](https://us.teambition.com))
  - [x] [分秒帧](https://www.mediatrack.cn)
  - [x] [和彩云](https://yun.139.com)（个人、家庭、群组）
  - [x] [YandexDisk](https://disk.yandex.com)
  - [x] [百度网盘](http://pan.baidu.com)
  - [x] [Terabox](https://www.terabox.com/main)
  - [x] [UC网盘](https://drive.uc.cn)
  - [x] [夸克网盘](https://pan.quark.cn)
  - [x] [迅雷网盘](https://pan.xunlei.com)
  - [x] [蓝奏云](https://www.lanzou.com)
  - [x] [蓝奏云优享版](https://www.ilanzou.com)
  - [x] [阿里云盘分享](https://www.alipan.com)
  - [x] [Google 相册](https://photos.google.com)
  - [x] [Mega.nz](https://mega.nz)
  - [x] [百度相册](https://photo.baidu.com)
  - [x] [SMB](https://en.wikipedia.org/wiki/Server_Message_Block)
  - [x] [115](https://115.com)
  - [x] [Cloudreve](https://cloudreve.org)
  - [x] [Dropbox](https://www.dropbox.com)
  - [x] [飞机盘](https://www.feijipan.com)
  - [x] [多吉云](https://www.dogecloud.com/product/oss)
  - [x] [Azure Blob Storage](https://azure.microsoft.com/products/storage/blobs)
- [x] 部署方便，开箱即用
- [x] 文件预览（PDF、markdown、代码、纯文本等）
- [x] 画廊模式下的图片预览
- [x] 视频和音频预览，支持歌词和字幕
- [x] Office 文档预览（docx、pptx、xlsx 等）
- [x] `README.md` 预览渲染
- [x] 文件永久链接复制和直接文件下载
- [x] 黑暗模式
- [x] 国际化
- [x] 受保护的路由（密码保护和认证）
- [x] WebDAV
- [x] Docker 部署
- [x] Cloudflare Workers 代理
- [x] 文件/文件夹打包下载
- [x] 网页上传（可允许访客上传）、删除、新建文件夹、重命名、移动和复制
- [x] 离线下载
- [x] 跨存储复制文件
- [x] 单文件多线程下载/流式加速

## 文档

- 📘 [文档与安装指南](https://docs.oplist.org)
- 📚 [备用文档站点](https://docs.openlist.team)

## 演示

N/A（待重建）

## 讨论

如有一般性问题请前往 [*Discussions*](https://github.com/OpenListTeam/OpenList/discussions) 讨论区，***Issues* 仅用于错误报告和功能请求。**

## 许可证

`OpenList` 是基于 [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.txt) 许可证的开源软件。

## 免责声明

- 本项目为免费开源软件，旨在通过网盘便捷分享文件，主要用于 Go 语言的下载与学习。
- 使用本软件时请遵守相关法律法规，严禁任何形式的滥用。
- 本软件基于官方 SDK 或 API 实现，未对其行为进行任何修改、破坏或干扰。
- 仅进行 HTTP 302 跳转或流量转发，不拦截、存储或篡改任何用户数据。
- 本项目与任何官方平台或服务提供商无关。
- 本软件按“原样”提供，不附带任何明示或暗示的担保，包括但不限于适销性或特定用途的适用性。
- 维护者不对因使用或无法使用本软件而导致的任何直接或间接损失负责。
- 您需自行承担使用本软件的所有风险，包括但不限于账号被封、下载限速等。
- 本项目遵循 [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.txt) 许可证，详情请参见 [LICENSE](./LICENSE) 文件。

## 联系我们

- [@GitHub](https://github.com/OpenListTeam)
- [Telegram 交流群](https://t.me/OpenListTeam)
- [Telegram 频道](https://t.me/OpenListOfficial)

## 贡献者

我们衷心感谢原项目 [AlistGo/alist](https://github.com/AlistGo/alist) 的作者 [Xhofe](https://github.com/Xhofe) 及所有其他贡献者。

感谢这些优秀的人：

[![Contributors](https://contrib.rocks/image?repo=OpenListTeam/OpenList)](https://github.com/OpenListTeam/OpenList/graphs/contributors)
