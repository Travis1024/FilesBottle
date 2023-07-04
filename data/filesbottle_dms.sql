/*
 Navicat Premium Data Transfer

 Source Server         : TianYi
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 140.246.171.8:3306
 Source Schema         : filesbottle_dms

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 28/06/2023 22:45:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dms_document
-- ----------------------------
DROP TABLE IF EXISTS `dms_document`;
CREATE TABLE `dms_document` (
  `doc_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文档自增ID',
  `doc_name` varchar(255) NOT NULL COMMENT '文档名称',
  `doc_size` double NOT NULL COMMENT '文档大小',
  `doc_upload_date` datetime NOT NULL COMMENT '文档上传时间',
  `doc_md5` varchar(255) DEFAULT NULL COMMENT '文档md5(验证)',
  `doc_file_type_code` smallint(4) DEFAULT NULL COMMENT '文档类型码',
  `doc_content_type_text` varchar(255) NOT NULL COMMENT '文档类型',
  `doc_suffix` varchar(255) DEFAULT NULL COMMENT '文档后缀',
  `doc_description` varchar(255) DEFAULT NULL COMMENT '文档描述',
  `doc_gridfs_id` varchar(255) NOT NULL COMMENT '文档mongo管理的gridfs ID',
  `doc_preview_id` varchar(255) DEFAULT NULL COMMENT '提供预览文档的ID',
  `doc_preview_url` varchar(255) DEFAULT NULL COMMENT 'kkFileView提供的预览URL',
  `doc_state` tinyint(4) DEFAULT NULL COMMENT '文档上传状态(等待、正在、成功、失败)',
  `doc_error_message` varchar(255) DEFAULT NULL COMMENT '文档错误信息',
  `doc_reviewing` tinyint(4) DEFAULT NULL COMMENT '文档审核状态(true、false)',
  `doc_prohibited_word` varchar(255) DEFAULT NULL COMMENT '文档违禁词列表',
  `doc_userid` varchar(255) NOT NULL COMMENT '文档所属用户ID',
  `doc_teamid` varchar(255) NOT NULL COMMENT '文档所属团队ID',
  `doc_property` varchar(16) NOT NULL COMMENT '文档开放性质',
  PRIMARY KEY (`doc_zzid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of dms_document
-- ----------------------------
BEGIN;
INSERT INTO `dms_document` VALUES (17, 'DOC语文成绩.doc', 0.048828125, '2023-04-17 10:40:42', 'b55915418facb21769ee5592f3c5b4b0', 4, 'application/msword', 'doc', '语文成绩方案', 'a87f3c3863ab4efc8b1313c38e0f0685', 'bf3ebff38df74218a2811b890bb9ab7d', NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (18, 'DOCX语文成绩.docx', 0.04457569122314453, '2023-04-17 10:44:19', '6bf41ec257b4a26c33eaed6143ef3af0', 5, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'docx', 'DOCX方案', '6b093595e57a4572aa65f26ffafcedba', '2376a673bce24e0989ce4a64859cc0fc', NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (19, 'PPT整体方案.ppt', 0.92919921875, '2023-04-17 10:46:02', '3c7398e80e3f9b3ccac93a3d25978801', 6, 'application/vnd.ms-powerpoint', 'ppt', 'PPT方案', '7c0761e9fd4e43908035479efc89405d', 'bf87a0781043485c9b556602cf7c804c', NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (20, 'PPTX整体方案.pptx', 0.5871849060058594, '2023-04-17 10:48:49', 'd459519247afad8faa99b40ee1af7e29', 7, 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 'pptx', 'PPTX方案', '2796786dacfb4b549777546f2ccd5902', '2c64a54f3db7410a9d12bc019379467d', NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (21, 'xlsx中国语文测试院系分组安排.xlsx', 0.012892723083496094, '2023-04-17 10:54:22', 'afd0a8b3ccdab6db2476ed9c93a885f5', 3, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx', 'xlsx语文分组', 'a94d85662f464a3eb56afffff761838b', '06fccae061fd4d5199b9d6b684d8177e', NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (22, 'xls中国语文测试院系分组安排.xls', 0.02685546875, '2023-04-17 10:59:51', 'd9a3afd1f0a25bc2c0eed358abc3eb9a', 2, 'application/vnd.ms-excel', 'xls', 'xls语文分组', 'ee9a2ca663134e0aa1ccefdf04e7481a', '82087baf1f9f4ba2b4805f496c74d58b', NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (23, '卖完不做&已结束.xlsx', 0.41460227966308594, '2023-04-17 11:04:35', '134ef050698024d0039216ae86797248', 3, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'xlsx', '商务信息表', 'd9bb7853985a48329bd8da9d9705a6f5', '61c4e60bf1364f9eb0f67e76775ea8c6', NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (36, 'sample_1280x720.mp4', 16.62837791442871, '2023-04-21 09:11:07', '4859ebd44eb1f044ef2e857a0d3400ff', 440, 'video/mp4', 'mp4', '测试视频流在线读取文件', 'cc09bc843a5d48ca8ee67faf46b87722', NULL, NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (37, 'sample4.mp3', 3.731107711791992, '2023-04-21 10:59:34', 'd50a546db1dad4067a9e2cfcc365c908', 436, 'audio/mpeg', 'mp3', '测试音频流在线读取', '5ed0ac35be6e4c1183885152887ebc2c', NULL, NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (40, 'sample1.wav', 20.539648056030273, '2023-04-21 11:15:17', 'f396e934f00bf1e0921d3f9f7f8c3c3d', 437, 'audio/wave', 'wav', '测试音频流在线读取', '53bdec17a5584f21bde0e900dcb4e983', NULL, NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (45, 'sample_1920x1080avi.avi', 9.450054168701172, '2023-04-24 22:16:02', '49c64d5d240cf9ef41a517dbed58a5fd', 352, 'video/x-msvideo', 'avi', '测试音频流在线读取', 'd7768d47f42043fb84c7e5e49c0ce129', NULL, NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
INSERT INTO `dms_document` VALUES (46, 'sample_960x400_ocean_with_audiomp4.mp4', 16.709230422973633, '2023-04-24 22:21:57', '14b631184bb5eecdd8c08a078fe3a3d4', 351, 'video/mp4', 'mp4', '测试音频流在线读取', '4a0c451cbc1f473a81edf3a145a5c806', NULL, NULL, NULL, NULL, NULL, NULL, 'travis', 'travisspace', 'private');
COMMIT;

-- ----------------------------
-- Table structure for dms_folder
-- ----------------------------
DROP TABLE IF EXISTS `dms_folder`;
CREATE TABLE `dms_folder` (
  `folder_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件夹自增 Id',
  `folder_name` varchar(255) NOT NULL COMMENT '文件夹名称',
  `folder_id` varchar(255) NOT NULL COMMENT '文件夹编号',
  `folder_creator` varchar(255) DEFAULT NULL COMMENT '文件夹创建人 Id',
  `folder_team` varchar(255) DEFAULT NULL COMMENT '文件夹所属团队 Id',
  `folder_parent_id` varchar(0) DEFAULT NULL COMMENT '父文件夹 Id',
  `folder_create_time` datetime DEFAULT NULL COMMENT '文件夹创建时间',
  `folder_layer` int(255) DEFAULT NULL COMMENT '文件夹层级',
  `folder_path` varchar(255) DEFAULT NULL COMMENT '文件夹路径',
  `folder_password` varchar(255) DEFAULT NULL COMMENT '文件夹密码',
  PRIMARY KEY (`folder_zzid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of dms_folder
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(128) NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AT transaction mode undo table';

-- ----------------------------
-- Records of undo_log
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
