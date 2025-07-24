package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminManagementVoucherController {
    
    @GetMapping("/admin/vouchers")
    public String viewVoucherManagementPage() {
        // Logic to retrieve and display vouchers can be added here
        return "admin/management-Voucher"; // Path to your Thymeleaf template for voucher management
    }
}
