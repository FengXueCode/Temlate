/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : kill_question

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 09/09/2025 10:11:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for peak
-- ----------------------------
DROP TABLE IF EXISTS `peak`;
CREATE TABLE `peak`  (
  `peak_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `question_bank_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time` int(11) NULL DEFAULT NULL COMMENT '答题用时（按秒计算）',
  `correct` int(11) NULL DEFAULT NULL COMMENT '正确数',
  `total` int(11) NULL DEFAULT NULL COMMENT '总题数',
  PRIMARY KEY (`peak_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of peak
-- ----------------------------
INSERT INTO `peak` VALUES ('1964991538475839489', '', '1964934017185685506', 0, 1, 0);
INSERT INTO `peak` VALUES ('1965216819992080385', '1964970914432954369', '1964934017185685506', 26, 3, 4);
INSERT INTO `peak` VALUES ('1965217219671502849', '', '1964934017185685506', 0, 1, 0);
INSERT INTO `peak` VALUES ('1965217363850702850', '', '1964934017185685506', 0, 2, 0);
INSERT INTO `peak` VALUES ('1965217412143919106', '', '1964934017185685506', 0, 0, 0);
INSERT INTO `peak` VALUES ('1965217996527906818', '', '1964934017185685506', 0, 0, 0);

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `question_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '题目id',
  `question_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '题目标题',
  `options` json NULL COMMENT '选项',
  `state` int(11) NULL DEFAULT 0 COMMENT '0单选 1多选 2 判断 3 填空',
  `answer` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '答案 判断0错1对',
  `analysis` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '解析',
  `question_bank_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '题库id',
  PRIMARY KEY (`question_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES ('1964972200691773442', '未来可能新增的功能', '[{\"title\": \"A\", \"content\": \"空教室查询\"}, {\"title\": \"B\", \"content\": \"酒桌骰子\"}, {\"title\": \"C\", \"content\": \"麻将记账\"}, {\"title\": \"D\", \"content\": \"图书馆选位\"}]', 1, '[\"空教室查询\",\"酒桌骰子\",\"麻将记账\",\"图书馆选位\"]', '如果你有什么想要的功能可以在个人设置-》联系客服和客服提议喔', '1964970876671635458');
INSERT INTO `question` VALUES ('1964972200561750017', '您觉得小程序对你有帮助嘛？', '[{\"title\": \"A\", \"content\": \"一点都没\"}, {\"title\": \"B\", \"content\": \"有一点\"}, {\"title\": \"C\", \"content\": \"有很大的帮助\"}, {\"title\": \"D\", \"content\": \"灰常大！\"}]', 0, '一点都没', '嗯…我想是有的叭嘿嘿', '1964970876671635458');
INSERT INTO `question` VALUES ('1964972200758882306', '是否想参与到小程序的研发中？', '[{\"title\": \"0\", \"content\": \"错\"}, {\"title\": \"1\", \"content\": \"对\"}]', 2, '对', '如果你想尝试给小程序添加新的功能，可以联系客服喔', '1964970876671635458');
INSERT INTO `question` VALUES ('1964972200888905730', '校训__、__(俩个下划线__表示一个空)', '[{\"title\": \"0\", \"content\": \"测试\"}]', 3, '[{\"content\":\"崇尚完美\",\"title\":\"答1\"},{\"content\":\"追求卓越\",\"title\":\"答2\"}]', '这个不会不知道叭', '1964970876671635458');
INSERT INTO `question` VALUES ('1964972264071901186', '您觉得小程序对你有帮助嘛？', '[{\"title\": \"A\", \"content\": \"一点都没\"}, {\"title\": \"B\", \"content\": \"有一点\"}, {\"title\": \"C\", \"content\": \"有很大的帮助\"}, {\"title\": \"D\", \"content\": \"灰常大！\"}]', 0, '一点都没', '嗯…我想是有的叭嘿嘿', '1964970914432954369');
INSERT INTO `question` VALUES ('1964972264139010049', '未来可能新增的功能', '[{\"title\": \"A\", \"content\": \"空教室查询\"}, {\"title\": \"B\", \"content\": \"酒桌骰子\"}, {\"title\": \"C\", \"content\": \"麻将记账\"}, {\"title\": \"D\", \"content\": \"图书馆选位\"}]', 1, '[\"空教室查询\",\"酒桌骰子\",\"麻将记账\",\"图书馆选位\"]', '如果你有什么想要的功能可以在个人设置-》联系客服和客服提议喔', '1964970914432954369');
INSERT INTO `question` VALUES ('1964972264206118914', '是否想参与到小程序的研发中？', '[{\"title\": \"0\", \"content\": \"错\"}, {\"title\": \"1\", \"content\": \"对\"}]', 2, '对', '如果你想尝试给小程序添加新的功能，可以联系客服喔', '1964970914432954369');
INSERT INTO `question` VALUES ('1964972264206118915', '校训__、__(俩个下划线__表示一个空)', '[{\"title\": \"0\", \"content\": \"测试\"}]', 3, '[{\"content\":\"崇尚完美\",\"title\":\"答1\"},{\"content\":\"追求卓越\",\"title\":\"答2\"}]', '这个不会不知道叭', '1964970914432954369');

-- ----------------------------
-- Table structure for question_bank
-- ----------------------------
DROP TABLE IF EXISTS `question_bank`;
CREATE TABLE `question_bank`  (
  `question_bank_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '题库id',
  `question_bank_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '题库名称',
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作者id',
  `author` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作者名称',
  `type` int(11) NULL DEFAULT 0 COMMENT '0 私人 1 共享',
  PRIMARY KEY (`question_bank_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_bank
-- ----------------------------
INSERT INTO `question_bank` VALUES ('1964970914432954369', '测试共享', '1964934017185685506', '作者大人', 1);

-- ----------------------------
-- Table structure for question_record
-- ----------------------------
DROP TABLE IF EXISTS `question_record`;
CREATE TABLE `question_record`  (
  `question_record_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `question_bank_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '题库id',
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `accomplish_array` json NULL COMMENT '已经做到题目id数组',
  `collect_array` json NULL COMMENT '以收藏的id数组',
  `error_array` json NULL COMMENT '错误id数组',
  PRIMARY KEY (`question_record_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_record
-- ----------------------------
INSERT INTO `question_record` VALUES ('1964970914508451842', '1964970914432954369', '1964934017185685506', '[]', '[]', '[]');

-- ----------------------------
-- Table structure for question_setting
-- ----------------------------
DROP TABLE IF EXISTS `question_setting`;
CREATE TABLE `question_setting`  (
  `question_setting_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `scope` int(11) NULL DEFAULT 0 COMMENT '刷题范围 0 全部 1 收藏 2 错误 3 未作',
  `type` int(11) NULL DEFAULT 0 COMMENT '筛选体系 0 全部 1 单选 2 多选 3 判断 4 填空',
  `mode` int(11) NULL DEFAULT 0 COMMENT '刷题模式 0 刷题模式 1 背题模式',
  `title_chaos` int(11) NULL DEFAULT 0 COMMENT '题目乱序 0 关闭 1开启',
  `option_chaos` int(11) NULL DEFAULT 0 COMMENT '选项乱序 0 关闭 1开启',
  `auto_switch` int(11) NULL DEFAULT 0 COMMENT '自动切题 0 关闭 1开启',
  `auto_collect` int(11) NULL DEFAULT 0 COMMENT '做错自动收藏 0 关闭 1开启',
  PRIMARY KEY (`question_setting_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_setting
-- ----------------------------
INSERT INTO `question_setting` VALUES ('1964989818219802625', '1964934017185685506', 0, 0, 0, 0, 0, 0, 1);

-- ----------------------------
-- Table structure for question_share
-- ----------------------------
DROP TABLE IF EXISTS `question_share`;
CREATE TABLE `question_share`  (
  `question_share_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `question_bank_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`question_share_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_share
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `nick_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `portrait` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头像路径',
  `open_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'openId',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1964934017185685506', '用户jUM8zNBKX9', NULL, 'NE4wbjlIR3hlTmIzdU9TNzl5alp2NDYwTG5Tbw==');

SET FOREIGN_KEY_CHECKS = 1;
