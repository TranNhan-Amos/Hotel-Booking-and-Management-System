package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sd19303no1.hotel_booking_and_management_system.Controller.PageController.BaseController;

@Controller
public class ListRoomController extends BaseController {
     @GetMapping("/ListRoom")
    public String ListRoom(Model model ) {
        return ("Page/ListRoom");
    }

}
