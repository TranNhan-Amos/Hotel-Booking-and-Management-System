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

    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session) {
        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        
        // Try to get user from session first
        SystemUserEntity sessionUser = (SystemUserEntity) session.getAttribute("loggedInUser");
        
        SystemUserEntity systemUser = null;
        CustomersEntity customer = null;
        
        if (sessionUser != null) {
            systemUser = sessionUser;
            // Get corresponding customer data
            customer = customersRepository.findByEmail(email).orElse(null);
        } else {
            // Fallback: get from database
            systemUser = systemUserRepository.findByEmail(email).orElse(null);
            customer = customersRepository.findByEmail(email).orElse(null);
        }
        
        if (systemUser == null && customer == null) {
            return "redirect:/login";
        }
        
        // Add user data to model
        if (systemUser != null) {
            model.addAttribute("user", systemUser);
            model.addAttribute("userType", "system");
        }
        
        if (customer != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("userType", customer != null ? "customer" : "system");
        }
        
        // Add combined user info for display
        String displayName = customer != null ? customer.getName() : 
                           (systemUser != null ? systemUser.getUsername() : "Unknown User");
        String displayEmail = customer != null ? customer.getEmail() : 
                            (systemUser != null ? systemUser.getEmail() : "");
        String displayPhone = customer != null ? customer.getPhone() : "Chưa cập nhật";
        String displayAddress = customer != null ? customer.getAddress() : "Chưa cập nhật";
        String displayCccd = customer != null ? customer.getCccd() : "Chưa cập nhật";
        String displayRole = systemUser != null ? systemUser.getRole().toString() : "CUSTOMER";

        model.addAttribute("displayName", displayName);
        model.addAttribute("displayEmail", displayEmail);
        model.addAttribute("displayPhone", displayPhone);
        model.addAttribute("displayAddress", displayAddress);
        model.addAttribute("displayCccd", displayCccd);
        model.addAttribute("displayRole", displayRole);
        model.addAttribute("isLoggedIn", true);
        
        return "Page/Profile";
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
            // Get user data
            SystemUserEntity sessionUser = (SystemUserEntity) session.getAttribute("loggedInUser");
            SystemUserEntity systemUser = null;
            CustomersEntity customer = null;
            
            if (sessionUser != null) {
                systemUser = sessionUser;
                customer = customersRepository.findByEmail(email).orElse(null);
            } else {
                systemUser = systemUserRepository.findByEmail(email).orElse(null);
                customer = customersRepository.findByEmail(email).orElse(null);
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
                
                // Update session
                session.setAttribute("loggedInUser", systemUser);
            }
            
            // Update or create Customer
            if (customer == null) {
                customer = new CustomersEntity();
                customer.setEmail(email);
            }
            
            customer.setName(name.trim());
            customer.setPhone(phone != null ? phone.trim() : null);
            customer.setAddress(address != null ? address.trim() : null);
            customer.setCccd(cccd != null ? cccd.trim() : null);
            
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                customer.setPassword(passwordEncoder.encode(newPassword));
            } else if (customer.getPassword() == null && systemUser != null) {
                // Set password from systemUser if customer doesn't have one
                customer.setPassword(systemUser.getPassword());
            }
            
            customersRepository.save(customer);
            
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            
            // Return updated data
            Map<String, String> updatedData = new HashMap<>();
            updatedData.put("name", customer.getName());
            updatedData.put("phone", customer.getPhone() != null ? customer.getPhone() : "Chưa cập nhật");
            updatedData.put("address", customer.getAddress() != null ? customer.getAddress() : "Chưa cập nhật");
            updatedData.put("cccd", customer.getCccd() != null ? customer.getCccd() : "Chưa cập nhật");
            response.put("data", updatedData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
