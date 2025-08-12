package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Controller
public class AdminManagementRoomController {

    private static final Logger logger = LoggerFactory.getLogger(AdminManagementRoomController.class);

    @Autowired
    private RoomService roomService;

    @Autowired
    private PartnerService partnerService;
    
    @Autowired
    private RoomRepository roomRepository;

    private static final String ROOM_IMAGE_UPLOAD_DIR = "F:/Githup/DUANTOTNGHIEP/Hotel-Booking-and-Management-System/Hotel-Booking-and-Management-System/src/main/resources/static/img/rooms";

    @GetMapping("/admin/rooms")
    public String viewRoomManagementPage(
            @RequestParam(value = "partnerId", required = false) Long partnerId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        // Lấy danh sách tất cả đối tác
        List<PartnerEntity> partners = partnerService.findAll();
        model.addAttribute("partners", partners);
        
        Page<RoomEntity> rooms;
        Map<String, Object> stats = new HashMap<>();
        
        // Sử dụng Pageable để hỗ trợ phân trang
        Pageable pageable = PageRequest.of(page, size);
        
        try {
            RoomEntity.RoomStatus roomStatus = (status != null && !status.isEmpty() && !status.equals("all")) ? 
                    RoomEntity.RoomStatus.valueOf(status.toUpperCase()) : null;
            RoomEntity.RoomType roomType = (type != null && !type.isEmpty() && !type.equals("all")) ? 
                    RoomEntity.RoomType.valueOf(type.toUpperCase()) : null;
            rooms = roomService.findRoomsWithFilters(partnerId, roomStatus, roomType, search, pageable);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid status or type, falling back to no filters: {}", e.getMessage());
            rooms = roomService.findRoomsWithFilters(partnerId, null, null, search, pageable);
        }
        
        // Tính toán thống kê tổng thể (không bị ảnh hưởng bởi filter)
        List<RoomEntity> allRooms = roomRepository.findAll();
        long totalRooms = allRooms.size();
        long availableRooms = allRooms.stream()
                .filter(room -> room.getStatus() == RoomEntity.RoomStatus.AVAILABLE)
                .count();
        long occupiedRooms = allRooms.stream()
                .filter(room -> room.getStatus() == RoomEntity.RoomStatus.OCCUPIED)
                .count();
        long maintenanceRooms = allRooms.stream()
                .filter(room -> room.getStatus() == RoomEntity.RoomStatus.MAINTENANCE)
                .count();
        long outOfServiceRooms = allRooms.stream()
                .filter(room -> room.getStatus() == RoomEntity.RoomStatus.OUT_OF_SERVICE)
                .count();
        
        stats.put("totalRooms", totalRooms);
        stats.put("availableRooms", availableRooms);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("maintenanceRooms", maintenanceRooms);
        stats.put("outOfServiceRooms", outOfServiceRooms);
        
        model.addAttribute("rooms", rooms);
        model.addAttribute("stats", stats);
        model.addAttribute("selectedPartnerId", partnerId);
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
            @RequestParam(value = "amenities", required = false) String[] amenities,
            RedirectAttributes redirectAttributes) {
        try {
            logger.info("Starting to save room with partnerId: {}", partnerId);
            logger.info("Room entity: {}", room);
            logger.info("Amenities received: {}", amenities != null ? Arrays.toString(amenities) : "null");
            
            // Validate required fields
            if (room.getNameNumber() == null || room.getNameNumber().trim().isEmpty()) {
                logger.warn("Name number is empty");
                redirectAttributes.addFlashAttribute("error", "Tên phòng không được để trống!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            
            if (room.getType() == null) {
                logger.warn("Room type is null");
                redirectAttributes.addFlashAttribute("error", "Loại phòng không được để trống!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            
            if (room.getPrice() == null || room.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Price is invalid: {}", room.getPrice());
                redirectAttributes.addFlashAttribute("error", "Giá phòng phải lớn hơn 0!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            
            if (room.getCapacity() == null || room.getCapacity() < 1) {
                logger.warn("Capacity is invalid: {}", room.getCapacity());
                redirectAttributes.addFlashAttribute("error", "Sức chứa phải từ 1 người trở lên!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            
            if (room.getTotalRooms() == null || room.getTotalRooms() < 1) {
                logger.warn("Total rooms is invalid: {}", room.getTotalRooms());
                redirectAttributes.addFlashAttribute("error", "Tổng số phòng phải từ 1 trở lên!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            
            // Validate capacity không vượt quá 10
            if (room.getCapacity() > 10) {
                logger.warn("Capacity exceeds limit: {}", room.getCapacity());
                redirectAttributes.addFlashAttribute("error", "Sức chứa không được vượt quá 10 người!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            
            // Validate price không vượt quá 99,999,999.99
            if (room.getPrice().compareTo(new BigDecimal("99999999.99")) > 0) {
                logger.warn("Price exceeds limit: {}", room.getPrice());
                redirectAttributes.addFlashAttribute("error", "Giá phòng không được vượt quá 99,999,999.99!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            
            PartnerEntity partner = partnerService.findById(partnerId);
            if (partner == null) {
                logger.warn("Partner not found with id: {}", partnerId);
                redirectAttributes.addFlashAttribute("error", "Đối tác không tồn tại!");
                return "redirect:/admin/rooms/add?partnerId=" + partnerId;
            }
            room.setPartner(partner);
            logger.info("Partner set: {}", partner.getCompanyName());

            // Xử lý amenities từ checkbox
            if (amenities != null && amenities.length > 0) {
                List<String> amenitiesList = new ArrayList<>();
                for (String amenity : amenities) {
                    if (amenity != null && !amenity.trim().isEmpty()) {
                        amenitiesList.add(amenity.trim());
                    }
                }
                room.setAmenities(amenitiesList);
                logger.info("Processed amenities: {}", amenitiesList);
            } else {
                room.setAmenities(new ArrayList<>());
                logger.info("No amenities selected");
            }

            // Đảm bảo status được set
            if (room.getStatus() == null) {
                room.setStatus(RoomEntity.RoomStatus.AVAILABLE);
                logger.info("Set default status: AVAILABLE");
            }

            // Đảm bảo các giá trị boolean được set
            if (room.getIsSmoking() == null) {
                room.setIsSmoking(false);
                logger.info("Set default isSmoking: false");
            }
            
            if (room.getIsPetFriendly() == null) {
                room.setIsPetFriendly(false);
                logger.info("Set default isPetFriendly: false");
            }
            
            // Đảm bảo các list được khởi tạo
            if (room.getAmenities() == null) {
                room.setAmenities(new ArrayList<>());
                logger.info("Initialize amenities list");
            }
            
            if (room.getImageUrls() == null) {
                room.setImageUrls(new ArrayList<>());
                logger.info("Initialize imageUrls list");
            }

            // Xử lý danh sách ảnh
            List<String> finalImages = new ArrayList<>();
            
            // Lấy ảnh hiện tại từ database nếu đang edit
            List<String> existingImages = new ArrayList<>();
            if (room.getRoomId() != null) {
                // Đang edit - lấy ảnh từ database
                Optional<RoomEntity> existingRoom = roomService.findById(room.getRoomId());
                if (existingRoom.isPresent()) {
                    existingImages = existingRoom.get().getImageUrls() != null ? 
                            existingRoom.get().getImageUrls() : new ArrayList<>();
                }
            } else {
                // Đang thêm mới - lấy từ form
                existingImages = room.getImageUrls() != null ? room.getImageUrls() : new ArrayList<>();
            }
            
            // Xóa ảnh cũ nếu được yêu cầu
            if (deletedImages != null && !deletedImages.isEmpty()) {
                String[] delIdx = deletedImages.split(",");
                for (int i = 0; i < existingImages.size(); i++) {
                    boolean isDeleted = false;
                    for (String idx : delIdx) {
                        if (Integer.parseInt(idx) == i) {
                            isDeleted = true;
                            String fileName = existingImages.get(i);
                            File file = new File(ROOM_IMAGE_UPLOAD_DIR, fileName);
                            if (file.exists()) {
                                try {
                                    if (!file.delete()) {
                                        logger.warn("Could not delete file: {}", fileName);
                                    }
                                } catch (Exception e) {
                                    logger.error("Error deleting file {}: {}", fileName, e.getMessage());
                                }
                            }
                            break;
                        }
                    }
                    if (!isDeleted) {
                        finalImages.add(existingImages.get(i));
                    }
                }
            } else {
                finalImages.addAll(existingImages);
            }

            // Upload ảnh mới
            final int MAX_IMAGES = 5;
            int canAdd = MAX_IMAGES - finalImages.size();
            logger.info("Can add {} more images, current images: {}", canAdd, finalImages.size());
            
            if (roomImages != null && roomImages.length > 0 && canAdd > 0) {
                File uploadDir = new File(ROOM_IMAGE_UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                    logger.info("Created upload directory: {}", uploadDir.getAbsolutePath());
                }
                int added = 0;
                for (MultipartFile file : roomImages) {
                    if (!file.isEmpty() && added < canAdd) {
                        String originalFileName = file.getOriginalFilename();
                        String extension = originalFileName != null && originalFileName.contains(".") 
                                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                                : "";
                        String cleanFileName = UUID.randomUUID().toString() + extension;
                        File dest = new File(uploadDir, cleanFileName);
                        try {
                            file.transferTo(dest);
                            finalImages.add(cleanFileName);
                            added++;
                            logger.info("Uploaded image: {} -> {}", originalFileName, cleanFileName);
                        } catch (IOException e) {
                            logger.error("Error uploading image {}: {}", originalFileName, e.getMessage());
                        }
                    }
                }
                logger.info("Total images uploaded: {}", added);
            }
            
            room.setImageUrls(finalImages);
            logger.info("Final images list: {}", finalImages);
            
            // Cập nhật thời gian
            if (room.getRoomId() == null) {
                room.setCreatedAt(LocalDateTime.now());
                logger.info("Set created time for new room");
            }
            room.setUpdatedAt(LocalDateTime.now());
            logger.info("Set updated time");
            
            logger.info("About to save room with final data: {}", room);
            logger.info("Room details - Name: {}, Type: {}, Price: {}, Capacity: {}, TotalRooms: {}", 
                       room.getNameNumber(), room.getType(), room.getPrice(), room.getCapacity(), room.getTotalRooms());
            
            roomService.save(room);
            logger.info("Room saved successfully with ID: {}", room.getRoomId());
            
            redirectAttributes.addFlashAttribute("success", "Phòng đã được lưu thành công!");
            return "redirect:/admin/rooms?partnerId=" + partnerId;
        } catch (Exception e) {
            logger.error("Error saving room: {}", e.getMessage(), e);
            logger.error("Stack trace: ", e);
            String errorMessage = "Lỗi khi lưu phòng: " + e.getMessage();
            
            // Xử lý lỗi floor field cụ thể
            if (e.getMessage() != null && e.getMessage().contains("floor")) {
                errorMessage = "Lỗi database: Column 'floor' không tồn tại hoặc có vấn đề. Vui lòng kiểm tra cấu hình database.";
                logger.error("Floor field error detected");
            }
            
            // Xử lý các lỗi khác
            if (e.getMessage() != null && e.getMessage().contains("DataIntegrityViolationException")) {
                errorMessage = "Lỗi dữ liệu: Có thể do trùng lặp hoặc dữ liệu không hợp lệ.";
                logger.error("Data integrity violation detected");
            }
            
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/admin/rooms/add?partnerId=" + partnerId;
        }
    }

    @GetMapping("/admin/rooms/edit/{id}")
    public String editRoomForm(@PathVariable("id") Integer id, Model model) {
        Optional<RoomEntity> roomOptional = roomService.findById(id);
        if (roomOptional.isEmpty()) {
            return "redirect:/admin/rooms?error=notfound";
        }
        
        RoomEntity room = roomOptional.get();
        model.addAttribute("room", room);
        
        List<PartnerEntity> partners = partnerService.findAll();
        model.addAttribute("partners", partners);
        model.addAttribute("selectedPartnerId", room.getPartner() != null ? room.getPartner().getId() : null);
        model.addAttribute("editMode", true);
        
        // Truyền các enum values cho form
        model.addAttribute("roomTypes", RoomEntity.RoomType.values());
        model.addAttribute("roomStatuses", RoomEntity.RoomStatus.values());
        model.addAttribute("bedTypes", RoomEntity.BedType.values());
        model.addAttribute("bathroomTypes", RoomEntity.BathroomType.values());
        model.addAttribute("viewTypes", RoomEntity.ViewType.values());
        model.addAttribute("amenities", RoomEntity.Amenity.values());
        
        return "Admin/edit-Room";
    }

    @GetMapping("/admin/rooms/delete/{id}")
    public String deleteRoom(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<RoomEntity> roomOptional = roomService.findById(id);
            if (roomOptional.isPresent()) {
                RoomEntity room = roomOptional.get();
                Long partnerId = room.getPartner() != null ? room.getPartner().getId() : null;
                List<String> images = room.getImageUrls();
                if (images != null) {
                    for (String imageName : images) {
                        File imageFile = new File(ROOM_IMAGE_UPLOAD_DIR, imageName);
                        if (imageFile.exists()) {
                            imageFile.delete();
                        }
                    }
                }
                roomService.delete(id);
                redirectAttributes.addFlashAttribute("success", "Phòng đã được xóa thành công!");
                return partnerId != null ? "redirect:/admin/rooms?partnerId=" + partnerId : "redirect:/admin/rooms";
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng!");
            }
        } catch (Exception e) {
            logger.error("Error deleting room: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phòng: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @GetMapping("/admin/rooms/add")
    public String addRoomForm(@RequestParam(value = "partnerId", required = false) Long partnerId, Model model) {
        List<PartnerEntity> partners = partnerService.findAll();
        model.addAttribute("partners", partners);
        model.addAttribute("selectedPartnerId", partnerId);
        
        // Tạo room mới với giá trị mặc định
        RoomEntity newRoom = new RoomEntity();
        newRoom.setStatus(RoomEntity.RoomStatus.AVAILABLE);
        newRoom.setIsSmoking(false);
        newRoom.setIsPetFriendly(false);
        newRoom.setCapacity(2);
        newRoom.setTotalRooms(1);
        
        model.addAttribute("room", newRoom);
        
        // Truyền các enum values cho form
        model.addAttribute("roomTypes", RoomEntity.RoomType.values());
        model.addAttribute("roomStatuses", RoomEntity.RoomStatus.values());
        model.addAttribute("bedTypes", RoomEntity.BedType.values());
        model.addAttribute("bathroomTypes", RoomEntity.BathroomType.values());
        model.addAttribute("viewTypes", RoomEntity.ViewType.values());
        model.addAttribute("amenities", RoomEntity.Amenity.values());
        
        return "Admin/add-Room";
    }

    @GetMapping("/admin/rooms/fix-schema")
    @ResponseBody
    public Map<String, Object> fixDatabaseSchema() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Thử tạo một room test để xem có lỗi gì
            RoomEntity testRoom = new RoomEntity();
            testRoom.setNameNumber("Test Room for Schema Check");
            testRoom.setType(RoomEntity.RoomType.STANDARD);
            testRoom.setPrice(new java.math.BigDecimal("100.00"));
            testRoom.setCapacity(2);
            testRoom.setTotalRooms(1);
            testRoom.setStatus(RoomEntity.RoomStatus.AVAILABLE);
            
            // Thử lưu để xem có lỗi gì
            roomService.save(testRoom);
            
            result.put("status", "SUCCESS");
            result.put("message", "Database schema hoạt động bình thường");
            result.put("testRoomId", testRoom.getRoomId());
            
            // Xóa room test
            if (testRoom.getRoomId() != null) {
                roomService.delete(testRoom.getRoomId());
                result.put("cleanup", "Test room đã được xóa");
            }
            
            return result;
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            result.put("suggestion", "Vui lòng chạy script check_and_fix_database.sql để kiểm tra database schema");
            
            // Log chi tiết lỗi
            logger.error("Database schema error: {}", e.getMessage(), e);
            
            return result;
        }
    }

    @GetMapping("/admin/rooms/debug-schema")
    @ResponseBody
    public Map<String, Object> debugDatabaseSchema() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Kiểm tra entity fields
            result.put("entityFields", "RoomEntity không có field 'floor'");
            result.put("entityStatus", "OK");
            
            // Thử tạo một room entity để test
            RoomEntity testRoom = new RoomEntity();
            testRoom.setNameNumber("Test Room");
            testRoom.setType(RoomEntity.RoomType.STANDARD);
            testRoom.setPrice(new java.math.BigDecimal("100.00"));
            testRoom.setCapacity(2);
            testRoom.setTotalRooms(1);
            
            result.put("testRoomCreation", "OK");
            result.put("testRoom", testRoom.toString());
            
            return result;
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("stackTrace", e.getStackTrace());
            return result;
        }
    }

    @PostMapping("/admin/rooms/update-status")
    @ResponseBody
    public Map<String, Object> updateRoomStatus(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("status") String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<RoomEntity> roomOptional = roomService.findById(roomId);
            if (roomOptional.isPresent()) {
                RoomEntity room = roomOptional.get();
                RoomEntity.RoomStatus roomStatus = RoomEntity.RoomStatus.valueOf(status.toUpperCase());
                room.setStatus(roomStatus);
                roomService.save(room);
                response.put("success", true);
                response.put("message", "Cập nhật trạng thái thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy phòng!");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status: {}", status, e);
            response.put("success", false);
            response.put("message", "Trạng thái không hợp lệ!");
        } catch (Exception e) {
            logger.error("Error updating room status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
        }
        return response;
    }
}