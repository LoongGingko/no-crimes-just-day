import { useMyStore } from '@/config/my-store';
import { downloadATag, isEmpty } from '@/utils/common';
import { messager } from '@/utils/global';
import { $HOST } from '@/utils/uri';
import Axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios';

// 全局axios参数配置
Axios.defaults.timeout = 360000; // 360秒=6分钟timeout
Axios.defaults.withCredentials = true; // 支持跨域时传递cookie
Axios.defaults.baseURL = $HOST; // 默认是'/api/'

// 通用Axios请求对象
class Http {
  private axios: AxiosInstance;

  constructor() {
    this.axios = Axios; // 继承全局axios参数配置
  }

  req(url: string, method: string, info?: any, callBack?: any): Promise<any> {
    return this.axios
      .request(this.getAxiosConfig(url, method, info))
      .then((resp: any) => {
        if (useMyStore().pingDebug('axios')) debugger; // 开发者断点
        // 成功且无返回数据时resp.data为空字符串
        if (!resp || resp.data === null || resp.data === undefined) {
          console.log('req无数据:', resp);
          throw new Error('req无数据');
        }
        const { data } = resp;
        return execData(data, callBack);
      })
      .catch((err: any) => {
        console.error(`req: ${url}\r\n`, err);
        throw err;
      });
  }

  // 返回文件下载时使用
  reqBlob(url: string, method: string, info?: any, callBack?: any): Promise<any> {
    return this.axios
      .request({ ...this.getAxiosConfig(url, method, info), responseType: 'blob' })
      .then((resp: any) => {
        if (useMyStore().pingDebug('axios')) debugger; // 开发者断点
        if (!resp || resp.data === null || resp.data === undefined) {
          console.log('reqBlob无数据:', resp);
          throw new Error('reqBlob无数据');
        }
        // 文件名
        if (isEmpty(resp.headers['content-disposition'])) {
          messager.error('未找到下载数据！');
          return;
        }
        const downName = resp.headers['content-disposition'].split(';')[1].split('filename=')[1];
        downloadATag(new Blob([resp.data]), downName);
        if (callBack) callBack();
      })
      .catch((err: any) => {
        console.error(`reqBlob: ${url}\r\n`, err);
        throw err;
      });
  }

  // 封装请求参数
  getAxiosConfig(url: string, method: string, info?: any): AxiosRequestConfig {
    method = method.toLowerCase();
    const config: AxiosRequestConfig = { url, method };
    const paramsMethods = ['get', 'delete', 'head', 'options'];
    if (info && paramsMethods.includes(method)) {
      config.params = info;
    } else {
      config.data = info;
    }
    return config;
  }
}

// 导出单例
export const http = new Http();

// 特殊用途实例（仅覆盖修改字段）
export const uploadHttp = Axios.create({
  headers: { 'Content-Type': 'multipart/form-data' },
});

// 下载实例（修改响应类型）
export const downloadHttp = Axios.create({
  responseType: 'blob',
});

// 执行回调
const execData = (R: any, callBack?: any) => {
  http_msg(R);
  if (callBack) callBack(R);
  return R;
};

// 显示业务信息
const http_msg = (R: any) => {
  if (isEmpty(R)) return;
  const success = R.success || []; // 业务成功消息
  const error = R.error || []; // 业务失败消息
  const msglength = (success ? success.length : 0) + (error ? error.length : 0);
  const eraseSeconds = 5000 + (1000 * msglength) / 20;
  if (!isEmpty(success)) messager.success(success, { duration: eraseSeconds });
  if (!isEmpty(error)) messager.error(error, { duration: eraseSeconds });
};

// 请求是否成功
export const hasSucc = (R: any) => {
  return R.code === 200;
};
