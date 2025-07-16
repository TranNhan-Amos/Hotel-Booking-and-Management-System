package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class ImageUploadController {

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private CustomersService customersService;

    private static final String UPLOAD_DIR = "src/main/resources/static/img";

    @PostMapping("/upload-avatar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return ResponseEntity.ok(response);
            }

            String email = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            
            if (systemUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return ResponseEntity.ok(response);
            }

            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn file ảnh");
                return ResponseEntity.ok(response);
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Chỉ chấp nhận file ảnh (JPG, PNG, GIF)");
                return ResponseEntity.ok(response);
            }

            // Check file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "Kích thước file không được vượt quá 5MB");
                return ResponseEntity.ok(response);
            }

            // Determine upload directory based on user role
            String uploadSubDir;
            switch (systemUser.getRole()) {
                case CUSTOMER:
                    uploadSubDir = "customers";
                    break;
                case PARTNER:
                    uploadSubDir = "partners";
                    break;
                case STAFF:
                    uploadSubDir = "staff";
                    break;
                case ADMIN:
                    uploadSubDir = "users";
                    break;
                default:
                    uploadSubDir = "users";
            }

            // Create upload directory if not exists
            String uploadPath = UPLOAD_DIR + "/" + uploadSubDir;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = Paths.get(uploadPath, newFilename);
            Files.copy(file.getInputStream(), filePath);

            // Update database
            String avatarPath = "/img/" + uploadSubDir + "/" + newFilename;
            
            // Update SystemUser img field
            systemUser.setImg(avatarPath);
            systemUserService.save(systemUser);

            // Update Customer avatar field if user is CUSTOMER
            if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                CustomersEntity customer = customersService.findBySystemUser(systemUser);
                if (customer != null) {
                    customer.setAvatar(avatarPath);
                    customersService.save(customer);
                }
            }

            response.put("success", true);
            response.put("message", "Cập nhật ảnh đại diện thành công!");
            response.put("avatarPath", avatarPath);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi lưu file: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-avatar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAvatar() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return ResponseEntity.ok(response);
            }

            String email = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            
            if (systemUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return ResponseEntity.ok(response);
            }

            // Delete old avatar file if exists
            String currentAvatar = systemUser.getImg();
            if (currentAvatar != null && !currentAvatar.isEmpty()) {
                String filePath = "src/main/resources/static" + currentAvatar;
                File avatarFile = new File(filePath);
                if (avatarFile.exists()) {
                    avatarFile.delete();
                }
            }

            // Clear avatar in database
            systemUser.setImg(null);
            systemUserService.save(systemUser);

            // Clear Customer avatar if user is CUSTOMER
            if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                CustomersEntity customer = customersService.findBySystemUser(systemUser);
                if (customer != null) {
                    customer.setAvatar(null);
                    customersService.save(customer);
                }
            }

            response.put("success", true);
            response.put("message", "Xóa ảnh đại diện thành công!");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
} 