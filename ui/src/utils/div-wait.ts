import type { AxiosRequestConfig } from "axios";
import { isEmpty } from "./common";
import { ref } from "vue";

export const divWaitShow = ref(false);
const divWaitIsLoading = {
  loading: {},
} as any;

// 显示loading弹出层
export function showLoadingAxios(config?: AxiosRequestConfig) {
  if (config?.url?.includes("/auth/verify")) return; // 不显示divWait的请求
  loadingHandler(1); // 加载请等待标志
}

// 隐藏loading弹出层
export function hideLoadingAxios() {
  loadingHandler(-1); // 解除请等待标志
}

// loading控制器
function loadingHandler(sign: 1 | -1) {
  const { loading } = divWaitIsLoading;
  if (sign === 1) {
    // 开始loading
    divWaitShow.value = true; // 第一次加载，显示divWait
    divWaitIsLoading.lastRunTime = new Date().getTime(); // 最新一次设置标志位的时间戳
    return;
  }
  // 结束loading
  const timePass = new Date().getTime() - divWaitIsLoading.lastRunTime;
  const maxTimePass = 30000; // 距离上次ajax调用过30秒未响应，可立刻清除divWait
  const duration = 300; // 至少停留0.3秒，以免一闪而过，影响视觉效果
  // 判断是否需要隐藏
  if (timePass > maxTimePass) {
    divWaitIsLoading.loading = {}; // 清空loading变量（容错）
    divWaitShow.value = false; // 隐藏divWait（超过30秒未响应）
  } else if (isEmpty(loading) && timePass >= duration) {
    divWaitShow.value = false; // 隐藏divWait（计数器已清空）
  } else if (isEmpty(loading)) {
    asyncCloseHandler(loading, duration - timePass); // 定时隐藏divWait（防抖）
  }
}

// 定时隐藏divWait
function asyncCloseHandler(loading: any, time: number) {
  setTimeout(() => {
    // 如果等待过程中新的ajax进入，则取消本次隐藏操作
    if (isEmpty(loading)) {
      divWaitShow.value = false; // 隐藏divWait
    }
  }, time);
}
