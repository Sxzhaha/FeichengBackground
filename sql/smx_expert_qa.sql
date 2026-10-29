-- ----------------------------
-- 专家问答模块建表脚本（开发者：smx）
-- 库名：bin_zhou
-- ----------------------------

CREATE TABLE `smx_expert_question` (
  `question_id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `user_id` bigint NOT NULL COMMENT '提问用户ID',
  `user_name` varchar(64)  DEFAULT NULL COMMENT '提问用户昵称',
  `user_avatar` varchar(255) DEFAULT NULL COMMENT '提问用户头像',
  `crop` varchar(32)  DEFAULT NULL COMMENT '作物名称',
  `title` varchar(128) NOT NULL COMMENT '问题标题',
  `content` varchar(1024) DEFAULT NULL COMMENT '问题描述',
  `images` varchar(2048) DEFAULT NULL COMMENT '问题图片URL，JSON数组',
  `ai_result` text NULL COMMENT 'AI逐图识别结果JSON（results数组，含imageIndex/detectedCrop/diseaseName/confidence/description/advice）',
  `ai_status` char(1) DEFAULT '0' COMMENT 'AI识别状态：0识别中 1完成 2失败/无图',
  `view_count` bigint NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` bigint NOT NULL DEFAULT 0 COMMENT '回复数',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态：0待回答 1已回答',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志：0存在 2删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`question_id`),
  KEY `idx_smxq_user` (`user_id`),
  KEY `idx_smxq_crop` (`crop`),
  KEY `idx_smxq_create` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='专家问答-问题表';

CREATE TABLE `smx_expert_answer` (
  `answer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '回答ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `answer_user_id` bigint DEFAULT NULL COMMENT '回答用户ID',
  `expert_name` varchar(64) DEFAULT NULL COMMENT '专家姓名',
  `expert_title` varchar(64) DEFAULT NULL COMMENT '回答者职称（如高级农艺师）',
  `expert_org` varchar(128) DEFAULT NULL COMMENT '专家所属机构',
  `expert_avatar` varchar(255) DEFAULT NULL COMMENT '专家头像',
  `content` varchar(2048) NOT NULL COMMENT '回答内容',
  `images` varchar(2048) DEFAULT NULL COMMENT '回答图片URL，JSON数组',
  `is_adoption` char(1) DEFAULT '0' COMMENT '是否被采纳：0否 1是',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '点赞数',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志：0存在 2删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '回答时间',
  PRIMARY KEY (`answer_id`),
  KEY `idx_smxa_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='专家问答-回答表';

CREATE TABLE `smx_expert_crop` (
  `crop_id` bigint NOT NULL AUTO_INCREMENT COMMENT '作物ID',
  `category` varchar(32) NOT NULL COMMENT '分类：粮食作物/蔬菜/水果/其他',
  `crop` varchar(32) NOT NULL COMMENT '作物名称',
  `followers` bigint NOT NULL DEFAULT 0 COMMENT '关注人数',
  `source` varchar(16) NOT NULL DEFAULT 'system' COMMENT '来源：system系统内置，user用户新增',
  `audit_status` char(1) NOT NULL DEFAULT '1' COMMENT '审核状态：0待审核 1通过 2驳回',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `audit_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`crop_id`),
  UNIQUE KEY `uk_smx_crop` (`crop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='专家问答-作物圈子';

-- ----------------------------
-- 种子数据
-- ----------------------------
INSERT IGNORE INTO smx_expert_crop (category, crop, followers) VALUES
('粮食作物', '小麦', 108133), ('粮食作物', '水稻', 113385), ('粮食作物', '玉米', 142097),
('粮食作物', '大豆', 86542), ('粮食作物', '高粱', 32618), ('粮食作物', '谷子', 28456),
('粮食作物', '荞麦', 19735), ('粮食作物', '燕麦', 16382),
('蔬菜', '土豆', 84735), ('蔬菜', '大白菜', 109349), ('蔬菜', '黄瓜', 119572),
('蔬菜', '番茄', 121937), ('蔬菜', '辣椒', 135596), ('蔬菜', '萝卜', 76428),
('蔬菜', '胡萝卜', 68315), ('蔬菜', '菠菜', 59274), ('蔬菜', '茄子', 71846),
('蔬菜', '洋葱', 53682), ('蔬菜', '大蒜', 62157), ('蔬菜', '芹菜', 48736),
('蔬菜', '西葫芦', 45129),
('水果', '冬枣', 8652), ('水果', '苹果', 128436), ('水果', '梨', 93618),
('水果', '葡萄', 104275), ('水果', '桃', 81764), ('水果', '草莓', 113829),
('水果', '西瓜', 97643), ('水果', '柑橘', 88952), ('水果', '樱桃', 76241),
('其他', '棉花', 57483), ('其他', '花生', 84371), ('其他', '油菜', 69825),
('其他', '烟草', 23618), ('其他', '中药材', 45276);

INSERT INTO smx_expert_question (user_id, user_name, user_avatar, crop, title, content, images, view_count, reply_count, status) VALUES
(1, 'admin', '', '冬枣', '下面叶片发黄', '枣树下部叶片脉间失绿发黄，请老师诊断', NULL, 106, 1, '1'),
(1, 'admin', '', '冬枣', '定植后四天发现叶片发黄，各位老师帮看看出现了什么问题？', '定植后四天，新叶发黄卷曲', NULL, 99, 0, '0');

INSERT INTO smx_expert_answer (question_id, answer_user_id, expert_name, expert_org, expert_avatar, content, is_adoption) VALUES
(1, 2, '向日葵', '山东省冠县烟庄街道办事处农业技术推广站', '', '根系吸收障碍、缺铁、营养不足引起的生理性黄叶，可结合土壤墒情与叶片情况进一步判断。', '1');

CREATE TABLE `smx_expert_like` (
  `like_id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `target_type` char(1) NOT NULL COMMENT '目标类型：1问题 2回答',
  `target_id` bigint NOT NULL COMMENT '目标ID（问题ID或回答ID）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `uk_smxl_user_target` (`user_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='专家问答-点赞记录';
