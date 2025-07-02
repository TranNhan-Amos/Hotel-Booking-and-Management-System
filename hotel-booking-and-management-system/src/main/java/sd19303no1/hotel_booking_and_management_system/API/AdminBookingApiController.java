package sd19303no1.hotel_booking_and_management_system.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd19303no1.hotel_booking_and_management_system.DTO.AdminBookingRequestDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;

import java.util.Map;

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

    // CONFIRM Payment (by Admin)
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
                return ResponseEntity.ok(bookingOpt.get());
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
}
