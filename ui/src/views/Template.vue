<!--
  @author       LiuRunYu 2026-04-01
  @description  组件名称
-->

<template>
  <div></div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onBeforeUpdate, useTemplateRef, onMounted } from 'vue';
import { NButton, NDialog, NForm, NFormItem, NInput } from 'naive-ui';
import { CirclePlus } from 'lucide-vue-next';
import { fmtUrl } from '@/utils/common';
import Sortable from 'sortablejs';
import { motion } from 'motion-v';
import { useMyStore } from "@/config/my-store";

const myStore = useMyStore();

</script>

<style lang="less" scoped></style>
