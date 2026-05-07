/**
 * @author       LiuRunYu 2026-04-08
 * @description  Pinia全局属性 (业务层)
 */
import { isEmpty, jump2 } from '@/utils/common';
import { defineStore } from 'pinia';
import { type MyStoreState } from './types';
import { http } from '@/api/http';

const STORAGE_KEY = 'myStore'; // 唯一名称

export const useMyStore = defineStore(STORAGE_KEY, {
  state: (): MyStoreState => ({
    // 系统
    furry_mode: false, // 福瑞模式 (链接分享)
    curr_module: '/', // 当前导航栏 (用于高亮显示)
    curr_search: '', // 当前搜索值 (实现简易事件总线：赋值触发搜索, 搜索完成重置)
    pre_route: '', // 上个页面（返回的目标路径）
    // 登录
    logged: false, // 是否登录
    loggedErr: '', // 登录错误消息 (用于JWT认证失败后提示)
    userid: '', // 用户唯一ID
    username: '', // 用户名
    nickname: '', // 用户昵称
    // 开发者模式
    debug_mode: [], // 断点控制
  }),
  getters: {},
  actions: {
    // 通用setter
    setAttribute(key: string, value: any) {
      (this as any)[key] = value;
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
    // 登录
    login(data: any) {
      this.logged = true;
      this.loggedErr = '';
      this.userid = data.id;
      this.username = data.username;
      this.nickname = data.nickname;
      this.persist();
    },
    logout(toLogin?: boolean) {
      if (this.logged) {
        // 请求后端删除jwt cookie
        http.req('/logout', 'post', { username: this.username }, () => {
          this.resetLogout();
        });
      } else {
        this.resetLogout(); // 不发请求，仅重置前端
      }
      if (toLogin) jump2('/login');
    },
    // 重置前端登录状态
    resetLogout() {
      this.logged = false;
      this.loggedErr = '';
      this.userid = '';
      this.username = '';
      this.nickname = '';
      this.persist();
    },
    // 开发者选项
    initDebug(debugs: any) {
      debugs.value.forEach((d: any) => {
        if (this.debug_mode.includes(d.key)) {
          d.enable = true;
        }
      });
    },
    // 设置断点
    updDebug(enable: boolean, debug: string) {
      if (debug === undefined || debug === null) return;
      if (enable) {
        if (!this.debug_mode.includes(debug)) {
          this.debug_mode.push(debug);
        }
      } else {
        const i = this.debug_mode.indexOf(debug);
        if (i !== -1) this.debug_mode.splice(i, 1);
      }
      this.persist();
    },
    // 是否启用某断点
    pingDebug(debug: string) {
      return this.debug_mode.includes(debug);
    },
  },
});
