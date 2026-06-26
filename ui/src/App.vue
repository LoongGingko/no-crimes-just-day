<template>
  <!-- Naive全局配置 -->
  <n-config-provider :locale="zhCN" :date-locale="dateZhCN" :theme="theme" :theme-overrides="themeOverrides">
    <n-dialog-provider>
      <n-message-provider>
        <router-view />
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { NConfigProvider, NMessageProvider, NDialogProvider, zhCN, dateZhCN, darkTheme, type GlobalThemeOverrides } from 'naive-ui';
import { useAppStore } from './config/app-store';
import { computed } from 'vue';
import { useResponsive, queryIsMobile } from './utils/responsive';

// 开启响应式，宝贝儿
useResponsive();

// 自动切换亮色/暗色 (Naive)
const appStore = useAppStore();
const theme = computed(() => (appStore.theme !== 'light' ? darkTheme : null));

// 自动切换亮色/暗色 (根节点)
if (appStore.theme !== 'light') {
  document.documentElement.classList.add('dark');
}

// 自动切换PC/手机 (根节点)
if (queryIsMobile()) {
  document.documentElement.classList.add('mobile');
}

// 自定义Naive主题
const themeOverrides: GlobalThemeOverrides = {
  Layout: {
    color: 'transparent',
  },
  Input: {
    color: 'var(--ncjd-color-card)',
    border: '1px solid var(--ncjd-color-border)',
    borderFocus: '',
    borderHover: '',
    borderHoverWarning: '',
    boxShadowFocus: '',
  },
  Popover: {
    padding: 0,
    color: 'transparent',
    boxShadow: 'none',
  },
};
</script>
