- 技术表

| 技术                 | 说明                     | 版本信息 |
| -------------------- | ------------------------ | -------- |
| spring cloud alibaba | 微服务框架               |          |
| spring cloud gateway | 服务网关                 |          |
| spring boot          | springboot               |          |
| spring security      | spring安全框架           |          |
| mybatis-plus         | mybatis增强工具包        |          |
| redis                | redis缓存数据库          |          |
| redisson             | redis客户端              |          |
| nacos                | 服务注册、发现、配置中心 |          |
| mysql                | 持久层数据库             |          |
| jimuReport           | 积木报表                 |          |
| knife4j              | swagger增强UI实现        |          |
| jackson              | json工具库               |          |
| lombok               | 注解                     |          |
| rocketMQ             | 消息队列                 |          |
| druid                | jdbc连接池、监控组件     |          |
| hutool               | 常用工具包               |          |
| mongoDB              | 文件存储数据库           |          |
| elasticsearch        | es搜索                   |          |
| kkFileView           | 文件在线预览             |          |
| xxl-job              | 定时任务                 |          |
| sentinel             | 流量防控                 |          |



- 后端微服务

| 后端模块名               | 模块说明                      |      |
| ------------------------ | ----------------------------- | ---- |
| filesbottle-common       | 公共模块                      |      |
| filesbottle-gateway      | 网关模块                      |      |
| filesbottle-dependencies | 统一管理版本信息              |      |
| filesbottle-search       | 搜索模块（文件搜索+其他搜索） |      |
| filesbottle-report       | 报表模块                      |      |
| filesbottle-system       | 系统模块                      |      |
| filesbottle-member       | 人员管理模块                  |      |
| filesbottle-wxm          | 微信公众号模块                |      |
| filesbottle-document     | 文件管理模块                  |      |
| filesbottle-auth         | 登录管理模块                  |      |



- 前端

| 前端服务            | 服务说明         |      |
| ------------------- | ---------------- | ---- |
| filesbottle-user    | 用户web-UI       |      |
| filesbottle-admin   | 系统管理员web-UI |      |
| 微信小程序、app待定 |                  |      |





- 进度表

| 日期             | 完成情况                                                     | 计划实现 |
| ---------------- | ------------------------------------------------------------ | -------- |
| 2023-03-31       | 初始化模块、实现公共依赖、跨域处理                           |          |
| 2023-04-01       | 通用返回、状态码定义                                         |          |
| 2023-04-02       | knife4j聚合、nacos配置                                       |          |
| 2023-04-03       | knife4j聚合、sso登录                                         |          |
| 2023-04-04       | sso登录、设计数据库                                          |          |
| 2023-04-05       | mybatisplus逆向生成、完善tokenjwt                            |          |
| 2023-04-06       | 完成鉴权、nacos整合dubbo                                     |          |
| 2023-04-07       | 对鉴权进行修改、简单压测                                     |          |
| 2023-04.08—04.14 | Elastic search、seats、kibana、itextpdf、poi集成<br />文件下载、文件上传、文件预览功能实现 |          |
| 2023-04-15       | 完成对ppt、pptx到pdf的转换                                   |          |
| 2023-04-16       | （是否采用上述转换未决定）可能会考虑使用jodconterver         |          |
| 2023-04-17       | 完成对jodconterver的实现                                     |          |
| 2023-04-18       | 集成并部署kkFileView                                         |          |
| 2023-04-19       | 部署kkFileView +完成文件删除服务                             |          |

