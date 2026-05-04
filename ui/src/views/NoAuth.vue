<!--
  @author       LiuRunYu 2025-05-23
  @description  令牌登录失败页
-->

<template>
  <div class="center">
    <n-result status="404" :title="'401' + errMsg" description="总有些门是对你关闭的">
      <template #footer>
        <n-button @click="jump2('/')">放轻松</n-button>
      </template>
    </n-result>
    <!-- <a-result :title="errMsg || '链接无效，请从OA系统进入'"></a-result> -->
  </div>
</template>

<script setup lang="ts">
import { useMyStore } from "@/config/my-store";
import { t } from "@/locales/lang";
import { computed } from "vue";
import { NResult, NButton } from "naive-ui";
import { jump2 } from "@/utils/common";

// 数据持久化
const myStore = useMyStore();

// 显示错误信息
const errMsg = computed(() => {
  if (!myStore.loggedErr) return "";
  const [zh, en] = myStore.loggedErr.split("@@");
  return t(zh, en);
});
</script>

<style lang="less" scoped>
.center > div {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
