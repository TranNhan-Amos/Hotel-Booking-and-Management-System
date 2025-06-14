package sd19303no1.hotel_booking_and_management_system.Controller.ClientController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PaymentController {

    @GetMapping("/Payment")
    public String Payment(Model model ) {
        return ("Page/Payment");
    }
    
}