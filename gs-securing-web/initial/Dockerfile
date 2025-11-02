# 1. Chọn base image Java 17
FROM eclipse-temurin:17-jdk-alpine
#test
# 2. Set working directory
WORKDIR /app

# 3. Copy file jar vào container
COPY target/securing-web-initial-0.0.1-SNAPSHOT.jar app.jar

# 4. Expose port mà ứng dụng chạy
EXPOSE 8085

# 5. Chạy ứng dụng
ENTRYPOINT ["java","-jar","app.jar"]
