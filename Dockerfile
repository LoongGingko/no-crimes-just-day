######### NCJD - Docker构建文件 ################
# Docker = 运行
# GitHub Actions = 编译 + 构建镜像

# Docker环境 (超轻量java 17运行环境) 
FROM eclipse-temurin:17-jre-alpine

# 执行目录
WORKDIR /app

# 安装时区数据 (Alpine 默认无时区包)
RUN apk add --no-cache tzdata

# 创建日志目录，同时创建spring用户 (避免root运行导致的权限过大问题，生产环境必备)
RUN addgroup -S spring \
    && adduser -S spring -G spring \
    && mkdir -p /logs \
    && chown -R spring:spring /app /logs

# 环境变量
ENV SPRING_PROFILES_ACTIVE=prod \
    TZ=Asia/Shanghai \
    JAVA_OPTS=""

# 只复制jar包
COPY target/*.jar app.jar

# 后续操作都由spring用户执行，包括启动Java (生产环境必备)
USER spring

# 服务启动在8080端口
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# 默认命令
CMD ["bash"]