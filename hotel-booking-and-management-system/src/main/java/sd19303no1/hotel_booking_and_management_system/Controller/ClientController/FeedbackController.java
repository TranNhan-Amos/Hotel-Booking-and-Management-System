package sd19303no1.hotel_booking_and_management_system.Controller.ClientController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.qos.logback.core.model.Model;


@Controller
public class FeedbackController {

    @GetMapping("/Feedback")
    public String Feedback(Model model ) {
        return ("Client/Feedback");
    }
    
}
