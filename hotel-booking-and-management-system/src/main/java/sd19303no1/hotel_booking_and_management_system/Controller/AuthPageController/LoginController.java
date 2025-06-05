package sd19303no1.hotel_booking_and_management_system.Controller.AuthPageController;


import org.springframework.web.bind.annotation.GetMapping;


public class LoginController {

    
        @GetMapping("/login")
    public String showLoginPage() {
        return "";
    }


}
