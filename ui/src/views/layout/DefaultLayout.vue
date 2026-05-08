<!--
  @author       LiuRunYu 2026-04-11
  @description  整体布局
-->
<template>
  <n-layout>
    <div-wait />
    <!-- 导航栏 -->
    <navbar />
    <!-- 渐变色背景 -->
    <Gradient />
    <!-- 光斑背景 -->
    <Blob class="-z-40" />
    <!-- 图片背景 -->
    <Imagebg v-if="myStore.furry_mode" class="-z-50" />
    <!-- 全局滚动条 -->
    <n-scrollbar class="max-h-screen">
      <router-view v-slot="{ Component, route }">
        <AnimatePresence mode="sync">
          <component
            :is="Component"
            :key="route.fullPath + routerSalt"
            route-comp
            :class="{ 'ncjd-layout': true, logged: myStore.logged }"
            v-motion
            :initial="{ y: '0', scale: 0.8, opacity: 0 }"
            :animate="{
              y: '0',
              scale: 1,
              opacity: 1,
              transition: {
                duration: 0.5,
                delay: 0.35,
                ease: [0, 0.71, 0.2, 1],
              },
            }"
            :exit="{
              y: '50vh',
              opacity: 0,
              transition: {
                duration: 0.35,
                ease: 'easeInOut',
              },
            }"
          />
        </AnimatePresence>
      </router-view>
    </n-scrollbar>
  </n-layout>
</template>

<script setup lang="ts">
import DivWait from '@/components/DivWait.vue';
import Navbar from '@/components/Navbar.vue';
import { useMyStore } from '@/config/my-store';
import { AnimatePresence } from 'motion-v';
import { NLayout, NScrollbar } from 'naive-ui';
import Blob from '@/components/Blob.vue';
import Gradient from '@/components/Gradient.vue';
import Imagebg from '@/components/Imagebg.vue';
import { routerSalt } from '@/utils/common';

const myStore = useMyStore();
</script>
