-- 生产补跑：V3.9.4_0__xgs_positions_contact_person.sql
ALTER TABLE `xgs_positions`
  ADD COLUMN `contact_person` varchar(100) DEFAULT NULL COMMENT '联系人姓名' AFTER `ktz`;
