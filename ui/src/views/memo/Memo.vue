<!--
  @author       LiuRunYu 2026-06-26
  @description  笔记页，类flomo设计 — 瀑布流卡片、标签筛选、富文本编辑
-->

<template>
  <div class="memo-page mx-auto max-w-6xl px-6 py-24 lg:px-12">
    <!-- 顶部标题 -->
    <section class="mt-12 mb-6 ml-6 rounded-3xl">
      <div class="self-start text-3xl font-semibold tracking-wide">笔记</div>
      <div class="mt-3 text-sm dark:text-slate-400">记录即思考</div>
    </section>
    <!-- ========== 顶部工具栏 ========== -->
    <div class="mb-6 flex flex-wrap items-center gap-3">
      <n-input v-model:value="searchKeyword" placeholder="搜索笔记..." clearable round class="!w-52" @input="onSearch">
        <template #prefix><Search class="h-4 w-4 text-gray-400" /></template>
      </n-input>

      <div class="flex flex-wrap items-center gap-1.5">
        <button
          v-for="tag in allTags"
          :key="tag"
          class="rounded-full border px-3 py-1 text-xs transition-all duration-200"
          :class="activeTag === tag ? 'border-blue-400 bg-blue-50 text-blue-600 dark:border-blue-500 dark:bg-blue-500/15 dark:text-blue-400' : 'ncjd-ring ncjd-hover text-gray-500 dark:text-gray-400'"
          @click="toggleTag(tag)"
        >
          #{{ tag }}
        </button>
        <button v-if="activeTag" class="ml-1 rounded-full px-2 py-1 text-xs text-gray-400 transition hover:text-gray-600 dark:hover:text-gray-300" @click="activeTag = null">
          <X class="h-3 w-3" />
        </button>
      </div>

      <div class="ml-auto text-xs text-gray-400">{{ filteredMemos.length }} 条笔记</div>
    </div>

    <!-- ========== 瀑布流卡片 ========== -->
    <div v-if="filteredMemos.length > 0" class="masonry-grid">
      <motion.div v-for="memo in filteredMemos" :key="memo.id" :initial="{ opacity: 0, y: 24 }" :animate="{ opacity: 1, y: 0 }" :transition="{ duration: 0.35, ease: 'easeOut' }" class="masonry-item">
        <div class="memo-card ncjd-card group relative cursor-pointer rounded-xl p-4 transition-all duration-300 hover:shadow-lg" @click="openEditor(memo)">
          <!-- 置顶标识 -->
          <div v-if="memo.isPinned === 1" class="mb-2 flex items-center gap-1 text-xs text-amber-500">
            <Pin class="h-3 w-3 fill-current" />
            <span>置顶</span>
          </div>

          <!-- 文本预览 -->
          <div class="memo-preview mb-3 text-sm leading-relaxed text-gray-700 dark:text-gray-300">
            <div v-if="memo.plainText" class="line-clamp-6 whitespace-pre-wrap break-words" v-html="highlightText(memo.plainText)" />
            <span v-else class="italic text-gray-400">空笔记</span>
          </div>

          <!-- 标签 -->
          <div v-if="parsedTags(memo.tags).length > 0" class="mb-3 flex flex-wrap gap-1">
            <span v-for="tag in parsedTags(memo.tags)" :key="tag" class="rounded-full bg-gray-100 px-2 py-0.5 text-xs text-gray-500 dark:bg-gray-700/50 dark:text-gray-400" @click.stop="activeTag = tag"> #{{ tag }} </span>
          </div>

          <!-- 底部信息 -->
          <div class="flex items-center justify-between text-xs text-gray-400">
            <span>{{ memo.dateShow }}</span>
            <span class="opacity-0 transition-opacity group-hover:opacity-100">{{ formatDate(memo.createAt) }}</span>
          </div>

          <!-- 悬浮操作按钮 -->
          <div class="absolute right-2 top-2 flex gap-1 opacity-0 transition-opacity group-hover:opacity-100">
            <button class="rounded-lg p-1.5 text-gray-400 transition hover:bg-amber-50 hover:text-amber-500 dark:hover:bg-amber-500/10" title="置顶" @click.stop="togglePin(memo)">
              <Pin class="h-3.5 w-3.5" :class="memo.isPinned === 1 && 'fill-current text-amber-500'" />
            </button>
            <button class="rounded-lg p-1.5 text-gray-400 transition hover:bg-red-50 hover:text-red-500 dark:hover:bg-red-500/10" title="删除" @click.stop="handleDelete(memo)">
              <Trash2 class="h-3.5 w-3.5" />
            </button>
          </div>
        </div>
      </motion.div>
    </div>

    <!-- 空状态 -->
    <div v-else class="flex flex-col items-center justify-center py-24 text-gray-400">
      <StickyNote class="mb-4 h-16 w-16 opacity-30" />
      <p class="text-sm">还没有笔记，点击右下角按钮开始记录</p>
    </div>

    <!-- ========== 悬浮新建按钮 ========== -->
    <motion.button
      :whileHover="{ scale: 1.08 }"
      :whileTap="{ scale: 0.95 }"
      class="fixed bottom-8 right-8 z-40 flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-500 text-white shadow-lg shadow-blue-500/30 transition-colors hover:bg-blue-600 dark:shadow-blue-500/20"
      @click="openEditor()"
    >
      <Plus class="h-6 w-6" />
    </motion.button>

    <!-- ========== 编辑弹窗 ========== -->
    <n-modal v-model:show="showEditor" :mask-closable="false" preset="card" title="" class="!w-[720px] !max-w-[95vw]" :bordered="false">
      <div class="flex flex-col gap-4">
        <!-- Tiptap 编辑器 -->
        <Tiptap v-model="editorContent" :placeholder="editingMemo ? '编辑笔记...' : '记录此刻的想法...'" />

        <!-- 标签输入 -->
        <div class="flex items-center gap-2">
          <Tag class="h-4 w-4 text-gray-400" />
          <n-dynamic-tags v-model:value="editorTags" :max="8" />
        </div>

        <!-- 底部操作 -->
        <div class="flex items-center justify-between border-t border-gray-100 pt-4 dark:border-gray-700">
          <div class="flex items-center gap-2">
            <button v-if="editingMemo" class="ncjd-btn-r flex items-center gap-1 rounded-lg px-3 py-1.5 text-xs" @click="handleDelete(editingMemo)">
              <Trash2 class="h-3.5 w-3.5" />
              删除
            </button>
          </div>
          <div class="flex items-center gap-2">
            <button class="ncjd-btn ncjd-ring rounded-lg px-4 py-1.5 text-sm" @click="closeEditor">取消</button>
            <button class="rounded-lg bg-blue-500 px-5 py-1.5 text-sm text-white transition-colors hover:bg-blue-600 disabled:opacity-50" :disabled="!editorContent.trim()" @click="saveMemo">
              {{ editingMemo ? '更新' : '发布' }}
            </button>
          </div>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { NInput, NModal, NDynamicTags, useDialog } from 'naive-ui';
