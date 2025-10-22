import type { AppRouteRecordRaw, AppRouteModule } from '/@/router/types';

import { PAGE_NOT_FOUND_ROUTE, REDIRECT_ROUTE } from '/@/router/routes/basic';

import { mainOutRoutes } from './mainOut';
import { PageEnum } from '/@/enums/pageEnum';
import { t } from '/@/hooks/web/useI18n';

const modules = import.meta.glob('./modules/**/*.ts', { eager: true });

const routeModuleList: AppRouteModule[] = [];

// 加入到路由集合中
Object.keys(modules).forEach((key) => {
  const mod = (modules as Recordable)[key].default || {};
  const modList = Array.isArray(mod) ? [...mod] : [mod];
  routeModuleList.push(...modList);
});

export const asyncRoutes = [PAGE_NOT_FOUND_ROUTE, ...routeModuleList];

export const RootRoute: AppRouteRecordRaw = {
  path: '/',
  name: 'Root',
  // redirect: PageEnum.BASE_HOME,
  redirect: '/home',
  meta: {
    title: 'Root',
  },
};

export const LoginRoute: AppRouteRecordRaw = {
  path: '/login',
  name: 'Login',
  //新版后台登录，如果想要使用旧版登录放开即可
  // component: () => import('/@/views/sys/login/Login.vue'),
  component: () => import('/@/views/system/loginmini/MiniLogin.vue'),
  meta: {
    title: t('routes.basic.login'),
  },
};

//update-begin---author:wangshuai ---date:20220629  for：auth2登录页面路由------------
export const Oauth2LoginRoute: AppRouteRecordRaw = {
  path: '/oauth2-app/login',
  name: 'oauth2-app-login',
  //新版钉钉免登录，如果想要使用旧版放开即可
  // component: () => import('/@/views/sys/login/OAuth2Login.vue'),
  component: () => import('/@/views/system/loginmini/OAuth2Login.vue'),
  meta: {
    title: t('routes.oauth2.login'),
  },
};
//update-end---author:wangshuai ---date:20220629  for：auth2登录页面路由------------

/**
 * 【通过token直接静默登录】流程办理登录页面 中转跳转
 */
export const TokenLoginRoute: AppRouteRecordRaw = {
  path: '/tokenLogin',
  name: 'TokenLoginRoute',
  component: () => import('/@/views/sys/login/TokenLoginPage.vue'),
  meta: {
    title: '带token登录页面',
    ignoreAuth: true,
  },
};

export const homeIndexRoute: AppRouteRecordRaw = {
  path: '/home',
  name: 'homeIndex',
  redirect: '/home/index',
  component: () => import('/@/views/home/index.vue'),
  meta: {
    title: '人才招聘',
    ignoreAuth: true,
    ignoreMenu: true,
  },
  children: [
    {
      path: 'index',
      name: 'homeHome',
      component: () => import('/@/views/home/home.vue'),
      meta: {
        title: '招聘首页',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },
    {
      path: 'ygqk',
      name: 'homeYgqk',
      component: () => import('/@/views/home/ygqk.vue'),
      meta: {
        title: '员工情况',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },
    {
      path: 'yjsk',
      name: 'homeYjsk',
      component: () => import('/@/views/home/yjsk.vue'),
      meta: {
        title: '研究所况',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },
    {
      path: 'positions',
      name: 'homePositions',
      component: () => import('/@/views/home/positions.vue'),
      children: [
        {
          path: 'positionDetail/:id',
          name: 'positionDetail',
          component: () => import('/@/views/home/positionDetail.vue'),
          meta: {
            title: '招聘岗位明细',
            ignoreAuth: true,
            ignoreMenu: true,
          },
        },
      ],
      meta: {
        title: '招聘岗位',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },
    // {
    //   path: '/positionDetail/:id',
    //   name: 'positionDetail',
    //   component: () => import('/@/views/home/positionDetail.vue'),
    //   meta: {
    //     title: '招聘岗位',
    //     ignoreAuth: true,
    //     ignoreMenu: true,
    //   },
    // },
    {
      path: '/announcement/:id',
      name: 'announcementDetail',
      component: () => import('/@/views/home/announcementDetail.vue'),
      meta: {
        title: '招聘信息',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },
    {
      path: 'news',
      name: 'homeNews',
      component: () => import('/@/views/home/news.vue'),
      meta: {
        title: '科研发展',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },
    {
      path: '/news/:id',
      name: 'newsDetail',
      component: () => import('/@/views/home/newsDetail.vue'),
      meta: {
        title: '详细内容',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },
    {
      path: 'contactUs',
      name: 'contactUs',
      component: () => import('/@/views/home/contactUs.vue'),
      meta: {
        title: '联系我们',
        ignoreAuth: true,
        ignoreMenu: true,
      },
    },

    // 添加其他子路由
  ],
};

// Basic routing without permission
export const basicRoutes = [
  LoginRoute,
  RootRoute,
  ...mainOutRoutes,
  REDIRECT_ROUTE,
  PAGE_NOT_FOUND_ROUTE,
  TokenLoginRoute,
  Oauth2LoginRoute,
  homeIndexRoute,
];
