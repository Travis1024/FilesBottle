/*
 Navicat Premium Data Transfer

 Source Server         : TianYi
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 140.246.171.8:3306
 Source Schema         : filesbottle_rms

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 28/06/2023 22:45:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rms_download_log
-- ----------------------------
DROP TABLE IF EXISTS `rms_download_log`;
CREATE TABLE `rms_download_log` (
  `down_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '下载记录自增 Id',
  `down_document_id` varchar(0) DEFAULT NULL COMMENT '下载文件 Id',
  `down_document_name` varchar(255) DEFAULT NULL COMMENT '下载文件名',
  `down_document_team` varchar(255) DEFAULT NULL COMMENT '下载文件所属团队',
  `down_person_id` varchar(0) DEFAULT NULL COMMENT '文件下载人Id',
  `down_person_name` varchar(255) DEFAULT NULL COMMENT '文件下载人名字',
  `down_time` datetime DEFAULT NULL COMMENT '文件下载时间',
  PRIMARY KEY (`down_zzid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of rms_download_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for rms_login_log
-- ----------------------------
DROP TABLE IF EXISTS `rms_login_log`;
CREATE TABLE `rms_login_log` (
  `login_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '登录记录 Id',
  `login_person_id` varchar(0) DEFAULT NULL COMMENT '登录人 Id',
  `login_person_name` varchar(255) DEFAULT NULL COMMENT '登录人名字',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `login_ip` varchar(255) DEFAULT NULL COMMENT '登录 ip',
  `login_way` varchar(255) DEFAULT NULL COMMENT '登录途径(app、web、小程序等)',
  PRIMARY KEY (`login_zzid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of rms_login_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for rms_view_log
-- ----------------------------
DROP TABLE IF EXISTS `rms_view_log`;
CREATE TABLE `rms_view_log` (
  `view_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '浏览记录自增 Id',
  `view_document_id` varchar(255) DEFAULT NULL COMMENT '浏览的文件 Id',
  `view_document_name` varchar(255) DEFAULT NULL COMMENT '浏览的文件名',
  `view_document_team` varchar(255) DEFAULT NULL COMMENT '浏览的文件所属团队Id',
  `view_person_id` varchar(11) DEFAULT NULL COMMENT '浏览人 Id',
  `view_person_name` varchar(255) DEFAULT NULL COMMENT '浏览人名字',
  `view_time` datetime DEFAULT NULL COMMENT '文件浏览时间',
  PRIMARY KEY (`view_zzid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of rms_view_log
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
