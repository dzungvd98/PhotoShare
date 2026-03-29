# PhotoShare Backend (Hệ thống chia sẻ ảnh)

Backend API cho hệ thống mạng xã hội chia sẻ ảnh, xây dựng với Spring Boot 3, PostgreSQL, Redis, JWT và Cloudflare R2.

## 1. Công nghệ đang dùng

- Java 17
- Spring Boot 3.5.7
- Spring Security + JWT
- Spring Data JPA (PostgreSQL)
- Spring Data Redis
- Spring Mail
- Springdoc OpenAPI (Swagger UI)
- Cloudflare R2 (S3-compatible)
- Maven
- Docker, Docker Compose

## 2. Tính năng chính

- Đăng ký, đăng nhập, refresh token, đăng xuất
- Xác minh tài khoản bằng OTP
- Quản lý ảnh: tạo, sửa, xóa, xem chi tiết
- Duyệt ảnh chờ phê duyệt
- Feed ảnh: mới nhất, phổ biến, theo dõi
- Bình luận và trả lời bình luận
- Like ảnh
- Theo dõi người dùng
- Trang cá nhân và chỉnh sửa hồ sơ
- Quản trị người dùng: đổi trạng thái, đổi role
- Báo cáo vi phạm và xử lý báo cáo
- Dashboard thống kê

## 3. Cấu trúc dự án

```
PhotoShare/
|-- src/
|   |-- main/
|   |   |-- java/com/dev/photoshare/
|   |   |   |-- config/
|   |   |   |-- controller/
|   |   |   |-- dto/
|   |   |   |-- entity/
|   |   |   |-- exception/
|   |   |   |-- repository/
|   |   |   |-- security/
|   |   |   |-- service/
|   |   |   |-- usecase/
|   |   |   \-- utils/
|   |   \-- resources/
|   |       |-- application.yaml
|   |       \-- templates/mail/otp-email.html
|   \-- test/
|       \-- java/
|-- init/
|   \-- init.sql
|-- upload/images/
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
\-- README.md
```

## 4. Yêu cầu môi trường

- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Redis 7+
- Docker + Docker Compose (nếu chạy container)

## 5. Biến môi trường

Dự án đang đọc biến môi trường từ application.yaml và docker-compose.yml. Tạo file .env ở thư mục gốc với các biến tối thiểu:

```
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/photoshare?currentSchema=public
SPRING_DATASOURCE_USERNAME=photoshare
SPRING_DATASOURCE_PASSWORD=your_db_password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=

# Mail
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=your_mail_username
MAIL_PASSWORD=your_mail_password
MAIL_FROM=noreply@example.com

# JWT
JWT_ACCESS_SECRET=replace_with_strong_secret
REFRESH_SECRET=replace_with_strong_secret

# Cloudflare R2
R2_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
R2_BUCKET=photoshare-images
R2_PUBLIC_URL=https://<public-domain>
R2_ACCESS_KEY=<access-key>
R2_SECRET_KEY=<secret-key>

# Docker compose Redis (nếu dùng compose)
REDIS_PORT=6379
REDIS_PASSWORD=
```

Luu y bao mat:

- Khong commit cac secret (JWT, mail, R2 key) len git.
- Neu file .env hien tai da lo secret, can rotate key ngay.

## 6. Khoi tao database

Ban co the tao user/database bang script:

- init/init.sql

Noi dung script tao:

- user photoshare
- database photoshare
- cap quyen cho user photoshare

## 7. Chay local

1. Tao database photoshare va cap quyen user.
2. Chinh .env cho dung moi truong.
3. Build:

```
./mvnw clean package
```

Windows:

```
mvnw.cmd clean package
```

4. Run:

```
./mvnw spring-boot:run
```

Hoac:

```
java -jar target/photoshare-0.0.1-SNAPSHOT.jar
```

Mac dinh server chay tai:

- http://localhost:8080

Swagger UI:

- http://localhost:8080/swagger-ui/index.html

## 8. Chay bang Docker Compose

Lenh:

```
docker compose up -d --build
```

Trang thai compose hien tai:

- Co service app
- Co service redis
- Khong co service postgresql

Vay nen PostgreSQL can chay ben ngoai (host may ban) va SPRING_DATASOURCE_URL phai tro den DB do.

## 9. Tai nguyen anh

- Thu muc upload tren host: ./upload
- Trong container: /upload
- API public anh qua duong dan /images/\*\*

## 10. Bao mat va quyen truy cap

Trong SecurityConfig:

- Mo cong khai:
  - /api/auth/\*\*
  - /swagger-ui/\*\*
  - /swagger-ui.html
  - /v3/api-docs/\*\*
  - /images/\*\*
- Cac endpoint con lai yeu cau JWT hop le.

## 11. CORS hien tai

WebConfig dang cho phep origin:

- http://localhost:3000
- http://localhost:5873
- https://\*.phao.id.vn

Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS

## 12. API hien tai (theo controller)

Auth (api/auth)

- POST /register
- POST /login
- POST /logout
- POST /verify-account
- POST /refresh

Photos (api/photos)

- POST /
- PUT /{photoId}
- DELETE /{photoId}
- GET /{photoId}
- PATCH /{photoId}/review
- GET /pending-approval
- GET /popular
- GET /latest
- GET /follow
- GET /{photoId}/comments
- POST /{photoId}/likes

Comments (api/comments)

- GET /{id}/replies
- POST /{targetId}/create
- PUT /{id}
- DELETE /{commentId}

Follow (api/follow)

- POST /?userId={id}

Profiles (api/profiles)

- GET /users/{userId}
- GET /users/{userId}/posts
- GET /users/{userId}/liked
- PUT /edit

Users (api/users)

- GET /
- PUT /{id}/status
- PUT /{userId}/role

Dashboard (api/dashboard)

- GET /stats

Reports (api/reports)

- GET /
- POST /
- POST /{id}/handle

## 13. Test

Chay test:

```
./mvnw test
```

Windows:

```
mvnw.cmd test
```

## 14. Ghi chu

- File enpo.txt dang la ghi chu endpoint cu, khong phai tai lieu chinh thuc.
- Tai lieu uu tien theo README nay + Swagger UI.

Finish 29.3.2026