/**
 * @author       LiuRunYu 2026-04-08
 * @description  VUE路由目录
 */
import { createApp } from 'vue';
import router from './router';
import { createPinia } from 'pinia';
import '@/assets/css/global.css';
import '@/assets/css/tailwind.css';
import { useAppStore } from './config/app-store';
import { MotionPlugin } from 'motion-v';
import App from './App.vue';
import { useMyStore } from './config/my-store';

const app = createApp(App);
app.use(router); // 单文件组件风格

// 全局安装打包会有冗余代码，不推荐
// app.use(naive)

// pinia实现数据持久化
app.use(createPinia());
const appStore = useAppStore();
const myStore = useMyStore();
appStore.hydrate();
myStore.hydrate();

// v-motion动画
app.use(MotionPlugin);

app.mount('#app');
