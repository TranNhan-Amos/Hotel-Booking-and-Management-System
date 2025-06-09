package sd19303no1.hotel_booking_and_management_system.Controller.ClientController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String IndexPage(Model model) {
        return "index"; 
    }
}
