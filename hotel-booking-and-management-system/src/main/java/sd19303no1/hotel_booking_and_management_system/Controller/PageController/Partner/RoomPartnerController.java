package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Partner;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomPartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomPartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;

@Controller
public class RoomPartnerController {

    @Autowired
    private RoomPartnerService roomPartnerService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private SystemUserService systemUserService;

    @GetMapping("/room/partner")
    public String roomPartner(Model model) {
        try {
            // Lấy thông tin người dùng đã đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // Lấy email từ Authentication

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {

                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    Long partnerId = partner.getId();

                    List<RoomPartnerEntity> roomPartners = roomPartnerService.findByPartnerId(partnerId);

                    RoomPartnerEntity roomPartner = new RoomPartnerEntity();
                    roomPartner.setPartnerId(partnerId);

                    model.addAttribute("roomPartners", roomPartners);
                    model.addAttribute("roomPartner", roomPartner);

                    return "Partner/RoomPartner";
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "Partner/RoomPartner";
                }
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách phòng: " + e.getMessage());
            return "Partner/RoomPartner";
        }
    }

    @PostMapping("/room/partner/add")
    public String addRoomPartner(
            @ModelAttribute("roomPartner") @Valid RoomPartnerEntity roomPartner,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {

                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    if (result.hasErrors()) {
                        return "Partner/AddRoomPartner";
                    }

                    roomPartner.setPartnerId(partner.getId());
                    boolean exists = roomPartnerService.existsByRoomNumberAndPartnerId(
                            roomPartner.getRoomNumber(),
                            partner.getId());

                    if (exists) {
                        return "redirect:/room/partner?exists=true";
                    }
                    roomPartnerService.save(roomPartner);
                    return "redirect:/room/partner?success=true";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "redirect:/room/partner";
                }
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm phòng: " + e.getMessage());
            return "redirect:/room/partner";
        }
    }

    @PostMapping("/room/partner/update")
    public String updateRoomPartner(
            @ModelAttribute RoomPartnerEntity roomPartner,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    if (result.hasErrors()) {
                        redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ.");
                        return "redirect:/room/partner";
                    }

                    RoomPartnerEntity existingRoom = roomPartnerService.findById(roomPartner.getRoomId());
                    if (!existingRoom.getRoomNumber().equals(roomPartner.getRoomNumber())) {
                        boolean exists = roomPartnerService.existsByRoomNumberAndPartnerId(
                                roomPartner.getRoomNumber(), partner.getId());
                        if (exists) {
                            return "redirect:/room/partner?exists=true&roomNumber=" + roomPartner.getRoomNumber();
                        }
                    }

                    roomPartner.setPartnerId(partner.getId());
                    roomPartnerService.updateRoomPartner(roomPartner);
                    return "redirect:/room/partner";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "redirect:/room/partner";
                }
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật phòng: " + e.getMessage());
            return "redirect:/room/partner";
        }
    }

}
