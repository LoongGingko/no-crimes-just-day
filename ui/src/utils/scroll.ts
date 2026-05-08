/**
 * @author       LiuRunYu 2026-05-08
 * @description  滚动工具
 */

import { onMounted, onBeforeUnmount, ref } from 'vue';

// 是否滚动 (目前用于隐藏导航)
export const scrolled = ref(false);

// 启用滚动事件
export function useScroll() {
  let lastY = 0;
  let startTouchY = 0;
  const offsetY = 50; // 滚动触发阈值

  // 获取滚动容器
  function getEl(): HTMLElement | null {
    return document.querySelector('.n-scrollbar-container');
  }

  // 滚动事件
  function onScroll(e: Event) {
    const y = (e.target as HTMLElement).scrollTop;
    if (!scrolled.value && y - lastY > 0 && y > offsetY) {
      scrolled.value = true; // 向下滚动且超过阈值
    } else if (scrolled.value && y < lastY) {
      scrolled.value = false; // 向上滚动时立即标记为 false
    }
    lastY = y;
  }

  // 触摸开始事件
  function onTouchStart(e: TouchEvent) {
    startTouchY = e.touches[0].clientY;
  }

  // 触摸移动事件
  function onTouchMove(e: TouchEvent) {
    const deltaY = startTouchY - e.touches[0].clientY; // 正数=下滑
    const y = getEl()?.scrollTop ?? 0;
    if (!scrolled.value && deltaY > offsetY && y > offsetY) scrolled.value = true;
    else if (scrolled.value && deltaY < -1) scrolled.value = false;
  }

  onMounted(() => {
    const el = getEl();
    el?.addEventListener('scroll', onScroll, { passive: true });
    el?.addEventListener('touchstart', onTouchStart, { passive: true });
    el?.addEventListener('touchmove', onTouchMove, { passive: true });
  });

  onBeforeUnmount(() => {
    const el = getEl();
    el?.removeEventListener('scroll', onScroll);
    el?.removeEventListener('touchstart', onTouchStart);
    el?.removeEventListener('touchmove', onTouchMove);
  });
}
