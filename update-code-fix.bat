@echo off
chcp 936 > nul
echo Starting code update process...

echo Creating config backup...
copy jeecg-boot\jeecg-module-system\jeecg-system-start\src\main\resources\application-dev.yml config_backup.yml

echo Discarding local config changes...
git checkout -- "jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/resources/application-dev.yml"

echo Pulling latest code...
git pull

echo Restoring local config...
copy config_backup.yml jeecg-boot\jeecg-module-system\jeecg-system-start\src\main\resources\application-dev.yml

echo Code update complete!
pause 