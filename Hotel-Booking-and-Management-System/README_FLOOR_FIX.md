# Hướng dẫn sửa lỗi "Field 'floor' doesn't have a default value"

## Vấn đề
Lỗi này xảy ra khi database có column `floor` nhưng entity không có field tương ứng, hoặc column `floor` có NOT NULL constraint mà không có default value.

## Các bước giải quyết

### 1. Kiểm tra database schema
Chạy script `check_and_fix_database.sql` để kiểm tra:
```sql
-- Kiểm tra cấu trúc bảng rooms
DESCRIBE rooms;

-- Kiểm tra xem có column floor không
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'nhanvmco_DATN' 
AND TABLE_NAME = 'rooms' 
AND COLUMN_NAME = 'floor';
```

### 2. Nếu có column floor và gây lỗi
Chạy lệnh sau để xóa column floor:
```sql
ALTER TABLE rooms DROP COLUMN floor;
```

### 3. Test database schema
Truy cập URL: `http://localhost:8080/admin/rooms/fix-schema`
- Nếu thành công: Database schema hoạt động bình thường
- Nếu lỗi: Xem thông báo lỗi và làm theo hướng dẫn

### 4. Reset database schema (nếu cần)
Nếu vẫn có lỗi, có thể cần reset database schema:
1. Backup dữ liệu hiện tại
2. Chạy script `reset_database_schema.sql` (bỏ comment các lệnh cần thiết)
3. Restart ứng dụng

### 5. Kiểm tra application.properties
Đảm bảo cấu hình Hibernate đúng:
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 6. Clear Hibernate cache
Nếu vẫn có lỗi, thử:
1. Dừng ứng dụng
2. Xóa thư mục target (nếu có)
3. Restart ứng dụng

## Debug endpoints
- `/admin/rooms/debug-schema`: Kiểm tra entity và tạo test room
- `/admin/rooms/fix-schema`: Test database schema và tự động sửa lỗi

## Logs
Kiểm tra logs để xem chi tiết lỗi:
```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Lưu ý
- Không thêm field `floor` vào `RoomEntity` vì user không muốn sử dụng
- Đảm bảo database schema khớp với entity definition
- Backup dữ liệu trước khi thay đổi database schema 