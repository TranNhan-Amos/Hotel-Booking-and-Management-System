# 🏨 LOGIC TRẠNG THÁI THANH TOÁN - HOTEL BOOKING SYSTEM

## 📋 TỔNG QUAN

Hệ thống quản lý trạng thái thanh toán và booking dựa trên phương thức thanh toán được chọn.

## 🔄 LUỒNG XỬ LÝ TRẠNG THÁI

### **1. PHƯƠNG THỨC THANH TOÁN NGAY**
**Bao gồm**: Thẻ tín dụng, MoMo, Chuyển khoản

#### **Trạng thái sau khi thanh toán:**
- **Payment Status**: `PAID` (Đã thanh toán)
- **Booking Status**: `CONFIRMED` (Đã xác nhận)
- **Paid Date**: Thời gian hiện tại
- **Thông báo**: "Đã thanh toán, chờ nhận phòng"
- **Mô tả**: Khách hàng đã thanh toán đầy đủ, booking được xác nhận ngay lập tức

#### **Luồng xử lý:**
```
[Payment Form] → [Validation] → [Process Payment] → [Set PAID] → [Set CONFIRMED] → [Success Page]
```

### **2. PHƯƠNG THỨC THANH TOÁN TẠI KHÁCH SẠN**
**Bao gồm**: Thanh toán khi check-in

#### **Trạng thái sau khi đặt phòng:**
- **Payment Status**: `PENDING` (Chờ thanh toán)
- **Booking Status**: `PENDING` (Chờ xác nhận)
- **Paid Date**: `null`
- **Thông báo**: "Đặt phòng thành công! Chờ thanh toán tại khách sạn"
- **Mô tả**: Khách hàng đặt phòng thành công nhưng chưa thanh toán, cần thanh toán khi check-in

#### **Luồng xử lý:**
```
[Payment Form] → [Validation] → [Create Booking] → [Set PENDING] → [Success Page] → [Check-in] → [Pay at Hotel]
```

## 🎯 CHI TIẾT IMPLEMENTATION

### **Enum PaymentMethod**
```java
public enum PaymentMethod {
    CREDIT_CARD("creditCard", "Thẻ tín dụng", true),
    MOMO("momo", "MoMo", true),
    BANK_TRANSFER("bankTransfer", "Chuyển khoản", true),
    PAY_ON_ARRIVAL("payOnArrival", "Thanh toán tại khách sạn", false);
}
```

### **Enum PaymentStatus**
```java
public enum PaymentStatus {
    PENDING("PENDING", "Chờ thanh toán"),
    PAID("PAID", "Đã thanh toán"),
    FAILED("FAILED", "Thanh toán thất bại"),
    REFUNDED("REFUNDED", "Đã hoàn tiền");
}
```

### **Service Method**
```java
@Transactional
public BookingOrderEntity processPayment(Integer bookingId, String paymentMethodCode) {
    PaymentMethod paymentMethod = PaymentMethod.fromCode(paymentMethodCode);
    
    if (paymentMethod.requiresImmediatePayment()) {
        // Thanh toán ngay
        booking.setPaymentStatus(PaymentStatus.PAID.getCode());
        booking.setStatus(StatusEntity.CONFIRMED);
        booking.setPaidDate(LocalDateTime.now());
    } else {
        // Thanh toán tại khách sạn
        booking.setPaymentStatus(PaymentStatus.PENDING.getCode());
        booking.setStatus(StatusEntity.PENDING);
    }
    
    return bookingOrderRepository.save(booking);
}
```

## 📊 BẢNG TRẠNG THÁI

| Phương thức | Payment Status | Booking Status | Thông báo |
|-------------|----------------|----------------|-----------|
| **Thẻ tín dụng** | `PAID` | `CONFIRMED` | "Đã thanh toán, chờ nhận phòng" |
| **MoMo** | `PAID` | `CONFIRMED` | "Đã thanh toán, chờ nhận phòng" |
| **Chuyển khoản** | `PAID` | `CONFIRMED` | "Đã thanh toán, chờ nhận phòng" |
| **Tại khách sạn** | `PENDING` | `PENDING` | "Đặt phòng thành công! Chờ thanh toán tại khách sạn" |

## 🔍 CÁC TRẠNG THÁI BOOKING KHÁC

### **StatusEntity.Status Enum:**
- `PENDING` - Chờ xác nhận
- `CONFIRMED` - Đã xác nhận
- `CANCELLED` - Đã hủy
- `COMPLETED` - Hoàn thành
- `CHECKED_IN` - Đã check-in
- `CHECKED_OUT` - Đã check-out
- `NO_SHOW` - Không đến
- `REFUNDED` - Đã hoàn tiền

## 🎯 LỢI ÍCH CỦA LOGIC MỚI

### **1. Rõ ràng về nghiệp vụ**
- Thanh toán ngay → Xác nhận ngay
- Thanh toán sau → Chờ xác nhận

### **2. Dễ quản lý**
- Sử dụng enum thay vì string
- Logic tập trung trong service
- Validation rõ ràng

### **3. Dễ mở rộng**
- Thêm phương thức thanh toán mới
- Thêm trạng thái mới
- Tích hợp payment gateway

### **4. Bảo mật**
- Validation chặt chẽ
- Không cho phép thay đổi trạng thái tùy ý
- Audit trail đầy đủ

## 🚀 HƯỚNG PHÁT TRIỂN

### **1. Tích hợp Payment Gateway**
```java
// TODO: Integrate with VNPay, MoMo, etc.
if (paymentMethod.requiresImmediatePayment()) {
    PaymentResult result = paymentGateway.processPayment(booking);
    if (result.isSuccess()) {
        booking.setPaymentStatus(PaymentStatus.PAID.getCode());
    } else {
        booking.setPaymentStatus(PaymentStatus.FAILED.getCode());
    }
}
```

### **2. Email Notification**
```java
// Send confirmation email
if (booking.getPaymentStatus().equals(PaymentStatus.PAID.getCode())) {
    emailService.sendPaymentConfirmation(booking);
} else {
    emailService.sendBookingConfirmation(booking);
}
```

### **3. SMS Notification**
```java
// Send SMS notification
smsService.sendBookingConfirmation(booking.getCustomer().getPhone(), booking);
```

## 📝 KẾT LUẬN

Logic trạng thái mới đã giải quyết vấn đề:
- ✅ Thanh toán ngay → Trạng thái CONFIRMED
- ✅ Thanh toán sau → Trạng thái PENDING
- ✅ Sử dụng enum thay vì string
- ✅ Logic tập trung trong service
- ✅ Dễ bảo trì và mở rộng 