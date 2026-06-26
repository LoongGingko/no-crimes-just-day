<!--
  @author       LiuRunYu 2026-06-23
  @description  富文本编辑器组件
-->

<template>
  <div class="ncjd-card rounded-lg border border-gray-200">
    <!-- 工具栏 -->
    <div class="flex flex-wrap items-center gap-1 border-b border-gray-200 p-2" v-if="!readonly">
      <!-- 撤销/重做 -->
      <n-button size="small" :disabled="!editor?.can().undo()" @click="editor?.chain().focus().undo().run()"><Undo2 class="h-4 w-4" /></n-button>
      <n-button size="small" :disabled="!editor?.can().redo()" @click="editor?.chain().focus().redo().run()"><Redo2 class="h-4 w-4" /></n-button>
      <div class="mx-1 h-6 w-px bg-gray-200 text-slate-500" />

      <!-- 文本格式 -->
      <n-button size="small" :quaternary="!editor?.isActive('bold')" @click="editor?.chain().focus().toggleBold().run()"><Bold class="h-4 w-4" /></n-button>
      <n-button size="small" :quaternary="!editor?.isActive('italic')" @click="editor?.chain().focus().toggleItalic().run()"><Italic class="h-4 w-4" /></n-button>
      <n-button size="small" :quaternary="!editor?.isActive('strike')" @click="editor?.chain().focus().toggleStrike().run()"><Strikethrough class="h-4 w-4" /></n-button>
      <n-button size="small" :quaternary="!editor?.isActive('underline')" @click="editor?.chain().focus().toggleUnderline().run()"><UnderlineIcon class="h-4 w-4" /></n-button>
      <n-button size="small" :quaternary="!editor?.isActive('highlight')" @click="editor?.chain().focus().toggleHighlight().run()"><Highlighter class="h-4 w-4" /></n-button>
      <div class="mx-1 h-6 w-px bg-gray-200" />

      <!-- 标题 -->
      <n-button size="small" :quaternary="!editor?.isActive('heading', { level: 1 })" @click="editor?.chain().focus().toggleHeading({ level: 1 }).run()"> H1 </n-button>
      <n-button size="small" :quaternary="!editor?.isActive('heading', { level: 2 })" @click="editor?.chain().focus().toggleHeading({ level: 2 }).run()"> H2 </n-button>
      <n-button size="small" :quaternary="!editor?.isActive('heading', { level: 3 })" @click="editor?.chain().focus().toggleHeading({ level: 3 }).run()"> H3 </n-button>
      <div class="mx-1 h-6 w-px bg-gray-200" />

      <!-- 代码 -->
      <n-button size="small" :quaternary="!editor?.isActive('code')" @click="editor?.chain().focus().toggleCode().run()"><Code class="h-4 w-4" /></n-button>
      <n-button size="small" :quaternary="!editor?.isActive('codeBlock')" @click="editor?.chain().focus().toggleCodeBlock().run()"><Code2 class="h-4 w-4" /></n-button>

      <!-- 引用 -->
      <n-button size="small" :quaternary="!editor?.isActive('blockquote')" @click="editor?.chain().focus().toggleBlockquote().run()"><Quote class="h-4 w-4" /></n-button>
      <div class="mx-1 h-6 w-px bg-gray-200" />

      <!-- 列表 -->
      <n-button size="small" :quaternary="!editor?.isActive('bulletList')" @click="editor?.chain().focus().toggleBulletList().run()"><List class="h-4 w-4" /></n-button>
      <n-button size="small" :quaternary="!editor?.isActive('orderedList')" @click="editor?.chain().focus().toggleOrderedList().run()"><ListOrdered class="h-4 w-4" /></n-button>
      <n-button size="small" :disabled="!editor?.can().liftListItem('listItem')" @click="editor?.chain().focus().liftListItem('listItem').run()"><Outdent class="h-4 w-4" /></n-button>
      <n-button size="small" :disabled="!editor?.can().sinkListItem('listItem')" @click="editor?.chain().focus().sinkListItem('listItem').run()"><Indent class="h-4 w-4" /></n-button>
    </div>

    <!-- 编辑器 -->
    <div class="min-h-[200px] p-3">
      <EditorContent :editor="editor" />
    </div>

    <!-- 底部栏 -->
    <div class="px-3 py-2 flex justify-end items-center text-xs text-gray-400" v-if="!readonly">
      <span>{{ wordCount }} 字</span>
      <button class="ml-3 ncjd-btn ncjd-ring h-8 rounded-full px-2.5 text-xs backdrop-blur"><Save/></button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { watch, computed } from 'vue';
import { useEditor, EditorContent } from '@tiptap/vue-3';
import StarterKit from '@tiptap/starter-kit';
import Underline from '@tiptap/extension-underline';
import Highlight from '@tiptap/extension-highlight';
import Placeholder from '@tiptap/extension-placeholder';
import { NButton } from 'naive-ui';
import { Bold, Italic, Underline as UnderlineIcon, Highlighter, List, ListOrdered, Outdent, Indent, Undo2, Redo2, Strikethrough, Code, Code2, Quote, Save } from 'lucide-vue-next';

// 组件 Props
interface TiptapProps {
  modelValue?: string;
  readonly?: boolean;
  placeholder?: string;
}

const props = withDefaults(defineProps<TiptapProps>(), {
  modelValue: '',
  readonly: false,
  placeholder: '请输入内容...',
});

// 组件 Emits
const emit = defineEmits(['update:modelValue']);

// 编辑器实例
const editor = useEditor({
  editable: !props.readonly,
  extensions: [
    StarterKit.configure({
      heading: { levels: [1, 2, 3] },
    }),
    Underline,
    Highlight.configure({
      multicolor: false,
    }),
    Placeholder.configure({
      placeholder: props.placeholder,
    }),
  ],
  content: props.modelValue,
  // 内容变化时触发
  onUpdate: ({ editor }) => {
    emit('update:modelValue', editor.getHTML());
  },
});

// 字数统计
const wordCount = computed(() => {
  if (!editor.value) return 0;
  const text = editor.value.getText();
  return text.replace(/\s/g, '').length;
});

// 监听外部传入的内容变化
watch(
  () => props.modelValue,
  newVal => {
    if (editor.value && newVal !== editor.value.getHTML()) {
      editor.value.commands.setContent(newVal);
    }
  }
);

// 监听只读模式变化
watch(
  () => props.readonly,
  newVal => {
    if (editor.value) {
      editor.value.setEditable(!newVal);
    }
  }
);
</script>

<style lang="less" scoped>
:deep(.ProseMirror) {
  min-height: 200px;
  outline: none;

  /* placeholder */
  p.is-editor-empty:first-child::before {
    content: attr(data-placeholder);
    float: left;
    color: var(--ncjd-color-text-ph);
    pointer-events: none;
    height: 0;
  }

  h1 {
    font-size: 2em;
    font-weight: bold;
    margin: 0.67em 0;
  }

  h2 {
    font-size: 1.5em;
    font-weight: bold;
    margin: 0.83em 0;
  }

  h3 {
    font-size: 1.17em;
    font-weight: bold;
    margin: 1em 0;
  }

  /* 删除线 */
  strike {
    text-decoration: line-through;
  }

  /* code {
    background-color: #f5f5f5;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: monospace;
  } */

  /* pre {
    background-color: #f5f5f5;
    padding: 12px;
    border-radius: 8px;
    overflow-x: auto;
    font-family: monospace;
  } */

  /* blockquote {
    border-left: 4px solid #ddd;
    padding-left: 16px;
    margin-left: 0;
    color: #666;
  } */
}
</style>
