package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Historybooking {
    private static final Logger logger = LoggerFactory.getLogger(Historybooking.class);

    @Autowired
    private BookingOrderService bookingOrderService;

    @Autowired
    private CustomersService customersService;

    @GetMapping("/historybooking")
    public String historybooking(Model model) {
        System.out.println("=== HISTORYBOOKING DEBUG START ===");
        // Kiểm tra xác thực người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            logger.warn("User is not authenticated. Redirecting to login page.");
            return "redirect:/login";
        }

        // Lấy customerId từ Principal (giả sử UserDetails chứa customerId)
        String email = authentication.getName();
        logger.info("Looking for customer with email: {}", email);
        CustomersEntity customer = customersService.findByEmail(email);
        if (customer == null) {
            logger.warn("Customer not found for email: {}", email);
            model.addAttribute("errorMessage", "Không tìm thấy thông tin khách hàng. Vui lòng kiểm tra tài khoản.");
            model.addAttribute("bookings", new ArrayList<>());
            model.addAttribute("statusTexts", new ArrayList<>());
            model.addAttribute("refundStatuses", new HashMap<>());
            model.addAttribute("cancellableMap", new HashMap<>());
            model.addAttribute("expiredMap", new HashMap<>());
            model.addAttribute("countConfirmed", 0);
            model.addAttribute("countCompleted", 0);
            model.addAttribute("countPending", 0);
            model.addAttribute("countCancelled", 0);
            model.addAttribute("countRefunded", 0);
            model.addAttribute("countCancellable", 0);
            model.addAttribute("totalSpent", BigDecimal.ZERO);
            return "Page/Historybooking";
        }

        Integer customerId = customer.getCustomerId();
        logger.info("Found customer: {} with ID: {}", customer.getName(), customerId);
        logger.info("Fetching booking history for customer ID: {}", customerId);

        // Lấy danh sách đặt phòng theo customerId
        List<BookingOrderEntity> bookings = bookingOrderService.getBookingsByCustomerIdForCustomer(customerId);
        if (bookings == null || bookings.isEmpty()) {
            bookings = new ArrayList<>();
            logger.warn("No bookings found for customer ID: {}", customerId);
            model.addAttribute("errorMessage", "Không tìm thấy lịch sử đặt phòng cho tài khoản này.");
            // Thêm empty maps để tránh null pointer
            model.addAttribute("cancellableMap", new HashMap<>());
            model.addAttribute("expiredMap", new HashMap<>());
            model.addAttribute("statusTexts", new ArrayList<>());
            model.addAttribute("refundStatuses", new HashMap<>());
            model.addAttribute("countConfirmed", 0);
            model.addAttribute("countCompleted", 0);
            model.addAttribute("countPending", 0);
            model.addAttribute("countCancelled", 0);
            model.addAttribute("countRefunded", 0);
            model.addAttribute("countCancellable", 0);
            model.addAttribute("totalSpent", BigDecimal.ZERO);
            System.out.println("=== HISTORYBOOKING DEBUG END (NO BOOKINGS) ===");
            return "Page/Historybooking";
        } else {
            logger.info("Found {} bookings for customer ID: {}", bookings.size(), customerId);
        }

        // Việt hóa trạng thái và kiểm tra null
        List<String> statusTexts = new ArrayList<>();
        Map<Long, String> refundStatuses = new HashMap<>();
        for (BookingOrderEntity booking : bookings) {
            String status = (booking.getStatus() != null && booking.getStatus().getStatusName() != null) 
                ? booking.getStatus().getStatusName() 
                : "";
            String statusText = switch (status) {
                case "PENDING" -> "Chờ xác nhận";
                case "CONFIRMED" -> "Đã xác nhận";
                case "COMPLETED" -> "Hoàn thành";
                case "CANCELLED" -> "Đã hủy";
                case "REFUNDED" -> "Đã hoàn tiền";
                default -> "Không xác định";
            };
            statusTexts.add(statusText);

            // Lưu trạng thái hoàn tiền
            String refundStatus = (booking.getRefundStatus() != null) 
                ? booking.getRefundStatus().toString() 
                : "NONE";
            // Đảm bảo kiểu dữ liệu của bookingId là Long để phù hợp với Map<Long, String>
            Long bookingIdLong = booking.getBookingId() != null ? booking.getBookingId().longValue() : null;
            if (bookingIdLong != null) {
                refundStatuses.put(bookingIdLong, refundStatus);
            } else {
                logger.warn("Booking ID null khi lưu trạng thái hoàn tiền.");
            }

            // Ghi log chi tiết để kiểm tra dữ liệu
            logger.debug("Booking ID: {}, Status: {}, Refund Status: {}, Total Price: {}", 
                bookingIdLong, status, refundStatus, booking.getTotalPrice());
        }

        // Đếm số lượng theo trạng thái
        long countConfirmed = bookings.stream()
            .filter(b -> b.getStatus() != null && "CONFIRMED".equals(b.getStatus().getStatusName()))
            .count();
        long countCompleted = bookings.stream()
            .filter(b -> b.getStatus() != null && "COMPLETED".equals(b.getStatus().getStatusName()))
            .count();
        long countPending = bookings.stream()
            .filter(b -> b.getStatus() != null && "PENDING".equals(b.getStatus().getStatusName()))
            .count();
        long countCancelled = bookings.stream()
            .filter(b -> b.getStatus() != null && "CANCELLED".equals(b.getStatus().getStatusName()))
            .count();
        long countRefunded = bookings.stream()
            .filter(b -> b.getStatus() != null && "REFUNDED".equals(b.getStatus().getStatusName()))
            .count();

        // Tính tổng chi tiêu
        BigDecimal totalSpent = bookings.stream()
            .filter(b -> b.getTotalPrice() != null)
            .map(BookingOrderEntity::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Đếm số booking có thể hủy
        long countCancellable = bookings.stream()
            .filter(b -> b.getStatus() != null && 
                        ("PENDING".equals(b.getStatus().getStatusName()) || "CONFIRMED".equals(b.getStatus().getStatusName())) && 
                        b.getCheckInDate() != null && 
                        !LocalDate.now().isAfter(b.getCheckInDate()))
            .count();

        // Tạo map để check booking có thể hủy không
        Map<Integer, Boolean> cancellableMap = new HashMap<>();
        Map<Integer, Boolean> expiredMap = new HashMap<>();
        for (BookingOrderEntity booking : bookings) {
            boolean canCancel = booking.getStatus() != null && 
                               ("PENDING".equals(booking.getStatus().getStatusName()) || "CONFIRMED".equals(booking.getStatus().getStatusName())) && 
                               booking.getCheckInDate() != null && 
                               !LocalDate.now().isAfter(booking.getCheckInDate());
            cancellableMap.put(booking.getBookingId(), canCancel);
            
            boolean isExpired = booking.getCheckInDate() != null && 
                               LocalDate.now().isAfter(booking.getCheckInDate()) &&
                               !canCancel;
            expiredMap.put(booking.getBookingId(), isExpired);
        }

        // Thêm các thuộc tính vào model
        model.addAttribute("bookings", bookings);
        model.addAttribute("statusTexts", statusTexts);
        model.addAttribute("refundStatuses", refundStatuses);
        model.addAttribute("cancellableMap", cancellableMap);
        model.addAttribute("expiredMap", expiredMap);
        model.addAttribute("countConfirmed", countConfirmed);
        model.addAttribute("countCompleted", countCompleted);
        model.addAttribute("countPending", countPending);
        model.addAttribute("countCancelled", countCancelled);
        model.addAttribute("countRefunded", countRefunded);
        model.addAttribute("countCancellable", countCancellable);
        model.addAttribute("totalSpent", totalSpent);

        System.out.println("=== HISTORYBOOKING DEBUG END ===");
        return "Page/Historybooking";
    }

    @PostMapping("/history/cancel-booking")
    public String cancelBooking(@RequestParam("bookingId") Integer bookingId,
                               @RequestParam("reason") String reason,
                               RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra xác thực người dùng
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thực hiện thao tác này.");
                return "redirect:/login";
            }

            // Lấy thông tin khách hàng
            String email = authentication.getName();
            CustomersEntity customer = customersService.findByEmail(email);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin khách hàng.");
                return "redirect:/historybooking";
            }

            // Kiểm tra xem booking có thuộc về khách hàng này không
            BookingOrderEntity booking = bookingOrderService.findBookingByIdForAdmin(bookingId)
                    .orElse(null);
            
            if (booking == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đặt phòng.");
                return "redirect:/historybooking";
            }

            if (!booking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền hủy đặt phòng này.");
                return "redirect:/historybooking";
            }

            // Kiểm tra xem booking có thể hủy không
            if (!bookingOrderService.canCancelBooking(booking)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy đặt phòng này. Vui lòng kiểm tra chính sách hủy phòng.");
                return "redirect:/historybooking";
            }

            // Kiểm tra ngày check-in
            if (booking.getCheckInDate() != null && booking.getCheckInDate().isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy đặt phòng đã qua ngày check-in.");
                return "redirect:/historybooking";
            }

            // Thực hiện hủy booking
            BookingOrderEntity cancelledBooking = bookingOrderService.cancelBooking(bookingId, reason);
            
            // Tính toán số tiền hoàn lại
            BigDecimal refundAmount = bookingOrderService.calculateRefundAmount(cancelledBooking);
            
            String successMessage = "Đã hủy đặt phòng thành công.";
            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                successMessage += " Số tiền hoàn lại: " + refundAmount.toString() + " ₫";
            } else {
                successMessage += " Không có tiền hoàn lại theo chính sách.";
            }
            
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            logger.info("Booking {} cancelled successfully by customer {}", bookingId, customer.getCustomerId());
            
        } catch (Exception e) {
            logger.error("Error cancelling booking {}: {}", bookingId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi hủy đặt phòng: " + e.getMessage());
        }
        
        return "redirect:/historybooking";
    }
    
    // Debug endpoint để test
    @GetMapping("/debug-bookings")
    public String debugBookings(Model model) {
        System.out.println("=== DEBUG BOOKINGS ENDPOINT ===");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println("User email: " + email);
        
        CustomersEntity customer = customersService.findByEmail(email);
        if (customer == null) {
            System.out.println("Customer not found!");
            return "Page/Historybooking";
        }
        
        System.out.println("Customer found: " + customer.getName() + " (ID: " + customer.getCustomerId() + ")");
        
        List<BookingOrderEntity> bookings = bookingOrderService.getBookingsByCustomerIdForCustomer(customer.getCustomerId());
        System.out.println("Bookings found: " + bookings.size());
        
        // Tạo maps cho debug endpoint
        Map<Integer, Boolean> cancellableMap = new HashMap<>();
        Map<Integer, Boolean> expiredMap = new HashMap<>();
        List<String> statusTexts = new ArrayList<>();
        Map<Long, String> refundStatuses = new HashMap<>();
        
        for (BookingOrderEntity booking : bookings) {
            boolean canCancel = booking.getStatus() != null && 
                               ("PENDING".equals(booking.getStatus().getStatusName()) || "CONFIRMED".equals(booking.getStatus().getStatusName())) && 
                               booking.getCheckInDate() != null && 
                               !LocalDate.now().isAfter(booking.getCheckInDate());
            cancellableMap.put(booking.getBookingId(), canCancel);
            
            boolean isExpired = booking.getCheckInDate() != null && 
                               LocalDate.now().isAfter(booking.getCheckInDate()) &&
                               !canCancel;
            expiredMap.put(booking.getBookingId(), isExpired);
            
            // Status texts
            String status = (booking.getStatus() != null && booking.getStatus().getStatusName() != null) 
                ? booking.getStatus().getStatusName() 
                : "";
            String statusText = switch (status) {
                case "PENDING" -> "Chờ xác nhận";
                case "CONFIRMED" -> "Đã xác nhận";
                case "COMPLETED" -> "Hoàn thành";
                case "CANCELLED" -> "Đã hủy";
                case "REFUNDED" -> "Đã hoàn tiền";
                default -> "Không xác định";
            };
            statusTexts.add(statusText);
            
            // Refund statuses
            String refundStatus = (booking.getRefundStatus() != null) 
                ? booking.getRefundStatus().toString() 
                : "NONE";
            Long bookingIdLong = booking.getBookingId() != null ? booking.getBookingId().longValue() : null;
            if (bookingIdLong != null) {
                refundStatuses.put(bookingIdLong, refundStatus);
            }
        }
        
        model.addAttribute("bookings", bookings);
        model.addAttribute("statusTexts", statusTexts);
        model.addAttribute("refundStatuses", refundStatuses);
        model.addAttribute("cancellableMap", cancellableMap);
        model.addAttribute("expiredMap", expiredMap);
        model.addAttribute("debug", true);
        return "Page/Historybooking";
    }
}