package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private BookingOrderService bookingOrderService;
    
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/admin/bookings/status/{status}")
    public String viewBookingsByStatus(@PathVariable String status, Model model) {
        List<BookingOrderEntity> allBookings = bookingOrderService.findAllBookingsForAdmin();
        
        List<BookingOrderEntity> filteredBookings;
        if ("ALL".equals(status)) {
            filteredBookings = allBookings;
        } else {
            filteredBookings = allBookings.stream()
                    .filter(booking -> status.equals(booking.getBookingStatus()))
                    .toList();
        }
        
        Map<String, Long> stats = calculateBookingStats(allBookings);
        
        model.addAttribute("currentView", "status");
        model.addAttribute("currentStatus", status);
        model.addAttribute("bookings", filteredBookings);
        model.addAttribute("stats", stats);
        model.addAttribute("allBookingsJson", filteredBookings);
        
        return "Admin/Management-Booking";
    }
    
    @GetMapping("/admin/bookings/payment/{status}")
    public String viewBookingsByPaymentStatus(@PathVariable String status, Model model) {
        List<BookingOrderEntity> allBookings = bookingOrderService.findAllBookingsForAdmin();
        
        List<BookingOrderEntity> filteredBookings;
        if ("ALL".equals(status)) {
            filteredBookings = allBookings;
        } else {
            filteredBookings = allBookings.stream()
                    .filter(booking -> status.equals(booking.getPaymentStatus()))
                    .toList();
        }
        
        Map<String, Long> stats = calculateBookingStats(allBookings);
        
        model.addAttribute("currentView", "payment");
        model.addAttribute("currentStatus", status);
        model.addAttribute("bookings", filteredBookings);
        model.addAttribute("stats", stats);
        model.addAttribute("allBookingsJson", filteredBookings);
        
        return "Admin/Management-Booking";
    }
    
    @GetMapping("/admin/bookings")
    public String viewAllBookings(
            @RequestParam(required = false) String searchTerm,
            Model model) {
        List<BookingOrderEntity> allBookings = bookingOrderService.findAllBookingsForAdmin();
        
        List<BookingOrderEntity> filteredBookings;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchLower = searchTerm.toLowerCase();
            filteredBookings = allBookings.stream()
                    .filter(booking -> {
                        String customerEmail = booking.getCustomer() != null ? 
                                booking.getCustomer().getEmail().toLowerCase() : "";
                        String bookingId = String.valueOf(booking.getBookingId());
                        
                        return customerEmail.contains(searchLower) || 
                               bookingId.contains(searchLower);
                    })
                    .toList();
        } else {
            filteredBookings = allBookings;
        }
        
        Map<String, Long> stats = calculateBookingStats(allBookings);
        
        model.addAttribute("currentView", "all");
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("bookings", filteredBookings);
        model.addAttribute("stats", stats);
        model.addAttribute("allBookingsJson", filteredBookings);
        
        return "Admin/Management-Booking";
    }

    @GetMapping("/admin/bookings/{bookingId}")
    public String viewBookingDetail(@PathVariable Integer bookingId, Model model) {
        var bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
        if (bookingOpt.isEmpty()) {
            return "redirect:/admin/bookings";
        }
        
        BookingOrderEntity booking = bookingOpt.get();
        model.addAttribute("booking", booking);
        
        if (booking.getRoom() != null) {
            List<ReviewEntity> reviews = reviewService.getReviewsByRoomId(booking.getRoom().getRoomId());
            model.addAttribute("reviews", reviews);
        }
        
        return "Admin/booking-detail";
    }

    // API endpoints moved to AdminBookingApiController

    private Map<String, Long> calculateBookingStats(List<BookingOrderEntity> bookings) {
        Map<String, Long> stats = new HashMap<>();
        
        // Tổng số booking
        long totalBookings = bookings.size();
        
        // Đếm số lượng theo trạng thái đặt phòng
        long confirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() != null && "CONFIRMED".equals(booking.getBookingStatus()))
                .count();
        
        long pendingBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() != null && "PENDING".equals(booking.getBookingStatus()))
                .count();
        
        long cancelledBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() != null && "CANCELLED".equals(booking.getBookingStatus()))
                .count();
        
        // Đếm số lượng theo trạng thái thanh toán
        long paidBookings = bookings.stream()
                .filter(booking -> "PAID".equals(booking.getPaymentStatus()))
                .count();
        
        long pendingPaymentBookings = bookings.stream()
                .filter(booking -> "PENDING".equals(booking.getPaymentStatus()))
                .count();
        
        // Lưu vào map
        stats.put("totalBookings", totalBookings);
        stats.put("confirmedBookings", confirmedBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("paidBookings", paidBookings);
        stats.put("pendingPaymentBookings", pendingPaymentBookings);
        
        return stats;
    }
}