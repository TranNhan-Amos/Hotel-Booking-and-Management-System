-- =====================================================
-- DỮ LIỆU MẪU CHO HỆ THỐNG ĐẶT PHÒNG KHÁCH SẠN (UTF-8)
-- =====================================================

-- Đặt encoding UTF-8
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- 1. THÊM DỮ LIỆU VÀO BẢNG system_users
INSERT INTO system_users (username, email, password, img, role, created_at) VALUES
('admin', 'admin@hotel.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin-avatar.jpg', 'ADMIN', '2024-01-01 00:00:00'),
('partner1', 'partner1@hotel.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'partner1-avatar.jpg', 'PARTNER', '2024-01-02 00:00:00'),
('partner2', 'partner2@hotel.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'partner2-avatar.jpg', 'PARTNER', '2024-01-03 00:00:00'),
('partner3', 'partner3@hotel.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'partner3-avatar.jpg', 'PARTNER', '2024-01-04 00:00:00'),
('customer1', 'customer1@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'customer1-avatar.jpg', 'CUSTOMER', '2024-01-05 00:00:00'),
('customer2', 'customer2@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'customer2-avatar.jpg', 'CUSTOMER', '2024-01-06 00:00:00'),
('customer3', 'customer3@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'customer3-avatar.jpg', 'CUSTOMER', '2024-01-07 00:00:00'),
('customer4', 'customer4@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'customer4-avatar.jpg', 'CUSTOMER', '2024-01-08 00:00:00'),
('customer5', 'customer5@gmail.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'customer5-avatar.jpg', 'CUSTOMER', '2024-01-09 00:00:00'),
('staff1', 'staff1@hotel.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'staff1-avatar.jpg', 'STAFF', '2024-01-10 00:00:00');

-- 2. THÊM DỮ LIỆU VÀO BẢNG partners (ĐÃ SỬA TÊN CỘT VÀ ENCODING)
INSERT INTO partners (id, company_name, tax_id, address, contact_person, email, phone, business_license, businessmodel, hotelamenities, user_id) VALUES
(1, 'Khách sạn Mường Thanh', '0123456789', '123 Đường ABC, Quận 1, TP.HCM', 'Nguyễn Văn A', 'partner1@hotel.com', '0901234567', 'GP123456', 'Khách sạn 4 sao', 'WiFi, Hồ bơi, Nhà hàng, Spa', 2),
(2, 'Resort Phú Quốc', '9876543210', '456 Đường XYZ, Phú Quốc, Kiên Giang', 'Trần Thị B', 'partner2@hotel.com', '0909876543', 'GP654321', 'Resort 5 sao', 'Biển riêng, Golf, Spa, Nhà hàng', 3),
(3, 'Hotel Đà Lạt', '1122334455', '789 Đường DEF, Đà Lạt, Lâm Đồng', 'Lê Văn C', 'partner3@hotel.com', '0905555666', 'GP789012', 'Khách sạn 3 sao', 'WiFi, Nhà hàng, Tour du lịch', 4);

-- 3. THÊM DỮ LIỆU VÀO BẢNG customers (ĐÃ SỬA ENCODING)
INSERT INTO customers (customer_id, password, address, name, phone, cccd, email, created_date, status, avatar, user_id) VALUES
(1, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '123 Nguyễn Huệ, Quận 1, TP.HCM', 'Nguyễn Văn Dương', '0901111111', '123456789012', 'customer1@gmail.com', '2024-01-05', 'ACTIVE', 'customer1-avatar.jpg', 5),
(2, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '456 Lê Lợi, Quận 3, TP.HCM', 'Trần Thị Hương', '0902222222', '234567890123', 'customer2@gmail.com', '2024-01-06', 'ACTIVE', 'customer2-avatar.jpg', 6),
(3, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '789 Trần Hưng Đạo, Quận 5, TP.HCM', 'Lê Văn Minh', '0903333333', '345678901234', 'customer3@gmail.com', '2024-01-07', 'ACTIVE', 'customer3-avatar.jpg', 7),
(4, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '321 Võ Văn Tần, Quận 3, TP.HCM', 'Phạm Thị Lan', '0904444444', '456789012345', 'customer4@gmail.com', '2024-01-08', 'ACTIVE', 'customer4-avatar.jpg', 8),
(5, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '654 Điện Biên Phủ, Quận Bình Thạnh, TP.HCM', 'Hoàng Văn Nam', '0905555555', '567890123456', 'customer5@gmail.com', '2024-01-09', 'ACTIVE', 'customer5-avatar.jpg', 9);

-- 4. THÊM DỮ LIỆU VÀO BẢNG status
INSERT INTO status (status_id, status_name, description, is_active) VALUES
(1, 'PENDING', 'Chờ xác nhận', true),
(2, 'CONFIRMED', 'Đã xác nhận', true),
(3, 'CANCELLED', 'Đã hủy', true),
(4, 'COMPLETED', 'Hoàn thành', true),
(5, 'CHECKED_IN', 'Đã check-in', true),
(6, 'CHECKED_OUT', 'Đã check-out', true),
(7, 'NO_SHOW', 'Không đến', true),
(8, 'REFUNDED', 'Đã hoàn tiền', true);

-- 5. THÊM DỮ LIỆU VÀO BẢNG voucher
INSERT INTO voucher (voucher_id, discount, discount_amount, code, expiration_date, expiry_date, start_date, end_date, status, usage_limit, used_count, description, minimum_order_amount) VALUES
(1, 10.00, 100000.00, 'WELCOME10', '2024-12-31', '2024-12-31', '2024-01-01', '2024-12-31', 'ACTIVE', 100, 5, 'Giảm 10% cho khách hàng mới', 1000000.00),
(2, 15.00, 200000.00, 'SUMMER15', '2024-08-31', '2024-08-31', '2024-06-01', '2024-08-31', 'ACTIVE', 50, 12, 'Giảm 15% cho mùa hè', 1500000.00),
(3, 20.00, 500000.00, 'VIP20', '2024-12-31', '2024-12-31', '2024-01-01', '2024-12-31', 'ACTIVE', 20, 8, 'Giảm 20% cho khách VIP', 2500000.00),
(4, 5.00, 50000.00, 'QUICK5', '2024-06-30', '2024-06-30', '2024-01-01', '2024-06-30', 'ACTIVE', 200, 45, 'Giảm 5% cho đặt nhanh', 1000000.00),
(5, 25.00, 300000.00, 'WEEKEND25', '2024-12-31', '2024-12-31', '2024-01-01', '2024-12-31', 'ACTIVE', 30, 15, 'Giảm 25% cho cuối tuần', 1200000.00);

-- 6. THÊM DỮ LIỆU VÀO BẢNG rooms
INSERT INTO rooms (room_id, room_number, name_number, type, price, status, description, capacity, service, view, bed_type, bathroom_type, total_rooms, is_smoking, is_pet_friendly, icon_path, created_at, updated_at, partner_id) VALUES
(1, 'A101', 'Phòng Deluxe Ocean View', 'DELUXE', 2500000.00, 'AVAILABLE', 'Phòng cao cấp với view biển tuyệt đẹp, trang thiết bị hiện đại', 2, 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay', 'SEA_VIEW', 'KING_BED', 'COMBINATION', 5, false, false, '/icons/deluxe-ocean.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 1),
(2, 'A102', 'Phòng Standard City View', 'STANDARD', 1200000.00, 'AVAILABLE', 'Phòng tiêu chuẩn với view thành phố, tiện nghi đầy đủ', 2, 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng', 'CITY_VIEW', 'DOUBLE_BED', 'SHOWER', 8, false, false, '/icons/standard-city.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 1),
(3, 'B201', 'Suite Presidential', 'SUITE', 5000000.00, 'AVAILABLE', 'Suite tổng thống với không gian rộng rãi, dịch vụ cao cấp', 4, 'Bữa sáng premium, Butler service, Spa access, Private pool', 'SEA_VIEW', 'KING_BED', 'COMBINATION', 2, false, false, '/icons/suite-presidential.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 1),
(4, 'C101', 'Phòng Family Garden View', 'FAMILY', 1800000.00, 'AVAILABLE', 'Phòng gia đình với view vườn, phù hợp cho gia đình có trẻ em', 4, 'Bữa sáng gia đình, Khu vui chơi trẻ em, Dịch vụ giữ trẻ', 'GARDEN_VIEW', 'TWIN_BEDS', 'COMBINATION', 6, false, true, '/icons/family-garden.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 2),
(5, 'D101', 'Phòng VIP Mountain View', 'VIP', 3500000.00, 'AVAILABLE', 'Phòng VIP với view núi, không gian riêng tư', 2, 'Bữa sáng riêng, Spa treatment, Private terrace', 'MOUNTAIN_VIEW', 'KING_BED', 'BATHTUB', 3, false, false, '/icons/vip-mountain.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 2),
(6, 'E101', 'Phòng Single Standard', 'SINGLE', 800000.00, 'AVAILABLE', 'Phòng đơn tiêu chuẩn, phù hợp cho khách đi công tác', 1, 'Bữa sáng, WiFi, Bàn làm việc', 'CITY_VIEW', 'SINGLE_BED', 'SHOWER', 10, false, false, '/icons/single-standard.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3),
(7, 'F101', 'Phòng Triple Standard', 'TRIPLE', 1500000.00, 'AVAILABLE', 'Phòng ba giường, phù hợp cho nhóm bạn', 3, 'Bữa sáng, WiFi, Khu vực chung', 'CITY_VIEW', 'TWIN_BEDS', 'SHOWER', 4, false, false, '/icons/triple-standard.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 3),
(8, 'G101', 'Phòng Quad Standard', 'QUAD', 2000000.00, 'AVAILABLE', 'Phòng bốn giường, không gian rộng rãi', 4, 'Bữa sáng, WiFi, Khu vực chung', 'GARDEN_VIEW', 'TWIN_BEDS', 'COMBINATION', 3, false, false, '/icons/quad-standard.png', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 1);

-- 7. THÊM DỮ LIỆU VÀO BẢNG room_amenities
INSERT INTO room_amenities (room_id, amenity) VALUES
(1, 'WIFI'), (1, 'TV'), (1, 'AIR_CONDITIONER'), (1, 'MINIBAR'), (1, 'SAFE'), (1, 'HAIR_DRYER'), (1, 'WORK_DESK'), (1, 'COFFEE_MAKER'), (1, 'REFRIGERATOR'), (1, 'BALCONY'), (1, 'ROOM_SERVICE'), (1, 'BREAKFAST_INCLUDED'),
(2, 'WIFI'), (2, 'TV'), (2, 'AIR_CONDITIONER'), (2, 'SAFE'), (2, 'HAIR_DRYER'), (2, 'BREAKFAST_INCLUDED'),
(3, 'WIFI'), (3, 'TV'), (3, 'AIR_CONDITIONER'), (3, 'MINIBAR'), (3, 'SAFE'), (3, 'HAIR_DRYER'), (3, 'WORK_DESK'), (3, 'COFFEE_MAKER'), (3, 'REFRIGERATOR'), (3, 'BALCONY'), (3, 'ROOM_SERVICE'), (3, 'BREAKFAST_INCLUDED'),
(4, 'WIFI'), (4, 'TV'), (4, 'AIR_CONDITIONER'), (4, 'SAFE'), (4, 'HAIR_DRYER'), (4, 'BREAKFAST_INCLUDED'),
(5, 'WIFI'), (5, 'TV'), (5, 'AIR_CONDITIONER'), (5, 'MINIBAR'), (5, 'SAFE'), (5, 'HAIR_DRYER'), (5, 'WORK_DESK'), (5, 'COFFEE_MAKER'), (5, 'BALCONY'), (5, 'ROOM_SERVICE'), (5, 'BREAKFAST_INCLUDED'),
(6, 'WIFI'), (6, 'TV'), (6, 'AIR_CONDITIONER'), (6, 'SAFE'), (6, 'HAIR_DRYER'), (6, 'WORK_DESK'), (6, 'BREAKFAST_INCLUDED'),
(7, 'WIFI'), (7, 'TV'), (7, 'AIR_CONDITIONER'), (7, 'SAFE'), (7, 'HAIR_DRYER'), (7, 'BREAKFAST_INCLUDED'),
(8, 'WIFI'), (8, 'TV'), (8, 'AIR_CONDITIONER'), (8, 'SAFE'), (8, 'HAIR_DRYER'), (8, 'BREAKFAST_INCLUDED');

-- 8. THÊM DỮ LIỆU VÀO BẢNG room_images
INSERT INTO room_images (room_id, image_url) VALUES
(1, '/img/rooms/1752415863151_426335510_921558952686932_3743900687233719794_n.jpg'),
(1, '/img/rooms/1752415882863_461167762_560564256663113_3076777522713791004_n.jpg'),
(2, '/img/rooms/1752415923538_435335210_931719935111260_9147544617306163351_n.jpg'),
(2, '/img/rooms/1752415953393_494446529_122176719500358054_5052781496710875410_n__1_.jpg'),
(3, '/img/rooms/1752415967056_1699259624649.png'),
(3, '/img/rooms/1752415988493_1699259624649.png'),
(4, '/img/rooms/1752570350904_1.jpg'),
(4, '/img/rooms/1752570350911_Hinh-anh-phong-ngu-khach-san-binh-dan-vua-dep-vua-tiet-kiem.jpg'),
(5, '/img/rooms/1752570350914_hotel-photography-chup-anh-khach-san-resortNovotel-phu-quoc-resort-03-1024x707.jpg'),
(5, '/img/rooms/1752570350916_Thiet-ke-khach-san-dep-tieu-chuan-4-sao-5-sao-achi-85106-01.jpg'),
(6, '/img/rooms/1752570369903_pexels-boonkong-boonpeng-442952-1134176.jpg'),
(6, '/img/rooms/1752570369909_Phoi-canh_636380426831654370.jpg'),
(7, '/img/rooms/1752570369915_SUPER-DELUXE2.jpg'),
(7, '/img/rooms/1752570369919_Thiet-ke-khach-san-3-sao-2-mat-tien-dep-tai-ha-long-luxviet-68201-01.jpg'),
(8, '/img/rooms/1752570391670_a3.jpg'),
(8, '/img/rooms/1752570391672_kh_ch_s_n_5_sao_1_.jpg');

-- 9. THÊM DỮ LIỆU VÀO BẢNG bookingorder
INSERT INTO bookingorder (booking_id, voucher_id, created_at, room_id, email, check_in_date, check_out_date, booking_date, customer_id, status_id, partner_id, total_price, cancellation_date, cancellation_reason, refund_amount, refund_status, refund_date, special_requests, payment_method, payment_status, paid_date, room_quantity) VALUES
(1, 1, '2024-01-15 10:30:00', 1, 'customer1@gmail.com', '2024-02-01', '2024-02-03', '2024-01-15', 1, 2, 1, 4500000.00, NULL, NULL, NULL, NULL, NULL, 'Yêu cầu phòng tầng cao, view biển', 'CREDIT_CARD', 'PAID', '2024-01-15 11:00:00', 1),
(2, NULL, '2024-01-16 14:20:00', 2, 'customer2@gmail.com', '2024-02-05', '2024-02-07', '2024-01-16', 2, 1, 1, 2400000.00, NULL, NULL, NULL, NULL, NULL, 'Yêu cầu phòng yên tĩnh', 'MOMO', 'PENDING', NULL, 1),
(3, 3, '2024-01-17 09:15:00', 3, 'customer3@gmail.com', '2024-02-10', '2024-02-12', '2024-01-17', 3, 2, 1, 9000000.00, NULL, NULL, NULL, NULL, NULL, 'Yêu cầu dịch vụ cao cấp', 'BANK_TRANSFER', 'PAID', '2024-01-17 10:00:00', 1),
(4, NULL, '2024-01-18 16:45:00', 4, 'customer4@gmail.com', '2024-02-15', '2024-02-17', '2024-01-18', 4, 3, 2, 3600000.00, '2024-01-20 10:30:00', 'Thay đổi lịch trình', 3240000.00, 'COMPLETED', '2024-01-21 14:00:00', 'Phòng gia đình có trẻ em', 'CREDIT_CARD', 'REFUNDED', '2024-01-18 17:00:00', 1),
(5, 2, '2024-01-19 11:30:00', 5, 'customer5@gmail.com', '2024-02-20', '2024-02-22', '2024-01-19', 5, 2, 2, 5950000.00, NULL, NULL, NULL, NULL, NULL, 'Yêu cầu phòng VIP', 'MOMO', 'PAID', '2024-01-19 12:00:00', 1),
(6, NULL, '2024-01-20 13:20:00', 6, 'customer1@gmail.com', '2024-02-25', '2024-02-26', '2024-01-20', 1, 1, 3, 800000.00, NULL, NULL, NULL, NULL, NULL, 'Phòng đơn cho công tác', 'PAY_ON_ARRIVAL', 'PENDING', NULL, 1),
(7, 4, '2024-01-21 15:10:00', 7, 'customer2@gmail.com', '2024-03-01', '2024-03-03', '2024-01-21', 2, 2, 3, 2850000.00, NULL, NULL, NULL, NULL, NULL, 'Phòng cho nhóm bạn', 'CREDIT_CARD', 'PAID', '2024-01-21 16:00:00', 1),
(8, NULL, '2024-01-22 08:45:00', 8, 'customer3@gmail.com', '2024-03-05', '2024-03-07', '2024-01-22', 3, 1, 1, 4000000.00, NULL, NULL, NULL, NULL, NULL, 'Phòng cho gia đình', 'BANK_TRANSFER', 'PENDING', NULL, 1),
(9, 5, '2024-01-23 12:00:00', 1, 'customer4@gmail.com', '2024-03-10', '2024-03-12', '2024-01-23', 4, 2, 1, 3750000.00, NULL, NULL, NULL, NULL, NULL, 'Yêu cầu phòng cao cấp', 'MOMO', 'PAID', '2024-01-23 13:00:00', 1),
(10, NULL, '2024-01-24 14:30:00', 2, 'customer5@gmail.com', '2024-03-15', '2024-03-17', '2024-01-24', 5, 1, 1, 2400000.00, NULL, NULL, NULL, NULL, NULL, 'Phòng tiêu chuẩn', 'CREDIT_CARD', 'PENDING', NULL, 1);

-- 10. THÊM DỮ LIỆU VÀO BẢNG review
INSERT INTO review (review_id, customer_id, room_id, rating, comment, review_date, response, response_date, responder_id) VALUES
(1, 1, 1, 5, 'Phòng rất đẹp, view biển tuyệt vời! Dịch vụ tốt, nhân viên thân thiện.', '2024-02-03', 'Cảm ơn bạn đã đánh giá tốt! Chúng tôi rất vui khi được phục vụ bạn.', '2024-02-04', 1),
(2, 2, 2, 4, 'Phòng sạch sẽ, vị trí thuận tiện. Giá cả hợp lý.', '2024-02-07', 'Cảm ơn bạn! Chúng tôi sẽ cố gắng phục vụ tốt hơn nữa.', '2024-02-08', 1),
(3, 3, 3, 5, 'Suite tuyệt vời! Dịch vụ cao cấp, không gian rộng rãi.', '2024-02-12', 'Cảm ơn bạn đã chọn dịch vụ của chúng tôi!', '2024-02-13', 1),
(4, 4, 4, 3, 'Phòng gia đình ổn, nhưng hơi ồn vào buổi sáng.', '2024-02-17', 'Cảm ơn phản hồi của bạn. Chúng tôi sẽ cải thiện vấn đề này.', '2024-02-18', 1),
(5, 5, 5, 5, 'Phòng VIP đúng như mong đợi! View núi đẹp, dịch vụ hoàn hảo.', '2024-02-22', 'Cảm ơn bạn! Chúng tôi rất vui khi được phục vụ bạn.', '2024-02-23', 1),
(6, 1, 6, 4, 'Phòng đơn phù hợp cho công tác. WiFi ổn định.', '2024-02-26', 'Cảm ơn bạn! Chúng tôi sẽ duy trì chất lượng dịch vụ.', '2024-02-27', 1),
(7, 2, 7, 4, 'Phòng cho nhóm bạn rộng rãi, tiện nghi đầy đủ.', '2024-03-03', 'Cảm ơn bạn đã chọn chúng tôi!', '2024-03-04', 1),
(8, 3, 8, 5, 'Phòng gia đình rất tốt, trẻ em thích thú với không gian.', '2024-03-07', 'Cảm ơn bạn! Chúng tôi rất vui khi gia đình bạn hài lòng.', '2024-03-08', 1),
(9, 4, 1, 5, 'Lần thứ 2 đến và vẫn rất hài lòng!', '2024-03-12', 'Cảm ơn bạn đã quay lại! Chúng tôi rất vui được phục vụ bạn.', '2024-03-13', 1),
(10, 5, 2, 4, 'Phòng tiêu chuẩn tốt, giá cả hợp lý.', '2024-03-17', 'Cảm ơn bạn! Chúng tôi sẽ cố gắng phục vụ tốt hơn.', '2024-03-18', 1);

-- =====================================================
-- HOÀN THÀNH THÊM DỮ LIỆU MẪU (UTF-8)
-- =====================================================

-- Lưu ý: 
-- - Đã thêm SET NAMES utf8mb4 để đảm bảo encoding tiếng Việt đúng
-- - Đã sửa tên cột trong bảng partners theo chuẩn snake_case
-- - Đã bỏ qua bảng Post vì không tồn tại trong database

-- Tổng cộng đã thêm:
-- - 10 system_users (Admin, Partners, Customers, Staff)
-- - 3 partners (Khách sạn, Resort)
-- - 5 customers
-- - 8 status types
-- - 5 vouchers
-- - 8 rooms với amenities và images
-- - 10 booking orders
-- - 10 reviews

-- Dữ liệu này sẽ giúp test đầy đủ các chức năng cốt lõi của hệ thống! 