import { Search, X, Pin, Trash2, Plus, StickyNote, Tag } from 'lucide-vue-next';
import { motion } from 'motion-v';
import { http, hasSucc } from '@/api/http';
import Tiptap from '@/components/Tiptap.vue';

// ==================== 类型定义 ====================
interface MemoItem {
  id: string;
  userId: string;
  content: string;
  plainText: string;
  tags: string;
  isPinned: number;
  isBanner: number;
  device: string;
  visible: number;
  createAt: string;
  updateAt: string;
  deletedAt: string | null;
}

// ==================== 状态 ====================
const dialog = useDialog();
const memos = ref<MemoItem[]>([]);
const allTags = ref<string[]>([]);
const activeTag = ref<string | null>(null);
const searchKeyword = ref('');
const showEditor = ref(false);
const editingMemo = ref<MemoItem | null>(null);
const editorContent = ref('');
const editorTags = ref<string[]>([]);

// ==================== 计算属性 ====================
/** 过滤 + 排序后的笔记列表 */
const filteredMemos = computed(() => {
  let list = [...memos.value];

  // 标签筛选
  if (activeTag.value) {
    list = list.filter(m => parsedTags(m.tags).includes(activeTag.value!));
  }

  // 关键字搜索
  if (searchKeyword.value.trim()) {
    const kw = searchKeyword.value.trim().toLowerCase();
    list = list.filter(m => m.plainText?.toLowerCase().includes(kw) || parsedTags(m.tags).some(t => t.toLowerCase().includes(kw)));
  }

  // 置顶优先，再按创建时间倒序
  list.sort((a, b) => {
    if (a.isPinned !== b.isPinned) return b.isPinned - a.isPinned;
    return new Date(b.createAt).getTime() - new Date(a.createAt).getTime();
  });

  return list;
});

// ==================== 工具函数 ====================
/** 解析标签字符串 ",tag1,tag2," → ["tag1", "tag2"] */
const parsedTags = (tagStr: string): string[] => {
  if (!tagStr) return [];
  return tagStr.split(',').filter(Boolean);
};

/** 去除 HTML 标签 */
const stripHtml = (html: string): string => {
  if (!html) return '';
  const tmp = document.createElement('div');
  tmp.innerHTML = html;
  return tmp.textContent || tmp.innerText || '';
};

/** 高亮搜索关键字 */
const highlightText = (text: string): string => {
  if (!searchKeyword.value.trim() || !text) return text;
  const escaped = searchKeyword.value.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return text.replace(new RegExp(`(${escaped})`, 'gi'), '<mark class="bg-yellow-200 dark:bg-yellow-500/30 rounded px-0.5">$1</mark>');
};

/** 完整日期格式化 */
const formatDate = (dateStr: string): string => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
};

// ==================== 数据请求 ====================
/** 获取笔记列表 */
const fetchMemos = async () => {
  try {
    const R = await http.req('/memo/list', 'post', {});
    if (hasSucc(R) && Array.isArray(R.data)) {
      memos.value = R.data;
    }
  } catch (e) {
    console.error('获取笔记列表失败:', e);
  }
};

