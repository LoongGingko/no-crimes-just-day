/**
 * @author       LiuRunYu 2026-04-17
 * @description  响应式工具
 */

import { useAppStore } from '@/config/app-store';
import { useDebounceFn } from '@vueuse/core';
import { onMounted, onBeforeMount, onBeforeUnmount } from 'vue';

// 开启响应式，兼容PC & 移动端
export function useResponsive() {
  const appStore = useAppStore();

  // 如果用户没有最小化、切到其他标签，则计算响应式
  function fn() {
    if (!document.hidden) {
      appStore.toggleMobile(queryIsMobile());
    }
  }
  const debounceFn = useDebounceFn(fn, 100); // 防抖
  const options = {
    passive: true, // 声明不会调用 preventDefault()，性能更好
    capture: false,
  };
  onBeforeMount(() => {
    window.addEventListener('resize', debounceFn, options);
  });
  onBeforeUnmount(() => {
    window.removeEventListener('resize', debounceFn, options);
  });
}

// 工具函数：判断当前窗口是否竖屏模式
const WIDTH = 768;
export const queryIsMobile = function () {
  const rect = document.body.getBoundingClientRect();
  return rect.width - 1 < WIDTH;
};

// 仅该页面为黑暗模式
export function useDarkOnce() {
  const appStore = useAppStore();
  const isDarkBefore = appStore.theme !== 'light'; // 临时变量
  onBeforeMount(() => {
    if (!isDarkBefore) appStore.toggleTheme(false);
  });
  onBeforeUnmount(() => {
    if (!isDarkBefore) appStore.toggleTheme(false);
  });
}
