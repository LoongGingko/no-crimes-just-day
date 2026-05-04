<!--
  @author       LiuRunYu 2026-04-19
  @description  施工页面
-->

<template>
  <div class="center">
    <n-result status="404" title="页面施工中..." description="总有些门是对你关闭的">
      <template #footer>
        <n-button @click="jump2('/')">放轻松</n-button>
      </template>
    </n-result>
    <!-- <a-result :title="errMsg || '链接无效，请从OA系统进入'"></a-result> -->
  </div>
</template>

<script setup lang="ts">
import { useMyStore } from '@/config/my-store';
import { NResult, NButton } from 'naive-ui';
import { jump2 } from '@/utils/common';

// 数据持久化
const myStore = useMyStore();
</script>

<style lang="less" scoped>
.center > div {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
