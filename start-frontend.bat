@echo off
chcp 65001 > nul
echo ======================================================================
echo                 JeecgBoot 前端启动脚本
echo ======================================================================
echo.
echo 正在检查环境...

REM 检查Node.js环境
echo 检查Node.js环境...
call node -v
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js环境，请安装Node.js 18+
    goto :end
)

REM 检查npm环境
echo 检查npm环境...
call npm -v
if %errorlevel% neq 0 (
    echo [错误] 未检测到npm环境
    goto :end
)

echo [信息] 环境检查通过

echo [信息] 当前目录: %cd%
echo [信息] 正在进入前端项目目录...

cd jeecgboot-vue3
if %errorlevel% neq 0 (
    echo [错误] 无法进入前端项目目录，请确认jeecgboot-vue3目录存在
    goto :end
)

echo [信息] 已进入前端项目目录: %cd%

REM 检查是否已安装依赖
if not exist "node_modules" (
    echo [信息] 首次运行，正在安装依赖...
    call npm install
    
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        goto :end
    )
)

echo [信息] 正在启动前端服务...
echo [信息] 执行命令: npm run dev
call npm run dev

if %errorlevel% neq 0 (
    echo [错误] 前端服务启动失败
    goto :end
)

:end
pause 