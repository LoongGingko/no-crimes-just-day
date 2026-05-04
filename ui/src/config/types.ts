import type { RouteRecordNormalized } from 'vue-router';

// App
export interface AppState {
  theme: string;
  lang: string;
  colorWeak: boolean;
  navbar: boolean;
  menu: boolean;
  hideMenu: boolean;
  menuCollapse: boolean;
  footer: boolean;
  themeColor: string;
  menuWidth: number;
  globalSettings: boolean;
  device: string;
  tabBar: boolean;
  menuFromServer: boolean;
  serverMenu: RouteRecordNormalized[];
  [key: string]: unknown;
}

// MyStore
export interface MyStoreState {
  furry_mode: boolean;
  curr_module: string;
  curr_search: string;
  logged: boolean;
  loggedErr: string;
  userid: string;
  username: string;
  nickname: string;
  debug_mode: string[];
}
