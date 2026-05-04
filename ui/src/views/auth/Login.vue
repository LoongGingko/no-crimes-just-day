<!--
  @author       LiuRunYu 2026-04-29
  @description  登录/注册页
-->
<template>
  <div class="flex min-h-screen">
    <!-- 背景图片 -->
    <div class="absolute inset-0 bg-[url('/images/login.webp')] bg-cover bg-center bg-no-repeat" />

    <!-- 左侧装饰区 -->
    <div class="relative hidden overflow-hidden lg:flex lg:w-2/3">
      <!-- 遮罩层 -->
      <div class="absolute inset-0 bg-black/30" />
      <!-- 文本 -->
      <div class="z-10 flex w-full flex-col justify-center pl-32 text-xl leading-loose text-white">
        <div>
          <div class="ncjd-slogen -ml-1 mb-2 text-6xl font-bold tracking-wide">
            <span>N</span>
            <span>C</span>
            <span>J</span>
            <span>D</span>
          </div>
          <div class="ncjd-text tracking-widest opacity-80">风平浪静的一天 | No Crimes Just Day</div>
          <div class="ncjd-text mt-8 text-2xl leading-loose opacity-80">又是寻常一天。存了点照片，记了几笔笔记，没干什么大事，但也没搞事<br />"你现在不做，之后会更烦的"</div>
        </div>
        <!-- 幻灯片 -->
        <!-- <div class="absolute bottom-20 left-1/2 flex -translate-x-1/2 gap-3">
          <div class="h-2 w-2 animate-pulse rounded-full bg-white/30"></div>
          <div class="h-2 w-2 animate-pulse rounded-full bg-white/50 delay-75"></div>
          <div class="h-2 w-2 animate-pulse rounded-full bg-white/30 delay-150"></div>
        </div> -->
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="relative flex w-full items-center justify-center p-8 lg:w-1/3">
      <!-- 遮罩层 -->
      <div class="absolute inset-0 bg-black/40 backdrop-blur-xl" />
      <!-- 顶部跳过 -->
      <button @click="jump2('/')" class="absolute right-8 top-8 text-gray-400 transition hover:text-gray-300">跳过</button>
      <!-- 移动端标题 -->
      <div class="z-10 w-full max-w-md">
        <div class="mb-4 text-center lg:hidden">
          <div class="ncjd-slogen mb-2 text-3xl font-bold tracking-wider">
            <span>N</span>
            <span>C</span>
            <span>J</span>
            <span>D</span>
          </div>
          <div class="ncjd-text tracking-widest opacity-80">风平浪静的一天 | No Crimes Just Day</div>
        </div>

        <!-- 表单标题 -->
        <div class="mb-2 h-8 text-center text-2xl tracking-wide"><span v-if="!isLogin">创建账号</span></div>

        <!-- 表单 -->
        <n-form ref="formRef" :model="formData" :rules="formRules" :show-require-mark="false">
          <!-- 用户名 -->
          <n-form-item path="username">
            <n-input v-model:value="formData.username" placeholder="请输入用户名" size="large" @keydown.enter="doLogin">
              <template #prefix>
                <span class="text-gray-400">👤</span>
              </template>
            </n-input>
          </n-form-item>

          <!-- 密码 -->
          <n-form-item path="password">
            <n-input v-model:value="formData.password" type="password" placeholder="请输入密码" size="large" show-password-on="click" @keydown.enter="doLogin">
              <template #prefix>
                <span class="text-gray-400">🔒</span>
              </template>
            </n-input>
          </n-form-item>

          <!-- 图形验证码 -->
          <n-form-item path="captcha">
            <div class="flex w-full gap-3">
              <n-input v-model:value="formData.captcha" placeholder="请输入验证码" size="large" :input-props="{ maxlength: 4 }" @keydown.enter="doLogin" />
              <div class="!w-28 !flex-shrink-0 cursor-pointer overflow-hidden rounded" @click="refreshCaptcha">
                <img :src="captchaUrl" alt="验证码" class="h-full w-full object-cover" />
              </div>
            </div>
          </n-form-item>

          <!-- 提交按钮 -->
          <n-form-item>
            <n-button type="primary" size="large" block :loading="loading" :disabled="loading" @click="doLogin" class="mt-2">
              {{ isLogin ? '登 录' : '注 册' }}
            </n-button>
          </n-form-item>
        </n-form>

        <!-- 切换模式 -->
        <div class="mt-6 text-center">
          <span class="text-sm text-gray-400">
            {{ isLogin ? '还没有账号？' : '已有账号？' }}
          </span>
          <n-button text type="primary" @click="toggleMode" class="ml-1">
            {{ isLogin ? '立即注册' : '立即登录' }}
          </n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { NForm, NFormItem, NInput, NButton } from 'naive-ui';
import type { FormInst, FormRules } from 'naive-ui';
import { $URL } from '@/utils/uri';
import { hasSucc, http } from '@/api/http';
import { useMyStore } from '@/config/my-store';
import { jump2 } from '@/utils/common';
import { useAppStore } from '@/config/app-store';
import { useDarkOnce } from '@/utils/responsive';
import { ping } from '@/api/interceptor';

const router = useRouter();
const myStore = useMyStore();
const appStore = useAppStore();
useDarkOnce(); // 仅该页面为黑暗模式

// 状态
const isLogin = ref(true);
const loading = ref(false);
const formRef = ref<FormInst | null>(null);
const captchaUrl = ref('');

// 刷新图形验证码
const refreshCaptcha = () => {
  captchaUrl.value = $URL('/captcha', { timestamp: new Date().valueOf() });
};

// 页面加载时获取图形验证码
onMounted(() => {
  ping(); // 测试后端连接
  refreshCaptcha();
});

// 表单数据
const formData = ref({
  username: '',
  password: '',
  captcha: '',
});

// 表单验证规则
const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\w{4}$/, message: '请输入4位验证码', trigger: 'blur' },
  ],
};

// 切换登录/注册
const toggleMode = () => {
  isLogin.value = !isLogin.value;
  formData.value = { username: '', password: '', captcha: '' };
  formRef.value?.restoreValidation();
  refreshCaptcha();
};

// 点击登录
const doLogin = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  try {
    loading.value = true;
    await http.req(isLogin.value ? '/login' : '/register', 'post', formData.value, (R: any) => {
      if (hasSucc(R)) {
        debugger;
        myStore.login(R.data);
        jump2('/');
      }
    });
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="less" scoped>
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
