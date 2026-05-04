<!--
  @author       LiuRunYu 2026-04-11
  @description  导航栏组件
-->
<template>
  <div>
    <div class="fixed left-0 right-0 top-4 z-50 mx-auto flex max-w-screen-2xl items-start px-6">
      <!-- 左侧按钮（PC） -->
      <template v-if="!isMobile">
        <div class="mt-5 flex flex-1 items-center justify-center">
          <AnimatePresence>
            <motion.button
              v-if="!isHome"
              :initial="{ opacity: 0 }"
              :animate="{ opacity: 1 }"
              :exit="{ opacity: 0 }"
              :whilePress="{ scale: 0.95, transition: { duration: 0.1 } }"
              :transition="{ duration: 0.3, ease: 'easeInOut' }"
              class="ncjd-card ncjd-hover rounded-full p-2.5 backdrop-blur-xl md:p-[11.5px]"
              @click="goBack()"
              ><ChevronLeft />
            </motion.button>
          </AnimatePresence>
        </div>
      </template>
      <!-- 左侧按钮（手机） -->
      <template v-else>
        <AnimatePresence>
          <motion.div
            :key="isHome ? 1 : 2"
            :initial="{ maxWidth: '0' }"
            :animate="{ maxWidth: '100%', transition: { duration: 0.5, delay: 0.2, ease: 'easeInOut' } }"
            :exit="{ maxWidth: '0' }"
            :transition="{ duration: 0.3, ease: 'easeInOut' }"
            class="mt-5 flex items-center justify-center"
          >
            <motion.button
              v-if="!isHome"
              :initial="{ opacity: 0 }"
              :animate="{ opacity: 1 }"
              :exit="{ opacity: 0 }"
              :whilePress="{ scale: 0.95, transition: { duration: 0.1 } }"
              class="ncjd-card ncjd-hover rounded-full p-2.5 backdrop-blur-xl md:p-[11.5px]"
              @click="goBack()"
              ><ChevronLeft />
            </motion.button>
          </motion.div>
        </AnimatePresence>
      </template>
      <!-- 中间导航栏 -->
      <nav
        class="nav ncjd-card ml-3 flex overflow-x-auto overflow-y-hidden rounded-full px-5 backdrop-blur-xl transition-all duration-500 sm:overflow-x-hidden md:px-14"
        :class="[isHovering ? 'mt-2.5 py-5' : 'mt-5 py-2.5', { 'flex-1': isMobile }]"
        ref="navRef"
        @mousemove="handleMouseMove"
        @mouseleave="handleMouseLeave"
      >
        <!-- 光效层 -->
        <div
          class="pointer-events-none absolute -inset-0 rounded-full transition-opacity duration-100"
          :class="isHovering ? 'opacity-100' : 'opacity-0'"
          :style="{ background: spotLightBackground }"
          aria-hidden="true"
        />
        <button
          v-for="(link, index) in links"
          :key="index"
          class="flex shrink-0 basis-14 flex-col items-center md:basis-16"
          @click="jump2Valid(link)"
          :class="['nav-item', link.path === myStore.curr_module ? ' text-sky-400 dark:text-cyan-400' : 'text-slate-500 dark:text-slate-400 hover:dark:text-white']"
        >
          <!-- 图标 / 标签 -->
          <component :is="link.icon" class="nav-icon md:h-8 md:w-8" />
          <span :class="isHovering ? 'mt-2 max-h-4  opacity-100' : 'max-h-0 opacity-0'" class="text-xs transition-all md:text-sm">{{ link.label }}</span>
        </button>
      </nav>
      <!-- 右侧按钮 -->
      <div class="ml-3 mt-5 flex items-center justify-center md:flex-1">
        <n-popover v-model:show="showPopover" trigger="click" display-directive="show" overlap :show-arrow="false" placement="top-end">
          <!-- 触发按钮 -->
          <template #trigger>
            <motion.button :whilePress="{ scale: 0.95 }" :transition="{ duration: 0.1 }" class="ncjd-card ncjd-hover rounded-full p-2.5 backdrop-blur-xl md:p-[11.5px]">
              <UserRound v-if="!myStore.logged" /><!-- 用户按钮（登录前） -->
              <EllipsisVertical v-else /><!-- 更多按钮（登录后） -->
            </motion.button>
          </template>
          <!-- 菜单内容（字体调大） -->
          <template #default>
            <div class="ncjd-card overflow-hidden rounded-[2rem] text-xl shadow-xl backdrop-blur-xl">
              <ul class="flex flex-col *:flex *:cursor-pointer *:items-center *:gap-x-[0.625em] *:py-[0.625em] *:pl-[1.25em] *:pr-[2.5em]">
                <!-- 界面功能 -->
                <li @click="updTheme" class="ncjd-hover2 first:pt-[0.9375em]">
                  <template v-if="isDark"><Sun class="svg-sm" />浅色模式</template>
                  <template v-else><Moon class="svg-sm" />深色模式</template>
                </li>
                <li @click="fullscreen" class="ncjd-hover2">
                  <template v-if="isFullscreen"><Minimize2 class="svg-sm" />退出全屏</template>
                  <template v-else><Maximize2 class="svg-sm" />全屏</template>
                </li>
                <li @click="todo" class="ncjd-hover2 last:pb-[0.9375em]"><Languages class="svg-sm" />英文</li>
                <!-- 登录功能 -->
                <div class="my-3 border-t border-dashed border-gray-400 !p-0" />
                <li v-if="!myStore.logged" @click="login" class="ncjd-hover2"><LogOut class="svg-sm" />登录</li>
                <li v-if="myStore.logged" class="!cursor-auto text-sm">👤 {{ myStore.nickname }}</li>
                <li v-if="myStore.logged" @click="logout" class="ncjd-hover2"><LogOut class="svg-sm" />退出登录</li>
                <!-- 开发者功能 -->
                <div class="my-3 border-t border-dashed border-gray-400 !p-0" />
                <li v-for="d in debugs" :key="d.key" @click.stop="updDebug(d)" class="ncjd-hover2 flex justify-between last:pb-[0.9375em]">
                  <div>{{ d.name }}</div>
                  <n-switch v-model:value="d.enable" class="pointer-events-none" />
                </li>
              </ul>
            </div>
          </template>
        </n-popover>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { goBack, jump2 } from '@/utils/common';
