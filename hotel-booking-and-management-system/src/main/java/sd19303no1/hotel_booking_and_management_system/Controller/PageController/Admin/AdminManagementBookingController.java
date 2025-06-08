package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;


@Controller
@RequestMapping("/admin/booking")
public class AdminManagementBookingController {
    
    @Autowired
    private BookingOrderService bookingOrderService;
    
    @GetMapping
    public String viewBookingManagementPage(Model model) {
        List<BookingOrderEntity> bookings = bookingOrderService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "admin/booking-management";
    }
 
   
}
