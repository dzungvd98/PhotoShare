# Sử dụng Java 17 JRE
FROM eclipse-temurin:17-jre

# Tạo thư mục app trong container
WORKDIR /app

# Copy JAR vào container
COPY target/photoshare-0.0.1-SNAPSHOT.jar app.jar

# Expose port ứng dụng (nếu muốn)
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
