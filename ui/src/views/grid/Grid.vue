<!--
  @author       LiuRunYu 2026-04-17
  @description  导航页（替代浏览器的收藏夹，支持文件夹、大文件夹（类似Android桌面）、按列查看、按网格查看、长按一键多开页面等功能。）
-->

<template>
  <div>
    <div class="relative mx-auto mt-4 max-w-5xl px-6 py-24 lg:px-12">
      <!-- 顶部搜索 / 按钮 -->
      <section class="mb-8 flex flex-col items-end gap-4 rounded-3xl p-6 pr-0 md:flex-row md:items-center md:justify-between">
        <div class="self-start text-3xl font-semibold">导航页</div>
        <div class="flex flex-col items-end gap-3 md:flex-row md:items-center">
          <n-input round placeholder="搜索" />
          <!-- <div @click="openDialog()" class="ncjd-btn-2 ncjd-text ncjd-ring text-nowrap rounded-full px-4 py-2">新增</div> -->
          <button @click="edit" class="ncjd-ring h-9 text-nowrap rounded-full px-4" :class="editable ? 'ncjd-btn-r' : 'ncjd-btn'">
            {{ editable ? '取消编辑' : '编辑' }}
          </button>
          <!-- <button v-else @click="edit(false)" class="ncjd-btn-r ncjd-ring text-nowrap rounded-full px-4 py-2">取消编辑</button> -->
        </div>
      </section>

      <!-- 网格布局 -->
      <div class="grid items-start gap-6 md:grid-cols-2">
        <!-- 每个组 -->
        <div v-for="group in groups" :key="group.name" class="ncjd-card ncjd-hover rounded-3xl" :class="{ 'md:col-span-2': group.span }">
          <header class="flex items-center justify-start px-5 pt-4">
            <span class="ml-2 text-lg font-semibold tracking-[0.2em]">{{ group.name || '未分组' }} <span></span></span>
            <span class="ncjd-text ncjd-l2 ml-2 rounded-full px-3 py-1 text-xs font-semibold shadow-inner">{{ group.sites?.length || 0 }}</span>
            <button class="ncjd-btn ncjd-ring ml-auto h-8 rounded-full px-2.5 text-xs backdrop-blur" @click="openAll(group.sites)">Open All</button>
          </header>
          <div class="grid grid-cols-[repeat(auto-fill,100px)] gap-y-5 px-5 py-4" ref="siteRefs">
            <!-- 每个网站 -->
            <div v-for="site in group.sites" :key="site.url" :class="{ 'ncjd-site-hover': !editable }">
              <!-- 图标 / 标签 -->
              <motion.a
                v-if="editable"
                :animate="{ rotate: [-1, 1.5, -1.5, 1, -0.5, 0.5, 0] }"
                :transition="{
                  duration: 0.7,
                  repeat: Infinity,
                  repeatType: 'loop',
                  ease: 'easeInOut',
                }"
                target="_blank"
                :href="fmtUrl(site.url)"
                class="flex cursor-pointer flex-col items-center gap-2 md:gap-3"
              >
                <img class="ncjd-ring ncjd-h h-12 w-12 rounded-full" :src="'https://favicon.im/' + site.url" />
                <div class="max-w-24 truncate text-sm font-semibold">{{ site.label || '未命名' }}</div>
              </motion.a>
              <a v-else target="_blank" :href="fmtUrl(site.url)" class="flex cursor-pointer flex-col items-center gap-2 md:gap-3">
                <img class="ncjd-ring ncjd-h h-12 w-12 rounded-full" :src="'https://favicon.im/' + site.url" />
                <div class="max-w-24 truncate text-sm font-semibold">{{ site.label || '未命名' }}</div>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onBeforeUpdate, useTemplateRef, onMounted } from 'vue';
import { NButton, NDialog, NForm, NFormItem, NInput } from 'naive-ui';
import { CirclePlus } from 'lucide-vue-next';
import { fmtUrl } from '@/utils/common';
import Sortable from 'sortablejs';
import { motion } from 'motion-v';

