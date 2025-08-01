-- File SQL chỉ thêm dữ liệu vào các bảng đã tồn tại
-- KHÔNG tạo lại cấu trúc bảng

-- Thêm dữ liệu vào bảng system_users
INSERT INTO `system_users` (`id`, `created_at`, `email`, `img`, `password`, `role`, `username`) VALUES
(1, '2024-01-01 00:00:00.000000', 'admin@hotel.com', 'admin-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'ADMIN', 'admin'),
(2, '2024-01-02 00:00:00.000000', 'partner1@hotel.com', 'partner1-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'PARTNER', 'partner1'),
(3, '2024-01-03 00:00:00.000000', 'partner2@hotel.com', 'partner2-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'PARTNER', 'partner2'),
(4, '2024-01-04 00:00:00.000000', 'partner3@hotel.com', 'partner3-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'PARTNER', 'partner3'),
(5, '2024-01-05 00:00:00.000000', 'customer1@gmail.com', '7edfbe89-f53f-4690-ad94-721621cca4ff.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'CUSTOMER', 'Nguyễn Văn Dương'),
(6, '2024-01-06 00:00:00.000000', 'customer2@gmail.com', 'customer2-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'CUSTOMER', 'customer2'),
(7, '2024-01-07 00:00:00.000000', 'customer3@gmail.com', 'customer3-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'CUSTOMER', 'customer3'),
(8, '2024-01-08 00:00:00.000000', 'customer4@gmail.com', 'customer4-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'CUSTOMER', 'customer4'),
(9, '2024-01-09 00:00:00.000000', 'customer5@gmail.com', 'customer5-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'CUSTOMER', 'customer5'),
(10, '2024-01-10 00:00:00.000000', 'staff1@hotel.com', 'staff1-avatar.jpg', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', 'STAFF', 'staff1');

-- Thêm dữ liệu vào bảng partners
INSERT INTO `partners` (`id`, `address`, `business_license`, `businessmodel`, `company_name`, `contact_person`, `email`, `hotelamenities`, `phone`, `tax_id`, `user_id`) VALUES
(1, '123 Đường ABC, Quận 1, TP.HCM', 'GP123456', 'Khách sạn 4 sao', 'Khách sạn Mường Thanh', 'Nguyễn Văn A', 'partner1@hotel.com', 'WiFi, Hồ bơi, Nhà hàng, Spa', '0901234567', '0123456789', 2),
(2, '456 Đường XYZ, Phú Quốc, Kiên Giang', 'GP654321', 'Resort 5 sao', 'Resort Phú Quốc', 'Trần Thị B', 'partner2@hotel.com', 'Biển riêng, Golf, Spa, Nhà hàng', '0909876543', '9876543210', 3),
(3, '789 Đường DEF, Đà Lạt, Lâm Đồng', 'GP789012', 'Khách sạn 3 sao', 'Hotel Đà Lạt', 'Lê Văn C', 'partner3@hotel.com', 'WiFi, Nhà hàng, Tour du lịch', '0905555666', '1122334455', 4);

-- Thêm dữ liệu vào bảng customers
INSERT INTO `customers` (`customer_id`, `address`, `avatar`, `cccd`, `created_date`, `email`, `name`, `password`, `phone`, `status`, `user_id`) VALUES
(1, '123 Nguyễn Huệ, Quận 1, TP.HCM', '7edfbe89-f53f-4690-ad94-721621cca4ff.jpg', '123456789012', '2024-01-05', 'customer1@gmail.com', 'Nguyễn Văn Dương', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', '0901111111', 'ACTIVE', 5),
(2, '456 Lê Lợi, Quận 3, TP.HCM', 'customer2-avatar.jpg', '234567890123', '2024-01-06', 'customer2@gmail.com', 'Trần Thị Hương', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', '0902222222', 'ACTIVE', 6),
(3, '789 Trần Hưng Đạo, Quận 5, TP.HCM', 'customer3-avatar.jpg', '345678901234', '2024-01-07', 'customer3@gmail.com', 'Lê Văn Minh', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', '0903333333', 'ACTIVE', 7),
(4, '321 Võ Văn Tần, Quận 3, TP.HCM', 'customer4-avatar.jpg', '456789012345', '2024-01-08', 'customer4@gmail.com', 'Phạm Thị Lan', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', '0904444444', 'ACTIVE', 8),
(5, '654 Điện Biên Phủ, Quận 3, TP.HCM', 'customer5-avatar.jpg', '567890123456', '2024-01-09', 'customer5@gmail.com', 'Hoàng Văn Nam', '$2a$10$8m4fahGYxOAyNJe6SLQFgOojo8RS/boT9Yq8e02R/LxZCdBzptP4y', '0905555555', 'ACTIVE', 9),
(6, NULL, NULL, '987654321', NULL, 'partner1@hotel.com', 'partner1', NULL, '0908765432', NULL, NULL);

-- Thêm dữ liệu vào bảng status
INSERT INTO `status` (`status_id`, `description`, `is_active`, `status_name`) VALUES
(1, 'Chờ xác nhận', b'1', 'PENDING'),
(2, 'Đã xác nhận', b'1', 'CONFIRMED'),
(3, 'Đã hủy', b'1', 'CANCELLED'),
(4, 'Hoàn thành', b'1', 'COMPLETED'),
(5, 'Đã check-in', b'1', 'CHECKED_IN'),
(6, 'Đã check-out', b'1', 'CHECKED_OUT'),
(7, 'Không đến', b'1', 'NO_SHOW'),
(8, 'Đã hoàn tiền', b'1', 'REFUNDED');

-- Thêm dữ liệu vào bảng voucher
INSERT INTO `voucher` (`voucher_id`, `code`, `description`, `discount`, `discount_amount`, `end_date`, `expiration_date`, `expiry_date`, `minimum_order_amount`, `start_date`, `status`, `usage_limit`, `used_count`) VALUES
(1, 'WELCOME10', 'Giảm 10% cho khách hàng mới', 10.00, 100000.00, '2024-12-31', '2024-12-31', '2024-12-31', 1000000.00, '2024-01-01', 'ACTIVE', 100, 5),
(2, 'SUMMER15', 'Giảm 15% cho mùa hè', 15.00, 200000.00, '2024-08-31', '2024-08-31', '2024-08-31', 1500000.00, '2024-06-01', 'ACTIVE', 50, 12),
(3, 'VIP20', 'Giảm 20% cho khách VIP', 20.00, 500000.00, '2024-12-31', '2024-12-31', '2024-12-31', 2500000.00, '2024-01-01', 'ACTIVE', 20, 8),
(4, 'QUICK5', 'Giảm 5% cho đặt nhanh', 5.00, 50000.00, '2024-06-30', '2024-06-30', '2024-06-30', 1000000.00, '2024-01-01', 'ACTIVE', 200, 45),
(5, 'WEEKEND25', 'Giảm 25% cho cuối tuần', 25.00, 300000.00, '2024-12-31', '2024-12-31', '2024-12-31', 1200000.00, '2024-01-01', 'ACTIVE', 30, 15);

-- Thêm dữ liệu vào bảng rooms
INSERT INTO `rooms` (`room_id`, `bathroom_type`, `bed_type`, `capacity`, `created_at`, `description`, `icon_path`, `is_pet_friendly`, `is_smoking`, `name_number`, `price`, `room_number`, `service`, `status`, `total_rooms`, `type`, `updated_at`, `view`, `partner_id`) VALUES
(1, NULL, NULL, 3, '2025-07-31 09:09:31', 'Phòng cao cấp với view biển tuyệt đẹp, trang thiết bị hiện đại', NULL, b'0', b'0', 'Phòng Deluxe Ocean View', 2500000.00, 'BKAV103', NULL, 'AVAILABLE', 61, 'DELUXE', '2025-07-31 16:09:31.000000', NULL, 1),
(2, 'SHOWER', 'DOUBLE_BED', 2, '2025-07-31 15:01:00', 'Phòng tiêu chuẩn với view thành phố, tiện nghi đầy đủ', NULL, b'0', b'0', 'Phòng Standard City View', 1200000.00, 'A102', 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng', 'AVAILABLE', 82, 'STANDARD', '2025-07-31 22:01:00.000000', 'CITY_VIEW', 1),
(3, 'COMBINATION', 'KING_BED', 4, '2025-07-27 10:38:15', 'Suite tổng thống với không gian rộng rãi, dịch vụ cao cấp', NULL, b'0', b'0', 'Suite Presidential', 5000000.00, 'B201', 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay', 'AVAILABLE', 212, 'SUITE', '2025-07-27 17:38:15.000000', 'SEA_VIEW', 1),
(4, 'COMBINATION', 'TWIN_BEDS', 4, '2025-07-27 10:39:05', 'Phòng gia đình rộng rãi, phù hợp cho 4-6 người', NULL, b'1', b'0', 'Phòng Gia Đình', 1800000.00, 'C101', 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng', 'AVAILABLE', 112, 'FAMILY', '2025-07-27 17:39:05.000000', 'GARDEN_VIEW', 2),
(5, 'BATHTUB', 'KING_BED', 2, '2025-07-31 09:12:25', 'Phòng VIP với view núi đẹp, dịch vụ đặc biệt', NULL, b'0', b'0', 'Phòng VIP Mountain View', 3500000.00, 'D101', 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay', 'AVAILABLE', 322, 'VIP', '2025-07-31 16:12:25.000000', 'MOUNTAIN_VIEW', 2),
(6, 'SHOWER', 'SINGLE_BED', 1, '2025-07-27 10:39:59', 'Phòng superior với tiện nghi hiện đại', NULL, b'0', b'0', 'Phòng Superior', 800000.00, 'E101', 'Bữa sáng, WiFi miễn phí, Dịch vụ phòng', 'AVAILABLE', 101, 'SINGLE', '2025-07-27 17:39:59.000000', 'CITY_VIEW', 3),
(7, 'SHOWER', 'TWIN_BEDS', 3, '2025-07-27 10:40:22', 'Phòng executive dành cho doanh nhân', NULL, b'0', b'0', 'Phòng Executive', 1500000.00, 'F101', 'Bữa sáng buffet, Dịch vụ phòng 24/7, Xe đưa đón sân bay', 'AVAILABLE', 41, 'TRIPLE', '2025-07-27 17:40:22.000000', 'CITY_VIEW', 3),
(8, NULL, NULL, 4, '2025-07-27 10:40:58', 'Phòng business với không gian làm việc', NULL, b'0', b'0', 'Phòng Business', 2000000.00, 'G101', NULL, 'AVAILABLE', 32, 'STANDARD', '2025-07-27 17:40:58.000000', NULL, 1),
(9, NULL, 'SINGLE_BED', 6, '2025-07-31 09:10:31', 'Phòng thích hợp làm việc', NULL, b'0', b'1', 'Phòng OKR New View', 5000000.00, 'Pd10222', '', 'AVAILABLE', 222, 'STANDARD', '2025-07-31 16:10:31.000000', 'CITY_VIEW', 1),
(10, 'BATHTUB', 'BUNK_BED', 3, '2025-07-31 15:03:30', 'Phòng thích hợp làm việc', NULL, b'0', b'1', 'Silk River Hoi An Hotel & Spa', 5000000.00, 'HNSP1103', '', 'AVAILABLE', 22, 'VIP', '2025-07-31 22:03:30.000000', 'GARDEN_VIEW', 1);

-- Thêm dữ liệu vào bảng room_amenities
INSERT INTO `room_amenities` (`room_id`, `amenity`) VALUES
(2, 'WIFI'),
(2, 'MINIBAR'),
(2, 'WORK_DESK'),
(2, 'BALCONY');

-- Thêm dữ liệu vào bảng room_images
INSERT INTO `room_images` (`room_id`, `image_url`) VALUES
(1, '1753952971498_515438228_1053268253627125_6881202572721739414_n.jpg'),
(1, '638aff33-7fa9-4e3f-8797-782269bba9c3_Golden Wings Logo.png'),
(2, 'ab1e85cb-fc15-4dce-a2eb-201c61faecc8.png'),
(3, '1753612695555_hotel-photography-chup-anh-khach-san-resortNovotel-phu-quoc-resort-03-1024x707.jpg'),
(3, '1753612695556_Thiet-ke-khach-san-3-sao-2-mat-tien-dep-tai-ha-long-luxviet-68201-01.jpg'),
(3, '1753612695558_Thiet-ke-khach-san-dep-tieu-chuan-4-sao-5-sao-achi-85106-01.jpg'),
(3, '1753612695560_Thi_t_k__ch_a_c__t_n.png'),
(4, '1753612745158_1.jpg'),
(4, '1753612745160_2023-09-07.jpg'),
(4, '1753612745161_4321b258-khach-san-o-da-lat-1.jpg'),
(4, '1753612745162_426239899_412657844442884_1597271860769380332_n.jpg'),
(4, '1753612745164_515438228_1053268253627125_6881202572721739414_n.jpg'),
(5, '1753953145839_494446529_122176719500358054_5052781496710875410_n__1_.jpg'),
(6, '1753612799361_Screenshot_2024-12-04_214620.png'),
(7, '1753612822736_Screenshot_2024-12-05_171628.png'),
(8, '1753612858572_aa1.jpg'),
(9, '1753953031758_snaptik_7527957782755331336_0.jpeg'),
(10, '5d953b53-4e67-4609-baba-9ace08629947.jpg');

-- Thêm dữ liệu vào bảng review
INSERT INTO `review` (`review_id`, `comment`, `rating`, `response`, `response_date`, `review_date`, `customer_id`, `responder_id`, `room_id`) VALUES
(1, 'Phòng rất đẹp, view biển tuyệt vời! Dịch vụ tốt, nhân viên thân thiện.', 5, 'Cảm ơn bạn đã đánh giá tốt! Chúng tôi rất vui khi được phục vụ bạn.', '2024-02-04', '2024-02-03', 1, 1, 1),
(2, 'Phòng sạch sẽ, vị trí thuận tiện. Giá cả hợp lý.', 4, 'Cảm ơn bạn! Chúng tôi sẽ cố gắng phục vụ tốt hơn nữa.', '2024-02-08', '2024-02-07', 2, 1, 2),
(3, 'Suite tuyệt vời! Dịch vụ cao cấp, không gian rộng rãi.', 5, 'Cảm ơn bạn đã chọn dịch vụ của chúng tôi!', '2024-02-13', '2024-02-12', 3, 1, 3),
(4, 'Phòng gia đình ổn, nhưng hơi ồn vào buổi sáng.', 3, 'Cảm ơn phản hồi của bạn. Chúng tôi sẽ cải thiện vấn đề này.', '2024-02-18', '2024-02-17', 4, 1, 4),
(5, 'Phòng VIP đúng như mong đợi! View núi đẹp, dịch vụ hoàn hảo.', 5, 'Cảm ơn bạn! Chúng tôi rất vui khi được phục vụ bạn.', '2024-02-23', '2024-02-22', 5, 1, 5),
(6, 'Phòng superior rất tốt, tiện nghi đầy đủ.', 4, 'Cảm ơn bạn đã đánh giá tốt!', '2024-02-27', '2024-02-26', 1, 1, 6),
(7, 'Phòng executive phù hợp cho công việc.', 4, 'Cảm ơn bạn đã chọn dịch vụ của chúng tôi!', '2024-03-04', '2024-03-03', 2, 1, 7),
(8, 'Phòng business với không gian làm việc tốt.', 5, 'Cảm ơn bạn! Chúng tôi sẽ cố gắng phục vụ tốt hơn.', '2024-03-08', '2024-03-07', 3, 1, 8),
(9, 'Phòng deluxe view biển rất đẹp.', 5, 'Cảm ơn bạn đã đánh giá tốt!', '2024-03-13', '2024-03-12', 4, 1, 1),
(10, 'Phòng standard giá cả hợp lý.', 4, 'Cảm ơn bạn! Chúng tôi rất vui khi được phục vụ bạn.', '2024-03-18', '2024-03-17', 5, 1, 2);

-- Thêm dữ liệu vào bảng bookingorder
INSERT INTO `bookingorder` (`booking_id`, `booking_date`, `cancellation_date`, `cancellation_reason`, `check_in_date`, `check_out_date`, `created_at`, `email`, `paid_date`, `payment_method`, `payment_status`, `refund_amount`, `refund_date`, `refund_status`, `room_quantity`, `special_requests`, `total_price`, `customer_id`, `partner_id`, `room_id`, `status_id`, `voucher_id`, `booking_status`) VALUES
(1, '2024-01-15', NULL, NULL, '2024-02-01', '2024-02-03', '2024-01-15 10:30:00.000000', 'customer1@gmail.com', '2024-01-15 11:00:00.000000', 'CREDIT_CARD', 'PAID', NULL, NULL, NULL, 1, 'Yêu cầu phòng tầng cao, view biển', 4500000.00, 1, 1, 1, 2, 1, 'CONFIRMED'),
(2, '2024-01-16', NULL, NULL, '2024-02-05', '2024-02-07', '2024-01-16 14:20:00.000000', 'customer2@gmail.com', NULL, 'MOMO', 'PENDING', NULL, NULL, NULL, 1, 'Yêu cầu phòng yên tĩnh', 2400000.00, 2, 1, 2, 1, NULL, 'CONFIRMED'),
(3, '2024-01-17', NULL, NULL, '2024-02-10', '2024-02-12', '2024-01-17 09:15:00.000000', 'customer3@gmail.com', '2024-01-17 10:00:00.000000', 'BANK_TRANSFER', 'PAID', NULL, NULL, NULL, 1, 'Yêu cầu dịch vụ cao cấp', 9000000.00, 3, 1, 3, 2, 3, 'CONFIRMED'),
(4, '2024-01-18', '2024-01-20 10:30:00.000000', 'Thay đổi lịch trình', '2024-02-15', '2024-02-17', '2024-01-18 16:45:00.000000', 'customer4@gmail.com', '2024-01-18 17:00:00.000000', 'CREDIT_CARD', 'REFUNDED', 3240000.00, '2024-01-21 14:00:00.000000', 'COMPLETED', 1, 'Yêu cầu phòng gần thang máy', 3600000.00, 4, 2, 4, 3, NULL, 'CONFIRMED'),
(5, '2024-01-19', NULL, NULL, '2024-02-20', '2024-02-22', '2024-01-19 11:30:00.000000', 'customer5@gmail.com', '2024-01-19 12:00:00.000000', 'MOMO', 'PAID', NULL, NULL, NULL, 1, 'Yêu cầu phòng tầng cao', 5950000.00, 5, 2, 5, 2, 2, 'CONFIRMED'),
(6, '2024-01-20', NULL, NULL, '2024-02-25', '2024-02-26', '2024-01-20 13:20:00.000000', 'customer1@gmail.com', NULL, 'PAY_ON_ARRIVAL', 'PENDING', NULL, NULL, NULL, 1, 'Yêu cầu phòng yên tĩnh', 800000.00, 1, 3, 6, 1, NULL, 'CONFIRMED'),
(7, '2024-01-21', NULL, NULL, '2024-03-01', '2024-03-03', '2024-01-21 15:10:00.000000', 'customer2@gmail.com', '2024-01-21 16:00:00.000000', 'CREDIT_CARD', 'PAID', NULL, NULL, NULL, 1, 'Yêu cầu dịch vụ đặc biệt', 2850000.00, 2, 3, 7, 2, 4, 'CONFIRMED'),
(8, '2024-01-22', NULL, NULL, '2024-03-05', '2024-03-07', '2024-01-22 08:45:00.000000', 'customer3@gmail.com', NULL, 'BANK_TRANSFER', 'PENDING', NULL, NULL, NULL, 1, 'Yêu cầu phòng gần nhà hàng', 4000000.00, 3, 1, 8, 1, NULL, 'CONFIRMED'),
(9, '2024-01-23', NULL, NULL, '2024-03-10', '2024-03-12', '2024-01-23 12:00:00.000000', 'customer4@gmail.com', '2024-01-23 13:00:00.000000', 'MOMO', 'PAID', NULL, NULL, NULL, 1, 'Yêu cầu phòng tầng cao', 3750000.00, 4, 1, 1, 2, 5, 'CONFIRMED'),
(10, '2024-01-24', NULL, NULL, '2024-03-15', '2024-03-17', '2024-01-24 14:30:00.000000', 'customer5@gmail.com', NULL, 'CREDIT_CARD', 'PENDING', NULL, NULL, NULL, 1, 'Yêu cầu phòng yên tĩnh', 2400000.00, 5, 1, 2, 1, NULL, 'CONFIRMED'),
(11, '2025-07-27', NULL, NULL, '2025-07-27', '2025-07-28', '2025-07-27 17:17:07.000000', 'customer1@gmail.com', NULL, 'payOnArrival', 'PENDING', NULL, NULL, NULL, 2, '', 2904000.00, 1, NULL, 2, 1, NULL, 'CONFIRMED'),
(12, '2025-07-29', NULL, NULL, '2025-07-29', '2025-07-31', '2025-07-29 15:40:24.000000', 'partner1@hotel.com', NULL, 'payOnArrival', 'PENDING', NULL, NULL, NULL, 1, '', 968000.00, 6, NULL, 6, 1, NULL, 'CONFIRMED'),
(13, '2025-07-29', NULL, NULL, '2025-07-29', '2025-12-12', '2025-07-29 21:32:16.000000', 'customer1@gmail.com', NULL, 'PENDING', 'PENDING', NULL, NULL, NULL, 1, '', NULL, 1, NULL, 7, 1, NULL, 'CONFIRMED'),
(14, '2025-07-29', NULL, NULL, '2025-07-29', '2025-12-12', '2025-07-29 23:43:40.000000', 'customer1@gmail.com', NULL, 'PENDING', 'PENDING', NULL, NULL, NULL, 1, '', NULL, 1, NULL, 1, 1, NULL, 'CONFIRMED'),
(15, '2025-07-29', NULL, NULL, '2025-07-29', '2025-08-02', '2025-07-29 23:56:00.000000', 'partner1@hotel.com', NULL, 'payOnArrival', 'PENDING', NULL, NULL, NULL, 1, '', 3025000.00, 6, NULL, 1, 1, NULL, 'CONFIRMED'),
(16, '2025-07-30', NULL, NULL, '2025-07-30', '2025-07-31', '2025-07-30 15:10:19.000000', 'customer1@gmail.com', NULL, 'PENDING', 'PENDING', NULL, NULL, NULL, 1, '', NULL, 1, NULL, 1, 1, NULL, 'CONFIRMED'),
(17, '2025-07-30', NULL, NULL, '2025-07-30', '2025-07-17', '2025-07-30 15:20:50.000000', 'customer1@gmail.com', '2025-07-30 15:20:56.000000', 'payOnArrival', 'PAID', NULL, NULL, NULL, 1, '', 2420000.00, 1, NULL, 8, 2, NULL, 'CONFIRMED'),
(19, '2025-07-30', NULL, NULL, '2025-07-30', '2025-07-31', '2025-07-30 15:31:08.000000', 'customer1@gmail.com', '2025-07-30 15:41:23.000000', 'payOnArrival', 'PAID', NULL, NULL, NULL, 1, '', 3025000.00, 1, NULL, 1, 2, NULL, 'CONFIRMED'),
(20, '2025-07-31', NULL, NULL, '2025-07-31', '2025-08-01', '2025-07-31 21:55:17.000000', 'customer1@gmail.com', NULL, 'payOnArrival', 'PENDING', NULL, NULL, NULL, 1, '', 4235000.00, 1, NULL, 5, 1, NULL, 'COMPLETED'); 