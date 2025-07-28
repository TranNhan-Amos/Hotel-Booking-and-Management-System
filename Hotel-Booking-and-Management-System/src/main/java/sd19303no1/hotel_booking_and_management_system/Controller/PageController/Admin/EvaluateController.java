package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class EvaluateController {
    
     @GetMapping("/admin/Evaluate")
    public String viewEvaluatePage(Model model) {
        return "Admin/Evaluate";
}
}