package sd19303no1.hotel_booking_and_management_system.Controller.ClientController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login"; // trả về login.html
    }
}