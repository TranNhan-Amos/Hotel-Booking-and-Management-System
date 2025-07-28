package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import java.util.List;

@Controller
public class AdminManagementBookingController {

    @Autowired
    private BookingOrderService bookingOrderService; // Service for booking logic

    @GetMapping("/admin/bookings")
    public String viewBookingManagementPage(Model model) {
        // Use the method intended for admin to view all bookings
        List<BookingOrderEntity> bookings = bookingOrderService.findAllBookingsForAdmin();
        model.addAttribute("bookings", bookings);
        // The 'allBookings' JS variable in HTML will use this 'bookings' model attribute
        model.addAttribute("allBookingsJson", bookings); // Pass for JS if needed, or use Thymeleaf's JS inlining
        return "Admin/Management-Booking"; // Path to your Thymeleaf template
    }
}