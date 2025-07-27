package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Partner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;

@Controller
public class RoomPartnerController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private SystemUserService systemUserService;

    // Đường dẫn upload ảnh phòng
    private static final String ROOM_IMAGE_UPLOAD_DIR = "F:/Githup/DUANTOTNGHIEP/Hotel-Booking-and-Management-System/Hotel-Booking-and-Management-System/src/main/resources/static/img/rooms";

    @GetMapping("/partner/rooms")
    public String viewPartnerRooms(Model model) {
        try {
            // Lấy thông tin người dùng đã đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    Long partnerId = partner.getId();
                    List<RoomEntity> roomPartners = roomService.findByPartnerId(partnerId);

                    // Lấy RoomEntity tương ứng cho mỗi RoomPartnerEntity để có thông tin ảnh
                    for (RoomEntity roomPartner : roomPartners) {
                        // Khởi tạo amenities để tránh lỗi lazy loading
                        if (roomPartner.getAmenities() == null) {
                            roomPartner.setAmenities(new ArrayList<>());
                        }
                    }

                    // Tính toán thống kê
                    long availableRoomsCount = roomPartners.stream()
                            .filter(room -> room.getStatus() == RoomEntity.RoomStatus.AVAILABLE)
                            .count();
                    
                    long unavailableRoomsCount = roomPartners.stream()
                            .filter(room -> room.getStatus() != RoomEntity.RoomStatus.AVAILABLE)
                            .count();

                    model.addAttribute("roomPartners", roomPartners);
                    model.addAttribute("partner", partner);
                    model.addAttribute("availableRoomsCount", availableRoomsCount);
                    model.addAttribute("unavailableRoomsCount", unavailableRoomsCount);
                    return "Partner/RoomPartner";
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "Partner/RoomPartner";
                }
            } else {
                model.addAttribute("error", "Bạn không có quyền truy cập.");
                return "Partner/RoomPartner";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "Partner/RoomPartner";
        }
    }

    @GetMapping("/partner/rooms/add")
    public String showAddRoomForm(Model model) {
        RoomEntity roomPartner = new RoomEntity();
        model.addAttribute("roomPartner", roomPartner);
        return "Partner/add-RoomPartner";
    }

    @PostMapping("/partner/rooms/add")
    public String addRoom(@ModelAttribute("roomPartner") @Valid RoomEntity roomPartner,
                         BindingResult result,
                         @RequestParam("images") MultipartFile[] images,
                         RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                return "Partner/add-RoomPartner";
            }

            // Lấy thông tin partner hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            PartnerEntity partner = partnerService.findBySystemUser(systemUser);

            if (partner == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đối tác.");
                return "redirect:/partner/rooms";
            }

            // Set partner cho phòng
            roomPartner.setPartner(partner);

            // Xử lý upload ảnh
            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        Path uploadPath = Paths.get(ROOM_IMAGE_UPLOAD_DIR);
                        
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }
                        
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(image.getInputStream(), filePath);
                        
                        imageUrls.add(fileName);
                    }
                }
            }
            roomPartner.setImageUrls(imageUrls);

            // Lưu phòng
            roomService.saveRoom(roomPartner);

            redirectAttributes.addFlashAttribute("success", "Thêm phòng thành công!");
            return "redirect:/partner/rooms";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/partner/rooms";
        }
    }

    @GetMapping("/partner/rooms/edit/{id}")
    public String showEditRoomForm(@PathVariable("id") Integer id, Model model) {
        try {
            RoomEntity roomPartner = roomService.getRoomById(id);
            if (roomPartner == null) {
                return "redirect:/partner/rooms?error=notfound";
            }
            model.addAttribute("roomPartner", roomPartner);
            return "Partner/edit-RoomPartner";
        } catch (Exception e) {
            return "redirect:/partner/rooms?error=error";
        }
    }

    @PostMapping("/partner/rooms/edit")
    public String updateRoom(@ModelAttribute RoomEntity roomPartner,
                           @RequestParam("images") MultipartFile[] images,
                           RedirectAttributes redirectAttributes) {
        try {
            RoomEntity existingRoom = roomService.getRoomById(roomPartner.getRoomId());

            if (existingRoom == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng.");
                return "redirect:/partner/rooms";
            }

            // Cập nhật thông tin phòng
            existingRoom.setNameNumber(roomPartner.getNameNumber());
            existingRoom.setType(roomPartner.getType());
            existingRoom.setPrice(roomPartner.getPrice());
            existingRoom.setStatus(roomPartner.getStatus());
            existingRoom.setDescription(roomPartner.getDescription());
            existingRoom.setCapacity(roomPartner.getCapacity());
            existingRoom.setService(roomPartner.getService());
            existingRoom.setView(roomPartner.getView());
            existingRoom.setBedType(roomPartner.getBedType());
            existingRoom.setBathroomType(roomPartner.getBathroomType());
            existingRoom.setTotalRooms(roomPartner.getTotalRooms());
            existingRoom.setIsSmoking(roomPartner.getIsSmoking());
            existingRoom.setIsPetFriendly(roomPartner.getIsPetFriendly());

            // Xử lý upload ảnh mới nếu có
            if (images != null && images.length > 0) {
                List<String> newImageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        Path uploadPath = Paths.get(ROOM_IMAGE_UPLOAD_DIR);
                        
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }
                        
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(image.getInputStream(), filePath);
                        
                        newImageUrls.add(fileName);
                    }
                }
                
                // Thêm ảnh mới vào danh sách hiện tại
                if (existingRoom.getImageUrls() == null) {
                    existingRoom.setImageUrls(new ArrayList<>());
                }
                existingRoom.getImageUrls().addAll(newImageUrls);
            }

            roomService.saveRoom(existingRoom);

            redirectAttributes.addFlashAttribute("success", "Cập nhật phòng thành công!");
            return "redirect:/partner/rooms";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/partner/rooms";
        }
    }

    @GetMapping("/partner/rooms/delete/{id}")
    public String deleteRoom(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa phòng: " + e.getMessage());
        }
        return "redirect:/partner/rooms";
    }
} 