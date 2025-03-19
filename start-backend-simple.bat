@echo off
chcp 65001 > nul
echo ======================================================================
echo             JeecgBoot 后端启动脚本 (简化版，无需Maven)
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

echo [信息] 环境检查通过

echo [信息] 当前目录: %cd%
echo [信息] 正在查找已编译的jar包...

REM 尝试查找已编译的jar包
set JAR_PATH=
for /r "jeecg-boot\jeecg-module-system\jeecg-system-start\target" %%i in (*.jar) do (
    if not "%%~ni"=="original" (
        set JAR_PATH=%%i
    )
)

if "%JAR_PATH%"=="" (
    echo [警告] 未找到已编译的jar包，请先使用Maven编译项目或下载预编译的jar包
    echo [信息] 尝试在其他位置查找jar包...
    
    for /r "jeecg-boot" %%i in (*system-start*.jar) do (
        if not "%%~ni"=="original" (
            set JAR_PATH=%%i
        )
    )
)

if "%JAR_PATH%"=="" (
    echo [错误] 未找到可执行的jar包，无法启动后端服务
    echo [提示] 请先使用Maven编译项目或下载预编译的jar包
    goto :end
)

echo [信息] 找到jar包: %JAR_PATH%
echo [信息] 正在启动后端服务...

java -jar "%JAR_PATH%" --spring.profiles.active=dev

if %errorlevel% neq 0 (
    echo [错误] 后端服务启动失败
    goto :end
)

:end
pause 