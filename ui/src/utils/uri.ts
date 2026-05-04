/**
 * @author       LiuRunYu 2026-04-29
 * @description  uri工具类
 */

// 补充url地址后续部分(自动判断是否带"/")
export const addPath = (path: string | undefined, ...postfix: unknown[]) => {
  postfix.forEach(pf => {
    path = addSlash(path);
    if (!pf || typeof pf !== 'string') return; // 跳过异常参数
    if (pf.startsWith('/'))
      path += pf.substring(1); // 拼路径
    else path += pf;
  });
  return path!;
};

// 补充后导"/"
export const addSlash = (path?: string) => {
  if (!path) return '/';
  if (path.endsWith('/')) return path;
  return `${path}/`;
};

// 补充url地址参数(自动添加"?"或"&"，支持格式如：xxx=yyy;[xxx,yyy])
export const addQuery = (path: string, ...qs: unknown[]) => {
  if (!qs || qs.length === 0) return path;
  let separate = path.indexOf('?') > 0 ? '&' : '?'; // 可能是第一个param
  qs.forEach(q => {
    // 数组
    if (Array.isArray(q)) {
      path += `${separate + q[0]}=${q[1]}`; // 格式：[xxx,yyy]
      // 对象
    } else if (typeof q === 'object') {
      Object.keys(q!).forEach(key => {
        path += `${separate + key}=${(q as any)[key]}`;
        separate = '&'; // 后续param用&连接
      });
      // 字符串
    } else path += separate + q; // 格式：xxx=yyy
    separate = '&'; // 后续param用&连接
  });
  return path;
};

export const $URL = (uri: string, ...params: unknown[]) => {
  return addQuery(addPath($HOST, uri), ...params); // 返回baseURL + URI + params
};

// 全局baseURL
export const $HOST = addPath(window.location.origin, import.meta.env.VITE_API_BASE_URL);
