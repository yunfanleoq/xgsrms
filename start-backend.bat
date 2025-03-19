@echo off
chcp 65001 > nul
echo ======================================================================
echo                 JeecgBoot 后端启动脚本
echo ======================================================================
echo.
echo 正在检查环境...

REM 检查Java环境
echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java环境，请安装JDK 1.8+
    goto :end
)
echo [成功] Java环境检查通过

REM 检查Maven环境
echo 检查Maven环境...
call mvn -v
set MVN_RESULT=%errorlevel%
if %MVN_RESULT% neq 0 (
    echo [错误] 未检测到Maven环境，请安装Maven
    echo.
    echo 您可以从以下地址下载Maven: https://maven.apache.org/download.cgi
    echo 下载后，请将Maven解压到一个目录，并将bin目录添加到系统PATH环境变量中
    echo.
    echo 或者，您可以使用已编译好的jar包直接运行项目（如果有的话）
    goto :end
)

echo [成功] Maven环境检查通过

echo [信息] 当前目录: %cd%
echo [信息] 正在进入后端项目目录...

if not exist jeecg-boot (
    echo [错误] 未找到jeecg-boot目录，脚本将尝试直接在当前目录编译启动
) else (
    cd jeecg-boot
    if %errorlevel% neq 0 (
        echo [错误] 无法进入后端项目目录，请确认jeecg-boot目录存在且有访问权限
        goto :end
    )
    echo [信息] 已进入后端项目目录: %cd%
)

echo [信息] 开始编译后端项目...
echo [信息] 执行命令: mvn clean install -DskipTests

call mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo [错误] 后端项目编译失败
    goto :end
)

echo [信息] 后端项目编译成功
echo [信息] 正在启动后端服务...

REM 检查目录结构，适应不同的项目布局
if exist jeecg-module-system\jeecg-system-start (
    echo [信息] 找到标准目录结构
    cd jeecg-module-system\jeecg-system-start
) else if exist backend\jeecg-module-system\jeecg-system-start (
    echo [信息] 找到旧目录结构
    cd backend\jeecg-module-system\jeecg-system-start
) else (
    echo [警告] 未找到标准启动模块目录，尝试搜索启动类...
    for /r %%i in (*SystemApplication.java) do (
        echo [信息] 找到可能的启动类: %%i
        cd %%~dpi..
        goto :found_start
    )
    echo [错误] 无法找到启动模块，请手动指定启动模块的位置
    goto :end
)

:found_start
echo [信息] 已进入启动模块目录: %cd%
echo [信息] 执行命令: mvn spring-boot:run
call mvn spring-boot:run

if %errorlevel% neq 0 (
    echo [错误] 后端服务启动失败
    goto :end
)

:end
echo [信息] 脚本执行完毕
pause 