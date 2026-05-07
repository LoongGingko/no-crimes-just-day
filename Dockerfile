######### NCJD - Docker构建文件 ################
# Docker = 运行
# GitHub Actions = 编译 + 构建镜像

# Docker环境 (超轻量java 17运行环境) 
FROM eclipse-temurin:17-jre-alpine

# 执行目录
WORKDIR /app

# 创建spring用户 (避免root运行导致的权限过大问题，生产环境必备)
RUN addgroup -S spring && adduser -S spring -G spring

# 环境变量
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

# 复制jar包
COPY target/*.jar app.jar

# 创建日志目录，改成spring用户
RUN mkdir -p /logs && chown -R spring:spring /app /logs

# 后续操作都由spring用户执行，包括启动Java (生产环境必备)
USER spring

# 服务启动在8080端口
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]





#WORKDIR /workspace
#
#COPY pom.xml ./
#RUN mvn -B -DskipTests dependency:go-offline
#
#COPY src src
#RUN mvn -B -DskipTests clean package
#
#FROM eclipse-temurin:17-jre-alpine
#
#WORKDIR /app
#
#RUN addgroup -S spring && adduser -S spring -G spring
#
#ENV SPRING_PROFILES_ACTIVE=prod \
#    JAVA_OPTS=""
#
#COPY --from=build /workspace/target/*.jar app.jar
#
#RUN mkdir -p /logs && chown -R spring:spring /app /logs
#
#USER spring
#
#EXPOSE 8080
#
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]





## 基础镜像: 365定制镜像
#FROM 365-custom-debian-jre:latest
#LABEL maintainer="TengSource:2025-04-30"
#
#
## 拷贝365yunhr压缩包
#COPY 365-hrm-1.0.tar.gz /var/lib/
#COPY dist.tar.gz /var/lib/
## COPY template.sql /var/lib
#
#
## 拷贝启动脚本
#COPY entrypoint.sh /entrypoint.sh
#
#
## 构建时赋予entrypoint.sh权限
#RUN chmod +x /entrypoint.sh
#
#
## 开机时执行启动脚本
## - 首次运行时解压jdk, tomcat, 365hrmmt, 365-ui (完成后清理压缩包)
## - 首次运行时创建365hrmmtdata软连接
## - 随容器同步启动/关闭tomcat
#ENTRYPOINT ["/entrypoint.sh"]


# 默认命令
CMD ["bash"]


