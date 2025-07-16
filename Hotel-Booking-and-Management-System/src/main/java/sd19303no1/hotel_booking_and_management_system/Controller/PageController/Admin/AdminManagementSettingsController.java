package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminManagementSettingsController {
    

    @GetMapping("/admin/settings")
    public String viewSettingsPage() {
        // Logic to retrieve and display settings can be added here
        return "admin/Settings-Management"; // Path to your Thymeleaf template for settings management
    }
}
