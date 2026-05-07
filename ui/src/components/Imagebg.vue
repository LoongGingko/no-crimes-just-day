<!--
  @author       LiuRunYu 2026-04-01
  @description  组件名称
-->

<template>
  <div>
    <motion.div
      class="image-bg fixed inset-0 bg-cover bg-center bg-no-repeat"
      :style="{ backgroundImage: bgImage }"
      :initial="{ scale: 1, opacity: 1 }"
      :animate="animateProps"
      :transition="{
        duration: 6,
        repeat: Infinity,
        repeatType: 'mirror',
        ease: 'easeInOut',
      }"
    />
    <motion.div
      class="absolute inset-0 bg-gradient-to-r from-yellow-500/30 to-transparent"
      :animate="{ opacity: [0, 0, 0.4, 0.4, 0, 0] }"
      :transition="{ duration: 10, repeat: Infinity }"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onBeforeUpdate, useTemplateRef, onMounted } from 'vue';
import { NButton, NDialog, NForm, NFormItem, NInput } from 'naive-ui';
import { CirclePlus } from 'lucide-vue-next';
import { fmtUrl } from '@/utils/common';
import Sortable from 'sortablejs';
import { motion } from 'motion-v';
import { useAppStore } from '@/config/app-store';

const appStore = useAppStore();

const animateProps = computed(() => {
  const base = { scale: 1.05, opacity: 1 };
  if (appStore.device !== 'mobile') {
    return { ...base, x: 25, y: -15 };
  }
  return base;
});
const bgImage = computed(() => {
  return appStore.device === 'mobile' ? "url('/images/white-dragon-on-grass-mobile.webp')" : "url('/images/white-dragon-on-grass.webp')";
});
</script>

<style lang="less" scoped>
.image-bg {
  background-image: url('/images/white-dragon-on-grass.webp');
}
</style>
