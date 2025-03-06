# 使用 OpenJDK 运行时环境
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 复制 Spring Boot 生成的 jar 文件到容器中
COPY target/cart-service.jar cart-service.jar

# 运行 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "cart-service.jar"]
