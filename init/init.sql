-- Tạo user và database
CREATE USER photoshare WITH PASSWORD '123456';
CREATE DATABASE photoshare OWNER photoshare;

-- Tạo extension cần thiết nếu có
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Cho quyền đầy đủ
GRANT ALL PRIVILEGES ON DATABASE photoshare TO photoshare;