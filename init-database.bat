@echo off
chcp 65001 > nul
echo ======================================================================
echo                 JeecgBoot 数据库初始化脚本
echo ======================================================================
echo.
echo 此脚本将帮助您初始化MySQL数据库
echo 请确保您已安装MySQL并已启动MySQL服务
echo.

set /p DB_HOST=请输入数据库主机地址 (默认localhost): 
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT=请输入数据库端口 (默认3306): 
if "%DB_PORT%"=="" set DB_PORT=3306

set /p DB_USER=请输入数据库用户名 (默认root): 
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASSWORD=请输入数据库密码: 

set /p DB_NAME=请输入要创建的数据库名 (默认jeecgboot): 
if "%DB_NAME%"=="" set DB_NAME=jeecgboot

echo.
echo [信息] 正在尝试创建数据库 %DB_NAME%...

REM 创建数据库
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

if %errorlevel% neq 0 (
    echo [错误] 创建数据库失败，请检查您的MySQL连接信息
    goto :end
)

echo [信息] 数据库创建成功
echo [信息] 正在导入数据...

REM 导入数据
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < jeecg-boot/db/jeecgboot-mysql-5.7.sql

if %errorlevel% neq 0 (
    echo [错误] 导入数据失败
    goto :end
)

echo [信息] 数据导入成功
echo [信息] 正在更新配置文件...

REM 更新配置文件中的数据库连接信息
powershell -Command "(Get-Content jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/resources/application-dev.yml) -replace 'url: jdbc:mysql://localhost:3306/.*\?', 'url: jdbc:mysql://%DB_HOST%:%DB_PORT%/%DB_NAME%?' -replace 'username: .*', 'username: %DB_USER%' -replace 'password: .*', 'password: %DB_PASSWORD%' | Set-Content jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/resources/application-dev.yml"

echo [信息] 数据库初始化完成

:end
pause 