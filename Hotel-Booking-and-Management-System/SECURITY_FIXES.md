# Các Sửa Đổi Bảo Mật và CSS

## Tóm Tắt Các Thay Đổi

### 1. Sửa Lỗi CSRF Token
**Vấn đề**: Lỗi `EL1007E: Property or field 'token' cannot be found on null` trong file `management-Room.html`

**Giải pháp**:
- Xóa các thẻ meta CSRF token trong `management-Room.html`
- Cấu hình CSRF với `CookieCsrfTokenRepository` để đảm bảo token luôn có sẵn
- Thêm các endpoint cần thiết vào danh sách ignore CSRF

### 2. Cải Thiện Cấu Hình Spring Security

**Các thay đổi chính**:
- Bật lại CSRF protection với cấu hình an toàn
- Sử dụng `CookieCsrfTokenRepository.withHttpOnlyFalse()` để token có thể truy cập từ JavaScript
- Thêm session management với giới hạn 1 session/người dùng
- Thêm exception handling cho access denied và authentication
- Tối ưu hóa các quyền truy cập cho từng role

**Cấu hình CSRF**:
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers(
        "/api/**", "/process-payment", 
        "/css/**", "/Css/**", "/js/**", "/images/**", 
        "/img/**", "/room-images/**", "/favicon.ico",
        "/login", "/register", "/partner/register"
    )
)
```

### 3. Sửa Đường Dẫn CSS
**Vấn đề**: CSS không load được do đường dẫn không chính xác

**Giải pháp**:
- Thay đổi từ `th:href="@{Css/...}"` thành `th:href="@{/Css/...}"` (absolute path)
- Áp dụng cho tất cả file HTML có sử dụng CSS

### 4. Thêm Favicon
- Tạo file `favicon.ico` placeholder để tránh 404 errors
- Thêm `/favicon.ico` vào danh sách permitAll

### 5. Tối Ưu Hóa Quyền Truy Cập

**Public endpoints** (không cần đăng nhập):
- Trang chủ, đăng nhập, đăng ký
- Static resources (CSS, JS, images)
- Trang tìm kiếm, danh sách phòng
- Trang About, Contact, Feedback
- Error pages

**Customer endpoints** (cần đăng nhập với role CUSTOMER):
- Profile, booking management
- Payment processing
- Avatar upload

**Admin endpoints** (cần role ADMIN):
- Tất cả `/admin/**` paths
- Quản lý phòng, khách hàng, voucher
- Báo cáo và cài đặt

**Partner endpoints** (cần role PARTNER):
- Tất cả `/partner/**` paths
- Dashboard, quản lý phòng, báo cáo

### 6. Session Management
- Giới hạn 1 session/người dùng
- Redirect khi session hết hạn
- Xóa cookies khi logout

### 7. Exception Handling
- Custom error pages cho 403, 404, 500
- Redirect unauthorized users về trang login
- Access denied page cho 403 errors

## Cách Sử Dụng CSRF Token

### Trong Form HTML:
```html
<form th:action="@{/protected-endpoint}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    <!-- form fields -->
</form>
```

### Trong JavaScript:
```javascript
// Lấy token từ meta tag
const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// Sử dụng trong fetch/axios
fetch('/api/endpoint', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        [header]: token
    },
    body: JSON.stringify(data)
});
```

## Testing

### Test CSRF:
- Truy cập `/test-csrf` để xem thông tin CSRF token
- Test form với và không có CSRF token

### Test Security:
- Truy cập `/admin/**` khi chưa đăng nhập → redirect to login
- Truy cập `/admin/**` với role CUSTOMER → access denied
- Test logout → clear session và cookies

## Lưu Ý Quan Trọng

1. **CSRF Protection**: Đã bật lại để đảm bảo bảo mật
2. **Session Security**: Giới hạn 1 session/người dùng
3. **Static Resources**: Đã được cấu hình đúng để truy cập tự do
4. **Error Handling**: Custom error pages cho trải nghiệm người dùng tốt hơn

## Files Đã Thay Đổi

1. `SecurityConfig.java` - Cấu hình bảo mật chính
2. `management-Room.html` - Xóa CSRF token meta tags
3. `Index.html` - Sửa đường dẫn CSS
4. `test-css.html` - Sửa đường dẫn CSS
5. `IndexController.java` - Thêm test endpoints
6. `test-csrf.html` - File test CSRF mới
7. `favicon.ico` - File placeholder mới 