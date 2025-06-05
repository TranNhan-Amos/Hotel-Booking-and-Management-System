package datn.hotel_booking_and_management_system.Controller.AuthPageController;

import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

public class LogoutController {
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        return "redirect:/login";
    }
}
