/**
 * @author       LiuRunYu 2026-04-29
 * @description  全局缓存（刷新后丢失）
 */

import { isEmpty } from './common';

// utils/cache.js
class Cache {
  private cache: Map<string, any>;

  constructor() {
    this.cache = new Map();
  }

  // set(key: string, value: any, ttl = null) {
  //   if (ttl) {
  //     const expires = Date.now() + ttl;
  //     this.cache.set(key, { value, expires });
  //   } else {
  //     this.cache.set(key, value);
  //   }
  // }

  set(key: string, value: any) {
    this.cache.set(key, value);
  }

  get(key: string) {
    const item = this.cache.get(key);

    // 缓存不存在
    if (isEmpty(item)) return null;

    // 检查是否过期
    // if (item && item.expires && item.expires < Date.now()) {
    //   this.cache.delete(key);
    //   return null;
    // }

    return item;
  }

  has(key: string) {}

  delete(key: string) {
    this.cache.delete(key);
  }

  clear() {
    this.cache.clear();
  }

  // 清理过期缓存
  // cleanExpired() {
  //   for (const [key, item] of this.cache.entries()) {
  //     if (item.expires && item.expires < Date.now()) {
  //       this.cache.delete(key);
  //     }
  //   }
  // }
}

// 导出单例
export const myCache = new Cache();

// 获取后台传值
export function getModel() {
  return myCache.get('model');
}
