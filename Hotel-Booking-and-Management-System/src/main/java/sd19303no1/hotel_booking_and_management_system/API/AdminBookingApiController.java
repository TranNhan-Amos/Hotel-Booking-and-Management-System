package sd19303no1.hotel_booking_and_management_system.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd19303no1.hotel_booking_and_management_system.DTO.AdminBookingRequestDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.BookingDetailDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;

import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingApiController {

    @Autowired
    private BookingOrderService bookingOrderService;

    // CREATE Booking (by Admin)
    @PostMapping
    public ResponseEntity<?> adminCreateBooking(@RequestBody AdminBookingRequestDTO bookingRequestDTO) {
        try {
            BookingOrderEntity createdBooking = bookingOrderService.adminCreateOrUpdateBooking(null, bookingRequestDTO, false);
            return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi máy chủ: " + e.getMessage()));
        }
    }

    // UPDATE Booking (by Admin)
    @PutMapping("/{bookingId}")
    public ResponseEntity<?> adminUpdateBooking(@PathVariable Integer bookingId,
                                                @RequestBody AdminBookingRequestDTO bookingRequestDTO) {
        try {
            BookingOrderEntity updatedBooking = bookingOrderService.adminCreateOrUpdateBooking(bookingId, bookingRequestDTO, false);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy đặt phòng")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi cập nhật: " + e.getMessage()));
        }
    }

    // DELETE Booking (by Admin)
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> adminDeleteBooking(@PathVariable Integer bookingId) {
        try {
            bookingOrderService.adminDeleteBookingById(bookingId);
            return ResponseEntity.ok(Map.of("message", "Xóa đặt phòng ID: " + bookingId + " thành công."));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy đặt phòng")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi xóa: " + e.getMessage()));
        }
    }

    // UPDATE Booking Status (by Admin)
    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<?> adminUpdateBookingStatus(@PathVariable Integer bookingId,
                                                      @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Trạng thái mới không được để trống."));
        }

        try {
            BookingOrderEntity updatedBooking = bookingOrderService.adminUpdateBookingStatus(bookingId, newStatus);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy đặt phòng")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi cập nhật trạng thái: " + e.getMessage()));
        }
    }

    // CANCEL Booking (by Admin)
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> adminCancelBooking(@PathVariable Integer bookingId,
                                                @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lý do hủy không được để trống."));
        }

        try {
            BookingOrderEntity cancelledBooking = bookingOrderService.cancelBooking(bookingId, reason);
            return ResponseEntity.ok(Map.of(
                "message", "Hủy đặt phòng thành công.",
                "booking", cancelledBooking,
                "refundAmount", cancelledBooking.getRefundAmount()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi hủy đặt phòng: " + e.getMessage()));
        }
    }

    // PROCESS Refund (by Admin)
    @PostMapping("/{bookingId}/refund")
    public ResponseEntity<?> adminProcessRefund(@PathVariable Integer bookingId) {
        try {
            BookingOrderEntity refundedBooking = bookingOrderService.processRefund(bookingId);
            return ResponseEntity.ok(Map.of(
                "message", "Xử lý hoàn tiền thành công.",
                "booking", refundedBooking
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi xử lý hoàn tiền: " + e.getMessage()));
        }
    }

    // CONFIRM Payment (by Admin) - cho thanh toán online
    @PostMapping("/{bookingId}/confirm-payment")
    public ResponseEntity<?> adminConfirmPayment(@PathVariable Integer bookingId) {
        try {
            BookingOrderEntity confirmedBooking = bookingOrderService.confirmPayment(bookingId);
            return ResponseEntity.ok(Map.of(
                "message", "Xác nhận thanh toán thành công.",
                "booking", confirmedBooking
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi xác nhận thanh toán: " + e.getMessage()));
        }
    }

    // CONFIRM Payment at Hotel (by Admin) - cho thanh toán tại khách sạn
    @PostMapping("/{bookingId}/confirm-payment-hotel")
    public ResponseEntity<?> adminConfirmPaymentAtHotel(@PathVariable Integer bookingId) {
        try {
            BookingOrderEntity confirmedBooking = bookingOrderService.confirmPaymentAtHotel(bookingId);
            return ResponseEntity.ok(Map.of(
                "message", "Xác nhận thanh toán tại khách sạn thành công.",
                "booking", confirmedBooking
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi xác nhận thanh toán tại khách sạn: " + e.getMessage()));
        }
    }

    // UPDATE Booking Details (by Admin) - chức năng chỉnh sửa đặt phòng
    @PutMapping("/{bookingId}/update")
    public ResponseEntity<?> adminUpdateBookingDetails(@PathVariable Integer bookingId,
                                                       @RequestBody Map<String, Object> updateData) {
        try {
            BookingOrderEntity updatedBooking = bookingOrderService.updateBookingDetails(bookingId, updateData);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật đặt phòng thành công.",
                "booking", updatedBooking
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy đặt phòng")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Lỗi khi cập nhật đặt phòng: " + e.getMessage()
                    ));
        }
    }

    // SEND Notification (by Admin) - chức năng gửi thông báo
    @PostMapping("/{bookingId}/send-notification")
    public ResponseEntity<?> adminSendNotification(@PathVariable Integer bookingId,
                                                   @RequestBody Map<String, Object> notificationData) {
        try {
            String notificationType = (String) notificationData.get("notificationType");
            String title = (String) notificationData.get("title");
            String content = (String) notificationData.get("content");
            Boolean sendEmail = (Boolean) notificationData.get("sendEmail");
            Boolean sendSMS = (Boolean) notificationData.get("sendSMS");

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Tiêu đề thông báo không được để trống."
                ));
            }

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Nội dung thông báo không được để trống."
                ));
            }

            // Gọi service để gửi thông báo
            boolean sent = bookingOrderService.sendNotification(bookingId, notificationType, title, content, sendEmail, sendSMS);
            
            if (sent) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã gửi thông báo thành công."
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                            "success", false,
                            "message", "Không thể gửi thông báo."
                        ));
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy đặt phòng")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Lỗi khi gửi thông báo: " + e.getMessage()
                    ));
        }
    }

    // CHECK-IN (by Admin)
    @PostMapping("/{bookingId}/check-in")
    public ResponseEntity<?> adminCheckIn(@PathVariable Integer bookingId) {
        try {
            BookingOrderEntity checkedInBooking = bookingOrderService.checkIn(bookingId);
            return ResponseEntity.ok(Map.of(
                "message", "Check-in thành công.",
                "booking", checkedInBooking
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi check-in: " + e.getMessage()));
        }
    }

    // CHECK-OUT (by Admin)
    @PostMapping("/{bookingId}/check-out")
    public ResponseEntity<?> adminCheckOut(@PathVariable Integer bookingId) {
        try {
            BookingOrderEntity checkedOutBooking = bookingOrderService.checkOut(bookingId);
            return ResponseEntity.ok(Map.of(
                "message", "Check-out thành công.",
                "booking", checkedOutBooking
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi check-out: " + e.getMessage()));
        }
    }

    // GET Booking by ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer bookingId) {
        try {
            var bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                BookingOrderEntity booking = bookingOpt.get();
                BookingDetailDTO dto = new BookingDetailDTO();
                
                // Map booking data to DTO với xử lý null safety
                dto.setBookingId(booking.getBookingId());
                dto.setCustomerName(booking.getCustomer() != null ? booking.getCustomer().getName() : "Chưa có");
                dto.setCustomerEmail(booking.getCustomer() != null ? booking.getCustomer().getEmail() : 
                    (booking.getEmail() != null ? booking.getEmail() : "Chưa có"));
                dto.setCustomerPhone(booking.getCustomer() != null ? booking.getCustomer().getPhone() : "Chưa cập nhật");
                dto.setRoomNumber(booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "Chưa có");
                dto.setRoomType(booking.getRoom() != null && booking.getRoom().getType() != null ? 
                    booking.getRoom().getType().getDescription() : "Chưa có");
                dto.setRoomQuantity(booking.getRoomQuantity() != null ? booking.getRoomQuantity() : 1);
                dto.setCheckInDate(booking.getCheckInDate());
                dto.setCheckOutDate(booking.getCheckOutDate());
                dto.setBookingDate(booking.getBookingDate());
                dto.setTotalPrice(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO);
                
                // Sử dụng booking_status từ BookingOrderEntity như yêu cầu
                dto.setStatusName(booking.getBookingStatus() != null ? booking.getBookingStatus() : "Chưa xác định");
                
                dto.setPaymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "Chưa xác định");
                dto.setPaymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "Chưa xác định");
                dto.setPaidDate(booking.getPaidDate());
                dto.setSpecialRequests(booking.getSpecialRequests() != null ? booking.getSpecialRequests() : "Không có");
                dto.setCancellationDate(booking.getCancellationDate());
                dto.setCancellationReason(booking.getCancellationReason() != null ? booking.getCancellationReason() : "Không có");
                dto.setRefundAmount(booking.getRefundAmount() != null ? booking.getRefundAmount() : BigDecimal.ZERO);
                dto.setRefundStatus(booking.getRefundStatus() != null ? booking.getRefundStatus() : "Chưa xác định");
                dto.setRefundDate(booking.getRefundDate());
                dto.setVoucherCode(booking.getVoucher() != null ? booking.getVoucher().getCode() : "Không có");
                dto.setVoucherDiscount(booking.getVoucher() != null ? booking.getVoucher().getDiscountAmount() : BigDecimal.ZERO);
                
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy đặt phòng với ID: " + bookingId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi lấy thông tin đặt phòng: " + e.getMessage()));
        }
    }

    // GET All Bookings
    @GetMapping
    public ResponseEntity<?> getAllBookings() {
        try {
            var bookings = bookingOrderService.findAllBookingsForAdmin();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi lấy danh sách đặt phòng: " + e.getMessage()));
        }
    }

    // GET Recent Bookings
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentBookings(@RequestParam(defaultValue = "10") int limit) {
        try {
            var recentBookings = bookingOrderService.getRecentBookings(limit);
            return ResponseEntity.ok(recentBookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi lấy đặt phòng gần đây: " + e.getMessage()));
        }
    }

    // GET Bookings by Status
    @GetMapping("/status/{statusName}")
    public ResponseEntity<?> getBookingsByStatus(@PathVariable String statusName) {
        try {
            var bookings = bookingOrderService.findByStatusName(statusName);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi lấy đặt phòng theo trạng thái: " + e.getMessage()));
        }
    }

    // GET Bookings by Booking Status (sử dụng booking_status field)
    @GetMapping("/booking-status/{bookingStatus}")
    public ResponseEntity<?> getBookingsByBookingStatus(@PathVariable String bookingStatus) {
        try {
            var bookings = bookingOrderService.findByBookingStatus(bookingStatus);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi lấy đặt phòng theo booking status: " + e.getMessage()));
        }
    }

    // GET Statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getBookingStatistics() {
        try {
            var stats = bookingOrderService.getBookingStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi lấy thống kê đặt phòng: " + e.getMessage()));
        }
    }
}