// 站点接口
interface SiteItem {
  id: number;
  icon: string;
  url: string;
  label: string;
  desc: string;
  group: string;
  sort: number;
}

// 对站点添加排序功能
const siteRefs = useTemplateRef('siteRefs');
let sortables = [] as any;
// 切换排序状态
const editable = ref(false);
const edit = () => {
  editable.value = !editable.value;
  if (editable.value === true) {
    // 创建拖动事件
    initSortable();
  } else {
    // 销毁拖动事件
    sortables.forEach((s: any) => {
      s.destroy();
    });
  }
};

// 创建拖动事件
const initSortable = () => {
  sortables = siteRefs.value?.map((container: any) => {
    return new Sortable(container, {
      sort: true,
      group: { name: 'shared', pull: true, put: true },
      animation: 300,
      delay: 0,
      fallbackTolerance: 3,
      touchStartThreshold: 5, // 触摸设备上的最小移动阈值
      ghostClass: 'sortable-ghost',
      dragClass: 'sortable-drag',
      // fallbackOnBody: true, // // 将cloned DOM 元素挂到body元素上。
    });
  });
};

// 站点
const groups: any[] = [
  {
    name: '开发站点',
    span: 2,
    sites: [
      { icon: '', url: 'github.com', label: 'GitHub', desc: '开发者社区', sort: 1 },
      { icon: '', url: 'www.json.cn', label: 'JSON.cn', desc: 'JSON工具', group: '工具', sort: 2 },
      { icon: '', url: 'stackoverflow.com', label: 'Stack Overflow', desc: '技术问答', sort: 3 },
      { icon: '', url: 'juejin.cn', label: '掘金', desc: '技术社区', sort: 4 },
      { icon: '', url: 'www.csdn.net', label: 'CSDN', desc: '技术博客', sort: 5 },
      { icon: '', url: 'segmentfault.com', label: 'SegmentFault', desc: '技术问答', sort: 6 },
    ],
  },
  {
    name: '资讯',
    span: 2,
    sites: [
      { icon: '', url: 'www.ithome.com', label: 'IT之家', desc: '科技资讯', sort: 1 },
      { icon: '', url: 'www.3dmgame.com', label: '3DM游戏', desc: '游戏社区', sort: 2 },
      { icon: '', url: 'www.xiaohongshu.com', label: '小红书', desc: '兴趣社区', sort: 3 },
      { icon: '', url: 'jandan.net', label: '煎蛋', desc: '幽默段子', sort: 4 },
      { icon: '', url: 'www.v2ex.com', label: 'V2EX', desc: '创意社区', group: '资讯', sort: 5 },
      { icon: '', url: 'sspai.com', label: '少数派', desc: '数字生活', group: '资讯', sort: 6 },
      { icon: '', url: 'www.huxiu.com', label: '虎嗅', desc: '商业科技', group: '资讯', sort: 8 },
      { icon: '', url: 'www.geekpark.net', label: '极客公园', desc: '科技媒体', group: '资讯', sort: 9 },
      { icon: '', url: 'www.google.com', label: 'Google', desc: '', sort: 10 },
      { icon: '', url: 'www.youtube.com', label: 'Youtube', desc: '', sort: 11 },
    ],
  },
  {
    name: '个人网站',
    sites: [
      { icon: '', url: 'www.bicorn.site', label: '风平浪静的一天', desc: '', sort: 1 },
      { icon: '', url: 'cali.so', label: 'Cali Castle', desc: '', sort: 1 },
      { icon: '', url: 'lvyovo-wiki.tech', label: 'lvy-neko', desc: '', sort: 1 },
      { icon: '', url: 'jyblog.cn', label: '倦意博客', desc: '', sort: 1 },
      { icon: '', url: 'furup.me', label: 'Furup 万事屋', desc: '', sort: 1 },
    ],
  },
  {
    name: '机器人',
    sites: [
      { icon: '', url: 'www.doubao.com', label: '豆包', desc: '', sort: 4 },
      { icon: '', url: 'www.deepseek.com', label: 'DeepSeek', desc: '', sort: 5 },
      { icon: '', url: 'gemini.google.com', label: 'Gemini', desc: '', sort: 5 },
      { icon: '', url: 'tongyi.aliyun.com', label: '通义千问', desc: '阿里AI', sort: 2 },
      { icon: '', url: 'kimi.moonshot.cn', label: 'Kimi', desc: '国产AI助手', group: 'AI工具', sort: 4 },
      { icon: '', url: 'www.zhipuai.cn', label: '智谱清言', desc: '清华AI', group: 'AI工具', sort: 6 },
    ],
  },
  {
    name: '创意设计',
    sites: [
      { icon: '', url: 'www.ui.cn', label: 'UI中国', desc: '设计作品', group: '设计', sort: 2 },
      { icon: '', url: 'huaban.com', label: '花瓣网', desc: '灵感采集', group: '设计', sort: 3 },
      { icon: '', url: 'www.pexels.com', label: 'Pexels', desc: '免费图片', group: '设计', sort: 4 },
    ],
  },
];

