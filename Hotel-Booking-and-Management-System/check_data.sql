-- Kiểm tra dữ liệu trong database
SELECT 'Partners' as table_name, COUNT(*) as count FROM partner;

SELECT 'Rooms' as table_name, COUNT(*) as count FROM rooms;

SELECT 'Customers' as table_name, COUNT(*) as count FROM customers;

SELECT 'Bookings' as table_name, COUNT(*) as count FROM bookingorder;

SELECT 'Status' as table_name, COUNT(*) as count FROM status;

-- Kiểm tra chi tiết
SELECT 'Partner Details' as info, id, company_name, email FROM partner LIMIT 5;

SELECT 'Room Details' as info, room_id, room_number, partner_id FROM rooms LIMIT 5;

SELECT 'Booking Details' as info, booking_id, room_id, customer_id, partner_id, status_id FROM bookingorder LIMIT 5;

SELECT 'Status Details' as info, status_id, status_name FROM status; 