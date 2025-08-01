-- Kiểm tra dữ liệu trong database
-- Chạy các câu lệnh này trong database để debug

-- 1. Kiểm tra tất cả partners
SELECT * FROM partners;

-- 2. Kiểm tra tất cả rooms
SELECT r.room_id, r.name_number, r.room_number, r.status, r.type, 
       p.id as partner_id, p.company_name as partner_name
FROM rooms r 
LEFT JOIN partners p ON r.partner_id = p.id;

-- 3. Đếm số phòng theo partner
SELECT p.id, p.company_name, COUNT(r.room_id) as room_count
FROM partners p
LEFT JOIN rooms r ON p.id = r.partner_id
GROUP BY p.id, p.company_name;

-- 4. Kiểm tra phòng của một partner cụ thể (thay đổi partner_id)
SELECT * FROM rooms WHERE partner_id = 1;

-- 5. Kiểm tra cấu trúc bảng
DESCRIBE rooms;
DESCRIBE partners; 