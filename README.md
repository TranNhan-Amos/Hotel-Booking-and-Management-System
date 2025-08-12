# 🏨 Hệ Thống Quản Lý Đặt Phòng Khách Sạn

Dự án tốt nghiệp với đề tài "Thiết kế và xây dựng website quản lý đặt phòng khách sạn" nhằm xây dựng một hệ thống trực tuyến giúp khách hàng dễ dàng tìm kiếm, đặt phòng và quản lý thông tin đặt phòng tại khách sạn. Hệ thống cũng hỗ trợ quản trị viên trong việc quản lý phòng, dịch vụ và đơn đặt một cách hiệu quả.

## 🚀 Tính Năng Đã Hoàn Thành

### ✅ **Chức Năng Đặt Phòng (Booking System)**
- **Đặt phòng cơ bản:** Khách hàng có thể chọn phòng, ngày check-in/check-out
- **Kiểm tra phòng trống real-time:** Hệ thống kiểm tra xung đột booking
- **Tính toán giá tự động:** Bao gồm phí dịch vụ, VAT và voucher
- **Validation dữ liệu:** Kiểm tra ngày tháng, thông tin khách hàng
- **Yêu cầu đặc biệt:** Khách hàng có thể ghi chú yêu cầu riêng

### ✅ **Hệ Thống Voucher**
- **Tạo và quản lý voucher:** Admin có thể tạo mã giảm giá
- **Validation voucher:** Kiểm tra hạn sử dụng, số lượng, trạng thái
- **Áp dụng voucher:** Tự động tính toán giảm giá trong booking

### ✅ **Chính Sách Hủy Phòng (Cancellation Policy)**
- **Hủy trước 48 giờ:** Hoàn 100% tiền phòng
- **Hủy trước 24 giờ:** Hoàn 50% tiền phòng  
- **Hủy trong vòng 24 giờ:** Không hoàn tiền
- **Lý do hủy:** Ghi lại lý do hủy phòng

### ✅ **Hệ Thống Hoàn Tiền (Refund Processing)**
- **Tính toán số tiền hoàn lại:** Tự động theo chính sách hủy
- **Trạng thái hoàn tiền:** PENDING → PROCESSED → COMPLETED
- **Lịch sử hoàn tiền:** Ghi lại ngày và số tiền hoàn

### ✅ **Thanh Toán Tại Khách Sạn**
- **Xác nhận thanh toán:** Admin có thể xác nhận khi khách thanh toán
- **Trạng thái thanh toán:** PENDING → PAID
- **Thông tin thanh toán:** Ghi lại phương thức và ngày thanh toán

### ✅ **Quản Lý Trạng Thái Booking**
- **PENDING:** Chờ xác nhận
- **CONFIRMED:** Đã xác nhận
- **CANCELLED:** Đã hủy
- **CHECKED_IN:** Đã check-in
- **CHECKED_OUT:** Đã check-out
- **REFUNDED:** Đã hoàn tiền

### ✅ **Admin Dashboard**
- **CRUD Booking:** Tạo, đọc, cập nhật, xóa đặt phòng
- **Quản lý trạng thái:** Cập nhật trạng thái booking
- **Check-in/Check-out:** Xử lý check-in và check-out
- **Xử lý hoàn tiền:** Admin có thể xử lý hoàn tiền
- **Báo cáo:** Thống kê booking theo trạng thái, thời gian

### ✅ **Giao Diện Người Dùng**
- **Responsive Design:** Tương thích mobile và desktop
- **Booking Confirmation:** Trang xác nhận đặt phòng chi tiết
- **Payment Page:** Trang thanh toán tại khách sạn
- **Admin Interface:** Giao diện quản lý cho admin

## 🛠️ Công Nghệ Sử Dụng

### **Backend**
- **Spring Boot 3.5.0:** Framework chính
- **Java 21:** Ngôn ngữ lập trình
- **Spring Data JPA:** ORM framework
- **Spring Security:** Bảo mật hệ thống
- **MySQL:** Cơ sở dữ liệu
- **Maven:** Build tool

