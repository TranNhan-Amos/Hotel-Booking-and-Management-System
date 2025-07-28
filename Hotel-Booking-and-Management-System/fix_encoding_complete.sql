-- =====================================================
-- SỬA LỖI ENCODING TIẾNG VIỆT HOÀN CHỈNH CHO TẤT CẢ BẢNG
-- =====================================================

-- 1. Đặt encoding cho session hiện tại
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;
SET character_set_client=utf8mb4;
SET character_set_results=utf8mb4;

-- 2. Đặt encoding cho database
ALTER DATABASE nhanvmco_DATN CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. Đặt encoding cho tất cả bảng
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

-- 4. CẬP NHẬT DỮ LIỆU TIẾNG VIỆT CHO TẤT CẢ BẢNG

-- 4.1. Cập nhật bảng customers
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

-- 4.2. Cập nhật bảng partners
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

-- 4.3. Cập nhật bảng status
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

-- 4.4. Cập nhật bảng voucher
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

-- 4.5. Cập nhật bảng rooms
UPDATE rooms SET 
    name_number = 'Phòng Deluxe Ocean View' WHERE room_id = 1;
UPDATE rooms SET 
    name_number = 'Phòng Standard City View' WHERE room_id = 2;
UPDATE rooms SET 
    name_number = 'Suite Presidential' WHERE room_id = 3;
UPDATE rooms SET 
    name_number = 'Phòng Gia Đình' WHERE room_id = 4;
UPDATE rooms SET 
    name_number = 'Phòng VIP Mountain View' WHERE room_id = 5;
UPDATE rooms SET 
    name_number = 'Phòng Superior' WHERE room_id = 6;
UPDATE rooms SET 
    name_number = 'Phòng Executive' WHERE room_id = 7;
UPDATE rooms SET 
    name_number = 'Phòng Business' WHERE room_id = 8;

UPDATE rooms SET 
    description = 'Phòng cao cấp với view biển tuyệt đẹp, trang thiết bị hiện đại' WHERE room_id = 1;
UPDATE rooms SET 
    description = 'Phòng tiêu chuẩn với view thành phố, tiện nghi đầy đủ' WHERE room_id = 2;
UPDATE rooms SET 
    description = 'Suite tổng thống với không gian rộng rãi, dịch vụ cao cấp' WHERE room_id = 3;
UPDATE rooms SET 
    description = 'Phòng gia đình rộng rãi, phù hợp cho 4-6 người' WHERE room_id = 4;
UPDATE rooms SET 
    description = 'Phòng VIP với view núi đẹp, dịch vụ đặc biệt' WHERE room_id = 5;
UPDATE rooms SET 
    description = 'Phòng superior với tiện nghi hiện đại' WHERE room_id = 6;
UPDATE rooms SET 
    description = 'Phòng executive dành cho doanh nhân' WHERE room_id = 7;
UPDATE rooms SET 
    description = 'Phòng business với không gian làm việc' WHERE room_id = 8;

UPDATE rooms SET 
    service = 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay' WHERE room_id = 1;
UPDATE rooms SET 
    service = 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng' WHERE room_id = 2;
UPDATE rooms SET 
    service = 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay' WHERE room_id = 3;
UPDATE rooms SET 
    service = 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng' WHERE room_id = 4;
UPDATE rooms SET 
    service = 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay' WHERE room_id = 5;
UPDATE rooms SET 
    service = 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng' WHERE room_id = 6;
UPDATE rooms SET 
    service = 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay' WHERE room_id = 7;
UPDATE rooms SET 
    service = 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng' WHERE room_id = 8;

-- 4.6. Cập nhật bảng bookingorder
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng tầng cao, view biển' WHERE booking_id = 1;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng yên tĩnh' WHERE booking_id = 2;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu dịch vụ cao cấp' WHERE booking_id = 3;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng gần thang máy' WHERE booking_id = 4;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng tầng cao' WHERE booking_id = 5;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng yên tĩnh' WHERE booking_id = 6;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu dịch vụ đặc biệt' WHERE booking_id = 7;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng gần nhà hàng' WHERE booking_id = 8;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng tầng cao' WHERE booking_id = 9;
UPDATE bookingorder SET 
    special_requests = 'Yêu cầu phòng yên tĩnh' WHERE booking_id = 10;

UPDATE bookingorder SET 
    cancellation_reason = 'Thay đổi lịch trình' WHERE booking_id = 4;

-- 4.7. Cập nhật bảng review
UPDATE review SET 
    comment = 'Phòng rất đẹp, view biển tuyệt vời! Dịch vụ tốt, nhân viên thân thiện.' WHERE review_id = 1;
UPDATE review SET 
    comment = 'Phòng sạch sẽ, vị trí thuận tiện. Giá cả hợp lý.' WHERE review_id = 2;
UPDATE review SET 
    comment = 'Suite tuyệt vời! Dịch vụ cao cấp, không gian rộng rãi.' WHERE review_id = 3;
UPDATE review SET 
    comment = 'Phòng gia đình ổn, nhưng hơi ồn vào buổi sáng.' WHERE review_id = 4;
UPDATE review SET 
    comment = 'Phòng VIP đúng như mong đợi! View núi đẹp, dịch vụ hoàn hảo.' WHERE review_id = 5;
UPDATE review SET 
    comment = 'Phòng superior rất tốt, tiện nghi đầy đủ.' WHERE review_id = 6;
UPDATE review SET 
    comment = 'Phòng executive phù hợp cho công việc.' WHERE review_id = 7;
UPDATE review SET 
    comment = 'Phòng business có không gian làm việc tốt.' WHERE review_id = 8;
UPDATE review SET 
    comment = 'Phòng deluxe view biển rất đẹp.' WHERE review_id = 9;
UPDATE review SET 
    comment = 'Phòng standard giá cả hợp lý.' WHERE review_id = 10;

UPDATE review SET 
    response = 'Cảm ơn bạn đã đánh giá tốt! Chúng tôi rất vui khi được phục vụ bạn.' WHERE review_id = 1;
UPDATE review SET 
    response = 'Cảm ơn bạn! Chúng tôi sẽ cố gắng phục vụ tốt hơn nữa.' WHERE review_id = 2;
UPDATE review SET 
    response = 'Cảm ơn bạn đã chọn dịch vụ của chúng tôi!' WHERE review_id = 3;
UPDATE review SET 
    response = 'Cảm ơn phản hồi của bạn. Chúng tôi sẽ cải thiện vấn đề này.' WHERE review_id = 4;
UPDATE review SET 
    response = 'Cảm ơn bạn! Chúng tôi rất vui khi được phục vụ bạn.' WHERE review_id = 5;
UPDATE review SET 
    response = 'Cảm ơn bạn đã đánh giá tốt!' WHERE review_id = 6;
UPDATE review SET 
    response = 'Cảm ơn bạn đã chọn dịch vụ của chúng tôi!' WHERE review_id = 7;
UPDATE review SET 
    response = 'Cảm ơn bạn! Chúng tôi sẽ cố gắng phục vụ tốt hơn.' WHERE review_id = 8;
UPDATE review SET 
    response = 'Cảm ơn bạn đã đánh giá tốt!' WHERE review_id = 9;
UPDATE review SET 
    response = 'Cảm ơn bạn! Chúng tôi rất vui khi được phục vụ bạn.' WHERE review_id = 10;

-- 5. Kiểm tra kết quả
SELECT 'Encoding đã được sửa thành công cho tất cả bảng!' as message; 