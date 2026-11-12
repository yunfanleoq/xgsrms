-- 首页通知公告同步官网人才招聘：用来源标记区分旧启事，不删除历史行
ALTER TABLE `xgs_journalism`
    ADD COLUMN `source_code` varchar(64) DEFAULT NULL COMMENT '来源标记，官网人才招聘同步为 iie-cas-rczp' AFTER `type_code`,
    ADD UNIQUE INDEX `uk_xgs_journalism_source_ext` (`source_code`, `news_path`);
