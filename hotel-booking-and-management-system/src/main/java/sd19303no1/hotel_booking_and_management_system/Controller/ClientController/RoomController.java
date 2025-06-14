package sd19303no1.hotel_booking_and_management_system.Controller.ClientController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoomController {
    @GetMapping("/rooms")
    public String danhsachPage(Model model) {
        return "danhsachdanhsach"; // trả về rooms.html
    }

    @GetMapping("/room-detail")
    public String DetailPage(Model model) {
        return "Details"; // trả về room-detail.html
    }
}