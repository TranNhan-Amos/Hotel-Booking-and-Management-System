package datn.hotel_booking_and_management_system.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/demo")
public class demo {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
