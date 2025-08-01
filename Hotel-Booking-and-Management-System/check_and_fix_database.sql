-- Script kiểm tra và sửa vấn đề floor column trong database
-- Chạy các câu lệnh này trong database để debug và sửa lỗi

-- 1. Kiểm tra cấu trúc bảng rooms
DESCRIBE rooms;

-- 2. Kiểm tra xem có column floor không
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'nhanvmco_DATN' 
AND TABLE_NAME = 'rooms' 
AND COLUMN_NAME = 'floor';

-- 3. Nếu có column floor và gây lỗi, xóa nó
-- ALTER TABLE rooms DROP COLUMN floor;

-- 4. Kiểm tra lại cấu trúc bảng sau khi sửa
DESCRIBE rooms;

-- 5. Kiểm tra dữ liệu hiện tại
SELECT room_id, name_number, room_number, status, type, partner_id 
FROM rooms 
LIMIT 10;

-- 6. Kiểm tra xem có lỗi nào khác không
SHOW CREATE TABLE rooms; 