// 新增Dlg
const dlg = reactive({
  modalView: false,
  raw: null as any,
  form: {
    id: 0,
    icon: '',
    url: '',
    label: '',
    desc: '',
    group: '',
    sort: 1,
  },
});

const dragState = ref<{ item: any; group: string | null }>({
  item: null,
  group: null,
});

// 点击全部打开
const openAll = (sites: any) => {
  // 处理单个 URL：补全协议
  const formatUrl = (url: string) => {
    return url.startsWith('http://') || url.startsWith('https://') ? url : `https://${url}`;
  };

  // 执行打开
  sites.forEach((grid: any, index: any) => {
    setTimeout(() => {
      const fullUrl = formatUrl(grid.url);
      const features = 'noopener,noreferrer'; // 安全性：防止反向访问
      window.open(fullUrl, '_blank', features);
    }, index * 100);
  });
};

function openDialog(item?: SiteItem) {
  // if (item) {
  //   editingId.value = item.id;
  //   form.value = { ...item };
  // } else {
  //   editingId.value = null;
  //   form.value = {
  //     id: 0,
  //     icon: '',
  //     url: '',
  //     label: '',
  //     desc: '',
  //
  //     sort: grids.value.filter(item => item.group === '常用').length + 1,
  //   };
  // }
  // dialogVisible.value = true;
}

function saveItem() {
  // if (!form.value.label.trim() || !form.value.url.trim()) {
  //   message.warning('名称与网址为必填项。');
  //   return;
  // }
  // const normalizedUrl = formatUrl(form.value.url);
  // if (editingId.value) {
  //   const index = grids.value.findIndex(item => item.id === editingId.value);
  //   if (index >= 0) {
  //     grids.value[index] = { ...grids.value[index], ...form.value, url: normalizedUrl };
  //   }
  //   message.success('导航项已更新。');
  // } else {
  //   const nextId = grids.value.length ? Math.max(...grids.value.map(item => item.id)) + 1 : 1;
  //   grids.value.push({
  //     ...form.value,
  //     id: nextId,
  //     url: normalizedUrl,
  //   });
  //   message.success('已新增导航项。');
  // }
  // dialogVisible.value = false;
  // editingId.value = null;
}

function removeItem(id: number) {
  // grids.value = grids.value.filter(item => item.id !== id);
  // message.info('已删除导航项。');
}
</script>

<style scoped>
.ncjd-site-hover {
  /* 出动画 */
  transition: all 0.5s cubic-bezier(0.2, 0.9, 0.4, 1); /* 苹果动画曲线 */

  &:hover {
    transform: scale(1.08);
    /* 入动画 */
    transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  }
}

.sortable-ghost {
  opacity: 0;
}
</style>