import { motion } from 'motion-v';
import { useMyStore } from '@/config/my-store';
import {
  Home,
  LayoutGrid,
  EllipsisVertical,
  Languages,
  LogOut,
  Maximize2,
  Minimize2,
  Heart,
  Brain,
  Image,
  Star,
  CheckSquare,
  Tv,
  Gamepad2,
  Sun,
  Moon,
  ChevronLeft,
  UserRound,
} from 'lucide-vue-next';
import { computed, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAppStore } from '@/config/app-store';
import { NPopover, NSwitch, NDivider } from 'naive-ui';
import { AnimatePresence } from 'motion-v';
import { h } from 'vue';
import router from '@/router';
import { messager } from '@/utils/global';

// 数据持久化
const myStore = useMyStore();
const appStore = useAppStore();
const route = useRoute();
const isDev = import.meta.env.DEV; // 是否开发环境：显示开发者模式

/**
 * VUE Data
 */
const showPopover = ref(false); // 显示Popover
const isFullscreen = ref(false); // 是否全屏
const inputLabel = ref('');
const links = [
  { path: '/', icon: Home, label: '首页', label_en: 'Home' },
  { path: '/tv', icon: Tv, label: '拟物TV', label_en: 'Formula Image\nScanner' },
  { path: '/grid', icon: LayoutGrid, label: '导航页', label_en: 'Home' },
  // { path: '/grid2', icon: LayoutGrid, label: '导航页2', label_en: 'Home' },
  { invalid: true, path: '/ack', icon: Brain, label: '知识库', label_en: 'PDF\nScanner' },
  { invalid: true, path: '/habit', icon: Heart, label: '习惯', label_en: 'PDF\nScanner' },
  { invalid: true, path: '/gallery', icon: Image, label: '相册', label_en: 'PDF\nScanner' },
  { invalid: true, path: '/clip', icon: Star, label: '剪藏', label_en: 'Formula Image\nScanner' },
  { invalid: true, path: '/todo', icon: CheckSquare, label: '待办', label_en: 'PDF\nScanner' },
  { invalid: true, path: '/game', icon: Gamepad2, label: '游戏', label_en: 'PDF\nScanner' },
];

