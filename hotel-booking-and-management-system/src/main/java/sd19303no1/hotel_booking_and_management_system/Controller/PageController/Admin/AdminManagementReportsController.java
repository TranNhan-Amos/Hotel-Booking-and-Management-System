package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminManagementReportsController {
    
    @GetMapping("/admin/reports")
    public String showReportsPage() {
        return "admin/Reports-Management";
    }
}
