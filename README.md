## 文瓶 — 团队文件共享平台

（微服务架构）文瓶—团队文档共享平台: 支持文件在线预览(70余种)、团队文件存储、文件共享等功能。



### 目录

​		1、[项目架构图](#index0)

​		2、[已支持预览的文件类型](#index1)

​		3、[技术表](#index2)

​		4、[后端微服务模块](#index3)

​		5、[前端计划](#index4)

​		6、[进度表](#index5)



---

- **<span id='index0'>项目架构图</span>**

  ![image-20230629110744194](https://travisnotes.oss-cn-shanghai.aliyuncs.com/mdpic/202306291107243.png)



- **<span id='index1'>已支持预览的文件类型</span>**

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



- <span id="index2">技术表</span>

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



- <span id="index3">后端微服务</span>

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



- <span id="index4">前端计划表</span>

  | 前端服务            | 服务说明         |      |
  | ------------------- | ---------------- | ---- |
  | filesbottle-user    | 用户web-UI       |      |
  | filesbottle-admin   | 系统管理员web-UI |      |
  | 微信小程序、app待定 |                  |      |



- <span id="index5">进度表</span>

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
  
  

