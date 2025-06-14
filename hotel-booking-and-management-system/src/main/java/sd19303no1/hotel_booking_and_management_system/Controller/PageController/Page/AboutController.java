package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.qos.logback.core.model.Model;


@Controller
public class AboutController {
    @GetMapping("/About")
    public String About(Model model) {
        return ("Page/about");
    }
    
}
