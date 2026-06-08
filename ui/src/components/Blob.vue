<!--
  @author       LiuRunYu 2026-04-12
  @description  动态光斑背景（Canvas 版）
-->
<template>
  <canvas ref="canvasRef" class="blob-canvas" :style="{ opacity: delayDisplay ? '1' : '0' }" />
</template>

<script setup lang="ts">
import { useAppStore } from '@/config/app-store';
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';

let animId = 0;
const appStore = useAppStore();
const canvasRef = ref<HTMLCanvasElement | null>(null);
const delayDisplay = ref(false);
const isDark = computed(() => appStore.theme !== 'light');

const W = () => window.innerWidth;
const H = () => window.innerHeight;
const isMobile = W() <= 640;
const dpr = Math.min(window.devicePixelRatio || 1, 1.5);

// 单个光斑的数据结构
interface Blob {
  startX: number;
  startY: number; // 起始坐标
  hue: number;
  sat: number;
  _sat: number;
  lightness: number; // 色相、饱和度、亮度
  size: number; // 基础半径
  vx: number;
  vy: number; // 运动频率
  rx: number;
  ry: number; // 运动振幅
  vs: number; // 缩放速度
  ob: number; // 基础透明度
  offsetTime: number; // 时间偏移秒
  hs: number; // 色相漂移速度
  startTime: number; // 动画起始时间戳
}

// 工具函数：生成 [a, b) 区间随机数
const rand = (a: number, b: number) => a + Math.random() * (b - a);

// 初始化所有光斑
const blobs: Blob[] = Array.from({ length: isMobile ? 5 : Math.floor(Math.random() * 3) + 4 }, () => ({
  startX: Math.random() * W(),
  startY: Math.random() * H(),
  hue: Math.random() * 360,
  sat: rand(66, 100),
  _sat: rand(66, 100),
  lightness: rand(50, 80),
  size: rand(isMobile ? 120 : 200, isMobile ? 260 : 600),
  vx: rand(0.1, 0.2),
  vy: rand(0.05, 0.15),
  rx: isMobile ? rand(80, 200) : rand(300, 700),
  ry: isMobile ? rand(60, 150) : rand(200, 600),
  vs: rand(0.1, 0.6),
  ob: rand(0.3, 1),
  offsetTime: rand(-30, 0),
  hs: rand(8, 20),
  startTime: 0,
}));

// 画布尺寸跟随窗口，应用设备像素比
const resize = () => {
  const c = canvasRef.value;
  if (!c) return;
  c.width = W() * dpr;
  c.height = H() * dpr;
  c.style.width = W() + 'px';
  c.style.height = H() + 'px';
  // 缩放上下文，让绘制坐标直接用 CSS 像素
  c.getContext('2d')!.setTransform(dpr, 0, 0, dpr, 0, 0);
};

// 每帧绘制：计算位置 → 创建渐变 → 填充圆形
const draw = (now: number) => {
  const c = canvasRef.value;
  if (!c) return;
  const ctx = c.getContext('2d')!;
  const w = W(),
    h = H();

  // 清空画布，用叠加模式让光斑互相增亮
  ctx.clearRect(0, 0, w, h);
  ctx.globalCompositeOperation = 'screen';

  for (const blob of blobs) {
    if (!blob.startTime) blob.startTime = now;
    const timed = (now - blob.startTime) / 1000 + blob.offsetTime; // 这个光斑已经运动了多少秒

    // 用正弦/余弦组合出 Lissajous 曲线轨迹
    const x = blob.startX + Math.sin(timed * blob.vx) * blob.rx + Math.cos(timed * 0.7) * blob.rx * 0.3;
    const y = blob.startY + Math.cos(timed * blob.vy) * blob.ry + Math.sin(timed * 0.5) * blob.ry * 0.3;
    // 呼吸式缩放和透明度波动
    const scale = 0.6 + Math.sin(timed * blob.vs) * 0.2; // 基础缩放 60%，上下浮动20%
    const op = blob.ob + Math.sin(timed * 0.5) * 0.15; // 上下波动 ±0.15，频率固定为 0.5
    // 色相随时间缓慢漂移
    const hue = blob.hue + Math.sin(timed * 0.5) * 30;

    // 飘出屏幕太远就随机重置位置
    if (x < -blob.size || x > w + blob.size || y < -blob.size || y > h + blob.size) {
      blob.startX = Math.random() * w;
      blob.startY = Math.random() * h;
      blob.startTime = now;
      continue;
    }

    const r = blob.size * scale;
    const g = ctx.createRadialGradient(x, y, 0, x, y, r);

    // 中心轻微衰减，模拟 blur 后中心也不是绝对满值
    g.addColorStop(0, `hsla(${hue},${blob.sat}%,${blob.lightness}%,${op * 0.9})`);
    // 柔和缓慢衰减
    g.addColorStop(0.2, `hsla(${hue},${blob.sat}%,${blob.lightness}%,${op * 0.8})`);
    g.addColorStop(0.8, `hsla(${hue},${blob.sat}%,${blob.lightness}%,${op * 0.5})`);
    // 70%~100%：快速衰减归零，模拟模糊边缘
    g.addColorStop(1, `hsla(${hue},${blob.sat}%,${blob.lightness}%,0)`);

    // 画一个填充圆
    ctx.fillStyle = g;
    ctx.beginPath();
    ctx.arc(x, y, r, 0, Math.PI * 2);
    ctx.fill();
  }

  animId = requestAnimationFrame(draw);
};

// 挂载时启动：设置画布尺寸、监听窗口变化、开始动画循环
onMounted(() => {
  resize();
  window.addEventListener('resize', resize);
  animId = requestAnimationFrame(draw);
  setTimeout(() => {
    delayDisplay.value = true;
  }, 600);
});

// 卸载时清理：取消动画帧、移除监听器
onUnmounted(() => {
  cancelAnimationFrame(animId);
  window.removeEventListener('resize', resize);
});

// 浅色模式降低饱和度
watch(
  isDark,
  dark => {
    for (const blob of blobs) {
      blob.sat = dark ? blob._sat : blob._sat * 0.55;
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.blob-canvas {
  contain: paint;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  position: fixed;
  transition: opacity 2s cubic-bezier(0.2, 0.9, 0.4, 1); /* 苹果动画曲线 */
}
</style>
