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
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminManagementRoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private PartnerService partnerService;

    // Đường dẫn upload ảnh phòng ngoài project
    private static final String ROOM_IMAGE_UPLOAD_DIR = "F:/Githup/DUANTOTNGHIEP/Hotel-Booking-and-Management-System/Hotel-Booking-and-Management-System/src/main/resources/static/img/rooms";

    @GetMapping("/admin/rooms")
    public String viewRoomManagementPage(
            @RequestParam(value = "partnerId", required = false) Long partnerId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        List<PartnerEntity> partners = partnerService.findAll();
        model.addAttribute("partners", partners);
        
        List<RoomEntity> rooms;
        Map<String, Object> stats = new HashMap<>();
        
        if (partnerId != null) {
            // Lọc theo partner
            rooms = roomService.findByPartnerId(partnerId);
            model.addAttribute("selectedPartnerId", partnerId);
        } else {
            // Lấy tất cả phòng
            rooms = roomService.getAllRooms();
        }
        
        // Lọc theo trạng thái
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            rooms = rooms.stream()
                    .filter(room -> room.getStatus().toString().equals(status.toUpperCase()))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Lọc theo loại phòng
        if (type != null && !type.isEmpty() && !type.equals("all")) {
            rooms = rooms.stream()
                    .filter(room -> room.getType().toString().equals(type.toUpperCase()))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Tìm kiếm theo tên
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            rooms = rooms.stream()
                    .filter(room -> room.getNameNumber() != null && 
                            room.getNameNumber().toLowerCase().contains(searchLower))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Tính toán thống kê
        long totalRooms = rooms.size();
        long availableRooms = rooms.stream()
                .filter(room -> "AVAILABLE".equals(room.getStatus().toString()))
                .count();
        long occupiedRooms = rooms.stream()
                .filter(room -> "OCCUPIED".equals(room.getStatus().toString()))
                .count();
        long maintenanceRooms = rooms.stream()
                .filter(room -> "MAINTENANCE".equals(room.getStatus().toString()))
                .count();
        
        stats.put("totalRooms", totalRooms);
        stats.put("availableRooms", availableRooms);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("maintenanceRooms", maintenanceRooms);
        
        model.addAttribute("rooms", rooms);
        model.addAttribute("stats", stats);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("searchQuery", search);
        
        return "Admin/management-Room";
    }

    @PostMapping("/admin/rooms/save")
    public String saveRoom(
            @ModelAttribute RoomEntity room,
            @RequestParam("partner.id") Long partnerId,
            @RequestParam(value = "roomImages", required = false) MultipartFile[] roomImages,
            @RequestParam(value = "deletedImages", required = false) String deletedImages,
            RedirectAttributes redirectAttributes
    ) {
        try {
            PartnerEntity partner = partnerService.findById(partnerId);
            room.setPartner(partner);

            // Xử lý danh sách ảnh cũ (nếu có)
            List<String> finalImages = new ArrayList<>();
            List<String> existingImages = room.getImageUrls();
            // Xóa file vật lý nếu ảnh bị xóa
            if (existingImages != null) {
                if (deletedImages != null && !deletedImages.isEmpty()) {
                    String[] delIdx = deletedImages.split(",");
                    for (int i = 0; i < existingImages.size(); i++) {
                        boolean isDeleted = false;
                        for (String idx : delIdx) {
                            if (Integer.parseInt(idx) == i) {
                                isDeleted = true;
                                // Xóa file vật lý
                                String fileName = existingImages.get(i);
                                File file = new File(new File(ROOM_IMAGE_UPLOAD_DIR).getAbsolutePath(), fileName);
                                if (file.exists()) file.delete();
                                break;
                            }
                        }
                        if (!isDeleted) finalImages.add(existingImages.get(i));
                    }
                } else {
                    finalImages.addAll(existingImages);
                }
            }
            // Số lượng ảnh tối đa
            final int MAX_IMAGES = 5;
            int canAdd = MAX_IMAGES - finalImages.size();
            // Upload ảnh mới
            if (roomImages != null && roomImages.length > 0 && canAdd > 0) {
                File uploadDir = new File(ROOM_IMAGE_UPLOAD_DIR); // Không kiểm tra hay tạo mới thư mục
                int added = 0;
                for (MultipartFile file : roomImages) {
                    if (!file.isEmpty() && added < canAdd) {
                        String originalFileName = file.getOriginalFilename();
                        String cleanFileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                        File dest = new File(uploadDir, cleanFileName);
                        try {
                            file.transferTo(dest);
                            finalImages.add(cleanFileName); // Lưu vào DB chỉ tên file
                            added++;
                        } catch (IOException e) {
                            // Có thể log lỗi từng file nếu muốn
                        }
                    }
                }
            }
            room.setImageUrls(finalImages);
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("success", "Phòng đã được lưu thành công!");
            return "redirect:/admin/rooms?partnerId=" + partnerId;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu phòng: " + e.getMessage());
            return "redirect:/admin/rooms/add?partnerId=" + partnerId;
        }
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
        return "Admin/edit-Room";
    }

    @GetMapping("/admin/rooms/delete/{id}")
    public String deleteRoom(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            RoomEntity room = roomService.getRoomById(id);
            if (room != null) {
                // Xóa ảnh vật lý
                List<String> images = room.getImageUrls();
                if (images != null) {
                    for (String imageName : images) {
                        File imageFile = new File(ROOM_IMAGE_UPLOAD_DIR, imageName);
                        if (imageFile.exists()) {
                            imageFile.delete();
                        }
                    }
                }
                roomService.deleteRoom(id);
                redirectAttributes.addFlashAttribute("success", "Phòng đã được xóa thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phòng: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @GetMapping("/admin/rooms/add")
    public String addRoomForm(@RequestParam(value = "partnerId", required = false) Long partnerId, Model model) {
        List<PartnerEntity> partners = partnerService.findAll();
        model.addAttribute("partners", partners);
        model.addAttribute("selectedPartnerId", partnerId);
        return "Admin/add-Room";
    }

    @PostMapping("/admin/rooms/update-status")
    @ResponseBody
    public Map<String, Object> updateRoomStatus(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("status") String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            RoomEntity room = roomService.getRoomById(roomId);
            if (room != null) {
                room.setStatus(RoomEntity.RoomStatus.valueOf(status.toUpperCase()));
                roomService.saveRoom(room);
                response.put("success", true);
                response.put("message", "Cập nhật trạng thái thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy phòng!");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
        }
        return response;
    }
}