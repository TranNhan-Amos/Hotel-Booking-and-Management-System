package sd19303no1.hotel_booking_and_management_system.Controller.PageController;

import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class IndexController {
     @GetMapping("/")
    public String home(Model model) {
        return "Page/index"; 
    }
}
