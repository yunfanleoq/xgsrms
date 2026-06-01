---
tags: [xgsrms, api, backend]
---

# 后端 API 清单（招聘模块）

← [[00-Index]]

> 基础路径：`{host}/jeecg-boot`。下列为 `@RequestMapping` 前缀；标准 Jeecg CRUD 另含 `/list`、`/add`、`/delete`、`/queryById` 等（未逐一列出）。

## 岗位与申请

| 前缀 | Controller | 定制接口摘要 |
|------|------------|--------------|
| `/positions/xgsPositions` | `XgsPositionsController` | `apply`、`check`、`post` |
| `/positions/xgsPositionApply` | `XgsPositionApplyController` | 申请主流程 |
| `/positions/xgsPositionApply2` | `XgsPositionApply2Controller` | 申请扩展/副本流程 |
| `/positions/xgsFavoriteJob` | `XgsFavoriteJobController` | 收藏岗位 |

## 简历

| 前缀 | 说明 |
|------|------|
| `/xgsResume/xgsResumeBase` | 普通简历 |
| `/xgsResume/xgsResumeFG` | FG 简历 |
| `/xgsResume/xgsResumeTJ` | TJ 简历 |
| `/xgsResume/xgsResumeBSH` | 博士后简历 |
| `/xgsMyresume/xgsMyresume` | 我的简历；`POST /uploadPdf` |
| `/resume/xgsUserResumeFile` | 附件；`PUT|POST /analysisResume` |
| `/resume/xgsFlowOpinions` | 审核意见；`POST /submitByApplyId` |

## 面试与人才

| 前缀 | 说明 |
|------|------|
| `/xgsInterview/xgsInterview` | 面试记录 |
| `/xgsInviteToInterview/xgsInviteToInterview` | 面试邀请 |
| `/xgsTalentpool/xgsTalentpool` | 人才库 |

## 门户内容

| 前缀 | 说明 |
|------|------|
| `/xgsHome/xgsHome` | 首页配置 |
| `/xgsJournalism/xgsJournalism` | 新闻 |
| `/xgsIntroduce/xgsIntroduce` | 介绍 |
| `/xgsContactUs/xgsContactUs` | 联系我们 |

## 导出

| 前缀 | 说明 |
|------|------|
| `/xgsExport` | `XgsResumeExportController` 简历导出 |

## 系统级（非 recruitment 包，但必联调）

| 前缀 | 说明 |
|------|------|
| `/sys/login` | 登录（anon） |
| `/sys/user/register` | 注册（anon） |
| `/sys/permission/getUserPermissionByToken` | 菜单与按钮权限 |
| `/sys/common/upload` | 文件上传 |

## 权限与数据范围

- 接口受 Shiro JWT 保护（非 anon 路径）
- 列表类接口常配合 Jeecg **数据权限**注解与部门编码过滤
- 菜单按钮权限 SQL 示例：`scripts/sql/mysql/xgs_grant_position_apply_list_permission.sql`

## 调试建议

1. 启动 [[10-开发指南]] 中的后端，打开 Knife4j（若已启用）：`/doc.html`
2. 用部门/人力测试账号分别调用同一 `applyId` 的列表接口，验证 [[09-安全与扫描]] 中的 IDOR 项
