package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.ReviewService;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class AdminManagementBookingController {

    @Autowired
    private BookingOrderService bookingOrderService; // Service for booking logic
    
    @Autowired
    private ReviewService reviewService; // Service for review logic

    @GetMapping("/admin/bookings")
    public String viewBookingManagementPage(Model model) {
        // Chỉ lấy những booking có trạng thái CONFIRMED (đã đặt phòng thành công)
        List<BookingOrderEntity> allBookings = bookingOrderService.findAllBookingsForAdmin();
        List<BookingOrderEntity> confirmedBookings = allBookings.stream()
                .filter(booking -> booking.getBookingStatus() != null && "CONFIRMED".equals(booking.getBookingStatus()))
                .toList();
        
        // Tính toán thống kê chỉ cho những booking đã xác nhận
        Map<String, Long> stats = new HashMap<>();
        long totalBookings = confirmedBookings.size();
        long confirmedBookingsCount = confirmedBookings.stream()
                .filter(booking -> booking.getBookingStatus() != null && "CONFIRMED".equals(booking.getBookingStatus()))
                .count();
        long pendingBookings = confirmedBookings.stream()
                .filter(booking -> booking.getBookingStatus() != null && "PENDING".equals(booking.getBookingStatus()))
                .count();
        long cancelledBookings = confirmedBookings.stream()
                .filter(booking -> booking.getBookingStatus() != null && "CANCELLED".equals(booking.getBookingStatus()))
                .count();
        long paidBookings = confirmedBookings.stream()
                .filter(booking -> "PAID".equals(booking.getPaymentStatus()))
                .count();
        long pendingPaymentBookings = confirmedBookings.stream()
                .filter(booking -> "PENDING".equals(booking.getPaymentStatus()))
                .count();
        
        stats.put("totalBookings", totalBookings);
        stats.put("confirmedBookings", confirmedBookingsCount);
        stats.put("pendingBookings", pendingBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("paidBookings", paidBookings);
        stats.put("pendingPaymentBookings", pendingPaymentBookings);
        
        model.addAttribute("bookings", confirmedBookings);
        model.addAttribute("stats", stats);
        // The 'allBookings' JS variable in HTML will use this 'bookings' model attribute
        model.addAttribute("allBookingsJson", confirmedBookings); // Pass for JS if needed, or use Thymeleaf's JS inlining
        return "Admin/Management-Booking"; // Path to your Thymeleaf template
    }

    @GetMapping("/admin/bookings/{bookingId}")
    public String viewBookingDetail(@PathVariable Integer bookingId, Model model) {
        var bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
        if (bookingOpt.isEmpty()) {
            // Redirect to booking management page if booking not found
            return "redirect:/admin/bookings";
        }
        
        BookingOrderEntity booking = bookingOpt.get();
        model.addAttribute("booking", booking);
        
        // Thêm reviews cho phòng này nếu có
        if (booking.getRoom() != null) {
            List<ReviewEntity> reviews = reviewService.getReviewsByRoomId(booking.getRoom().getRoomId());
            model.addAttribute("reviews", reviews);
        }
        
        return "Admin/booking-detail";
    }
}