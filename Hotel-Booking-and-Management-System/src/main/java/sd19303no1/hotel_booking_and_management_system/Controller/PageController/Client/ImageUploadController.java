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
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ImageUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private CustomersService customersService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/img/customers/";

    @PostMapping("/upload-avatar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String error = validateAuthAndFile(authentication, file);
            if (error != null) {
                response.put("success", false);
                response.put("message", error);
                return ResponseEntity.ok(response);
            }
            String email = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            if (systemUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return ResponseEntity.ok(response);
            }
            String uploadPath = UPLOAD_DIR;
            createDirIfNotExists(uploadPath);
            String newFilename = generateUniqueFilename(file.getOriginalFilename());
            File dest = new File(uploadPath, newFilename);
            file.transferTo(dest);
            deleteOldAvatarFile(uploadPath, systemUser.getImg());
            systemUser.setImg(newFilename);
            systemUserService.save(systemUser);
            if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                CustomersEntity customer = customersService.findBySystemUser(systemUser);
                if (customer != null) {
                    customer.setAvatar(newFilename);
                    customersService.save(customer);
                }
            }
            response.put("success", true);
            response.put("message", "Cập nhật ảnh đại diện thành công!");
            response.put("avatarPath", "/img/customers/" + newFilename);
        } catch (IOException e) {
            logger.error("Lỗi IO khi upload avatar", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi lưu file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi upload avatar", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // Hàm kiểm tra xác thực và file upload
    private String validateAuthAndFile(Authentication authentication, MultipartFile file) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "Vui lòng đăng nhập";
        }
        if (file == null || file.isEmpty()) {
            return "Vui lòng chọn file ảnh";
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return "Chỉ chấp nhận file ảnh (JPG, PNG, GIF)";
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return "Kích thước file không được vượt quá 5MB";
        }
        return null;
    }

    // Hàm tạo thư mục nếu chưa có
    private void createDirIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Hàm tạo tên file duy nhất
    private String generateUniqueFilename(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + ext;
    }

    // Hàm xóa file avatar cũ nếu có
    private void deleteOldAvatarFile(String uploadPath, String oldAvatar) {
        if (oldAvatar != null && !oldAvatar.isEmpty()) {
            File oldFile = new File(uploadPath, oldAvatar.replace("/img/customers/", ""));
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
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
                String filePath = UPLOAD_DIR + currentAvatar;
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