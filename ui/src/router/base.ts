/**
 * @author       LiuRunYu 2025-05-09
 * @description  VUE路由目录
 */
import { useMyStore } from '@/config/my-store';
import { type RouteRecordRaw } from 'vue-router';

// 整体布局
export const BASE_ROUTE: RouteRecordRaw = {
  path: '/',
  component: () => import('@/views/layout/DefaultLayout.vue'),
  children: [
    {
      path: '', // 主页
      name: 'Home',
      component: () => import('@/views/Home.vue'),
    },
    // 令牌登录失败页
    {
      path: 'noauth',
      name: 'NoAuth',
      component: () => import('@/views/NoAuth.vue'),
    },
    // 施工页
    {
      path: 'building',
      name: 'Building',
      component: () => import('@/views/Building.vue'),
    },
    {
      path: 'tv',
      name: 'Tv',
      component: () => import('@/views/tv/Tv.vue'),
    },
    {
      path: 'grid',
      name: 'Grid',
      component: () => import('@/views/grid/Grid.vue'),
    },
    {
      path: 'manual',
      name: 'Manual',
      meta: {
        isAjax: 1,
      },
      component: () => import('@/views/manual/Manual.vue'),
    },
    {
      path: 'memo',
      name: 'Memo',
      meta: {
        isAjax: 1,
      },
      component: () => import('@/views/memo/Memo.vue'),
    },
    {
      path: 'furry',
      name: 'Furry',
      component: () => import('@/views/NotFound.vue'),
      beforeEnter: () => {
        useMyStore().setAttribute('furry_mode', true);
        return '/';
      },
    },
    // {
    //   path: 'home2',
    //   name: 'Home2',
    //   component: () => import('@/views/Home2.vue'),
    // },
  ],
};

// 登录页面
export const LOGIN_ROUTE: RouteRecordRaw = {
  path: '/login',
  name: 'Login',
  // meta: {
  //   isAjax: 1,
  // },
  component: () => import('@/views/auth/Login.vue'),
};

// 404页面
export const NOT_FOUND_ROUTE: RouteRecordRaw = {
  path: '/:pathMatch(.*)*',
  name: 'notFound',
  component: () => import('@/views/NotFound.vue'),
};
