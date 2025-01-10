# 使用 Java 11 作为基础镜像
FROM eclipse-temurin:21-jre 
# 将工作目录设置为 /app
WORKDIR /app 
# 将 JAR 文件添加到镜像中
ADD target/balance-calc-app.jar app.jar 
# 暴露应用程序的端口，假设是 8080
EXPOSE 8080 
# 运行 JAR 文件
CMD ["java", "-jar", "app.jar"] 
