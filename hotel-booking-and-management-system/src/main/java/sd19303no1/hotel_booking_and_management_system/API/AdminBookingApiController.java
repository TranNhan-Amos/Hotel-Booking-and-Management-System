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
}
