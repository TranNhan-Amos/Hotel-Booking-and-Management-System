package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
public class AdminManagementRoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String viewRoomManagementPage(Model model) {
        List<RoomEntity> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", rooms);
        return "admin/room-management";
    }

    @PostMapping("/save")
    public String saveRoom(@ModelAttribute RoomEntity room) {
        roomService.saveRoom(room);
        return "redirect:/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String editRoomForm(@PathVariable("id") Integer id, Model model) {
        RoomEntity room = roomService.getRoomById(id);
        model.addAttribute("room", room);
        return "admin/room-management";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Integer id) {
        roomService.deleteRoom(id);
        return "redirect:/admin/rooms";
    }
}