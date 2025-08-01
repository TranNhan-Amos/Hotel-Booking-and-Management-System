# Hệ Thống Quản Lý Đặt Phòng Khách Sạn

## Mô tả
Hệ thống quản lý đặt phòng khách sạn là một ứng dụng web được phát triển bằng Spring Boot, cho phép admin quản lý tất cả các phòng mà đối tác rao bán trên nền tảng.

## Tính năng chính

### 1. Quản lý phòng (Admin)
- **Xem tất cả phòng**: Hiển thị danh sách tất cả phòng từ các đối tác
- **Lọc theo đối tác**: Lọc phòng theo từng đối tác cụ thể
- **Lọc theo trạng thái**: Lọc theo trạng thái phòng (Còn trống, Đã đặt, Bảo trì, Đang dọn dẹp, Đã đặt trước, Hết phòng)
- **Lọc theo loại phòng**: Lọc theo loại phòng (Standard, Deluxe, Suite, VIP, Family)
- **Tìm kiếm**: Tìm kiếm phòng theo tên, số phòng, hoặc mô tả
- **Thống kê real-time**: Hiển thị thống kê tổng quan và theo đối tác
- **Quản lý trạng thái**: Cập nhật trạng thái phòng trực tiếp từ giao diện
- **Thêm/Sửa/Xóa phòng**: Quản lý thông tin phòng

### 2. Thống kê và báo cáo
- **Thống kê tổng quan**: Số lượng phòng theo trạng thái
- **Thống kê theo đối tác**: Thống kê chi tiết cho từng đối tác
- **Tỷ lệ sử dụng**: Tính toán tỷ lệ sử dụng phòng
- **Thống kê theo loại phòng**: Phân tích phân bố loại phòng

### 3. Giao diện người dùng
- **Responsive Design**: Giao diện thích ứng với mọi thiết bị
- **Real-time Filtering**: Lọc dữ liệu theo thời gian thực
- **Interactive Cards**: Thẻ phòng với hiệu ứng hover
- **Status Badges**: Hiển thị trạng thái phòng rõ ràng
- **Partner Information**: Hiển thị thông tin đối tác sở hữu phòng

## Cấu trúc dự án

```
Hotel-Booking-and-Management-System/
├── src/main/java/sd19303no1/hotel_booking_and_management_system/
│   ├── Controller/
│   │   ├── PageController/Admin/
│   │   │   └── AdminManagementRoomController.java
│   │   └── ClientController/
│   ├── Entity/
│   │   ├── RoomEntity.java
│   │   ├── PartnerEntity.java
│   │   └── BookingOrderEntity.java
│   ├── Service/
│   │   ├── RoomService.java
│   │   └── PartnerService.java
│   └── Repository/
│       ├── RoomRepository.java
│       └── PartnerRepository.java
└── src/main/resources/templates/Admin/
    ├── management-Room.html
    ├── add-Room.html
    └── edit-Room.html
```

## API Endpoints

### Quản lý phòng
- `GET /admin/rooms` - Trang quản lý phòng
- `GET /admin/rooms/add` - Trang thêm phòng mới
- `GET /admin/rooms/edit/{id}` - Trang chỉnh sửa phòng
- `POST /admin/rooms/save` - Lưu phòng
- `GET /admin/rooms/delete/{id}` - Xóa phòng
- `POST /admin/rooms/update-status` - Cập nhật trạng thái phòng

### API thống kê
- `GET /admin/rooms/partner-stats?partnerId={id}` - Thống kê theo đối tác
- `GET /admin/rooms/overall-stats` - Thống kê tổng quan

## Cài đặt và chạy

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Cài đặt
1. Clone repository:
```bash
git clone [repository-url]
cd Hotel-Booking-and-Management-System
```

2. Cấu hình database trong `application.properties`

3. Chạy ứng dụng:
```bash
mvn spring-boot:run
```

4. Truy cập: `http://localhost:8080/admin/rooms`

## Tính năng nâng cao

### 1. Real-time Filtering
- Lọc dữ liệu theo thời gian thực khi người dùng thay đổi bộ lọc
- Cập nhật số lượng phòng hiển thị động
- Tối ưu hiệu suất với debouncing

### 2. Advanced Search
- Tìm kiếm theo tên phòng, số phòng, mô tả
- Tìm kiếm không phân biệt hoa thường
- Hỗ trợ tìm kiếm một phần từ khóa

### 3. Partner Management
- Quản lý thông tin đối tác
- Thống kê chi tiết theo từng đối tác
- Phân quyền theo đối tác

### 4. Room Status Management
- 6 trạng thái phòng: AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING, RESERVED, FULL
- Cập nhật trạng thái trực tiếp từ giao diện
- Lịch sử thay đổi trạng thái

## Cấu hình

### Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_booking
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### File Upload Configuration
```properties
# Đường dẫn upload ảnh phòng
room.image.upload.dir=F:/path/to/upload/directory
```

## Đóng góp

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

## Liên hệ

- Email: [your-email@example.com]
- GitHub: [your-github-profile]

---

**Lưu ý**: Đây là phiên bản cải tiến của hệ thống quản lý phòng khách sạn với các tính năng nâng cao và giao diện người dùng hiện đại. 