Docker 是一个开源的应用容器引擎，docker 翻译过来就是搬运工～可以让开发者打包自己的应用及相关依赖到一个可移植的容器中，然后发布到任何支持 Docker 的机器上。
![image](https://note.youdao.com/yws/public/resource/c552324e76d455ee54bbc9b32e237f87/xmlnote/F70293A9CF344CB79B5BD8955ED2F0E8/10131)

了解 Docker，需要先知道三个概念：
1. Image（镜像）
2. Container（容器）
3. Repository（仓库）

Docker Daemon 其实就是 Docker 服务。可以通过 Docker 命令客户端发送相关 Docker 命令和 Docker 服务进行通信。

Docker Client 有两种：1. Docker 命令客户端，在终端输入相关 Docker 命令即可操作 Docker 引擎。2. REST API 客户端，一般会在应用程序中通过 REST API 和 Docker 引擎交互。

获取 Docker 镜像后，就能将其载入到 Docker Daemon 中，并运行 Docker 镜像中的程序，一般会先把程序打包到 Docker 镜像中，随后才能将 Docker 镜像交付给他人使用。

每次运行 Docker 镜像，就会启动一个 Docker 容器（Container），该容器会运行镜像中的程序。同一个 Docker 镜像理论上可以运行无数个 Docker 容器。

Docker 镜像注册中心（Registry）是 Docker 官方提供的用于存放公开、私有的 Docker 镜像的仓库。开发者可以通过 Docker Hub 拉取和推送镜像。并且因为 Docker 将 Docker Registry 开源了，所以我们可以获取它的镜像，在局域网内迅速搭建一套私有的 Docker 镜像注册中心。

Docker 优势：
1. 高效利用系统资源
2. 快速启动（秒级）
3. 一致的运行环境
4. 便于迁移、扩展、维护