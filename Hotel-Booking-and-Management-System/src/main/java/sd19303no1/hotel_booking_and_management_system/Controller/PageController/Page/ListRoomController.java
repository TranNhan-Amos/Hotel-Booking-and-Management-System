package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sd19303no1.hotel_booking_and_management_system.Controller.PageController.BaseController;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;

@Controller
public class ListRoomController extends BaseController {
    @Autowired
    private RoomService RoomService;

    @Autowired
    private RoomRepository RoomRepository;
    
     @GetMapping("/ListRoom")
    public String ListRoom(Model model ) {
        List<RoomEntity> AllRooms = RoomRepository.findAll();
        model.addAttribute("featuredRooms", AllRooms);
        return ("Page/ListRoom");
    }

}
