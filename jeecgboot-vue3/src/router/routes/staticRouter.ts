import type { AppRouteRecordRaw } from '/@/router/types';
import { LAYOUT } from '/@/router/constant';

export const AI_ROUTE: AppRouteRecordRaw = {
  path: '',
  name: 'ai-parent',
  component: LAYOUT,
  meta: {
    title: 'ai',
  },
  children: [
    {
      path: '/ai',
      name: 'ai',
      component: () => import('/@/views/dashboard/ai/index.vue'),
      meta: {
        title: 'AI助手',
      },
    },
  ],
};

// export const home: AppRouteRecordRaw[] = [
//   {
//     path: '/',
//     name: 'home',
//     component: () => import('/@/home/index.vue'),
//     meta: {
//       title: '首页',
//       ignoreAuth: true,
//       ignoreMenu: true,
//     },
//   },
// ];
export const RMS_AGENT_ROUTE: AppRouteRecordRaw = {
  path: '',
  name: 'rms-agent-parent',
  component: LAYOUT,
  meta: {
    title: '招聘智能体',
  },
  children: [
    {
      path: '/rms/agent-workflow',
      name: 'RmsAgentWorkflow',
      component: () => import('/@/views/rmsAgent/RmsAgentWorkflow.vue'),
      meta: {
        title: '招聘智能体工作流',
      },
    },
  ],
};

export const staticRoutesList = [AI_ROUTE, RMS_AGENT_ROUTE];
