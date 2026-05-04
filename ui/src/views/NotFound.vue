<!--
  @author       LiuRunYu 2025-05-09
  @description  404页面
-->
<template>
  <div class="center">
    <n-result status="404" :title="'404'" :description="t('页面未找到', 'Page not found')">
      <template #footer>
        <n-button @click="jump2('/')">我是个失败的孩子</n-button>
      </template>
    </n-result>
  </div>
</template>

<script lang="ts" setup>
import { t } from "@/locales/lang";
import { NResult, NButton } from "naive-ui";
import { jump2 } from "@/utils/common";
import { useAppStore } from "@/config/app-store";
import { computed } from "vue";
const appStore = useAppStore();

// 自动切换亮色/暗色
const isDark = computed(() => appStore.theme !== "light");
</script>

<style scoped lang="css">
.center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
