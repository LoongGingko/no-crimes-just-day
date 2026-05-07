/**
 * @author       LiuRunYu 2026-04-08
 * @description  创建Vue路由实例
 */
import { createRouter, createWebHistory, type RouteLocationNormalizedGeneric, type RouteRecordRaw } from 'vue-router';
import { BASE_ROUTE, LOGIN_ROUTE, NOT_FOUND_ROUTE } from './base';
import { verifyToken } from '@/api/interceptor';
import { hasSucc, http } from '@/api/http';
import { myCache } from '@/utils/cache';
import { useMyStore } from '@/config/my-store';
import { $HOST, addPath } from '@/utils/uri';

// 基础页面
const routes: RouteRecordRaw[] = [BASE_ROUTE, LOGIN_ROUTE, NOT_FOUND_ROUTE];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  },
});

// 路由白名单
const whiteList = ['/noauth', '/'];

// 创建路由守卫
router.beforeEach(async (to: RouteLocationNormalizedGeneric, from) => {
  const myStore = useMyStore();
  if (myStore.pingDebug('router')) debugger; // 开发者断点

  // todo: 加载横条动画

  // 路由前置操作（重置全局缓存、上一页、滚动条等）
  beforeRoute();

  // 1. 白名单
  // if (whiteList.includes(to.path)) return true;

  // 2. 刷新时校验令牌
  if (myStore.logged) verifyToken();

  // 3. 打开页面是否有后台请求
  if (to.meta.isAjax) await asyncAjax(to);

  // 尝试登录
  // if (to.path === '/auth') {
  //   myStore.setAttribute('loggedErr', ''); // 重置错误消息
  //   if (to.query.token) {
  //     sessionStorage.setItem('accessToken', to.query.token as string);
  //     const success = await verifyToken();
  //     if (success) {
  //       // 登录成功：跳转到首页
  //       myStore.setAttribute('logged', true);
  //       myStore.setAttribute('historys_flag', true);
  //       return '/image';
  //     }
  //     // 登录失败：跳转到noauth失败页
  //     return '/noauth';
  //   }
  // }

  // 未登录状态
  // if (!myStore.logged) {
  //   return "/"; // 跳转到无状态首页
  // }

  return true; // 成功跳转
});

// 路由前置操作（重置全局缓存、上一页、滚动条等）
function beforeRoute() {
  const myStore = useMyStore();
  myStore.setAttribute('pre_route', '');
  myCache.clear();
}

// 打开有ajax请求的页面
async function asyncAjax(to: RouteLocationNormalizedGeneric) {
  const url = addPath($HOST, useMyStore().curr_module, '/pre', to.path);
  // 入口ajax函数(异步)
  const retBol = await http.req(url, 'post', to.query, (R: any) => {
    if (hasSucc(R)) {
      myCache.set('model', R.data);
      myCache.set('modelExtra', R.extra);
    }
  });
  return retBol; // 如果运行失败，返回undefined
}

// export async function beforeRouteEnter(ub: UriBean, to: TYObj) {
//   /**
//    * 365定制：登录后页面，先执行入口ajax，后跳转
//    */
//   // isAjax = 1，有入口ajax
//   if (to.meta.isAjax === 1) {
//     const path = pathAddLsMs(to.path, ub); // 去掉shortName
//     // 入口ajax函数(异步)
//     const retBol = await $POSTFORM(path, to.query, {
//       success: (data: any) => {
//         initCacheRouteChange('routeHist', 'siteHead', 'passThrough'); // 初始化route缓存
//         mergeData2Meta(data); // 原ModelView / authToken / soObj / dgObj数据
//         return { stop: true, data: true }; // stop:true 第一个例子，入口函数成功后，返回true
//       },
//       exerror: () => {
//         return false; // 后台抛出异常，返回false
//       },
//     });
//     return retBol; // 如果运行失败，返回undefined
//   }
//   initCacheRouteChange('routeHist', 'siteHead', 'passThrough'); // 初始化route缓存
//   return true; // 无入口ajax，始终返回true
// }

export default router;
