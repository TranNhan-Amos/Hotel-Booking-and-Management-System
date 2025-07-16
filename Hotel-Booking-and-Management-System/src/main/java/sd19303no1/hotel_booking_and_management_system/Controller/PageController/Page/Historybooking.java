package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class Historybooking {
    
     @GetMapping("/historybooking")
    public String historybooking(Model model) {
        return ("Page/Historybooking");
    }
}
