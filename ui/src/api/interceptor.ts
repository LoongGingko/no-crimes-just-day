/**
 * @author       LiuRunYu 2026-04-08
 * @description  配置拦截器
 */

import axios from 'axios';
import type { InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { showLoadingAxios, hideLoadingAxios } from '../utils/div-wait';
import { useAppStore } from '@/config/app-store';
import router from '@/router';
import { http } from './http';
import { useMyStore } from '@/config/my-store';
import { messager } from '@/utils/global';

// export interface HttpResponse<T = unknown> {
//   status: number;
//   msg: string;
//   code: number;
//   data: T;
// }

// JWT令牌
// export function getAuthToken() {
//   const token = sessionStorage.getItem("accessToken");
//   if (token) return `Bearer ${token}`;
// }

// 请求体拦截器
axios.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    showLoadingAxios(config); // 显示loading弹出层
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应体拦截器
axios.interceptors.response.use(
  (resp: AxiosResponse) => {
    // 2xx 范围内的状态码
    hideLoadingAxios();
    return resp; // 向下传递给http.then()
  },
  error => {
    // 非 2xx 的错误状态码
    if (useMyStore().pingDebug('interceptor')) debugger; // 开发者断点
    hideLoadingAxios();
    if (error.response || error.status) {
      return errHandler(error); // 有状态错误
    }
    return toastError(error, error.message); // 无状态错误
  }
);

// 处理请求错误
function errHandler(error: any) {
  // 区分response的error和纯error
  const { response } = error;
  const status = response?.status ?? error.status; // ?? 只在左侧是 null 或 undefined 时才取右侧值。
  const msg = response?.statusText || '未知错误';
  const text = typeof msg === 'object' ? msg.message : msg;
  // 返回Promise对象
  switch (status) {
    case 401:
      return errToken('401 : 未登录（Token无效）');
    case 403:
      return errToken('403 : 已登录，权限不足');
    case 500: // 错误码500: 服务器内部错误
      return toastError(error, '500 : Internal Server Error');
    default: // 其他请求错误
      return toastError(error, `${status}：${text}`);
  }
}

// 显示错误
function toastError(error: any, text: any) {
  return new Promise(reject => {
    messager.error(text);
    reject(error); // 异常未处理，返回异常本身
  });
}

// 未登录 / 已登录但权限不足
function errToken(text: string) {
  return new Promise(resolve => {
    useMyStore().logout();
    messager.error(text);
    resolve({ error: text });
    // router.push('/noauth'); // 跳转到noauth失败页
  });
}

// 登录后每次刷新，后台重新校验token有效期
export async function verifyToken() {
  let retBol = false;
  await http.req('/auth/verify', 'post', {}, () => {
    // 抛异常不会执行
    retBol = true;
  });
  return retBol;
}

export async function ping() {
  await http.req('/ping', 'post');
}
