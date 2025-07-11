package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminProfileController {
    
    @GetMapping("/admin/profile")
    public String viewAdminProfilePage() {
        // Logic to retrieve and display admin profile information can be added here
        return "admin/Profile-Admin"; // Path to your Thymeleaf template for admin profile
    }
}