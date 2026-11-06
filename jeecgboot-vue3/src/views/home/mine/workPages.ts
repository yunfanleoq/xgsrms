import type { Component } from 'vue';
import { defineAsyncComponent } from 'vue';

export const ROLE_APPLICANT = 'register';
export const ROLE_DEPARTMENT = 'depart_position_manager';
export const ROLE_HR = 'hr_position_manager';

export type WorkGroup = 'flow' | 'site';

export interface WorkPage {
  name: string;
  path: string;
  label: string;
  roles: string[];
  group: WorkGroup;
  /** 有值时收进同名下拉。同一角色只能看到一项时，仍按本页名称平铺。 */
  menu?: string;
  component: Component;
}

function page(
  name: string,
  path: string,
  label: string,
  roles: string[],
  load: () => Promise<any>,
  group: WorkGroup = 'flow',
  menu?: string,
): WorkPage {
  return { name, path, label, roles, group, menu, component: defineAsyncComponent(load) };
}

/** 部门管理员、人事处管理员在前台办理的功能。同一页可同时给两个角色。 */
export const WORK_PAGES: WorkPage[] = [
  page('workDepartAudit', 'work/depart-audit', '部门审核', [ROLE_DEPARTMENT], () => import('/@/views/xgsResumeApproval/departApproval/XgsPositionApplyList.vue')),
  page('workHrCollect', 'work/hr-collect', '人力初筛', [ROLE_HR], () => import('/@/views/xgsResumeApproval/hrLookApproval/XgsPositionApplyList.vue'), 'flow', '应聘审核'),
  page('workHrAudit', 'work/hr-audit', '人力决审', [ROLE_HR], () => import('/@/views/xgsResumeApproval/hrApproval/XgsPositionApplyList.vue'), 'flow', '应聘审核'),
  page('workPositionCheck', 'work/position-check', '岗位审核', [ROLE_HR], () => import('/@/views/positionsCheck/XgsPositionsList.vue')),
  page('workPublishApply', 'work/publish-apply', '发布申请', [ROLE_DEPARTMENT], () => import('/@/views/positions/XgsPositionPublishList.vue')),
  page('workPositionPost', 'work/position-post', '岗位发布', [ROLE_HR], () => import('/@/views/postionsPost/XgsPositionsList.vue')),
  page('workInvite', 'work/invite', '面试邀请', [ROLE_HR], () => import('/@/views/xgsInviteToInterview/XgsInviteToInterviewList.vue'), 'flow', '面试'),
  page('workInterviewQuery', 'work/interview-query', '面试查询', [ROLE_DEPARTMENT, ROLE_HR], () => import('/@/views/xgsInterview/XgsInterviewList.vue'), 'flow', '面试'),
  page('workPositionQuery', 'work/position-query', '岗位查询', [ROLE_DEPARTMENT, ROLE_HR], () => import('/@/views/positions/XgsPositionsList.vue')),
  page('workRecruitment', 'work/recruitment', '招聘信息', [ROLE_HR], () => import('/@/views/xgsRecruitment/XgsPositionsList.vue'), 'site'),
  page('workJournalism', 'work/journalism', '科研发展', [ROLE_HR], () => import('/@/views/xgsJournalism/XgsJournalismList.vue'), 'site'),
  page('workIntroduce', 'work/introduce', '研究所况', [ROLE_HR], () => import('/@/views/xgsIntroduce/XgsIntroduceList.vue'), 'site'),
  page('workContact', 'work/contact', '联系我们', [ROLE_HR], () => import('/@/views/xgsContactUs/XgsContactUsList.vue'), 'site'),
  page('workHomeContent', 'work/home-content', '首页内容', [ROLE_HR], () => import('/@/views/xgsHome/XgsHomeList.vue'), 'site'),
];

export function workPagesForRoles(roles: string[]) {
  return WORK_PAGES.filter((item) => item.roles.some((role) => roles.includes(role)));
}
