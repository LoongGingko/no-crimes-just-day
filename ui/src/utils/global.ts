/**
 * @author       LiuRunYu 2026-05-04
 * @description  全局消息工具（懒加载）
 */
/**
 * Naive UI 离散 API 工具
 * 用于在非组件环境（router、store、utils 等）中使用消息/对话框等功能
 */
import { useAppStore } from '@/config/app-store';
import { createDiscreteApi, type ConfigProviderProps, lightTheme, darkTheme, type DiscreteApi } from 'naive-ui';
import { computed } from 'vue';

let api: DiscreteApi<'message' | 'notification' | 'loadingBar' | 'dialog'> | null = null;

function getApi() {
  if (api) return api;

  // 脱离上下文的API，需指定主题
  const appStore = useAppStore();
  const configProviderPropsRef = computed<ConfigProviderProps>(() => ({
    theme: appStore.theme !== 'light' ? darkTheme : lightTheme,
  }));

  api = createDiscreteApi(['message', 'notification', 'dialog', 'loadingBar'], { configProviderProps: configProviderPropsRef });

  return api;
}

/** 创建懒代理，首次访问属性时才初始化 */
function createProxy<T extends keyof DiscreteApi>(key: T): DiscreteApi[T] {
  return new Proxy(
    {} as DiscreteApi[T], // 代理目标，一个空对象，实际不会被用到
    {
      // 拦截所有属性访问，比如 message.success / message.error
      get: (_, prop) => {
        const _api = getApi(); // 拿到 api 懒加载实例
        const _sub = _api[key] as any; // 例：_api['message']
        return _sub[prop];
      },
    }
  );
}

export const messager = createProxy('message');
export const dialog = createProxy('dialog');
export const notification = createProxy('notification');
export const loadingBar = createProxy('loadingBar');

/* 
使用示例：

message.success('操作成功')
message.error('请求失败')
message.warning('请注意')
message.loading('加载中...')

dialog.warning({
  title: '确认删除',
  content: '此操作不可撤销，是否继续？',
  positiveText: '确认',
  negativeText: '取消',
  onPositiveClick: () => {}
})
*/