### **Frontend**
- **Thymeleaf:** Template engine
- **HTML5/CSS3:** Giao diện người dùng
- **JavaScript:** Tương tác client-side
- **Bootstrap:** CSS framework (nếu có)

### **Database**
- **MySQL 8.0:** Hệ quản trị cơ sở dữ liệu
- **JPA/Hibernate:** ORM mapping

## 📊 Cấu Trúc Database

### **Các Entity Chính**
- `BookingOrderEntity`: Quản lý đặt phòng
- `RoomEntity`: Thông tin phòng
- `CustomersEntity`: Thông tin khách hàng
- `VoucherEntity`: Mã giảm giá
- `StatusEntity`: Trạng thái booking
- `SystemUserEntity`: Người dùng hệ thống

### **Các Trường Mới Đã Thêm**
- `totalPrice`: Tổng tiền booking
- `cancellationDate`: Ngày hủy phòng
- `cancellationReason`: Lý do hủy
- `refundAmount`: Số tiền hoàn lại
- `refundStatus`: Trạng thái hoàn tiền
- `paymentMethod`: Phương thức thanh toán
- `paymentStatus`: Trạng thái thanh toán
- `specialRequests`: Yêu cầu đặc biệt

## 🔧 API Endpoints

### **Admin Booking API**
```
POST   /api/admin/bookings                    # Tạo booking mới
PUT    /api/admin/bookings/{id}               # Cập nhật booking
DELETE /api/admin/bookings/{id}               # Xóa booking
PATCH  /api/admin/bookings/{id}/status        # Cập nhật trạng thái
POST   /api/admin/bookings/{id}/cancel        # Hủy booking
POST   /api/admin/bookings/{id}/refund        # Xử lý hoàn tiền
POST   /api/admin/bookings/{id}/confirm-payment # Xác nhận thanh toán
POST   /api/admin/bookings/{id}/check-in      # Check-in
POST   /api/admin/bookings/{id}/check-out     # Check-out
GET    /api/admin/bookings                    # Lấy tất cả booking
GET    /api/admin/bookings/{id}               # Lấy booking theo ID
GET    /api/admin/bookings/recent             # Lấy booking gần đây
GET    /api/admin/bookings/status/{status}    # Lấy booking theo trạng thái
```

### **Client Booking API**
```
GET    /bookings                              # Trang đặt phòng
POST   /bookings                              # Tạo booking
GET    /payment                               # Trang thanh toán
POST   /process-payment                       # Xử lý thanh toán
POST   /cancel-booking                        # Hủy booking
POST   /process-refund                        # Xử lý hoàn tiền
GET    /booking-confirmation                  # Trang xác nhận
```

## 🚀 Hướng Dẫn Chạy Dự Án

### **Yêu Cầu Hệ Thống**
- Java 21+
- MySQL 8.0+
- Maven 3.6+

### **Cài Đặt**
1. Clone repository:
```bash
git clone <repository-url>
cd Hotel-Booking-and-Management-System
```

2. Cấu hình database trong `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel-booking-and-management-system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Chạy ứng dụng:
```bash
mvn spring-boot:run
```

4. Truy cập ứng dụng:
- Client: http://localhost:8080
- Admin: http://localhost:8080/admin

## 📋 Tính Năng Có Thể Phát Triển Thêm

### **Tính Năng Nâng Cao**
- [ ] Email confirmation
- [ ] SMS notification
- [ ] Payment gateway integration (VNPay, MoMo)
- [ ] Booking history cho khách hàng
- [ ] Review và rating system
- [ ] Room availability calendar
- [ ] Multi-language support
- [ ] Mobile app

### **Tính Năng Báo Cáo**
- [ ] Revenue reports
- [ ] Occupancy reports
- [ ] Customer analytics
- [ ] Booking trends
- [ ] Export to Excel/PDF

## 👥 Tác Giả

Dự án được phát triển bởi sinh viên ngành Công nghệ thông tin.

## 📄 License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

---

**Lưu ý:** Đây là phiên bản hoàn chỉnh của hệ thống booking với đầy đủ các tính năng cơ bản. Hệ thống đã sẵn sàng để triển khai và sử dụng trong thực tế. 