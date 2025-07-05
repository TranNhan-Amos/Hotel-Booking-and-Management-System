package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller

public class AdminManagementRoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private PartnerService partnerService;

    @GetMapping("/admin/rooms")
    public String viewRoomManagementPage(@RequestParam(value = "partnerId", required = false) Long partnerId, Model model) {
        List<PartnerEntity> partners = partnerService.findAll();
        model.addAttribute("partners", partners);
        if (partnerId != null) {
            List<RoomEntity> rooms = roomService.findByPartnerId(partnerId);
            model.addAttribute("rooms", rooms);
            model.addAttribute("selectedPartnerId", partnerId);
        }
        return "Admin/management-Room";
    }

    @PostMapping("/admin/rooms/save")
    public String saveRoom(@ModelAttribute RoomEntity room, @RequestParam("partner.id") Long partnerId) {
        PartnerEntity partner = partnerService.findById(partnerId);
        room.setPartner(partner);
        roomService.saveRoom(room);
        return "redirect:/admin/rooms?partnerId=" + partnerId;
    }

    @GetMapping("/admin/rooms/edit/{id}")
    public String editRoomForm(@PathVariable("id") Integer id, Model model) {
        RoomEntity room = roomService.getRoomById(id);
        if (room == null) {
            return "redirect:/admin/rooms?error=notfound";
        }
        model.addAttribute("room", room);
        List<PartnerEntity> partners = partnerService.findAll();
        model.addAttribute("partners", partners);
        model.addAttribute("selectedPartnerId", room.getPartner() != null ? room.getPartner().getId() : null);
        model.addAttribute("editMode", true);
        return "Admin/management-Room";
    }

    @GetMapping("/admin/rooms/delete/{id}")
    public String deleteRoom(@PathVariable("id") Integer id) {
        roomService.deleteRoom(id);
        return "redirect:/admin/rooms";
    }
}