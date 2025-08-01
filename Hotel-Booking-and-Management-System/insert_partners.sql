-- Insert sample partners
INSERT INTO partner_entity (id, company_name, email, phone, address, contact_person, tax_id, business_license, businessmodel, hotelamenities, system_user_id) 
VALUES 
(1, 'Khách sạn Mường Thanh', 'muongthanh@example.com', '0123456789', 'Hà Nội, Việt Nam', 'Nguyễn Văn A', 'TAX001', 'LIC001', 'Luxury', 'Pool, Spa, Restaurant', NULL),
(2, 'Grand Hotel & Resort', 'grandhotel@example.com', '0987654321', 'TP.HCM, Việt Nam', 'Trần Thị B', 'TAX002', 'LIC002', 'Premium', 'Gym, Bar, Conference Room', NULL),
(3, 'Seaside Resort', 'seaside@example.com', '0555666777', 'Đà Nẵng, Việt Nam', 'Lê Văn C', 'TAX003', 'LIC003', 'Resort', 'Beach, Water Sports, Spa', NULL);

-- Insert sample rooms for partners
INSERT INTO room_entity (room_id, name_number, room_number, type, price, capacity, description, status, partner_id, image_urls) 
VALUES 
(1, 'Phòng Deluxe Ocean View', 'A101', 'DELUXE', 2500000, 2, 'Phòng đẹp view biển', 'AVAILABLE', 1, '["room1.jpg"]'),
(2, 'Phòng Suite Premium', 'B201', 'SUITE', 3500000, 4, 'Phòng suite cao cấp', 'AVAILABLE', 1, '["room2.jpg"]'),
(3, 'Phòng Standard', 'C301', 'STANDARD', 1500000, 2, 'Phòng tiêu chuẩn', 'OCCUPIED', 2, '["room3.jpg"]'),
(4, 'Phòng Premium', 'D401', 'PREMIUM', 3000000, 3, 'Phòng premium', 'MAINTENANCE', 2, '["room4.jpg"]'),
(5, 'Phòng Resort', 'E501', 'DELUXE', 2800000, 2, 'Phòng resort', 'AVAILABLE', 3, '["room5.jpg"]'); 