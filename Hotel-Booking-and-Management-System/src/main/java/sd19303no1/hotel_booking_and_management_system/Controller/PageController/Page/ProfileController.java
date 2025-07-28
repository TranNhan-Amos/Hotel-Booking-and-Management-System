package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private static final String UPLOAD_DIR = "F:\\Githup\\DUANTOTNGHIEP\\Hotel-Booking-and-Management-System\\Hotel-Booking-and-Management-System\\src\\main\\resources\\static\\img\\customers\\";
    private static final String DEFAULT_AVATAR = "default-avatar.png"; // TODO: Đảm bảo file này tồn tại trong static/img/customers/
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private SystemUserRepository systemUserRepository;
    
    @Autowired
    private CustomersRepository customersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BookingOrderService bookingOrderService;

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        logger.debug("Accessing profile page. Authentication: {}, IsAuthenticated: {}", 
            authentication, authentication != null && authentication.isAuthenticated());

        if (!isAuthenticated(authentication)) {
            logger.warn("Unauthenticated access attempt to profile page");
            return "redirect:/login";
        }

        String email = authentication.getName();
        SystemUserEntity systemUser = systemUserRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("User not found for email: " + email));

        CustomersEntity customer = null;
        if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
            customer = customersRepository.findBySystemUser(systemUser)
                .orElseGet(() -> customersRepository.findByEmail(email).orElse(null));
        }

        // Lấy lịch sử đặt phòng
        java.util.List<sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity> bookings = bookingOrderService.getBookingsByCustomerEmailForCustomer(email);
        model.addAttribute("bookings", bookings);

        // Prepare model attributes
        model.addAttribute("user", systemUser);
        model.addAttribute("userType", systemUser.getRole().toString().toLowerCase());
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", true);
        
        // Set display attributes
        setupDisplayAttributes(model, systemUser, customer);

        return "Page/profile";
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String cccd,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword) {
        
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticated(authentication)) {
            return createErrorResponse("Phiên đăng nhập đã hết hạn!", 401);
        }

        String email = authentication.getName();
        try {
            SystemUserEntity systemUser = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found for email: " + email));

            CustomersEntity customer = systemUser.getRole() == SystemUserEntity.Role.CUSTOMER
                ? customersRepository.findBySystemUser(systemUser).orElse(null)
                : null;

            // Validate inputs
            String validationError = validateProfileInputs(name, phone, cccd, currentPassword, newPassword, confirmPassword, systemUser);
            if (validationError != null) {
                return createErrorResponse(validationError, 400);
            }

            // Update user information
            updateUserInformation(systemUser, customer, name, phone, address, cccd, newPassword);
            
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            response.put("data", createUpdatedDataMap(systemUser, customer));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating profile for email: {}", email, e);
            return createErrorResponse("Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage(), 500);
        }
    }

    @PostMapping("/profile/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!isAuthenticated(authentication)) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để cập nhật ảnh đại diện!");
            return "redirect:/profile";
        }

        String email = authentication.getName();
        SystemUserEntity systemUser = systemUserRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("User not found for email: " + email));

        if (systemUser.getRole() != SystemUserEntity.Role.CUSTOMER) {
            redirectAttributes.addFlashAttribute("error", "Chỉ khách hàng có thể cập nhật ảnh đại diện!");
            return "redirect:/profile";
        }

        CustomersEntity customer = customersRepository.findBySystemUser(systemUser)
            .orElseThrow(() -> new IllegalStateException("Customer not found for user: " + email));

        // Validate file
        String fileValidationError = validateAvatarFile(file);
        if (fileValidationError != null) {
            redirectAttributes.addFlashAttribute("error", fileValidationError);
            return "redirect:/profile";
        }

        try {
            String fileName = saveAvatarFile(file, customer.getAvatar());
            customer.setAvatar(fileName);
            customersRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");
        } catch (IOException e) {
            logger.error("Error uploading avatar for user: {}", email, e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && 
               authentication.isAuthenticated() && 
               !authentication.getName().equals("anonymousUser");
    }

    private void setupDisplayAttributes(Model model, SystemUserEntity systemUser, CustomersEntity customer) {
        String displayName = customer != null ? customer.getName() : systemUser.getUsername();
        String displayEmail = systemUser.getEmail();
        String displayPhone = customer != null && customer.getPhone() != null ? customer.getPhone() : "Chưa cập nhật";
        String displayAddress = customer != null && customer.getAddress() != null ? customer.getAddress() : "Chưa cập nhật";
        String displayCccd = customer != null && customer.getCccd() != null ? customer.getCccd() : "Chưa cập nhật";
        String displayRole = systemUser.getRole().toString();
        String avatarPath = getAvatarPath(systemUser, customer);

        model.addAttribute("displayName", displayName);
        model.addAttribute("displayEmail", displayEmail);
        model.addAttribute("displayPhone", displayPhone);
        model.addAttribute("displayAddress", displayAddress);
        model.addAttribute("displayCccd", displayCccd);
        model.addAttribute("displayRole", displayRole);
        model.addAttribute("avatarPath", avatarPath);
    }

    private String getAvatarPath(SystemUserEntity systemUser, CustomersEntity customer) {
        String avatarName = DEFAULT_AVATAR;
        if (customer != null && customer.getAvatar() != null && !customer.getAvatar().isEmpty()) {
            avatarName = customer.getAvatar();
        } else if (systemUser.getImg() != null && !systemUser.getImg().isEmpty()) {
            avatarName = systemUser.getImg();
        }
        return "/img/customers/" + avatarName;
    }

    private String validateProfileInputs(String name, String phone, String cccd, 
            String currentPassword, String newPassword, String confirmPassword,
            SystemUserEntity systemUser) {
        
        if (name == null || name.trim().isEmpty()) {
            return "Tên không được để trống!";
        }
        
        if (phone != null && !phone.trim().isEmpty() && !phone.matches("^[0-9]{10,11}$")) {
            return "Số điện thoại không hợp lệ! (10-11 chữ số)";
        }
        
        if (cccd != null && !cccd.trim().isEmpty() && !cccd.matches("^[0-9]{12}$")) {
            return "Số CCCD không hợp lệ! (12 chữ số)";
        }
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return "Vui lòng nhập mật khẩu hiện tại!";
            }
            
            if (!confirmPassword.equals(newPassword)) {
                return "Mật khẩu xác nhận không khớp!";
            }
            
            if (newPassword.length() < 6) {
                return "Mật khẩu mới phải có ít nhất 6 ký tự!";
            }
            
            if (!passwordEncoder.matches(currentPassword, systemUser.getPassword())) {
                return "Mật khẩu hiện tại không đúng!";
            }
        }
        
        return null;
    }

    private void updateUserInformation(SystemUserEntity systemUser, CustomersEntity customer,
            String name, String phone, String address, String cccd, String newPassword) {
        
        systemUser.setUsername(name.trim());
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            systemUser.setPassword(passwordEncoder.encode(newPassword));
        }
        systemUserRepository.save(systemUser);

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
    }

    private Map<String, String> createUpdatedDataMap(SystemUserEntity systemUser, CustomersEntity customer) {
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
        return updatedData;
    }

    private String validateAvatarFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "Vui lòng chọn một file ảnh!";
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return "Kích thước file quá lớn! Tối đa 5MB";
        }

        String contentType = file.getContentType();
        boolean isValidType = false;
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equals(contentType)) {
                isValidType = true;
                break;
            }
        }
        
        if (!isValidType) {
            return "Định dạng file không hợp lệ! Chỉ chấp nhận JPEG, PNG, GIF";
        }

        return null;
    }

    private String saveAvatarFile(MultipartFile file, String oldAvatar) throws IOException {
        File uploadPath = new File(UPLOAD_DIR);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        if (oldAvatar != null && !oldAvatar.equals(DEFAULT_AVATAR)) {
            File oldFile = new File(uploadPath, oldAvatar);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        String originalFileName = file.getOriginalFilename();
        String cleanFileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        File dest = new File(uploadPath, cleanFileName);
        System.out.println("Upload path: " + dest.getAbsolutePath()); // Log đường dẫn thực tế
        file.transferTo(dest);
        return cleanFileName;
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}