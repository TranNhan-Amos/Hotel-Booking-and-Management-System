package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Partner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;

@Controller
public class BookingPartnerController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private BookingOrderService bookingOrderService;

    @GetMapping("/partner/bookings")
    public String viewPartnerBookings(Model model) {
        try {
            System.out.println("=== DEBUG: viewPartnerBookings started ===");

            // Lấy thông tin người dùng đã đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // Lấy email từ Authentication

            System.out.println("=== DEBUG: User email: " + userEmail + " ===");

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            System.out.println("=== DEBUG: SystemUser found: " + (systemUser != null) + " ===");
            if (systemUser != null) {
                System.out.println("=== DEBUG: SystemUser isPartner: " + systemUser.isPartner() + " ===");
            }

            if (systemUser != null && systemUser.isPartner()) {

                PartnerEntity partner = partnerService.findBySystemUser(systemUser);
                System.out.println("=== DEBUG: Partner found: " + (partner != null) + " ===");
                if (partner != null) {
                    System.out.println("=== DEBUG: Partner ID: " + partner.getId() + " ===");
                    System.out.println("=== DEBUG: Partner Email: " + partner.getEmail() + " ===");
                }

                if (partner != null) {
                    Long partnerId = partner.getId();

                    long bookingshomNay = bookingOrderService.countTodayBookingsByPartner(partnerId);
                    System.out.println("=== DEBUG: Today bookings count: " + bookingshomNay + " ===");

                    List<BookingOrderEntity> RoomBookingsPartner = bookingOrderService
                            .findAllBookingsByPartner(partnerId); // Lấy 10 booking gần nhất
                    System.out.println("=== DEBUG: Total bookings found: " + RoomBookingsPartner.size() + " ===");

                    // Debug: Print first few bookings if any exist
                    if (!RoomBookingsPartner.isEmpty()) {
                        System.out.println("=== DEBUG: First booking details ===");
                        BookingOrderEntity firstBooking = RoomBookingsPartner.get(0);
                        System.out.println("=== DEBUG: Booking ID: " + firstBooking.getBookingId() + " ===");
                        System.out.println("=== DEBUG: Booking Email: " + firstBooking.getEmail() + " ===");
                        System.out.println("=== DEBUG: Room: " + (firstBooking.getRoom() != null ? firstBooking.getRoom().getRoomNumber() : "NULL") + " ===");
                        System.out.println("=== DEBUG: Room Partner: " + (firstBooking.getRoom() != null && firstBooking.getRoom().getPartner() != null ? firstBooking.getRoom().getPartner().getId() : "NULL") + " ===");
                        System.out.println("=== DEBUG: Booking Partner: " + (firstBooking.getPartner() != null ? firstBooking.getPartner().getId() : "NULL") + " ===");
                    }

                                                    Integer sumDoanhThuToday = bookingOrderService.sumDoanhThuToday(partnerId);
                                long sumDoanhThuTodayLong = sumDoanhThuToday != null ? sumDoanhThuToday.longValue() : 0L;
                                System.out.println("=== DEBUG: Today revenue: " + sumDoanhThuTodayLong + " ===");

                    model.addAttribute("bookingshomNay", bookingshomNay);
                    model.addAttribute("RoomBookingsPartner", RoomBookingsPartner);
                                                    model.addAttribute("sumDoanhThuToday", sumDoanhThuTodayLong);
                    model.addAttribute("partner", partner);

                    System.out.println("=== DEBUG: Model attributes set successfully ===");
                    return "Partner/BookingsPartner";
                } else {
                    System.out.println("=== DEBUG: Partner not found ===");
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "Partner/BookingsPartner";
                }
            } else {
                System.out.println("=== DEBUG: User is not a partner or systemUser is null ===");
                model.addAttribute("error", "Bạn không có quyền truy cập.");
                return "Partner/BookingsPartner";
            }

        } catch (Exception e) {
            System.out.println("=== DEBUG: Exception occurred: " + e.getMessage() + " ===");
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "Partner/BookingsPartner";
        }
    }

    @GetMapping("/partner/bookings/{bookingId}")
    public String viewBookingDetail(@PathVariable Integer bookingId, Model model) {
        try {
            // Lấy thông tin người dùng đã đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            if (systemUser == null || !systemUser.isPartner()) {
                model.addAttribute("error", "Không có quyền truy cập");
                return "redirect:/partner/bookings";
            }

            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            if (partner == null) {
                model.addAttribute("error", "Không tìm thấy thông tin đối tác");
                return "redirect:/partner/bookings";
            }

            // Tìm booking theo ID
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (!bookingOpt.isPresent()) {
                model.addAttribute("error", "Không tìm thấy đặt phòng");
                return "redirect:/partner/bookings";
            }

            BookingOrderEntity booking = bookingOpt.get();
            
            // Kiểm tra xem booking có thuộc về partner này không
            if (booking.getRoom() == null || booking.getRoom().getPartner() == null || 
                !booking.getRoom().getPartner().getId().equals(partner.getId())) {
                model.addAttribute("error", "Không có quyền xem đặt phòng này");
                return "redirect:/partner/bookings";
            }

            model.addAttribute("booking", booking);
            model.addAttribute("partner", partner);
            
            return "Partner/BookingDetailPartner";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/partner/bookings";
        }
    }

    // API endpoint để xem chi tiết booking
    @GetMapping("/api/partner/bookings/{bookingId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBookingDetails(@PathVariable Integer bookingId) {
        try {
            // Lấy thông tin người dùng đã đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            if (systemUser == null || !systemUser.isPartner()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Không có quyền truy cập");
                return ResponseEntity.badRequest().body(error);
            }

            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            if (partner == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Không tìm thấy thông tin đối tác");
                return ResponseEntity.badRequest().body(error);
            }

            // Tìm booking theo ID
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (!bookingOpt.isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Không tìm thấy đặt phòng");
                return ResponseEntity.badRequest().body(error);
            }

            BookingOrderEntity booking = bookingOpt.get();
            
            // Kiểm tra xem booking có thuộc về partner này không
            if (booking.getRoom() == null || booking.getRoom().getPartner() == null || 
                !booking.getRoom().getPartner().getId().equals(partner.getId())) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Không có quyền xem đặt phòng này");
                return ResponseEntity.badRequest().body(error);
            }

            // Tạo response data
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("bookingId", booking.getBookingId());
            bookingData.put("bookingStatus", booking.getBookingStatus());
            bookingData.put("bookingDate", booking.getBookingDate());
            bookingData.put("totalPrice", booking.getTotalPrice());
            bookingData.put("checkInDate", booking.getCheckInDate());
            bookingData.put("checkOutDate", booking.getCheckOutDate());
            bookingData.put("roomQuantity", booking.getRoomQuantity());
            bookingData.put("specialRequests", booking.getSpecialRequests());
            bookingData.put("paymentMethod", booking.getPaymentMethod());
            bookingData.put("paymentStatus", booking.getPaymentStatus());
            bookingData.put("paidDate", booking.getPaidDate());
            
            // Thông tin khách hàng
            if (booking.getCustomer() != null) {
                bookingData.put("customerName", booking.getCustomer().getName());
                bookingData.put("customerEmail", booking.getCustomer().getEmail());
                bookingData.put("customerPhone", booking.getCustomer().getPhone());
            }
            
            // Thông tin phòng
            if (booking.getRoom() != null) {
                bookingData.put("roomNumber", booking.getRoom().getRoomNumber());
                bookingData.put("roomType", booking.getRoom().getType() != null ? booking.getRoom().getType().getDescription() : null);
            }

            return ResponseEntity.ok(bookingData);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // API endpoint để xác nhận booking
        @PostMapping("/api/partner/bookings/{bookingId}/confirm")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> confirmBooking(@PathVariable Integer bookingId) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String userEmail = authentication.getName();

                SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
                if (systemUser == null || !systemUser.isPartner()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không có quyền truy cập");
                    return ResponseEntity.badRequest().body(error);
                }

                PartnerEntity partner = partnerService.findBySystemUser(systemUser);
                if (partner == null) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không tìm thấy thông tin đối tác");
                    return ResponseEntity.badRequest().body(error);
                }

                Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
                if (!bookingOpt.isPresent()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không tìm thấy đặt phòng");
                    return ResponseEntity.badRequest().body(error);
                }

                BookingOrderEntity booking = bookingOpt.get();
                
                if (booking.getRoom() == null || booking.getRoom().getPartner() == null || 
                    !booking.getRoom().getPartner().getId().equals(partner.getId())) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không có quyền xác nhận đặt phòng này");
                    return ResponseEntity.badRequest().body(error);
                }

                // Xác nhận booking
                bookingOrderService.confirmBooking(bookingId);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Đã xác nhận đặt phòng thành công");
                response.put("success", true);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Có lỗi xảy ra: " + e.getMessage());
                return ResponseEntity.badRequest().body(error);
            }
        }

        // API endpoint để hủy booking
        @PostMapping("/api/partner/bookings/{bookingId}/cancel")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable Integer bookingId, 
                                                                 @RequestParam(required = false) String reason) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String userEmail = authentication.getName();

                SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
                if (systemUser == null || !systemUser.isPartner()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không có quyền truy cập");
                    return ResponseEntity.badRequest().body(error);
                }

                PartnerEntity partner = partnerService.findBySystemUser(systemUser);
                if (partner == null) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không tìm thấy thông tin đối tác");
                    return ResponseEntity.badRequest().body(error);
                }

                Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
                if (!bookingOpt.isPresent()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không tìm thấy đặt phòng");
                    return ResponseEntity.badRequest().body(error);
                }

                BookingOrderEntity booking = bookingOpt.get();
                
                if (booking.getRoom() == null || booking.getRoom().getPartner() == null || 
                    !booking.getRoom().getPartner().getId().equals(partner.getId())) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không có quyền hủy đặt phòng này");
                    return ResponseEntity.badRequest().body(error);
                }

                // Hủy booking
                bookingOrderService.cancelBooking(bookingId, reason != null ? reason : "Được hủy bởi đối tác");

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Đã hủy đặt phòng thành công");
                response.put("success", true);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Có lỗi xảy ra: " + e.getMessage());
                return ResponseEntity.badRequest().body(error);
            }
        }

        // API endpoint để xác nhận thanh toán
        @PostMapping("/api/partner/bookings/{bookingId}/confirm-payment")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable Integer bookingId) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String userEmail = authentication.getName();

                SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
                if (systemUser == null || !systemUser.isPartner()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không có quyền truy cập");
                    return ResponseEntity.badRequest().body(error);
                }

                PartnerEntity partner = partnerService.findBySystemUser(systemUser);
                if (partner == null) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không tìm thấy thông tin đối tác");
                    return ResponseEntity.badRequest().body(error);
                }

                Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
                if (!bookingOpt.isPresent()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không tìm thấy đặt phòng");
                    return ResponseEntity.badRequest().body(error);
                }

                BookingOrderEntity booking = bookingOpt.get();
                
                if (booking.getRoom() == null || booking.getRoom().getPartner() == null || 
                    !booking.getRoom().getPartner().getId().equals(partner.getId())) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", "Không có quyền xác nhận thanh toán cho đặt phòng này");
                    return ResponseEntity.badRequest().body(error);
                }

                // Xác nhận thanh toán
                bookingOrderService.processPayment(bookingId, "PAID");

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Đã xác nhận thanh toán thành công");
                response.put("success", true);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Có lỗi xảy ra: " + e.getMessage());
                return ResponseEntity.badRequest().body(error);
            }
        }
}
