@echo off
echo 开始代码更新过程...

REM 创建配置文件备份
echo 创建配置文件备份...
copy jeecg-boot\jeecg-module-system\jeecg-system-start\src\main\resources\application-dev.yml config_backup.yml

REM 先丢弃配置文件的本地修改，防止git pull冲突
echo 丢弃配置文件的本地修改...
git checkout -- jeecg-boot\jeecg-module-system\jeecg-system-start\src\main\resources\application-dev.yml

REM 拉取最新代码
echo 拉取最新代码...
git pull

REM 恢复备份的配置文件
echo 恢复本地配置文件...
copy config_backup.yml jeecg-boot\jeecg-module-system\jeecg-system-start\src\main\resources\application-dev.yml

echo 代码更新完成!
pause 