<!--
  @author       LiuRunYu 2026-05-05
  @description  渐变层背景 (优先级高于Blob.vue)
-->

<template>
  <div>
    <AnimatePresence>
      <motion.div
        v-if="isDark && showGradient"
        class="gradient-dark fixed inset-0 -z-30 text-slate-500"
        :initial="{ opacity: 0 }"
        :animate="{ opacity: 1, transition: { delay: 0.6, duration: 0.7, ease: 'easeOut' } }"
        :exit="{ opacity: 0, transition: { duration: 0.6, ease: 'easeOut' } }"
      />
      <motion.div
        v-if="!isDark && showGradient"
        class="gradient fixed inset-0 -z-30"
        :initial="{ opacity: 0 }"
        :animate="{ opacity: 1, transition: { delay: 0.6, duration: 0.7, ease: 'easeOut' } }"
        :exit="{ opacity: 0, transition: { duration: 0.6, ease: 'easeOut' } }"
      />
    </AnimatePresence>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onBeforeUpdate, useTemplateRef, onMounted } from 'vue';
import { AnimatePresence, motion } from 'motion-v';
import { useAppStore } from '@/config/app-store';
import { showGradient } from '@/utils/responsive';

const appStore = useAppStore();

const isDark = computed(() => appStore.theme !== 'light');

/** 延迟触发动画 */
const motionReady = ref(false);
onMounted(() => {
  setTimeout(() => {
    motionReady.value = true;
  }, 500);
});
</script>

<style lang="less" scoped>
.gradient {
  background-image: linear-gradient(rgb(209 221 244 / 80%), rgb(163 184 211 / 80%), rgb(119 148 179 / 90%), rgb(73 114 147 / 90%), rgb(14 81 117 / 90%));
  /* background-position: center;
  background-repeat: no-repeat;
  background-size: cover; */
}

.gradient-dark {
  background-image: radial-gradient(ellipse at 20% 10%, rgb(30 60 100) 0%, transparent 100%);
}
</style>
