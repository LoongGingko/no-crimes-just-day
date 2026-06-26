# NCJD 项目守则

## 前端部分

- 框架：Vue 3 + TypeScript + Vite
- 样式：TailwindCSS + Less，能用 TailwindCSS 工具类就不要自己写样式
- 图标：统一使用 lucide-vue-next
- UI 组件：Naive UI
- 状态管理：Pinia
- npm安装扩展之前，必须和用户确认是否需要安装
- 文件头必须包含 `@author` 和 `@description` 注释
- 方法必须加简洁的双斜杠注释

## 后端部分

- 框架：Spring Boot + Java + MyBatis-Plus
- 分层架构：controller → service → mapper
- 工具：Lombok + Hutool
- 统一返回：`R<T>` 封装结果
- 判空统一用 StrUtil.isBlank() + CollUtil.isEmpty()
- 文件头、方法必须包含 JavaDoc 注释
- DTO 对象属性必须包含注释
- 对只有一个参数的 @RequestParam，不用创建DTO，直接用参数类型接收
- 修改POM文件之前，必须和用户确认是否需要修改
- 数据库自动维护时间戳：create_at, update_at
- 日期统一用 LocalDateTime.now()
