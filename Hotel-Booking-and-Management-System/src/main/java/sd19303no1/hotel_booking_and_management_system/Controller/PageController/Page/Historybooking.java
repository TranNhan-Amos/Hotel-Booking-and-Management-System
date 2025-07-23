package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Historybooking {
    @Autowired
    private BookingOrderService bookingOrderService;

    @GetMapping("/historybooking")
    public String historybooking(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        List<sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity> bookings = bookingOrderService.getBookingsByCustomerEmailForCustomer(email);
        // Việt hóa trạng thái cho từng booking
        List<String> statusTexts = new ArrayList<>();
        for (var booking : bookings) {
            String status = booking.getStatus() != null ? booking.getStatus().getStatusName() : "";
            String statusText = switch (status) {
                case "PENDING" -> "Chờ xác nhận";
                case "CONFIRMED" -> "Đã xác nhận";
                case "COMPLETED" -> "Hoàn thành";
                case "CANCELLED" -> "Đã hủy";
                default -> status;
            };
            statusTexts.add(statusText);
        }
        // Đếm số lượng theo trạng thái
        long countConfirmed = bookings.stream().filter(b -> b.getStatus() != null && "CONFIRMED".equals(b.getStatus().getStatusName())).count();
        long countCompleted = bookings.stream().filter(b -> b.getStatus() != null && "COMPLETED".equals(b.getStatus().getStatusName())).count();
        long countPending = bookings.stream().filter(b -> b.getStatus() != null && "PENDING".equals(b.getStatus().getStatusName())).count();
        long countCancelled = bookings.stream().filter(b -> b.getStatus() != null && "CANCELLED".equals(b.getStatus().getStatusName())).count();
        java.math.BigDecimal totalSpent = bookings.stream().map(sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity::getTotalPrice).filter(java.util.Objects::nonNull).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("bookings", bookings);
        model.addAttribute("statusTexts", statusTexts);
        model.addAttribute("countConfirmed", countConfirmed);
        model.addAttribute("countCompleted", countCompleted);
        model.addAttribute("countPending", countPending);
        model.addAttribute("countCancelled", countCancelled);
        model.addAttribute("totalSpent", totalSpent);
        return "Page/Historybooking";
    }
}
