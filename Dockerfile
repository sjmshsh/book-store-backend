# 最小的JDK镜像 根据开发版本选择
FROM openjdk:8-jre-alpine

# 开发者信息
LABEL maintainer="1575197604@qq.com"
#复制打好的jar包

# 将打包好的jar文件复制并改名
COPY target/*.jar /app.jar
RUN  apk add -U tzdata; \
ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime; \
echo 'Asia/Shanghai' >/etc/timezone; \
touch /app.jar;
ENV JAVA_OPTS=""
ENV PARAMS=""

# springboot 项目的端口
EXPOSE 8088

ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar /app.jar $PARAMS" ]

