/*
 Navicat Premium Data Transfer

 Source Server         : TianYi
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 140.246.171.8:3306
 Source Schema         : filesbottle_ums

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 28/06/2023 22:45:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ums_role
-- ----------------------------
DROP TABLE IF EXISTS `ums_role`;
CREATE TABLE `ums_role` (
  `role_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色自增ID',
  `role_id` tinyint(4) NOT NULL COMMENT '角色ID',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  PRIMARY KEY (`role_zzid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of ums_role
-- ----------------------------
BEGIN;
INSERT INTO `ums_role` VALUES (1, 1, '系统管理员');
INSERT INTO `ums_role` VALUES (2, 2, '团队创建者');
INSERT INTO `ums_role` VALUES (3, 3, '团队管理员');
INSERT INTO `ums_role` VALUES (4, 4, '团队用户');
COMMIT;

-- ----------------------------
-- Table structure for ums_team
-- ----------------------------
DROP TABLE IF EXISTS `ums_team`;
CREATE TABLE `ums_team` (
  `team_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '团队自增ID',
  `team_id` varchar(64) NOT NULL COMMENT '团队ID',
  `team_name` varchar(255) NOT NULL COMMENT '团队名称',
  `team_creator` varchar(255) DEFAULT NULL COMMENT '团队创建者',
  `team_create_time` datetime DEFAULT NULL COMMENT '团队创建时间',
  `team_level` tinyint(4) DEFAULT NULL COMMENT '团队级别',
  `team_people_number` int(11) DEFAULT NULL COMMENT '团队人数',
  `team_description` varchar(255) DEFAULT NULL COMMENT '团队描述信息',
  `team_enable` tinyint(4) DEFAULT NULL COMMENT '团队是否启用',
  `team_doc_public_number` int(11) DEFAULT NULL COMMENT '团队开放文档数量',
  `team_doc_private_number` int(11) DEFAULT NULL COMMENT '团队私有文档数量',
  PRIMARY KEY (`team_zzid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of ums_team
-- ----------------------------
BEGIN;
INSERT INTO `ums_team` VALUES (1, 'system', '系统管理员团队', 'admin', NULL, 1, 1, '系统管理员团队', 1, 0, 0);
INSERT INTO `ums_team` VALUES (2, 'travisspace', 'travis的团队', 'travis', NULL, 2, 1, 'travis的团队', 1, 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for ums_teamlevel
-- ----------------------------
DROP TABLE IF EXISTS `ums_teamlevel`;
CREATE TABLE `ums_teamlevel` (
  `teamlevel_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '团队级别自增ID',
  `teamlevel_id` tinyint(4) NOT NULL COMMENT '团队级别ID',
  `teamlevel_name` varchar(255) DEFAULT NULL COMMENT '团队级别名称',
  `teamlevel_max_people_number` int(255) DEFAULT NULL COMMENT '当前团队级别最大人数限制',
  `teamlevel_max_storage_space` bigint(255) DEFAULT NULL COMMENT '当前团队级别最大存储空间（MB）',
  PRIMARY KEY (`teamlevel_zzid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of ums_teamlevel
-- ----------------------------
BEGIN;
INSERT INTO `ums_teamlevel` VALUES (1, 1, '系统团队', 10, 1024);
INSERT INTO `ums_teamlevel` VALUES (2, 2, '普通团队', 20, 1024);
INSERT INTO `ums_teamlevel` VALUES (3, 3, '会员一级团队', 50, 2048);
INSERT INTO `ums_teamlevel` VALUES (4, 4, '会员二级团队', 80, 4096);
INSERT INTO `ums_teamlevel` VALUES (5, 5, '会员三级团队', 100, 10240);
COMMIT;

-- ----------------------------
-- Table structure for ums_user
-- ----------------------------
DROP TABLE IF EXISTS `ums_user`;
CREATE TABLE `ums_user` (
  `user_zzid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `user_name` varchar(32) NOT NULL COMMENT '用户名字',
  `user_password` varchar(255) NOT NULL COMMENT '用户密码',
  `user_role` tinyint(4) NOT NULL COMMENT '用户角色',
  `user_team` varchar(255) NOT NULL COMMENT '用户所属团队',
  `user_team_role` tinyint(4) NOT NULL COMMENT '用户团队角色',
  `user_enable` tinyint(4) NOT NULL COMMENT '用户是否启用',
  `user_banning` tinyint(4) NOT NULL COMMENT '用户封禁状态',
  `user_gender` tinyint(4) DEFAULT NULL COMMENT '用户性别',
  `user_create_time` datetime DEFAULT NULL COMMENT '用户创建时间',
  `user_login_time` datetime DEFAULT NULL COMMENT '用户上次登录时间',
  `user_pic_url` varchar(255) DEFAULT NULL COMMENT '用户头像图片地址',
  `user_phone` varchar(16) DEFAULT NULL COMMENT '用户手机号码',
  `user_email` varchar(32) DEFAULT NULL COMMENT '用户邮箱地址',
  `user_phone_hide` tinyint(4) DEFAULT NULL COMMENT '用户是否隐藏手机号',
  `user_email_hide` tinyint(4) DEFAULT NULL COMMENT '用户是否隐藏邮箱',
  `user_doc_public_number` int(11) DEFAULT NULL COMMENT '用户发布的公共文件数量',
  `user_doc_private_number` int(11) DEFAULT NULL COMMENT '用户发布的团队文件数量',
  PRIMARY KEY (`user_zzid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of ums_user
-- ----------------------------
BEGIN;
INSERT INTO `ums_user` VALUES (1, 'admin', '管理员', '$2a$10$KVFr3Se31ESmpMCYczlI1.GfkM2SFRT97kpPP0EUu0m5NVIOwqwYi', 1, 'system', 1, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0);
INSERT INTO `ums_user` VALUES (2, 'travis', 'guest', '$2a$10$OHyTK5/vKb4.bWb8udIDje9MbU/uw14uxPM5EGtdWLO2Si.7HLGJi', 2, 'travisspace', 2, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 44);
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