// 开发者选项
const debugs = ref([
  { name: '路由守卫', key: 'router', enable: false },
  { name: '拦截器', key: 'interceptor', enable: false },
  { name: 'Axios', key: 'axios', enable: false },
]);
myStore.initDebug(debugs);

// h 函数重载：
// 2个参数：若第2个参数为对象非数组则为 props，否则为 children（字符串、数组、null）
// 3个参数：第2个为 props、第3个为 children。

// 监听route更新导航栏高亮
watch(
  () => route.path,
  routePath => {
    const link = links.find((_link: any) => _link.path === routePath);
    if (link) myStore.setAttribute('curr_module', link.path);
  },
  { immediate: true } // 刷新立即触发
);

const isDark = computed(() => appStore.theme !== 'light');
const isHome = computed(() => route.path === '/');
const isMobile = computed(() => appStore.device === 'mobile');

/**
 * VUE Methods
 */
// 搜索事件
const onSearch = () => {
  myStore.setAttribute('curr_search', inputLabel.value);
  myStore.setAttribute('historys_flag', true); // 输入关键词后，需要刷新historys
  jump2('/image', true);
};

// 切换中英文
const chgLang = () => {
  appStore.setAttribute('lang', appStore.lang === 'en' ? 'zh' : 'en');
  showPopover.value = false;
};

// 点击全屏
const fullscreen = () => {
  if (isFullscreen.value === false) {
    document.documentElement.requestFullscreen();
    isFullscreen.value = true;
  } else {
    document.exitFullscreen();
    isFullscreen.value = false;
  }
  showPopover.value = false;
};

let timeId: any = null;
const jump2Valid = (link: any) => {
  myStore.setAttribute('curr_module', link.path); // 设置高亮

  // 手机端延时关闭导航栏
  if (appStore.isMobile()) {
    if (timeId) clearTimeout(timeId);
    timeId = setTimeout(() => {
      isHovering.value = false;
    }, 800);
  }
  // 功能暂未启用...
  if (link.invalid) {
    jump2('/building', true);
    return;
  }

  // 跳转页面
  jump2(link.path);
};

const updTheme = () => {
  appStore.toggleTheme(true);
  showPopover.value = false;
};

const updDebug = (d: any) => {
  d.enable = !d.enable;
  myStore.updDebug(d.enable, d.key);
};

// 提示暂未启用
const todo = () => {
  messager.error('从技术上来说，这个功能还在开发中。就像宇宙中的暗物质，虽然知道它存在，但还需要时间来发现它。', { icon: () => h(Heart) });
  showPopover.value = false;
};

// 点击登录
const login = () => {
  jump2('/login');
};

// 点击退出登录
const logout = () => {
  messager.success('已退出登录');
  myStore.logout();
  showPopover.value = false;
};

// 光效层属性
const navRef = ref<any>(null);
const mouseX = ref(0);
const mouseY = ref(0);
const isHovering = ref(false);

// 鼠标移动光效
const handleMouseMove = (e: any) => {
  const rect = navRef.value.getBoundingClientRect();
  mouseX.value = e.clientX - rect.left;
  mouseY.value = e.clientY - rect.top;
  isHovering.value = true;
};

// 鼠标离开光效
const handleMouseLeave = () => {
  isHovering.value = false;
};

// 计算光效层
const spotLightBackground = computed(() => {
  const spotColor = isDark.value ? 'hsla(60, 10%, 96%, 0.25)' : 'hsla(200, 5%, 120%, 1)';
  const x = isDark.value ? '120px' : '180px';
  return `radial-gradient(ellipse ${x} 250px at ${mouseX.value}px ${mouseY.value}px, ${spotColor} 0%, transparent 65%)`;
});
</script>

<style lang="less" scoped>
/* ========= navbar自定义样式 =============================*/
.nav-item {
  /* 出动画 */
  transition: all 0.2s cubic-bezier(0.2, 0.9, 0.4, 1); /* 苹果动画曲线 */
  will-change: transform;
  z-index: 1;

  &:hover {
    /* 入动画 */
    transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 2);
    transform: scale(1.15);
  }
}
</style>
