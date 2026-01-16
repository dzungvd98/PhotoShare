# Sử dụng Java 17 JRE
FROM eclipse-temurin:17-jre

# Tạo thư mục app trong container
WORKDIR /app

# Copy JAR vào container
COPY target/photoshare-0.0.1-SNAPSHOT.jar app.jar

# Tạo thư mục upload và set quyền (THÊM DÒNG NÀY)
RUN mkdir -p /upload/images && chmod -R 777 /upload

# Expose port ứng dụng
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
