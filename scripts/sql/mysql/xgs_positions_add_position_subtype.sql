-- 引进高层次人才子项。门户顶级类型仍用 position_type。
-- 本脚本只加可空列，不改已有岗位数据。
ALTER TABLE `xgs_positions`
  ADD COLUMN `position_subtype` varchar(100) DEFAULT NULL COMMENT '引进高层次人才子项' AFTER `position_type`;
