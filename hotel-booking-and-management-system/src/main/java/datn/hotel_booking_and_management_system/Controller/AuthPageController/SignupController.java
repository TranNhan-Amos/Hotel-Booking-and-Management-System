package datn.hotel_booking_and_management_system.Controller.AuthPageController;

import org.springframework.web.bind.annotation.GetMapping;

public class SignupController {
        @GetMapping("/signup")
    public String showSignupPage() {
        return "";
    }
}
