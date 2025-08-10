package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.core.io.ResourceLoader;

@Controller
public class RoomImageUploadController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private ResourceLoader resourceLoader;

    private String getRoomImageUploadDir() {
        try {
            return resourceLoader.getResource("classpath:static/img/rooms").getFile().getAbsolutePath();
        } catch (IOException e) {
            // Fallback to system property if resource loader fails
            return System.getProperty("user.dir") + "/src/main/resources/static/img/rooms";
        }
    }

    @PostMapping("/upload-room-images")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadRoomImages(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("files") MultipartFile[] files) {
        
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("=== UPLOAD ROOM IMAGES ===");
        System.out.println("Room ID: " + roomId);
        System.out.println("Files count: " + (files != null ? files.length : 0));
        
        try {
            // Kiểm tra quyền truy cập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra phòng tồn tại
            RoomEntity room = roomService.getRoomById(roomId);
            if (room == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy phòng");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra quyền (Admin hoặc Partner sở hữu phòng)
            String email = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            
            boolean hasPermission = false;
            if (systemUser.getRole() == SystemUserEntity.Role.ADMIN) {
                hasPermission = true;
            } else if (systemUser.getRole() == SystemUserEntity.Role.PARTNER) {
                // Kiểm tra xem partner có sở hữu phòng này không
                if (room.getPartner() != null && room.getPartner().getId() != null) {
                    // Tìm partner từ systemUser
                    PartnerEntity partner = partnerService.findBySystemUser(systemUser);
                    if (partner != null && partner.getId().equals(room.getPartner().getId())) {
                        hasPermission = true;
                    }
                }
            }

            if (!hasPermission) {
                response.put("success", false);
                response.put("message", "Bạn không có quyền upload ảnh cho phòng này");
                return ResponseEntity.ok(response);
            }

            // Tạo thư mục upload nếu chưa tồn tại
            File uploadDir = new File(getRoomImageUploadDir());
            System.out.println("Upload directory: " + uploadDir.getAbsolutePath());
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                System.out.println("Created upload directory");
            }

            List<String> uploadedImages = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            // Xử lý từng file
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                // Validate file type
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    errors.add("File " + file.getOriginalFilename() + " không phải là ảnh");
                    continue;
                }

                // Validate file size (max 10MB)
                if (file.getSize() > 10 * 1024 * 1024) {
                    errors.add("File " + file.getOriginalFilename() + " quá lớn (tối đa 10MB)");
                    continue;
                }

                try {
                    // Generate unique filename
                    String originalFilename = file.getOriginalFilename();
                    String fileExtension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String newFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");

                    System.out.println("Processing file: " + originalFilename);
                    System.out.println("New filename: " + newFilename);

                    // Save file
                    Path filePath = Paths.get(getRoomImageUploadDir(), newFilename);
                    Files.copy(file.getInputStream(), filePath);

                    System.out.println("File saved to: " + filePath.toAbsolutePath());

                    // Add to uploaded images list
                    uploadedImages.add(newFilename);

                } catch (IOException e) {
                    System.err.println("Error saving file " + file.getOriginalFilename() + ": " + e.getMessage());
                    errors.add("Lỗi khi lưu file " + file.getOriginalFilename() + ": " + e.getMessage());
                }
            }

            // Cập nhật database nếu có ảnh upload thành công
            if (!uploadedImages.isEmpty()) {
                List<String> currentImages = room.getImageUrls();
                if (currentImages == null) {
                    currentImages = new ArrayList<>();
                }
                currentImages.addAll(uploadedImages);
                room.setImageUrls(currentImages);
                roomService.saveRoom(room);
            }

            // Trả về kết quả
            System.out.println("Upload completed. Success: " + uploadedImages.size() + ", Errors: " + errors.size());
            System.out.println("Uploaded images: " + uploadedImages);
            
            response.put("success", true);
            response.put("message", "Upload thành công " + uploadedImages.size() + " ảnh");
            response.put("uploadedImages", uploadedImages);
            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-room-image")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRoomImage(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("imageUrl") String imageUrl) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra quyền truy cập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra phòng tồn tại
            RoomEntity room = roomService.getRoomById(roomId);
            if (room == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy phòng");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra quyền
            String email = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            
            boolean hasPermission = false;
            if (systemUser.getRole() == SystemUserEntity.Role.ADMIN) {
                hasPermission = true;
            } else if (systemUser.getRole() == SystemUserEntity.Role.PARTNER) {
                if (room.getPartner() != null && room.getPartner().getSystemUser().getId().equals(systemUser.getId())) {
                    hasPermission = true;
                }
            }

            if (!hasPermission) {
                response.put("success", false);
                response.put("message", "Bạn không có quyền xóa ảnh của phòng này");
                return ResponseEntity.ok(response);
            }

            // Xóa file từ filesystem
            File imageFile = new File(getRoomImageUploadDir(), imageUrl);
            if (imageFile.exists()) {
                imageFile.delete();
            }

            // Cập nhật database
            List<String> currentImages = room.getImageUrls();
            if (currentImages != null) {
                currentImages.remove(imageUrl);
                room.setImageUrls(currentImages);
                roomService.saveRoom(room);
            }

            response.put("success", true);
            response.put("message", "Xóa ảnh thành công");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/room-images/{roomId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomImages(@PathVariable("roomId") Integer roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            RoomEntity room = roomService.getRoomById(roomId);
            if (room == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy phòng");
                return ResponseEntity.ok(response);
            }

            List<String> images = room.getImageUrls();
            if (images == null) {
                images = new ArrayList<>();
            }

            response.put("success", true);
            response.put("images", images);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
} 