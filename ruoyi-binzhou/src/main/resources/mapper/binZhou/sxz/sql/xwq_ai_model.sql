/*
 Navicat Premium Data Transfer

 Source Server         : Mysql 8.0.32
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3307
 Source Schema         : bin_zhou

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 20/07/2026 21:36:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xwq_ai_model
-- ----------------------------
DROP TABLE IF EXISTS `xwq_ai_model`;
CREATE TABLE `xwq_ai_model`  (
  `model_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '唯一标识一个模型',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型名称',
  `model_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系统内部模型编号',
  `model_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型类型，字典：病虫害、虫情、物候期',
  `model_crop` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '适用作物，如小麦、玉米、水稻',
  `model_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型服务调用地址',
  `model_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '模型用途说明',
  `model_status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '模型状态：0 停用，1 启用',
  `model_a` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段1',
  `model_b` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段2',
  `model_c` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段3',
  `model_d` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段4',
  `model_e` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段5',
  `model_f` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段6',
  `model_g` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段7',
  `model_h` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段8',
  `model_i` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段9',
  `model_j` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备用字段10',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`model_id`) USING BTREE,
  UNIQUE INDEX `uk_model_code`(`model_code`) USING BTREE,
  INDEX `idx_model_name`(`model_name`) USING BTREE,
  INDEX `idx_model_type`(`model_type`) USING BTREE,
  INDEX `idx_model_crop`(`model_crop`) USING BTREE,
  INDEX `idx_model_status`(`model_status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9009 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '模型管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xwq_ai_model
-- ----------------------------
INSERT INTO `xwq_ai_model` VALUES (1, '默认虫情识别模型', 'PEST_DEFAULT', '虫情', '小麦', 'http://127.0.0.1:5002/predict_pest', '虫情识别模型接口，用于小麦虫情识别。接口接收 multipart/form-data 文件参数 file。', 1, '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', NULL, '', NULL, NULL);
INSERT INTO `xwq_ai_model` VALUES (9006, '小麦物候期测试模型', 'WHEAT_PHENOLOGY_TEST_V1', 'phenology', 'wheat', 'mock://phenology', '用于物候期识别功能联调的模拟模型', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', NULL, '', NULL, NULL);
INSERT INTO `xwq_ai_model` VALUES (9007, '枣树病虫害四分类模型', 'JUJUBE_EFFICIENTNET_B0_V1', '病虫害', '枣树', 'http://127.0.0.1:9002/predict', '本地 EfficientNet-B0 四分类模型：枣褐斑病、枣瘿蚊、健康叶片、枣蓟马', 1, 'best_model_strict_split.pth', 'confidence_threshold=0.65', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'codex', '2026-07-16 21:48:21', 'codex', '2026-07-16 21:50:33', '本地演示模型，仅监听 127.0.0.1');

SET FOREIGN_KEY_CHECKS = 1;
