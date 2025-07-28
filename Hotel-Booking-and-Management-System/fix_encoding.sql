-- =====================================================
-- SỬA LỖI ENCODING TIẾNG VIỆT TRONG DATABASE
-- =====================================================

-- 1. Đặt encoding cho session hiện tại
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;
SET character_set_client=utf8mb4;
SET character_set_results=utf8mb4;

-- 2. Đặt encoding cho database
ALTER DATABASE nhanvmco_DATN CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. Đặt encoding cho từng bảng
ALTER TABLE system_users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE partners CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE customers CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE status CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE voucher CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE rooms CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE room_amenities CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE room_images CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE bookingorder CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE review CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. Cập nhật dữ liệu tiếng Việt bị lỗi
UPDATE customers SET 
    name = 'Nguyễn Văn Dương' WHERE customer_id = 1;
UPDATE customers SET 
    name = 'Trần Thị Hương' WHERE customer_id = 2;
UPDATE customers SET 
    name = 'Lê Văn Minh' WHERE customer_id = 3;
UPDATE customers SET 
    name = 'Phạm Thị Lan' WHERE customer_id = 4;
UPDATE customers SET 
    name = 'Hoàng Văn Nam' WHERE customer_id = 5;

UPDATE customers SET 
    address = '123 Nguyễn Huệ, Quận 1, TP.HCM' WHERE customer_id = 1;
UPDATE customers SET 
    address = '456 Lê Lợi, Quận 3, TP.HCM' WHERE customer_id = 2;
UPDATE customers SET 
    address = '789 Trần Hưng Đạo, Quận 5, TP.HCM' WHERE customer_id = 3;
UPDATE customers SET 
    address = '321 Võ Văn Tần, Quận 3, TP.HCM' WHERE customer_id = 4;
UPDATE customers SET 
    address = '654 Điện Biên Phủ, Quận 3, TP.HCM' WHERE customer_id = 5;

UPDATE partners SET 
    company_name = 'Khách sạn Mường Thanh' WHERE id = 1;
UPDATE partners SET 
    company_name = 'Resort Phú Quốc' WHERE id = 2;
UPDATE partners SET 
    company_name = 'Hotel Đà Lạt' WHERE id = 3;

UPDATE partners SET 
    address = '123 Đường ABC, Quận 1, TP.HCM' WHERE id = 1;
UPDATE partners SET 
    address = '456 Đường XYZ, Phú Quốc, Kiên Giang' WHERE id = 2;
UPDATE partners SET 
    address = '789 Đường DEF, Đà Lạt, Lâm Đồng' WHERE id = 3;

UPDATE partners SET 
    contact_person = 'Nguyễn Văn A' WHERE id = 1;
UPDATE partners SET 
    contact_person = 'Trần Thị B' WHERE id = 2;
UPDATE partners SET 
    contact_person = 'Lê Văn C' WHERE id = 3;

UPDATE partners SET 
    businessmodel = 'Khách sạn 4 sao' WHERE id = 1;
UPDATE partners SET 
    businessmodel = 'Resort 5 sao' WHERE id = 2;
UPDATE partners SET 
    businessmodel = 'Khách sạn 3 sao' WHERE id = 3;

UPDATE partners SET 
    hotelamenities = 'WiFi, Hồ bơi, Nhà hàng, Spa' WHERE id = 1;
UPDATE partners SET 
    hotelamenities = 'Biển riêng, Golf, Spa, Nhà hàng' WHERE id = 2;
UPDATE partners SET 
    hotelamenities = 'WiFi, Nhà hàng, Tour du lịch' WHERE id = 3;

UPDATE status SET 
    description = 'Chờ xác nhận' WHERE status_id = 1;
UPDATE status SET 
    description = 'Đã xác nhận' WHERE status_id = 2;
UPDATE status SET 
    description = 'Đã hủy' WHERE status_id = 3;
UPDATE status SET 
    description = 'Hoàn thành' WHERE status_id = 4;
UPDATE status SET 
    description = 'Đã check-in' WHERE status_id = 5;
UPDATE status SET 
    description = 'Đã check-out' WHERE status_id = 6;
UPDATE status SET 
    description = 'Không đến' WHERE status_id = 7;
UPDATE status SET 
    description = 'Đã hoàn tiền' WHERE status_id = 8;

UPDATE voucher SET 
    description = 'Giảm 10% cho khách hàng mới' WHERE voucher_id = 1;
UPDATE voucher SET 
    description = 'Giảm 15% cho mùa hè' WHERE voucher_id = 2;
UPDATE voucher SET 
    description = 'Giảm 20% cho khách VIP' WHERE voucher_id = 3;
UPDATE voucher SET 
    description = 'Giảm 5% cho đặt nhanh' WHERE voucher_id = 4;
UPDATE voucher SET 
    description = 'Giảm 25% cho cuối tuần' WHERE voucher_id = 5;

-- 5. Kiểm tra kết quả
SELECT 'Encoding đã được sửa thành công!' as message; 