/** 获取所有标签 */
const fetchTags = async () => {
  try {
    const R = await http.req('/memo/pre/memo', 'post');
    if (hasSucc(R) && Array.isArray(R.data)) {
      allTags.value = R.data;
    }
  } catch (e) {
    console.error('获取标签失败:', e);
  }
};

// ==================== 操作 ====================
/** 标签筛选切换 */
const toggleTag = (tag: string) => {
  activeTag.value = activeTag.value === tag ? null : tag;
};

/** 搜索（防抖） */
let searchTimer: ReturnType<typeof setTimeout>;
const onSearch = () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {}, 150);
};

/** 打开编辑器 */
const openEditor = (memo?: MemoItem) => {
  if (memo) {
    editingMemo.value = memo;
    editorContent.value = memo.content || '';
    editorTags.value = parsedTags(memo.tags);
  } else {
    editingMemo.value = null;
    editorContent.value = '';
    editorTags.value = [];
  }
  showEditor.value = true;
};

/** 关闭编辑器 */
const closeEditor = () => {
  showEditor.value = false;
  editingMemo.value = null;
  editorContent.value = '';
  editorTags.value = [];
};

/** 保存笔记 */
const saveMemo = async () => {
  const content = editorContent.value.trim();
  if (!content) return;

  const plainText = stripHtml(content);
  const payload: Record<string, unknown> = {
    content,
    plainText,
    tags: editorTags.value,
    device: 'Web',
  };

  if (editingMemo.value) {
    payload.id = editingMemo.value.id;
    payload.isPinned = editingMemo.value.isPinned;
    payload.visible = editingMemo.value.visible;
  } else {
    payload.isPinned = 0;
    payload.visible = 2;
  }

  try {
    const R = await http.req('/memo/save', 'post', payload);
    if (hasSucc(R)) {
      closeEditor();
      await fetchMemos();
      await fetchTags();
    }
  } catch (e) {
    console.error('保存笔记失败:', e);
  }
};

/** 置顶切换 */
const togglePin = async (memo: MemoItem) => {
  const newPinned = memo.isPinned === 1 ? 0 : 1;
  try {
    const R = await http.req('/memo/save', 'post', {
      id: memo.id,
      isPinned: newPinned,
    });
    if (hasSucc(R)) {
      memo.isPinned = newPinned;
    }
  } catch (e) {
    console.error('置顶操作失败:', e);
  }
};

/** 删除笔记 */
const handleDelete = (memo: MemoItem) => {
  dialog.warning({
    title: '确认删除',
    content: '删除后笔记将进入回收站，确定要删除吗？',
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const R = await http.req('/memo/delete', 'post', { id: memo.id });
        if (hasSucc(R)) {
          if (showEditor.value) closeEditor();
          await fetchMemos();
          await fetchTags();
        }
      } catch (e) {
        console.error('删除笔记失败:', e);
      }
    },
  });
};

// ==================== 生命周期 ====================
onMounted(() => {
  fetchMemos();
  fetchTags();
});
</script>

<style lang="less" scoped>
/* 瀑布流布局 */
.masonry-grid {
  column-count: 4;
  column-gap: 1rem;

  @media (max-width: 1024px) {
    column-count: 3;
  }

  @media (max-width: 768px) {
    column-count: 2;
  }

  @media (max-width: 480px) {
    column-count: 1;
  }
}

.masonry-item {
  break-inside: avoid;
  margin-bottom: 1rem;
}

/* 笔记预览富文本样式重置 */
.memo-preview {
  :deep(h1) {
    font-size: 1.25rem;
    font-weight: 700;
    margin: 0.5rem 0;
  }

  :deep(h2) {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0.4rem 0;
  }

  :deep(h3) {
    font-size: 1rem;
    font-weight: 600;
    margin: 0.3rem 0;
  }

  :deep(p) {
    margin: 0.25rem 0;
  }

  :deep(blockquote) {
    border-left: 3px solid #d1d5db;
    padding-left: 0.75rem;
    color: #9ca3af;
    margin: 0.5rem 0;
  }

  :deep(code) {
    background: rgb(0 0 0 / 6%);
    padding: 1px 5px;
    border-radius: 4px;
    font-size: 0.85em;
  }

  :deep(pre) {
    background: rgb(0 0 0 / 4%);
    padding: 0.75rem;
    border-radius: 8px;
    overflow-x: auto;
    font-size: 0.8em;
  }

  :deep(ul),
  :deep(ol) {
    padding-left: 1.25rem;
    margin: 0.25rem 0;
  }

  :deep(li) {
    margin: 0.15rem 0;
  }

  :deep(mark) {
    background-color: rgb(253 224 71 / 40%);
    border-radius: 2px;
    padding: 0 2px;
  }

  :deep(img) {
    max-width: 100%;
    border-radius: 8px;
    margin: 0.5rem 0;
  }

  :deep(a) {
    color: #3b82f6;
    text-decoration: underline;
  }
}

/* 行截断 */
.line-clamp-6 {
  display: -webkit-box;
  -webkit-line-clamp: 6;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
