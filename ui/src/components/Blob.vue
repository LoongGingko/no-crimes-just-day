<!--
  @author       LiuRunYu 2026-04-12
  @description  动态光斑背景
-->
<template>
  <div class="blob-container" ref="blobRef" />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const blobRef = ref(null);
let animationId = -1;

class BlobApp {
  // 类变量
  ref = null as any;
  blobs = [] as any[];
  isMobile = window.innerWidth <= 640;

  constructor(ref: any) {
    this.ref = ref;
    this.blobs = [];
  }

  // 创建光斑div
  createBlob(i: number) {
    const blob = document.createElement('div');
    const isMobile = this.isMobile;

    // 颜色：高饱和度，适合流光效果
    const hue = Math.random() * 360;
    const saturation = 60 + Math.random() * 35; // 60-95%
    const lightness = 50 + Math.random() * 30; // 50-80%

    // 尺寸：200px - 700px
    const minSize = isMobile ? 120 : 200;
    const maxSize = isMobile ? 260 : 700;
    const size = minSize + Math.random() * (maxSize - minSize);

    // 随机起始位置
    const startX = Math.random() * window.innerWidth;
    const startY = Math.random() * window.innerHeight;

    // 动画参数
    const duration = 15 + Math.random() * 15; // 15-30秒，更慢更飘逸
    const delay = Math.random() * -30; // 随机起始点

    const rangeX = isMobile ? 80 + Math.random() * 120 : 300 + Math.random() * 400;
    const rangeY = isMobile ? 60 + Math.random() * 90 : 200 + Math.random() * 400;

    // 存储动画参数供 requestAnimationFrame 使用
    const speed = {
      speedX: 0.2 + Math.random() * 0.3,
      speedY: 0.15 + Math.random() * 0.35,
      scaleSpeed: 0.5 + Math.random() * 1,
    };
    const speed2 = {
      speedX: 0.1 + Math.random() * 0.1,
      speedY: 0.05 + Math.random() * 0.1,
      scaleSpeed: 0.1 + Math.random() * 0.5,
    };

    const blobData = {
      element: blob,
      hue,
      saturation,
      lightness,
      size,
      startX,
      startY,
      duration,
      delay,
      rangeX,
      rangeY,
      startTime: null,
      // 随机运动曲线参数
      ...speed2,
      opacityBase: 0.3 + Math.random() * 0.4,
    };

    const blurAmount = isMobile ? 26 : 40;
    const brightness = isMobile ? 1.1 : 1.2;
    const opacityStart = 0.7;

    blob.style.cssText = `
      position: absolute;
      width: ${size}px;
      height: ${size}px;
      border-radius: 50%;
      background: radial-gradient(circle at 30% 30%,
        hsla(${hue}, ${saturation}%, ${lightness}%, ${opacityStart}),
        hsla(${hue}, ${saturation}%, ${Math.max(lightness - 24, 20)}%, 0)
      );
      filter: blur(${blurAmount}px) brightness(${brightness});
      mix-blend-mode: screen;
      pointer-events: none;
      will-change: transform, background;
      transform: translate3d(0, 0, 0) scale(1);
      left: 0;
      top: 0;
    `;

    this.ref.appendChild(blob);
    return blobData;
  }

  // 创建若干光斑
  init() {
    const blobCount = this.isMobile ? 5 : Math.floor(Math.random() * 5) + 6;

    for (let i = 0; i < blobCount; i++) {
      this.blobs.push(this.createBlob(i));
    }
  }

  // 每秒执行60次，改变轨迹颜色大小
  animate(currentTime: number) {
    for (const blob of this.blobs) {
      if (blob.startTime === null) {
        blob.startTime = currentTime;
      }

      // 计算进度（考虑延迟）
      const progress = (currentTime - blob.startTime) / 1000 + blob.delay;

      // 使用正弦余弦创建平滑的 Lissajous 曲线运动
      const x = blob.startX + Math.sin(progress * blob.speedX) * blob.rangeX + Math.cos(progress * 0.7) * (blob.rangeX * 0.3);

      const y = blob.startY + Math.cos(progress * blob.speedY) * blob.rangeY + Math.sin(progress * 0.5) * (blob.rangeY * 0.3);

      // 动态缩放（在 0.7 到 1.3 倍之间变化）
      const scale = 0.8 + Math.sin(progress * blob.scaleSpeed) * 0.3;

      // 动态改变颜色（轻微色相漂移）
      const hueShift = Math.sin(progress * 0.3) * 30;
      const currentHue = (blob.hue + hueShift + 360) % 360;

      // 动态透明度（呼吸效果）
      const opacity = blob.opacityBase + Math.sin(progress * 0.5) * 0.15;

      // 应用变换
      blob.element.style.transform = `translate3d(${x}px, ${y}px, 0) scale(${scale})`;
      blob.element.style.background = `radial-gradient(circle at 30% 30%,
        hsla(${currentHue}, ${blob.saturation}%, ${blob.lightness}%, ${opacity}),
        hsla(${currentHue}, ${blob.saturation}%, ${blob.lightness - 25}%, ${opacity * 0.6})
      )`;

      // 如果超出屏幕太远，重置位置
      if (x < -blob.size || x > window.innerWidth + blob.size || y < -blob.size || y > window.innerHeight + blob.size) {
        blob.startX = Math.random() * window.innerWidth;
        blob.startY = Math.random() * window.innerHeight;
        blob.startTime = null;
      }
    }
  }

  // 清理所有光斑元素
  destroy() {
    this.blobs.forEach(blob => {
      if (blob.element && blob.element.parentNode) {
        blob.element.parentNode.removeChild(blob.element);
      }
    });
    this.blobs = [];
  }
}

let blobApp = null as any;

// 动画无限循环
const animateFrame = (timestamp: number) => {
  if (blobApp) {
    blobApp.animate(timestamp);
    animationId = requestAnimationFrame(animateFrame);
  }
};

// 窗口resize事件，确保光斑不会跑出屏幕
const handleResize = () => {
  if (blobApp) {
    blobApp.blobs.forEach((blob: any) => {
      blob.startX = Math.min(Math.max(blob.startX, -blob.size), window.innerWidth + blob.size);
      blob.startY = Math.min(Math.max(blob.startY, -blob.size), window.innerHeight + blob.size);
    });
  }
};

onMounted(() => {
  if (blobRef.value) {
    blobApp = new BlobApp(blobRef.value);
    blobApp.init();
    animationId = requestAnimationFrame(animateFrame);
    window.addEventListener('resize', handleResize);
  }
});

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  window.removeEventListener('resize', handleResize);
  if (blobApp) {
    blobApp.destroy();
  }
});
</script>

<style scoped>
.blob-container {
  contain: paint;
  filter: saturate(0.55);
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  position: fixed;
}

html.dark .blob-container {
  filter: saturate(1);
}
</style>
