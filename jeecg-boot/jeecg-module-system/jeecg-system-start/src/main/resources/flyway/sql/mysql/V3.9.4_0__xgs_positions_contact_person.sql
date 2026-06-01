-- 招聘岗位：新增独立联系人姓名字段，与课题组(ktz)分离
ALTER TABLE `xgs_positions`
  ADD COLUMN `contact_person` varchar(100) DEFAULT NULL COMMENT '联系人姓名' AFTER `ktz`;
