<!--
  @author       LiuRunYu 2026-04-11
  @description  个人风格首页，带动态特效与交互卡片
-->

<template>
  <div>
    <div class="relative mx-auto mt-4 flex min-h-screen max-w-6xl flex-col justify-center px-6 py-24 md:px-12">
      <!-- 页面顶部横幅 -->
      <div class="ncjd-card ncjd-hover mb-8 flex flex-wrap items-center justify-between gap-4 rounded-3xl px-5 py-4 backdrop-blur-xl">
        <div>
          <p class="text-sm tracking-[0.35em] text-sky-400/80 dark:text-cyan-300">个人网站 · HomeLAB · 技术学习</p>
          <p class="mt-2 text-sm text-slate-500">欢迎来到本程序员的电子自留地，本站是一块新技术试验田，同时实现一些我认为日常比较频繁使用的功能。</p>
        </div>
        <div class="flex flex-wrap items-center gap-3 text-sm text-slate-300">
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">SpringBoot</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">MyBatis-Plus</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">MySQL</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">Redis</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">Nginx</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">Axios</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">JWT</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">Vue 3</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">Vite</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">Tailwind</span>
          <span class="rounded-full bg-slate-900/5 px-3 py-1 text-slate-700 dark:bg-white/5 dark:text-slate-200">Three.js</span>
        </div>
      </div>

      <section class="grid gap-10 lg:grid-cols-[1.35fr_0.9fr] lg:items-center">
        <!-- 左侧主展示区域 -->
        <div class="space-y-8">
          <div class="ncjd-card ncjd-hover max-w-xl rounded-3xl p-8 backdrop-blur-xl">
            <p class="tracking-[0.4em] text-sky-400/80 dark:text-cyan-300">你好，我是</p>
            <h1 class="mt-4 text-5xl font-semibold leading-tight text-gray-700 dark:text-white md:text-6xl">
              <!-- 根据状态值切换显示名称 -->
              {{ myStore.furry_mode ? '龍杏 Loogingko' : 'LiuRunYu' }}
            </h1>
            <p class="mt-4 text-xl text-slate-300 md:text-2xl">
              <span class="text-sky-400/80 dark:text-cyan-300">{ </span>
              <span class="font-medium text-gray-700 dark:text-white">{{ typedTitle }}</span>
              <span class="text-sky-400/80 dark:text-cyan-300"> }</span>
            </p>
            <p class="mt-6 text-slate-500">没搞事，依然风平浪静的一天。</p>
            <p class="mt-2 text-slate-500">No crimes Just day.</p>
            <p class="mt-2 text-slate-500">一个不想搞事的数字角落。</p>
            <p class="mt-2 text-slate-500">今天也没搞事。</p>
            <div class="mt-8 flex flex-wrap gap-4">
              <a
                href="https://unsplash.com/@loogingko"
                target="_blank"
                rel="noopener noreferrer"
                class="rounded-full bg-cyan-400 px-6 py-3 text-sm font-semibold text-slate-950 transition hover:-translate-y-0.5 hover:bg-cyan-300"
              >
                查看作品
              </a>
              <button
                class="rounded-full border border-white/20 bg-slate-900/5 px-6 py-3 text-sm text-slate-700 transition hover:border-cyan-300 hover:text-cyan-300 dark:bg-white/5 dark:text-slate-200"
                @click="showEmail"
              >
                联系我
              </button>
            </div>
          </div>

          <!-- 统计概览卡片区 -->
          <div class="grid gap-4 md:grid-cols-3">
            <div class="ncjd-card ncjd-hover rounded-3xl p-5 text-center backdrop-blur-xl transition hover:-translate-y-1">
              <p class="text-4xl font-semibold text-sky-400/80 dark:text-cyan-300">
                {{ stats.projects }}
              </p>
              <p class="mt-2 text-sm text-slate-500">项目</p>
            </div>
            <div class="ncjd-card ncjd-hover rounded-3xl p-5 text-center backdrop-blur-xl transition hover:-translate-y-1">
              <p class="text-4xl font-semibold text-fuchsia-300">
                {{ stats.years }}
              </p>
              <p class="mt-2 text-sm text-slate-500">年经验</p>
            </div>
            <div class="ncjd-card ncjd-hover rounded-3xl p-5 text-center backdrop-blur-xl transition hover:-translate-y-1">
              <p class="text-4xl font-semibold text-emerald-300">
                {{ stats.coffee }}
              </p>
              <p class="mt-2 text-sm text-slate-500">如厕次数</p>
            </div>
          </div>
        </div>

        <!-- 右侧动态信息区域 -->
        <div class="space-y-6">
          <div class="ncjd-card ncjd-hover rounded-[32px] p-6 backdrop-blur-xl">
            <div class="mb-4 flex items-center justify-between gap-3">
              <div>
                <p class="text-xs uppercase tracking-[0.35em] text-slate-500">动态仪表板</p>
                <h2 class="mt-2 text-2xl font-semibold text-gray-700 dark:text-white">一些搞事数据</h2>
              </div>
              <span class="rounded-full bg-cyan-400/10 px-3 py-1 text-xs text-cyan-300">Live</span>
            </div>
            <div class="space-y-4">
              <!-- 迭代渲染状态卡片 -->
              <div
                v-for="item in liveCards"
                :key="item.title"
                class="hover:bg-white-900/90 rounded-3xl border border-white/10 bg-slate-900/5 p-4 transition hover:border-cyan-300/40"
              >
                <div class="flex items-center justify-between gap-3">
                  <div>
                    <p class="text-sm text-slate-500">{{ item.title }}</p>
                    <p class="mt-2 text-lg font-medium text-gray-700 dark:text-white">
                      {{ item.value }}
                    </p>
                  </div>
                  <span :class="['inline-flex rounded-full px-3 py-1 text-xs font-semibold', item.variant]">{{ item.tag }}</span>
                </div>
                <div class="mt-4 h-2 overflow-hidden rounded-full bg-gray-100 dark:bg-slate-800">
                  <div :style="{ width: item.fill + '%' }" class="h-full rounded-full bg-gradient-to-r from-cyan-400 to-fuchsia-500 transition-all duration-700"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="ncjd-card ncjd-hover rounded-[32px] p-6 backdrop-blur-xl">
            <p class="text-xs uppercase tracking-[0.35em] text-slate-500">标签</p>
            <div class="mt-4 flex flex-wrap gap-3">
              <span
                v-for="tag in tags"
                :key="tag"
                class="rounded-full bg-slate-900/5 px-4 py-2 text-sm text-slate-700 transition hover:bg-cyan-400/10 hover:text-cyan-300 dark:bg-white/5 dark:text-slate-200"
                >{{ tag }}</span
              >
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入全局状态与 Vue 响应式 API
import { useMyStore } from '@/config/my-store';
import { messager } from '@/utils/global';
import { onMounted, onUnmounted, ref } from 'vue';

