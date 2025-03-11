# 选择构建用基础镜像
FROM maven:3.6.0-jdk-8-slim as build

# 指定构建过程中的工作目录
WORKDIR /app

# 复制Maven配置
COPY settings.xml /app/

# 复制父项目的pom.xml
COPY pom.xml /app/

# 复制子模块
COPY sky-common /app/sky-common
COPY sky-pojo /app/sky-pojo
COPY sky-server /app/sky-server

# 执行代码编译命令，使用settings.xml以提高下载速度
RUN mvn -s /app/settings.xml clean package -DskipTests

# 选择运行时基础镜像
FROM alpine:3.13

# 安装依赖包，使用腾讯镜像源提高下载速度
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tencent.com/g' /etc/apk/repositories \
    && apk add --update --no-cache openjdk8-jre-base \
    && rm -f /var/cache/apk/*

# 设置上海时区
RUN apk add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo Asia/Shanghai > /etc/timezone

# 使用 HTTPS 协议访问容器云调用证书安装
RUN apk add ca-certificates

# 指定运行时的工作目录
WORKDIR /app

# 将构建产物jar包拷贝到运行时目录中
COPY --from=build /app/sky-server/target/*.jar /app/app.jar

# 确保证书文件被复制到最终镜像中
COPY --from=build /app/sky-server/src/main/resources/apiclient_key.pem /app/apiclient_key.pem
COPY --from=build /app/sky-server/src/main/resources/wechatpay_6FF64294A34E64F7C99FBE9C20DF022C99749775.pem /app/wechatpay_6FF64294A34E64F7C99FBE9C20DF022C99749775.pem

# 验证证书文件是否存在
RUN ls -la /app/*.pem

# 暴露端口 - 微信云托管使用80端口
EXPOSE 8080

# 添加环境变量，指定服务端口为80
ENV SERVER_PORT=8080

# 执行启动命令，添加日志输出，并设置系统属性以传递证书路径
CMD ["java", "-Dserver.port=80", "-Dlogging.level.root=info", "-Dlogging.level.com.sky=debug", "-Dsky.wechat.privateKeyFilePath=/app/apiclient_key.pem", "-Dsky.wechat.weChatPayCertFilePath=/app/wechatpay_6FF64294A34E64F7C99FBE9C20DF022C99749775.pem", "-jar", "/app/app.jar"] 