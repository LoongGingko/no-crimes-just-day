import { mergeConfig } from 'vite';
import baseConfig from './vite.config';
import compressPlugin from 'vite-plugin-compression';

export default mergeConfig(
  {
    mode: 'production',
    plugins: [
      compressPlugin({
        algorithm: 'gzip', // 压缩算法
        ext: '.gz', // 压缩后文件后缀
        threshold: 10240, // 只压缩大于 10KB 的文件
        verbose: true, // 是否输出压缩日志
        deleteOriginFile: false, // 是否删除源文件
      }),
    ],
    build: {
      sourcemap: false, // 构建后生成source map文件，默认为false
      // rollupOptions: {
      //   output: {
      //     manualChunks: {
      //       vue: ["vue", "vue-router", "pinia", "@vueuse/core", "vue-i18n"],
      //     },
      //   },
      // },
      chunkSizeWarningLimit: 2000, // 当单个文件超过这个大小（KB）时，Vite 会警告
    },
    define: {
      'process.platform': {},
    },
  },
  baseConfig
);
