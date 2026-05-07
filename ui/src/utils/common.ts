/**
 * @author       LiuRunYu 2026-04-08
 * @description  通用工具类
 */

import { nextTick, ref } from 'vue';
import router from '../router';
import { useMyStore } from '@/config/my-store';

// =============================================================================
//                                 业务工具
// =============================================================================
// export const showInvalid = () => {
//   messager.error('从技术上来说，这个功能还在开发中。\n就像宇宙中的暗物质，虽然知道它存在，但还需要时间来发现它。', { icon: () => h(Heart) });
// };

// =============================================================================
//                                 路由工具
// =============================================================================
// 刷新当前路由
export const routerSalt = ref(0);
export function reloadRouter() {
  routerSalt.value = Date.now();
}

// 跳转到新路由
export function jump2(path: string, reload?: boolean) {
  router.push({ path });
  if (reload) reloadRouter();
}

// 跳转到上个路由
export function goBack(reload?: boolean) {
  const myStore = useMyStore();
  if (isEmpty(myStore.pre_route)) {
    router.push({ path: '/' });
  } else {
    router.push({ path: myStore.pre_route });
  }
  if (reload) reloadRouter();
}

// 打开新页面
export function open2(url: string, target?: string, features?: string) {
  window.open(fmtUrl(url), target || '_blank', features);
}

// 拼URL：注意uri开头无斜杠
// export function $URL(uri: string, path?: string) {
//   if (isEmpty(uri)) return '';
//   return window.location.origin + $HOST + (path || 'static/') + uri;
// }

export const fmtUrl = (url: string) => {
  return url.startsWith('http://') || url.startsWith('https://') ? url : `https://${url}`;
};

// =============================================================================
//                               数据类型工具
// =============================================================================
// 是否空对象
export function isEmpty(obj: any) {
  if (obj == null || obj === undefined) {
    return true;
  }
  if (typeof obj === 'string' && obj.length === 0) {
    return true;
  }
  if (typeof obj === 'number' && Number.isNaN(obj)) {
    return true;
  }
  if (typeof obj === 'object') {
    if (Array.isArray(obj) && obj.length === 0) {
      return true; // 空数组
    }
    if (obj instanceof Date) {
      return false; // 日期型总是非空
    }
    if (Object.keys(obj).length === 0) {
      return true; // 空对象
    }
  }
  return false;
}

// =============================================================================
//                                 日期工具
// =============================================================================
// 日期转为YYYY-MM-DD HH_mm_ss格式
export function getDateTimeStr(date?: Date) {
  if (!date) date = new Date();
  const pad = (n: any) => String(n).padStart(2, '0');
  // 不安装dayjs, 手动拼字符串
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ` + `${pad(date.getHours())}_${pad(date.getMinutes())}_${pad(date.getSeconds())}`;
}

// =============================================================================
//                               浏览器工具
// =============================================================================
// 复制文本到剪贴板 (防止浏览器不支持 Clipboard API)
export function copyToClipboard(value: string, succMsg: string) {
  const input = document.createElement('input');
  input.value = value;
  document.body.appendChild(input);
  input.select();
  try {
    const success = document.execCommand('copy');
    // messager.vToast(success ? { text: succMsg, type: "success" } : { text: "复制失败".$td("Copy failed"), type: "error" });
  } catch (err) {
    console.error('Text复制出错: ', err);
  } finally {
    document.body.removeChild(input);
  }
}

// 复制html结构到剪贴板 (防止浏览器不支持 Clipboard API)
export function copyToClipboardHtml(html: string, succMsg: string) {
  // 创建一个临时容器
  const container = document.createElement('div') as HTMLElement;
  container.contentEditable = 'true';
  container.style.position = 'absolute';
  container.style.left = '-9999px'; // 隐藏容器
  container.innerHTML = html;

  // 处理特殊元素
  container.querySelectorAll('mathml').forEach(ele => {
    (ele as HTMLElement).style.display = 'block';
  });
  container.querySelectorAll('mjx-container').forEach(ele => {
    (ele as HTMLElement).style.display = 'none';
  });
  container.querySelectorAll('table').forEach(ele => {
    (ele as HTMLElement).style.width = '100%';
  });

  // 选中并复制
  document.body.appendChild(container);
  const range = document.createRange();
  range.selectNodeContents(container);
  const sel = window.getSelection()!;
  sel.removeAllRanges();
  sel.addRange(range);
  try {
    const success = document.execCommand('copy');
    // messager.vToast(success ? { text: succMsg, type: "success" } : { text: "复制失败".$td("Copy failed"), type: "error" });
  } catch (err) {
    console.error('HTML复制出错: ', err);
  } finally {
    document.body.removeChild(container);
    sel.removeAllRanges();
  }
}

// 附件下载
export function downloadATag(blobData: Blob | MediaSource, name: string) {
  // 动态下载
  const a = document.createElement('a');
  a.href = window.URL.createObjectURL(blobData);
  a.download = name;
  a.click();
}

// 自动播放幻灯片视频
document.addEventListener('DOMContentLoaded', function () {
  // 每0.5秒检查一次
  var interval = setInterval(function () {
    // 找这个元素 .main-slideshow > rs-module-wrap > rs-module
    var box = document.getElementsByClassName('defaultimg')[0];
    if (box) {
      clearInterval(interval);
      box.innerHTML =
        '<div class="html5vid" style="position: relative; top: 0px; left: 0px; width: 100%; height: 100%; overflow: hidden; transition: none; text-align: inherit; line-height: 0px; border-width: 0px; margin: 0px; padding: 0px; letter-spacing: 0px; font-weight: 400; font-size: 14px; z-index:99;"><video autoplay muted style="object-fit: cover; background-size: cover; width: 100%; height: 100%; transition: none; text-align: inherit; line-height: 0px; border-width: 0px; margin: 0px; padding: 0px; letter-spacing: 0px; font-weight: 400; font-size: 14px; display: block; visibility: inherit; opacity: 1;" class="" loop="" preload="auto"><source src="https://qingpu.suis.com.cn/wp-content/uploads/sites/27/2025/04/协和青浦网站首页视频202410.mp4" type="video/mp4" style="transition: none; text-align: inherit; line-height: 0px; border-width: 0px; margin: 0px; padding: 0px; letter-spacing: 0px; font-weight: 400; font-size: 16px;"></video></div>';
    }
  }, 500);
});