const myStore = useMyStore();

// 轮播标题数组，用于打字机效果展示
const titles = ['全栈工程师', '3D 视觉爱好者', '喜欢剖根问底', '创意页面设计师', 'Java 开发者'];
const typedTitle = ref('全栈工程师');

// 页面统计数据，初始从 0 开始动画增长
const stats = ref({ projects: 0, years: 0, coffee: 0 });

// 右侧标签列表，用于展示关键词风格
const tags = ['交互动效', '沉浸式 UI', '性能优化', '组件化', '未来感', '深色模式'];

// 动态卡片数据，包含标题、数值、标签样式和进度条数值
const liveCards = ref([
  {
    title: '创作热度',
    value: '92%',
    tag: '火力全开',
    variant: 'bg-fuchsia-500/15 text-fuchsia-300',
    fill: 92,
  },
  {
    title: '速度优化',
    value: '76ms',
    tag: '流畅',
    variant: 'bg-cyan-500/15 text-cyan-300',
    fill: 76,
  },
  {
    title: '灵感存储',
    value: '18 项',
    tag: '待实现',
    variant: 'bg-emerald-500/15 text-emerald-300',
    fill: 58,
  },
]);

let typeInterval: number;
let countInterval: number;
let currentIndex = 0;
let letterIndex = 0;
let forward = true;

// 文字打字机动画逻辑
const animateTyped = () => {
  const title = titles[currentIndex];
  if (forward) {
    letterIndex += 1;
    typedTitle.value = title.slice(0, letterIndex);
    if (letterIndex === title.length) {
      forward = false;
      // 当前词打完后暂停一段时间再开始删除
      setTimeout(animateTyped, 1200);
      return;
    }
  } else {
    letterIndex -= 1;
    typedTitle.value = title.slice(0, letterIndex);
    if (letterIndex === 0) {
      forward = true;
      currentIndex = (currentIndex + 1) % titles.length;
    }
  }
  typeInterval = window.setTimeout(animateTyped, forward ? 120 : 60);
};

// 统计数字滚动动画逻辑
const animateStats = () => {
  const target = { projects: 7, years: 4, coffee: 1200 };
  stats.value = {
    projects: Math.min(target.projects, stats.value.projects + 1),
    years: Math.min(target.years, stats.value.years + 1),
    coffee: Math.min(target.coffee, stats.value.coffee + 20),
  };
  if (stats.value.projects === target.projects && stats.value.years === target.years && stats.value.coffee === target.coffee) {
    return;
  }
  countInterval = window.setTimeout(animateStats, 80);
};

const showEmail = () => {
  messager.info('联系邮箱：ahornbeast@outlook.com');
};

// 组件挂载后启动动画逻辑
onMounted(() => {
  animateTyped();
  animateStats();
});

// 组件卸载时清理定时器，防止内存泄漏
onUnmounted(() => {
  window.clearTimeout(typeInterval);
  window.clearTimeout(countInterval);
});
</script>

<style lang="less" scoped>
/* 漂浮光斑动画 */
.animate-blob {
  animation: blob 8s infinite;
}

/* 延迟动画类，分别给不同光斑设置时间差 */
.animation-delay-2000 {
  animation-delay: 2s;
}

.animation-delay-4000 {
  animation-delay: 4s;
}

@keyframes blob {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(150px, -150px) scale(1.1);
  }
  66% {
    transform: translate(-150px, 150px) scale(0.95);
  }
}
</style>
