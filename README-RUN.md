# JeecgBoot 项目启动指南

本指南将帮助您快速启动 JeecgBoot 项目，包括后端服务 (jeecg-boot) 和前端应用 (jeecgboot-vue3)。

## 环境要求

### 后端环境要求
- JDK 1.8+ (推荐 JDK 8)
- Maven 3.6+
- MySQL 5.7+ (推荐 MySQL 5.7)
- Redis (可选，用于缓存)

### 前端环境要求
- Node.js 18+ (推荐 Node.js 20)
- npm 或 pnpm

## 快速启动

我们提供了几个批处理脚本来简化项目的启动过程：

### 1. 一键启动脚本 (推荐)

运行 `start-all.bat` 脚本，它提供了一个交互式菜单，您可以选择：
- 初始化数据库
- 启动后端服务
- 启动前端服务
- 一键启动全部服务

```
start-all.bat
```

### 2. 单独启动各个组件

如果您想单独启动各个组件，可以使用以下脚本：

#### 初始化数据库
```
init-database.bat
```
此脚本将引导您创建数据库并导入初始数据。

#### 启动后端服务
```
start-backend.bat
```
此脚本将编译并启动后端服务。

#### 启动前端服务
```
start-frontend.bat
```
此脚本将安装依赖并启动前端开发服务器。

## 访问系统

启动成功后，您可以通过以下地址访问系统：

- 后端服务: http://localhost:8080/jeecg-boot
- 前端应用: http://localhost:3100/

默认登录信息：
- 用户名: admin
- 密码: 123456

## 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 检查数据库连接信息是否正确

2. **后端编译失败**
   - 检查JDK版本是否正确
   - 检查Maven配置是否正确

3. **前端启动失败**
   - 检查Node.js版本是否符合要求
   - 尝试删除node_modules目录并重新安装依赖

4. **前端访问后端API失败**
   - 检查后端服务是否正常运行
   - 检查前端配置文件中的API地址是否正确

## 项目结构

- **jeecg-boot**: 后端项目，基于SpringBoot的微服务架构
- **jeecgboot-vue3**: 前端项目，基于Vue3+Vite+TypeScript的现代前端应用
- **jeecg-uniapp**: 移动端应用，支持APP、小程序、H5 