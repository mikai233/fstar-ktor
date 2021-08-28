# FSTAR KTOR 繁星课程表后端

此后端服务于[繁星课程表](https://github.com/mikai233/fstar-client)
为课程表前端提供 **更新** **消息推送** **成绩查询**等功能

## 技术选型

- Gradle 构建工具
- Kotlin 后端服务主语言
- Redis
- MySQL
- 采用Kotlin Ktor Web框架
- ORM框架采用Ktorm
- 鉴权JWT

## 部署

1. 配置文件为 **application.conf**共同的配置（配置中environment可以配置加载dev或者prod配置） **application_dev.conf**开发配置
   **application_prod.conf**生产环境配置
2. 执行SQL建表
3. 七牛云配置（非必要，用于客户端上传文件使用）
4. 配置服务器证书

## 其它

**application_prod**在版本控制中没有提供，请自行添加 之前的后端服务是基于**SpringBoot**框架编写 这次的后端服务为了兼容老的客户端API做了一些取舍