@echo off
chcp 65001 > nul
echo ======================================================================
echo             JeecgBoot 前端启动脚本 (简化版)
echo ======================================================================
echo.
echo 正在检查环境...

REM 检查Node.js环境
echo 检查Node.js环境...
node -v
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js环境，请安装Node.js 18+
    echo.
    echo 您可以从以下地址下载Node.js: https://nodejs.org/
    echo 推荐安装LTS版本
    goto :end
)

REM 检查npm环境
echo 检查npm环境...
npm -v
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

REM 检查package.json是否存在
if not exist "package.json" (
    echo [错误] 未找到package.json文件，请确认您在正确的目录中
    goto :end
)

REM 检查是否已安装依赖
if not exist "node_modules" (
    echo [信息] 首次运行，正在安装依赖...
    echo [信息] 这可能需要几分钟时间，请耐心等待...
    
    REM 尝试使用npm安装
    call npm install
    
    if %errorlevel% neq 0 (
        echo [警告] npm install失败，尝试使用cnpm安装...
        
        REM 检查是否安装了cnpm
        cnpm -v >nul 2>&1
        if %errorlevel% neq 0 (
            echo [信息] 正在安装cnpm...
            call npm install -g cnpm --registry=https://registry.npmmirror.com
        )
        
        call cnpm install
        
        if %errorlevel% neq 0 (
            echo [错误] 依赖安装失败
            echo [提示] 您可以尝试手动执行以下命令:
            echo npm install --registry=https://registry.npmmirror.com
            goto :end
        )
    )
)

echo [信息] 正在启动前端服务...
echo [信息] 执行命令: npm run dev
call npm run dev

if %errorlevel% neq 0 (
    echo [错误] 前端服务启动失败
    echo [提示] 您可以尝试手动执行以下命令:
    echo cd jeecgboot-vue3
    echo npm run dev
    goto :end
)

:end
pause 