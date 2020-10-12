/*
Navicat MySQL Data Transfer

Source Server         : mybatis
Source Server Version : 50155
Source Host           : localhost:3307
Source Database       : boot_doc

Target Server Type    : MYSQL
Target Server Version : 50155
File Encoding         : 65001

Date: 2020-07-08 16:44:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for doc_file
-- ----------------------------
DROP TABLE IF EXISTS `doc_file`;
CREATE TABLE `doc_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `size` varchar(20) DEFAULT NULL,
  `master` varchar(20) DEFAULT NULL,
  `visible` varchar(255) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `updatetime` date DEFAULT NULL,
  `recycle` int(20) unsigned zerofill DEFAULT NULL COMMENT '0 normal 1 recycling',
  `recycletime` date DEFAULT NULL,
  `days` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of doc_file
-- ----------------------------
INSERT INTO `doc_file` VALUES ('26', '2019', '9KB', 'admin', '', 'xlsx', 'd6dcca3f-fae1-416d-aefd-4127616f2b0c.xlsx', '2020-01-04', '00000000000000000000', null, null);
INSERT INTO `doc_file` VALUES ('30', 'MySQL实验指导书7 综合练习1', '33KB', 'admin', null, 'docx', '18bae7e4-6637-4231-a183-9fad938e0e39.docx', '2020-01-05', '00000000000000000000', null, null);
INSERT INTO `doc_file` VALUES ('31', 'e2005aafa40f4bfbf594dd2e0c4f78f0f7361801', '36KB', 'admin', null, 'jpg', 'f4f3a9a9-608f-442c-8817-36c649e5e67c.jpg', '2020-01-06', '00000000000000000000', null, null);
INSERT INTO `doc_file` VALUES ('35', '2222', '9KB', 'myuser2', '', 'xlsx', '2f49c54c-4987-4b3d-ab5a-ca4788de8315.xlsx', '2020-01-07', '00000000000000000000', null, null);
INSERT INTO `doc_file` VALUES ('38', '微信冻结', '8KB', 'myuser2', null, 'jpg', '052ba43d-edfb-4907-b192-cdc03601c6a2.jpg', '2020-01-07', '00000000000000000000', null, null);
INSERT INTO `doc_file` VALUES ('42', '支付宝挂失', '7KB', 'admin', null, 'jpg', 'ff2c21fe-3b3c-4efb-a0b7-d70fae4cdc02.jpg', '2020-01-07', '00000000000000000000', null, null);
INSERT INTO `doc_file` VALUES ('54', '百度云电影集合', '2KB', 'user', ',5,', 'txt', '35abdea3-0f0a-4660-9fe2-f60ade8ad44a.txt', '2020-07-07', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('55', '宣传单页', '36KB', 'user', null, 'png', '885b50c3-8d4b-4f1d-aa36-f98e3a02e2b5.png', '2020-07-08', '00000000000000000001', '2020-07-08', '-1');
INSERT INTO `doc_file` VALUES ('57', 'cf53f3deb48f8c543ac7875535292df5e0fe7fb2', '78KB', 'user', null, 'jpg', '82098c7b-5db3-41ed-9241-43f6d2d5cb0c.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '-1');
INSERT INTO `doc_file` VALUES ('58', '2e31d609b3de9c825b5c9c866781800a19d84363', '92KB', 'user', null, 'jpg', 'da70a1a1-277f-4df1-9207-646c11cd85e6.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('59', 'timg', '33KB', 'user', null, 'jpg', 'c35caa5e-7293-4d02-a1d6-7e1eec2a14be.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('60', 'hhh', '100KB', 'user', null, 'jpg', '21a0e0de-3698-43b3-915b-d5b577a69805.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('61', '百度云电影集合', '2KB', 'user', null, 'txt', '512243cb-c1b2-4301-931e-bc6c67394d35.txt', '2020-07-08', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('62', 'timg', '33KB', 'user', null, 'jpg', 'ea06c368-bb3f-4ce2-983f-d3c778657bf6.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('63', 'cf53f3deb48f8c543ac7875535292df5e0fe7fb2', '78KB', 'user', null, 'jpg', 'a2f2faf2-2892-4f09-bf74-f05d610b4a8e.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('64', 'e2005aafa40f4bfbf594dd2e0c4f78f0f7361801', '36KB', 'user', null, 'jpg', '96432415-92ec-46c1-8c4e-177cab7341e9.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '0');
INSERT INTO `doc_file` VALUES ('65', 'cf53f3deb48f8c543ac7875535292df5e0fe7fb2', '78KB', 'user', null, 'jpg', 'be54ce41-2215-43d8-a5cb-8f97dc42e407.jpg', '2020-07-08', '00000000000000000001', '2020-07-08', '0');

-- ----------------------------
-- Table structure for doc_relation
-- ----------------------------
DROP TABLE IF EXISTS `doc_relation`;
CREATE TABLE `doc_relation` (
  `id1` int(11) DEFAULT NULL,
  `id2` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of doc_relation
-- ----------------------------
INSERT INTO `doc_relation` VALUES ('1', '2');
INSERT INTO `doc_relation` VALUES ('2', '3');
INSERT INTO `doc_relation` VALUES ('5', '6');
INSERT INTO `doc_relation` VALUES ('3', '7');
INSERT INTO `doc_relation` VALUES ('7', '5');
INSERT INTO `doc_relation` VALUES ('16', '7');
INSERT INTO `doc_relation` VALUES ('5', '7');
INSERT INTO `doc_relation` VALUES ('7', '10');
INSERT INTO `doc_relation` VALUES ('7', '12');

-- ----------------------------
-- Table structure for doc_syfile
-- ----------------------------
DROP TABLE IF EXISTS `doc_syfile`;
CREATE TABLE `doc_syfile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `size` varchar(20) DEFAULT NULL,
  `master` varchar(20) DEFAULT NULL,
  `visible` varchar(255) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `updatetime` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of doc_syfile
-- ----------------------------
INSERT INTO `doc_syfile` VALUES ('17', '关于增设宿舍自习室的修改过程', '12KB', 'admin', null, 'docx', 'b2f4eaf4-61fb-4344-a34a-62276124066f.docx', '2020-07-07');
INSERT INTO `doc_syfile` VALUES ('21', 'cf53f3deb48f8c543ac7875535292df5e0fe7fb2', '78KB', 'admin', null, 'jpg', 'fabfe190-4841-45d1-b6f0-c6ddfb7894ec.jpg', '2020-07-08');

-- ----------------------------
-- Table structure for doc_user
-- ----------------------------
DROP TABLE IF EXISTS `doc_user`;
CREATE TABLE `doc_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `tel` varchar(20) DEFAULT NULL,
  `intime` date DEFAULT NULL,
  `right1` int(5) DEFAULT NULL,
  `head` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of doc_user
-- ----------------------------
INSERT INTO `doc_user` VALUES ('3', 'admin', '123123', '78576', '2019-09-09', '0', 'avatar0.jpg');
INSERT INTO `doc_user` VALUES ('5', 'user1', '123123', '136321321', '2019-09-09', '1', 'avatar0.jpg');
INSERT INTO `doc_user` VALUES ('7', 'user', '123123', '190909090', '2019-09-09', '1', '86637a8e-f969-433d-a000-cf1c53115827.jpg');
INSERT INTO `doc_user` VALUES ('10', '我我我', '123123', '1772312312', '2020-01-04', '1', '0e93e401-3b67-4bc1-a750-0725176fd858.jpg');
INSERT INTO `doc_user` VALUES ('16', 'myuser2', '111111', '123123123123', '2020-01-07', '1', '0df4a0eb-6caf-4b8a-a6d6-9f1445352a7e.jpg');
INSERT INTO `doc_user` VALUES ('18', 'admin1', '111111', '1772312312', '2020-01-07', '0', 'avatar0.jpg');
INSERT INTO `doc_user` VALUES ('19', 'admin2', '111111', '1772312312', '2020-01-07', '0', 'avatar0.jpg');
INSERT INTO `doc_user` VALUES ('27', 'tset1', '123123', '', '2020-07-08', '1', 'avatar0.jpg');

-- ----------------------------
-- Table structure for share_code
-- ----------------------------
DROP TABLE IF EXISTS `share_code`;
CREATE TABLE `share_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `docid` int(11) NOT NULL,
  `code` varchar(255) NOT NULL,
  `time` date NOT NULL,
  `days` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of share_code
-- ----------------------------

-- ----------------------------
-- Table structure for statistics
-- ----------------------------
DROP TABLE IF EXISTS `statistics`;
CREATE TABLE `statistics` (
  `theday` date NOT NULL,
  `upload` int(255) DEFAULT NULL,
  `download` int(255) DEFAULT NULL,
  PRIMARY KEY (`theday`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of statistics
-- ----------------------------
INSERT INTO `statistics` VALUES ('2019-10-20', '1', '2');
INSERT INTO `statistics` VALUES ('2019-12-27', '1', '0');
INSERT INTO `statistics` VALUES ('2019-12-28', '1', '0');
INSERT INTO `statistics` VALUES ('2019-12-29', '1', '0');
INSERT INTO `statistics` VALUES ('2019-12-30', '1', '0');
INSERT INTO `statistics` VALUES ('2019-12-31', '1', '0');
INSERT INTO `statistics` VALUES ('2020-01-02', '1', '0');
INSERT INTO `statistics` VALUES ('2020-01-03', '2', '0');
INSERT INTO `statistics` VALUES ('2020-01-04', '3', '3');
INSERT INTO `statistics` VALUES ('2020-01-05', '3', '10');
INSERT INTO `statistics` VALUES ('2020-01-06', '3', '2');
INSERT INTO `statistics` VALUES ('2020-01-07', '7', '4');
INSERT INTO `statistics` VALUES ('2020-07-03', '1', '0');
INSERT INTO `statistics` VALUES ('2020-07-05', '11', '9');
INSERT INTO `statistics` VALUES ('2020-07-07', '20', '0');
INSERT INTO `statistics` VALUES ('2020-07-08', '15', '0');
