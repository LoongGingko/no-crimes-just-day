import { mergeConfig } from "vite";
import eslint from "vite-plugin-eslint";
import baseConfig from "./vite.config";

export default mergeConfig(
  {
    mode: "development",
    server: {
      host: "0.0.0.0", // 允许局域网内访问
      port: 8081, // 前端访问端口
      open: true, // 启动后自动打开浏览器
      proxy: {
        "/api": {
          target: "http://localhost:8080", // api转发到后端接口
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ""),
        },
      },
      fs: {
        strict: true, // 安全性增强：Vite只能访问根目录内的文件
      },
    },
  },
  baseConfig
);
