package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminManagementBookingController {
    
     @GetMapping("/admin/booking")
    public String viewBookingManagementPage(Model model) {
        return "Admin/management-Booking";
}
}