# Hướng Dẫn Sử Dụng Tính Năng Upload Ảnh Phòng

## Tổng Quan
Hệ thống đã được tích hợp tính năng upload và quản lý ảnh cho các phòng khách sạn. Tính năng này cho phép Admin và Partner upload nhiều ảnh cho mỗi phòng.

## Các Tính Năng Đã Thêm

### 1. RoomImageUploadController
- **File**: `src/main/java/sd19303no1/hotel_booking_and_management_system/Controller/PageController/Client/RoomImageUploadController.java`
- **Chức năng**:
  - Upload nhiều ảnh cho phòng (`POST /upload-room-images`)
  - Xóa ảnh phòng (`DELETE /delete-room-image`)
  - Lấy danh sách ảnh phòng (`GET /room-images/{roomId}`)

### 2. Cập Nhật Entity
- **RoomEntity** đã có sẵn trường `imageUrls` (List<String>) để lưu trữ URL ảnh

### 3. Cập Nhật Templates

#### Admin Management Room (`/admin/rooms`)
- Thêm form upload ảnh trong trang chỉnh sửa phòng
- Hiển thị ảnh hiện tại với nút xóa
- Preview ảnh trước khi upload
- Upload nhiều ảnh cùng lúc

#### Partner Room Management (`/room/partner`)
- Thêm nút quản lý ảnh cho mỗi phòng
- Modal quản lý ảnh với giao diện thân thiện
- Upload và xóa ảnh trực tiếp

#### Room Details Page (`/details/{roomId}`)
- Hiển thị gallery ảnh phòng
- Ảnh chính và thumbnail
- Chuyển đổi ảnh chính bằng cách click thumbnail

### 4. Cập Nhật CSS
- **Admin**: `src/main/resources/static/Css/Admin/management-Room.css`
- **Partner**: `src/main/resources/static/Css/Partner/RoomPartner.css`
- **Details**: `src/main/resources/static/Css/Details.css`

### 5. Cập Nhật Security
- Thêm các endpoint upload ảnh vào SecurityConfig
- Chỉ Admin và Partner sở hữu phòng mới có quyền upload/xóa ảnh

## Cách Sử Dụng

### Cho Admin:
1. Truy cập `/admin/rooms`
2. Chọn công ty quản lý phòng
3. Click "Sửa" cho phòng cần thêm ảnh
4. Trong form chỉnh sửa, cuộn xuống phần "Hình ảnh phòng"
5. Xem ảnh hiện tại và xóa nếu cần
6. Click "Chọn ảnh" để upload ảnh mới
7. Preview ảnh và click "Upload Ảnh"

### Cho Partner:
1. Truy cập `/room/partner`
2. Trong bảng danh sách phòng, click nút camera (📷) ở cột "Hình ảnh"
3. Modal quản lý ảnh sẽ mở
4. Xem ảnh hiện tại và xóa nếu cần
5. Click "Chọn ảnh" để upload ảnh mới
6. Preview và click "Upload Ảnh"

### Cho Khách Hàng:
1. Truy cập trang chi tiết phòng `/details/{roomId}`
2. Xem gallery ảnh phòng
3. Click thumbnail để chuyển đổi ảnh chính

## Yêu Cầu Kỹ Thuật

### File Upload:
- **Định dạng**: JPG, PNG, GIF
- **Kích thước tối đa**: 10MB mỗi ảnh
- **Lưu trữ**: `src/main/resources/static/img/rooms/`
- **Naming**: `room_{roomId}_{UUID}.{extension}`

### Bảo Mật:
- Chỉ Admin và Partner sở hữu phòng mới có quyền upload
- Validate file type và size
- Kiểm tra quyền truy cập trước khi thao tác

### Database:
- Ảnh được lưu dưới dạng URL trong trường `imageUrls` của `RoomEntity`
- URL format: `/img/rooms/{filename}`

## API Endpoints

### Upload Images
```
POST /upload-room-images
Content-Type: multipart/form-data

Parameters:
- roomId: Integer (ID phòng)
- files: MultipartFile[] (Danh sách file ảnh)

Response:
{
  "success": true,
  "message": "Upload thành công X ảnh",
  "uploadedImages": ["/img/rooms/room_1_abc123.jpg", ...],
  "errors": [] // Nếu có lỗi
}
```

### Delete Image
```
DELETE /delete-room-image
Content-Type: application/x-www-form-urlencoded

Parameters:
- roomId: Integer (ID phòng)
- imageUrl: String (URL ảnh cần xóa)

Response:
{
  "success": true,
  "message": "Xóa ảnh thành công"
}
```

### Get Room Images
```
GET /room-images/{roomId}

Response:
{
  "success": true,
  "images": ["/img/rooms/room_1_abc123.jpg", ...]
}
```

## Cấu Trúc Thư Mục
```
src/main/resources/static/img/
├── customers/          # Ảnh avatar khách hàng
├── partners/           # Ảnh avatar partner
├── staff/              # Ảnh avatar staff
├── users/              # Ảnh avatar admin
└── rooms/              # Ảnh phòng (MỚI)
    ├── room_1_abc123.jpg
    ├── room_1_def456.png
    └── ...
```

## Lưu Ý
1. Đảm bảo thư mục `src/main/resources/static/img/rooms/` có quyền ghi
2. Restart ứng dụng sau khi thêm tính năng mới
3. Kiểm tra log để debug nếu có lỗi upload
4. Backup dữ liệu trước khi test trên production

## Troubleshooting

### Lỗi Upload:
- Kiểm tra quyền thư mục
- Kiểm tra kích thước file
- Kiểm tra định dạng file
- Kiểm tra log lỗi

### Ảnh Không Hiển Thị:
- Kiểm tra đường dẫn file
- Kiểm tra quyền đọc file
- Kiểm tra cấu hình static resources

### Lỗi Database:
- Kiểm tra kết nối database
- Kiểm tra cấu trúc bảng rooms
- Kiểm tra trường imageUrls 