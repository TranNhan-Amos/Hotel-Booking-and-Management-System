-- Script reset database schema nếu cần thiết
-- CHÚ Ý: Chạy script này sẽ xóa dữ liệu hiện tại!

-- 1. Backup dữ liệu hiện tại (nếu cần)
-- CREATE TABLE rooms_backup AS SELECT * FROM rooms;

-- 2. Xóa bảng rooms hiện tại
-- DROP TABLE IF EXISTS rooms;

-- 3. Tạo lại bảng rooms với schema đúng (không có floor)
-- CREATE TABLE rooms (
--     room_id INT AUTO_INCREMENT PRIMARY KEY,
--     room_number VARCHAR(20),
--     name_number TEXT NOT NULL,
--     type VARCHAR(50) NOT NULL,
--     price DECIMAL(10,2) NOT NULL,
--     status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
--     description TEXT,
--     capacity INT NOT NULL,
--     service VARCHAR(10000),
--     view VARCHAR(500),
--     bed_type VARCHAR(500),
--     bathroom_type VARCHAR(500),
--     total_rooms INT NOT NULL,
--     is_smoking BOOLEAN NOT NULL DEFAULT FALSE,
--     is_pet_friendly BOOLEAN NOT NULL DEFAULT FALSE,
--     icon_path VARCHAR(255),
--     created_at DATETIME NOT NULL,
--     updated_at DATETIME NOT NULL,
--     partner_id BIGINT,
--     FOREIGN KEY (partner_id) REFERENCES partners(id)
-- );

-- 4. Tạo bảng room_amenities
-- CREATE TABLE room_amenities (
--     room_id INT,
--     amenity VARCHAR(255),
--     FOREIGN KEY (room_id) REFERENCES rooms(room_id)
-- );

-- 5. Tạo bảng room_images
-- CREATE TABLE room_images (
--     room_id INT,
--     image_url VARCHAR(255),
--     FOREIGN KEY (room_id) REFERENCES rooms(room_id)
-- );

-- 6. Kiểm tra lại cấu trúc
-- DESCRIBE rooms;
-- DESCRIBE room_amenities;
-- DESCRIBE room_images; 