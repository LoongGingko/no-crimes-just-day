<!--
  @author       LiuRunYu 2025-05-14
  @description  divWait遮罩层
-->

<template>
  <div id="divWait" v-show="divWaitShow">
    <div id="waitMask" />
    <div id="waitBox">
      <!-- <a-spin dot /> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { divWaitShow } from "@/utils/div-wait";
</script>

<style lang="less" scoped>
#divWait {
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
  z-index: 999;
}
#waitMask {
  background: #999;
  opacity: 0;
  filter: Alpha(opacity=0);
}
#waitBox {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  height: 50px;
  border: 1px solid var(--ty-border);
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: space-between;
  -webkit-backdrop-filter: blur(18px);
  backdrop-filter: blur(18px);
  background-color: #f2f2f233;
  border-radius: 13px;
  width: auto;
  padding: 0 2em;

  > div {
    transform: translate(-7%, -10%);
  }
}
</style>
