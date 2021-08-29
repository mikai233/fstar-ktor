# FSTAR KTOR 繁星课程表后端

此后端服务于[繁星课程表](https://github.com/mikai233/fstar-client)
为课程表前端提供 **更新** **消息推送** **成绩查询**等功能

## 技术选型

- JDK11
- Gradle 构建工具
- Kotlin 后端服务主语言
- Redis
- MySQL
- Ktor Web框架
- Ktorm ORM框架
- JWT 鉴权

## 部署

1. 配置文件为 **application.conf** 共同的配置（配置中environment可以配置加载dev或者prod配置） **application_dev.conf** 开发配置
   **application_prod.conf** 生产环境配置
2. 执行SQL建表
3. 七牛云配置（非必要，用于客户端上传文件使用）
4. 开启HTTPS访问需要配置服务器证书
5. 本项目采用Docker一键部署（手动部署执行Gradle Task **shadowJar**可生成Jar包）

## 其它

之前的后端服务是基于**SpringBoot**框架编写 这次的后端为了做兼容提供了之间的API接口