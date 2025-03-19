@echo off
chcp 65001 > nul
echo ======================================================================
echo                 JeecgBoot 一键启动脚本
echo ======================================================================
echo.
echo 此脚本将帮助您一键启动JeecgBoot项目
echo.
echo 请选择要执行的操作:
echo 1. 初始化数据库
echo 2. 启动后端服务 (需要Maven)
echo 3. 启动前端服务
echo 4. 一键启动全部(先后端后前端)
echo 5. 启动后端服务 (简化版，无需Maven)
echo 6. 启动前端服务 (简化版，更多错误处理)
echo 7. 一键启动全部 (简化版)
echo 8. 退出
echo.

set /p choice=请输入选项(1-8): 

if "%choice%"=="1" (
    call init-database.bat
    goto :menu
)

if "%choice%"=="2" (
    start cmd /k call start-backend.bat
    goto :menu
)

if "%choice%"=="3" (
    start cmd /k call start-frontend.bat
    goto :menu
)

if "%choice%"=="4" (
    echo [信息] 正在启动后端服务...
    start cmd /k call start-backend.bat
    
    echo [信息] 等待后端服务启动...
    timeout /t 30
    
    echo [信息] 正在启动前端服务...
    start cmd /k call start-frontend.bat
    
    echo [信息] 全部服务启动完成
    echo [信息] 后端地址: http://localhost:8080/jeecg-boot
    echo [信息] 前端地址: http://localhost:3100/
    echo [信息] 默认账号: admin
    echo [信息] 默认密码: 123456
    
    goto :menu
)

if "%choice%"=="5" (
    start cmd /k call start-backend-simple.bat
    goto :menu
)

if "%choice%"=="6" (
    start cmd /k call start-frontend-simple.bat
    goto :menu
)

if "%choice%"=="7" (
    echo [信息] 正在启动后端服务 (简化版)...
    start cmd /k call start-backend-simple.bat
    
    echo [信息] 等待后端服务启动...
    timeout /t 30
    
    echo [信息] 正在启动前端服务 (简化版)...
    start cmd /k call start-frontend-simple.bat
    
    echo [信息] 全部服务启动完成
    echo [信息] 后端地址: http://localhost:8080/jeecg-boot
    echo [信息] 前端地址: http://localhost:3100/
    echo [信息] 默认账号: admin
    echo [信息] 默认密码: 123456
    
    goto :menu
)

if "%choice%"=="8" (
    exit
) else (
    echo 无效的选项，请重新输入
    goto :menu
)

:menu
echo.
call start-all.bat 