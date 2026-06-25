/**
 * @author       LiuRunYu 2026-04-08
 * @description  Pinia全局属性 (应用层)
 */
import { defineStore } from 'pinia';
import defaultSettings from './app-settings.json';
import { type AppState } from './types';

const STORAGE_KEY = 'appStore'; // 唯一名称

export const useAppStore = defineStore(STORAGE_KEY, {
  state: (): AppState => ({ ...defaultSettings }),
  getters: {
    // 当前result页码
    currDevice(status: AppState) {
      return status.device;
    },
  },
  actions: {
    // 通用setter
    setAttribute(key: string, value: any) {
      (this as any)[key] = value;
      this.persist();
    },
    toggleAttribute(key: string) {
      (this as any)[key] = !(this as any)[key];
      this.persist();
    },
    // 加载持久化
    hydrate() {
      const data = localStorage.getItem(STORAGE_KEY);
      if (data) {
        this.$patch(JSON.parse(data)); // 自动恢复所有字段
      }
    },
    // 保存持久化
    persist() {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.$state));
    },
    // 重置
    reset() {
      this.$reset();
    },
    // 切换亮色/暗色主题
    // true=持久化
    toggleTheme(persist?: boolean) {
      const doc = document.documentElement;
      if (this.theme !== 'light') {
        this.theme = 'light';
        doc.classList.remove('dark');
      } else {
        this.theme = 'dark';
        doc.classList.add('dark');
      }
      if (persist) this.persist();
    },
    toggleMobile(isMobile: boolean) {
      const doc = document.documentElement;
      if (isMobile) {
        this.device = 'mobile';
        doc.classList.add('mobile');
      } else {
        this.device = 'desktop';
        doc.classList.remove('mobile');
      }
      this.persist();
    },
    isMobile() {
      return this.device === 'mobile';
    },
  },
});
