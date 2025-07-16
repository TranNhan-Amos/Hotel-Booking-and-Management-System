package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private SystemUserRepository systemUserRepository;
    
    @Autowired
    private CustomersRepository customersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "F:\\Githup\\Myweb\\Hotel-Booking-and-Management-System\\src\\main\\resources\\static\\img";

    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session) {
        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        System.out.println("=== PROFILE DEBUG ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Is authenticated: " + (authentication != null && authentication.isAuthenticated()));
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
        System.out.println("Name: " + (authentication != null ? authentication.getName() : "null"));
        System.out.println("Authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getName().equals("anonymousUser")) {
            System.out.println("Redirecting to login - not authenticated");
            return "redirect:/login";
        }

        String email = authentication.getName();
        System.out.println("Looking for user with email: " + email);
        
        // Get user data from database
        SystemUserEntity systemUser = systemUserRepository.findByEmail(email).orElse(null);
        
        if (systemUser == null) {
            System.out.println("SystemUser not found for email: " + email);
            return "redirect:/login";
        }
        
        System.out.println("Found SystemUser: ID=" + systemUser.getId() + ", Email=" + systemUser.getEmail() + ", Role=" + systemUser.getRole());
        
        // Get customer details if user is CUSTOMER
        CustomersEntity customer = null;
        if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
            // Try to find customer by systemUser first
            customer = customersRepository.findBySystemUser(systemUser).orElse(null);
            
            // If not found, try to find by email as fallback
            if (customer == null) {
                customer = customersRepository.findByEmail(email).orElse(null);
            }
        }
        
        // Add user data to model
        model.addAttribute("user", systemUser);
        model.addAttribute("userType", systemUser.getRole().toString().toLowerCase());
        
        if (customer != null) {
            model.addAttribute("customer", customer);
        }
        
        // Add combined user info for display
        String displayName = customer != null ? customer.getName() : systemUser.getUsername();
        String displayEmail = systemUser.getEmail();
        String displayPhone = customer != null ? customer.getPhone() : "Chưa cập nhật";
        String displayAddress = customer != null ? customer.getAddress() : "Chưa cập nhật";
        String displayCccd = customer != null ? customer.getCccd() : "Chưa cập nhật";
        String displayRole = systemUser.getRole().toString();

        // Get avatar path
        String avatarPath = null;
        if (customer != null && customer.getAvatar() != null && !customer.getAvatar().isEmpty()) {
            avatarPath = customer.getAvatar();
        } else if (systemUser.getImg() != null && !systemUser.getImg().isEmpty()) {
            avatarPath = systemUser.getImg();
        }

        model.addAttribute("displayName", displayName);
        model.addAttribute("displayEmail", displayEmail);
        model.addAttribute("displayPhone", displayPhone);
        model.addAttribute("displayAddress", displayAddress);
        model.addAttribute("displayCccd", displayCccd);
        model.addAttribute("displayRole", displayRole);
        model.addAttribute("avatarPath", avatarPath);
        model.addAttribute("isLoggedIn", true);
        
        return "Page/profile";
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String cccd,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getName().equals("anonymousUser")) {
            response.put("success", false);
            response.put("message", "Phiên đăng nhập đã hết hạn!");
            return ResponseEntity.ok(response);
        }

        String email = authentication.getName();
        
        try {
            // Get user data from database
            SystemUserEntity systemUser = systemUserRepository.findByEmail(email).orElse(null);
            
            if (systemUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng!");
                return ResponseEntity.ok(response);
            }
            
            // Get customer details if user is CUSTOMER
            CustomersEntity customer = null;
            if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                customer = customersRepository.findBySystemUser(systemUser).orElse(null);
            }
            
            // Validation
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tên không được để trống!");
                return ResponseEntity.ok(response);
            }
            
            if (phone != null && !phone.trim().isEmpty() && !phone.matches("^[0-9]{10,11}$")) {
                response.put("success", false);
                response.put("message", "Số điện thoại không hợp lệ! (10-11 chữ số)");
                return ResponseEntity.ok(response);
            }
            
            if (cccd != null && !cccd.trim().isEmpty() && !cccd.matches("^[0-9]{12}$")) {
                response.put("success", false);
                response.put("message", "Số CCCD không hợp lệ! (12 chữ số)");
                return ResponseEntity.ok(response);
            }
            
            // Password validation if changing password
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    response.put("success", false);
                    response.put("message", "Vui lòng nhập mật khẩu hiện tại!");
                    return ResponseEntity.ok(response);
                }
                
                if (!confirmPassword.equals(newPassword)) {
                    response.put("success", false);
                    response.put("message", "Mật khẩu xác nhận không khớp!");
                    return ResponseEntity.ok(response);
                }
                
                if (newPassword.length() < 6) {
                    response.put("success", false);
                    response.put("message", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                    return ResponseEntity.ok(response);
                }
                
                // Verify current password
                if (systemUser != null && !passwordEncoder.matches(currentPassword, systemUser.getPassword())) {
                    response.put("success", false);
                    response.put("message", "Mật khẩu hiện tại không đúng!");
                    return ResponseEntity.ok(response);
                }
                
                if (customer != null && !passwordEncoder.matches(currentPassword, customer.getPassword())) {
                    response.put("success", false);
                    response.put("message", "Mật khẩu hiện tại không đúng!");
                    return ResponseEntity.ok(response);
                }
            }
            
            // Update SystemUser if exists
            if (systemUser != null) {
                systemUser.setUsername(name.trim());
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    systemUser.setPassword(passwordEncoder.encode(newPassword));
                }
                systemUserRepository.save(systemUser);
            }
            
            // Update or create Customer (only for CUSTOMER role)
            if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                if (customer == null) {
                    customer = new CustomersEntity();
                    customer.setSystemUser(systemUser);
                }
                
                customer.setName(name.trim());
                customer.setPhone(phone != null ? phone.trim() : null);
                customer.setAddress(address != null ? address.trim() : null);
                customer.setCccd(cccd != null ? cccd.trim() : null);
                
                customersRepository.save(customer);
            }
            
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            
            // Return updated data
            Map<String, String> updatedData = new HashMap<>();
            if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER && customer != null) {
                updatedData.put("name", customer.getName());
                updatedData.put("phone", customer.getPhone() != null ? customer.getPhone() : "Chưa cập nhật");
                updatedData.put("address", customer.getAddress() != null ? customer.getAddress() : "Chưa cập nhật");
                updatedData.put("cccd", customer.getCccd() != null ? customer.getCccd() : "Chưa cập nhật");
            } else {
                updatedData.put("name", systemUser.getUsername());
                updatedData.put("phone", "Chưa cập nhật");
                updatedData.put("address", "Chưa cập nhật");
                updatedData.put("cccd", "Chưa cập nhật");
            }
            response.put("data", updatedData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/profile/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file, RedirectAttributes redirectAttributes) {
        // Lấy user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để cập nhật ảnh đại diện!");
            return "redirect:/profile";
        }

        String email = authentication.getName();
        SystemUserEntity systemUser = systemUserRepository.findByEmail(email).orElse(null);
        if (systemUser == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/profile";
        }

        CustomersEntity customer = null;
        if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
            customer = customersRepository.findBySystemUser(systemUser).orElse(null);
        }

        if (customer == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin khách hàng!");
            return "redirect:/profile";
        }

        // Thư mục lưu ảnh
        String uploadDir = "src/main/resources/static/img/customers/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        // Xóa ảnh cũ nếu không phải ảnh mặc định
        String oldAvatar = customer.getAvatar();
        if (oldAvatar != null && !oldAvatar.equals("default-avatar.png")) {
            File oldFile = new File(uploadDir + oldAvatar);
            if (oldFile.exists()) oldFile.delete();
        }

        // Lưu file mới
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);
        try {
            file.transferTo(dest);
            customer.setAvatar(fileName); // Lưu tên file vào DB
            customersRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
