/**
 * @author       LiuRunYu 2026-04-29
 * @description  uri工具类
 */

/* ------ window.location 常见属性 --------------
href	      https://bicorn.site:8080/auth?token=1#hash	(常用) 完整 URL	跳转、刷新、获取完整地址
origin	    https://bicorn.site:8080	                  (常用) 构建 API 前缀、跨域判断
protocol	  https:	                                    判断 HTTPS、WebSocket 协议
host	      bicorn.site:8080	                          获取域名+端口
hostname	  bicorn.site	                                获取纯域名
port	      8080	                                      获取端口号
pathname	  /auth	                                      路由匹配、菜单高亮
search	    ?token=1	                                  获取查询参数
hash	      #hash	                                      改变 # 后面的值，浏览器不会刷新页面，也不会向服务器发送任何请求，适用于纯前端跳转
*/

/** URI解析对象 */
export interface UriBean {
  uri: string; // 原始 URI
  module: string; // 模块（路径第一段）
  operate: string; // 操作（路径第二段）
  params: Record<string, string>; // 查询参数
}

/**
 * 解析URI，返回UriBean对象
 * @example getUriBean('/user/edit?id=1') => { module:'user', operate:'edit', params:{id:'1'} }
 */
export function getUriBean(uri = ''): UriBean {
  if (!uri) uri = window.location.pathname + window.location.search; // 例：/users/profile?id=123
  const [path, query = ''] = uri.split('?');

  // 去除首尾斜杠后按 / 分割，过滤空段
  const [module = '', operate = ''] = path
    .replace(/^\/+|\/+$/g, '')
    .split('/')
    .filter(Boolean); // 等同于item => Boolean(item)

  return {
    uri,
    module,
    operate,
    params: Object.fromEntries(new URLSearchParams(query)),
  };
}

// 拼GET路径
export const $URL = (uri: string, ...params: unknown[]) => {
  return addQuery(addPath($HOST, uri), ...params); // 返回baseURL + URI + params
};

// ---------- 工具方法 --------------

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

// 全局baseURL
export const $HOST = addPath(window.location.origin, import.meta.env.VITE_API_BASE_URL);
