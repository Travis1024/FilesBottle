## 文瓶 — 团队文件共享平台

（微服务架构）文瓶—团队文档共享平台: 支持文件在线预览(70余种)、团队文件存储、文件共享等功能。



### 目录

​		1、[项目架构图](#index1)

​		2、[已支持预览的文件类型](#index2)

​		3、[技术框架表](#index3)

​		4、[项目代码结构](#index4)

​		5、[开发进度表](#index5)

​		6、[TODO](#index6)



---

### <span id='index1'>项目架构图</span>

![image-20230629110744194](https://travisnotes.oss-cn-shanghai.aliyuncs.com/mdpic/202306291107243.png)



### <span id='index2'>已支持预览的文件类型</span>

| 01    | 02   | 03   | 04   | 05    | 06   | 07   | 08   |
| ----- | ---- | ---- | ---- | ----- | ---- | ---- | ---- |
| xls   | xlsx | doc  | docx | ppt   | pptx | txt  | odt  |
| ott   | sxw  | rtf  | wpd  | xsi   | ods  | ots  | sxc  |
| csv   | tsv  | odp  | otp  | pdf   | html | png  | jpeg |
| jpg   | py   | java | cpp  | c     | xml  | php  | js   |
| json  | css  | mp4  | avi  | mov   | mp3  | wav  | flv  |
| xmind | bpmn | eml  | epub | obj   | 3ds  | stl  | ply  |
| gltf  | glb  | off  | 3dm  | fbx   | dae  | wrl  | 3mf  |
| ifc   | brep | step | iges | fcstd | bim  | dwg  | dxf  |
| md    | tif  | tiff | tga  | svg   | zip  | rar  | jar  |
| tar   | gzip | 7z   |      |       |      |      |      |



### <span id="index3">技术框架表</span>

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
| kibana               | 可视化                   |          |
| kkFileView           | 文件在线预览             |          |
| xxl-job              | 定时任务                 |          |
| sentinel             | 流量防控                 |          |
| seata                | 分布式事务               |          |



### <span id="index4">项目代码结构</span>

- 后端微服务

  | 后端模块名           | 模块说明                           | Mysql数据库     | 启动端口 |
  | -------------------- | ---------------------------------- | --------------- | -------- |
  | filesbottle-common   | 公共模块                           |                 |          |
  | filesbottle-gateway  | 网关模块                           |                 | 48080    |
  | filesbottle-search   | 搜索模块（文件搜索+其他搜索）      |                 |          |
  | filesbottle-report   | 报表模块 + 日志                    | filesbottle-rms | 48087    |
  | filesbottle-system   | 系统模块                           |                 |          |
  | filesbottle-member   | 人员管理模块                       | filesbottle-ums | 48083    |
  | filesbottle-wxm      | 微信公众号模块                     |                 |          |
  | filesbottle-document | 文件管理模块                       | filesbottle-dms | 48082    |
  | filesbottle-auth     | 登录管理模块                       |                 | 48081    |
  | filesbottle-ffmpeg   | ffmpeg视频转码切片模块（单独部署） |                 | 48086    |
  | filesbottle-knife4j  | knife4文档聚合模块                 |                 | 48079    |



- 前端计划模块

  | 前端服务            | 服务说明         |      |
  | ------------------- | ---------------- | ---- |
  | filesbottle-user    | 用户web-UI       |      |
  | filesbottle-admin   | 系统管理员web-UI |      |
  | 微信小程序、app待定 |                  |      |



### <span id="index5">开发进度表</span>

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
| 2023-04.08—04.14 | ElasticSearch、seata、kibana、itextpdf、poi集成<br />文件下载、文件上传、文件预览功能实现 |          |
| 2023-04-15       | 完成对ppt、pptx到pdf的转换                                   |          |
| 2023-04-16       | （是否采用上述转换未决定）可能会考虑使用jodconterver         |          |
| 2023-04-17       | 完成对jodconterver的实现                                     |          |
| 2023-04-18       | 集成并部署kkFileView                                         |          |
| 2023-04-19       | 部署kkFileView + 完成文件删除服务                            |          |
| 2023-04-20       | 完成ElasticSearch文件搜索功能                                |          |
| 2023-04-21       | 实现ffmpeg的转码，计划单独部署ffmpeg转码的模块               |          |
| 2023-04-22       | 在服务器中搭建nginx实现m3u8+ts切片的读取                     |          |
| 2023-04-23       | 继续完成 nginx 的部署 + ffmpeg 模块的部署                    |          |
| 2023-06-30       | 考虑采用 minio 替换 gridfs                                   |          |
| 2023-07-03       | 对视频切片读取请求加鉴权（两种方式：nginx 启用 auth_request、无需 nginx 模块直接返回文件流[此时已经在 gateway 中完成鉴权]） |          |
| 2023-07-05       | 完成 hls 加密防止视频泄露（采用华为云同步方法：二次加密+一次性 token） |          |



### <span id='index6'>TODO</span>

#### Gateway

- [x] 微服务路由分发（routers）
- [x] 跨域配置
- [x] 鉴权过滤器
  - [x] 设置鉴权白名单（无需鉴权 URL 路径集合）
  - [x] 通过 Dubbo 携带 token 向 Auth 模块请求鉴权

#### Auth

- [x] 实现 JWT、Token 相关工具包

- [x] 接收 Gateway token，对 token 进行鉴权，返回鉴权结果
- [x] 处理用户登录、用户退出、令牌刷新请求（完成redis缓存、mysql数据库读等其他附加动作）
- [x] 实现 nginx auth_request 鉴权请求服务
- [x] 实现一次性令牌生成功能，并完成缓存（供 ffmpeg 模块使用）
- [x] 实现对 hls-Token 鉴权功能，实现 hls 加密

#### Document

- [x] 团队文档下载、删除、新增等（MongoDB-GridFs + Mysql）
- [x] 异步执行生成预览文件（线程池）、更新 ElasticSearch 分词数据任务
  - [x] （四种预览处理任务分类）
  - [x]  JODConverter 实现将部分文件转 PDF 进行在线预览（依赖 LibreOffice）
  - [x] 源文件数据直接在线预览
  - [x] KkFileView 实现部分文件在线预览
  - [x] Ffmpeg + hls + nginx 实现视频切片 + 视频在线观看
- [x] 根据关键词 ElasticSearch 搜索相关文档
- [x] Dubbo 实现 视频文件密钥（enc.key）管理接口
- [ ] 实现团队文档-文件夹管理
- [ ] 考虑将 GridFs 转为 Minio 进行分布式文件存储
- [ ] 文档预览生成在线预览链接（提供分享）

#### Ffmpeg

- [x] 接收多种格式视频文件，转换格式为 MP4，生成 HLS 密钥及信息文件，进行视频切片存储
- [x] nginx-rtmp 搭建，实现 ffmpeg + hls + nginx-rtmp 的视频在线观看与存储功能
- [x] 实现视频在线观看 URL 获取接口
- [x] 视频文件删除，视频切片删除

#### Member

- [ ] 入驻团队管理、团队级别管理
- [ ] 团队成员、管理员个人信息管理
- [ ] 用户角色管理

#### Report

- [ ] 文件浏览记录日志
- [ ] 用户登录记录日志
- [ ] 文件下载记录日志

#### 

