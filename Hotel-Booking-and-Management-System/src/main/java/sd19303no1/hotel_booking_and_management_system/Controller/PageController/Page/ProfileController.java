package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sd19303no1.hotel_booking_and_management_system.Controller.PageController.BaseController;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.CustomUserDetailsService.CustomUserDetails;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/img/customers/";
    private static final String DEFAULT_AVATAR = "default-avatar.svg";
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
    public String showProfilePage(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.debug("Accessing profile page. Authentication: {}, IsAuthenticated: {}", 
            authentication, authentication != null && authentication.isAuthenticated());

        if (!isAuthenticated(authentication)) {
            logger.warn("Unauthenticated access attempt to profile page");
            return "redirect:/login";
        }

        String email = authentication.getName();
        logger.debug("Authenticated user email: {}", email);
        
        // Log all users in the database for debugging
        List<SystemUserEntity> allUsers = systemUserRepository.findAll();
        logger.debug("All SystemUsers in database: {}", allUsers.stream()
            .map(u -> u.getEmail() + " (ID: " + u.getId() + ", Username: " + u.getUsername() + ")")
            .collect(Collectors.joining(", ")));
            
        // Log all customers in the database for debugging
        List<CustomersEntity> allCustomers = customersRepository.findAll();
        logger.debug("All Customers in database: {}", allCustomers.stream()
            .map(c -> c.getEmail() + " (ID: " + c.getCustomerId() + ", Name: " + c.getName() + ")")
            .collect(Collectors.joining(", ")));

        // Tìm user bằng email - ưu tiên CustomersEntity trước
        SystemUserEntity systemUser = null;
        CustomersEntity customer = null;
        
        // Thử tìm trong CustomersEntity trước (ưu tiên customer data)
        customer = customersRepository.findByEmailIgnoreCase(email).orElse(null);
        
        if (customer != null) {
            logger.debug("Found Customer: {} (ID: {})", customer.getEmail(), customer.getCustomerId());
            
            // Lấy SystemUser liên kết với customer này
            systemUser = customer.getSystemUser();
            if (systemUser == null) {
                // Nếu customer không có systemUser, tìm bằng email
                systemUser = systemUserRepository.findByEmailIgnoreCase(customer.getEmail()).orElse(null);
            }
            
            if (systemUser == null) {
                // Tạo SystemUserEntity tạm thời từ customer
                systemUser = new SystemUserEntity();
                systemUser.setEmail(customer.getEmail());
                systemUser.setUsername(customer.getName());
                systemUser.setRole(SystemUserEntity.Role.CUSTOMER);
                systemUser.setId(customer.getCustomerId().longValue());
                logger.debug("Created temporary SystemUser from Customer data");
            }
        } else {
            // Nếu không tìm thấy customer, thử tìm trong SystemUserEntity
            logger.warn("Customer not found with email: {}. Searching in SystemUserEntity...", email);
            systemUser = systemUserRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    logger.warn("SystemUser not found with email: {}. Trying to find by username...", email);
                    return systemUserRepository.findByUsernameIgnoreCase(email).orElse(null);
                });
            
            if (systemUser != null) {
                logger.debug("Found SystemUser: {} (ID: {})", systemUser.getEmail(), systemUser.getId());
                
                // Nếu là CUSTOMER, tìm thông tin customer liên kết
                if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                    customer = customersRepository.findBySystemUser(systemUser).orElse(null);
                }
            } else {
                String errorMsg = "Không tìm thấy người dùng với email/username: " + email;
                logger.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
        }
            
        logger.debug("Final user: {} (ID: {})", systemUser.getEmail(), systemUser.getId());

        // Lấy dữ liệu từ database và log để debug
        String authEmail = email; // Email đăng nhập
        String systemUserEmail = systemUser.getEmail(); // Email trong SystemUser
        String customerEmail = customer != null ? customer.getEmail() : null; // Email trong Customer
        
        logger.debug("Authentication email: {}", authEmail);
        logger.debug("SystemUser email: {}", systemUserEmail);
        logger.debug("Customer email: {}", customerEmail);
        
        // Ưu tiên email từ Customer nếu có, vì đó là dữ liệu chính xác
        String currentEmail;
        if (customer != null && customerEmail != null) {
            currentEmail = customerEmail; // Sử dụng email từ Customer
            logger.info("✅ Using Customer email: {} (Customer ID: {})", currentEmail, customer.getCustomerId());
        } else {
            currentEmail = systemUserEmail; // Fallback to SystemUser email
            logger.info("⚠️ Using SystemUser email: {} (SystemUser ID: {})", currentEmail, systemUser.getId());
        }
        
        // Log final decision với thông tin chi tiết
        logger.info("🎯 Final email decision: {} | Auth: {} | Customer: {} | SystemUser: {}", 
                   currentEmail, authEmail, customerEmail, systemUserEmail);

        // Đồng bộ avatar lên session cho navbar
        String navAvatarPath = getAvatarPath(systemUser, customer);
        session.setAttribute("avatarPath", navAvatarPath);

        // Lấy lịch sử đặt phòng sử dụng email từ authentication
        java.util.List<sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity> bookings = bookingOrderService.getBookingsByCustomerEmailForCustomer(currentEmail);
        model.addAttribute("bookings", bookings);

        // Prepare model attributes - KHÔNG override currentUser từ BaseController
        model.addAttribute("user", systemUser);
        model.addAttribute("userType", systemUser.getRole().toString().toLowerCase());
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", true);

        // ✅ Đảm bảo currentUser sử dụng customer data với email chính xác
        if (customer != null) {
            model.addAttribute("currentUser", customer);
            logger.debug("🔄 Set currentUser to customer with email: {}", customer.getEmail());
        }

        // Set display attributes
        setupDisplayAttributes(model, systemUser, customer);

        return "Page/profile";
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String cccd,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticated(authentication)) {
            return createErrorResponse("Phiên đăng nhập đã hết hạn!", 401);
        }

        final String originalEmail = authentication.getName();
        String currentEmail = originalEmail;
        logger.info("Bắt đầu cập nhật thông tin cho email: {}", currentEmail);
        
        try {
            // Tìm người dùng hiện tại - kiểm tra cả SystemUserEntity và CustomersEntity
            SystemUserEntity systemUser = null;
            CustomersEntity customer = null;
            
            // Thử tìm trong SystemUserEntity trước
            systemUser = systemUserRepository.findByEmailIgnoreCase(originalEmail)
                .orElseGet(() -> systemUserRepository.findByUsernameIgnoreCase(originalEmail).orElse(null));
            
            if (systemUser != null) {
                logger.debug("Found SystemUser: {} (ID: {})", systemUser.getEmail(), systemUser.getId());
                
                // Nếu là CUSTOMER, tìm thông tin customer
                if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                    customer = customersRepository.findBySystemUser(systemUser).orElse(null);
                    if (customer == null) {
                        // Thử tìm customer bằng email
                        customer = customersRepository.findByEmailIgnoreCase(originalEmail).orElse(null);
                    }
                }
            } else {
                // Nếu không tìm thấy trong SystemUserEntity, thử tìm trong CustomersEntity
                logger.warn("SystemUser not found, searching in CustomersEntity...");
                customer = customersRepository.findByEmailIgnoreCase(originalEmail).orElse(null);
                
                if (customer != null) {
                    logger.debug("Found Customer: {} (ID: {})", customer.getEmail(), customer.getCustomerId());
                    // Tạo SystemUserEntity tạm thời từ customer
                    systemUser = customer.getSystemUser();
                    if (systemUser == null) {
                        // Nếu customer không có systemUser, tạo một cái tạm thời
                        systemUser = new SystemUserEntity();
                        systemUser.setEmail(customer.getEmail());
                        systemUser.setUsername(customer.getName());
                        systemUser.setRole(SystemUserEntity.Role.CUSTOMER);
                        systemUser.setId(customer.getCustomerId().longValue());
                    }
                } else {
                    throw new IllegalStateException("Không tìm thấy người dùng với email/username: " + originalEmail);
                }
            }

            // Sử dụng giá trị hiện tại nếu không có giá trị mới
            String effectiveName = name != null && !name.trim().isEmpty() ? name.trim() : 
                (customer != null && customer.getName() != null ? customer.getName() : systemUser.getUsername());
                
            String effectiveEmail = email != null && !email.trim().isEmpty() ? email.trim() : systemUser.getEmail();
            
            // ✅ Kiểm tra thay đổi email - sử dụng customer email làm baseline
            String currentDisplayEmail = customer != null && customer.getEmail() != null ? customer.getEmail() : systemUser.getEmail();
            boolean isEmailChanged = !effectiveEmail.equalsIgnoreCase(currentDisplayEmail);
            logger.info("Email hiện tại (display): {}, Email mới: {}, Đã thay đổi: {}", 
                currentDisplayEmail, effectiveEmail, isEmailChanged);

            // Validate dữ liệu đầu vào
            String validationError = validateProfileInputs(effectiveName, effectiveEmail, phone, address, cccd, 
                currentPassword, newPassword, confirmPassword, systemUser, customer);
                
            if (validationError != null) {
                logger.warn("Lỗi validate dữ liệu: {}", validationError);
                return createErrorResponse(validationError, 400);
            }

            // Kiểm tra email đã tồn tại chưa (nếu thay đổi email)
            if (isEmailChanged) {
                if (systemUserRepository.findByEmailIgnoreCase(effectiveEmail).isPresent()) {
                    logger.warn("Email mới đã được sử dụng bởi tài khoản khác: {}", effectiveEmail);
                    return createErrorResponse("Email đã được sử dụng bởi tài khoản khác!", 400);
                }
                logger.info("Xác nhận thay đổi email từ {} thành {}", originalEmail, effectiveEmail);
            }

            // Cập nhật thông tin người dùng
            logger.info("Đang cập nhật thông tin người dùng...");
            updateUserInformation(systemUser, customer, effectiveName, effectiveEmail, phone, address, cccd, newPassword);
            logger.info("Đã cập nhật thông tin người dùng thành công");

            // Cập nhật authentication context nếu thay đổi email
            if (isEmailChanged) {
                logger.info("Đang cập nhật authentication context...");
                updateAuthenticationContext(effectiveEmail, authentication);
                logger.info("Đã cập nhật authentication context thành công");
                
                // Cập nhật lại currentEmail với email mới
                currentEmail = effectiveEmail;
            }
            
            // Tạo phản hồi thành công
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            
            // Xóa cache session để force refresh user data
            clearUserCache(session);
            
            // Trả về dữ liệu cập nhật
            Map<String, String> userData = createUpdatedDataMap(systemUser, customer);
            response.put("data", userData);
            
            logger.info("Cập nhật thông tin thành công cho email: {} - Cache cleared", currentEmail);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật thông tin cho email: " + currentEmail, e);
            return createErrorResponse("Có lỗi xảy ra khi cập nhật thông tin: " + 
                (e.getMessage() != null ? e.getMessage() : "Vui lòng thử lại sau"), 500);
        }
    }

    @PostMapping("/profile/upload-avatar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!isAuthenticated(authentication)) {
            return createErrorResponse("Bạn cần đăng nhập để cập nhật ảnh đại diện!", 401);
        }

        String email = authentication.getName();
        
        // Tìm người dùng - kiểm tra cả SystemUserEntity và CustomersEntity
        SystemUserEntity systemUser = null;
        CustomersEntity customer = null;
        
        // Thử tìm trong SystemUserEntity trước
        systemUser = systemUserRepository.findByEmailIgnoreCase(email)
            .orElseGet(() -> systemUserRepository.findByUsernameIgnoreCase(email).orElse(null));
        
        if (systemUser != null) {
            if (systemUser.getRole() != SystemUserEntity.Role.CUSTOMER) {
                return createErrorResponse("Chỉ khách hàng có thể cập nhật ảnh đại diện!", 403);
            }
            customer = customersRepository.findBySystemUser(systemUser).orElse(null);
            if (customer == null) {
                customer = customersRepository.findByEmailIgnoreCase(email).orElse(null);
            }
        } else {
            // Nếu không tìm thấy trong SystemUserEntity, thử tìm trong CustomersEntity
            customer = customersRepository.findByEmailIgnoreCase(email).orElse(null);
            if (customer != null) {
                systemUser = customer.getSystemUser();
            }
        }
        
        if (customer == null) {
            return createErrorResponse("Không tìm thấy thông tin khách hàng!", 404);
        }

        // Validate file
        String fileValidationError = validateAvatarFile(file);
        if (fileValidationError != null) {
            return createErrorResponse(fileValidationError, 400);
        }

        try {
            String fileName = saveAvatarFile(file, customer.getAvatar());
            customer.setAvatar(fileName);
            customersRepository.save(customer);
            
            // Cập nhật session avatarPath
            String avatarPath = "/img/customers/" + fileName;
            
            // Clear cache để force refresh user data
            HttpSession session = request.getSession();
            clearUserCache(session);
            
            response.put("success", true);
            response.put("message", "Cập nhật ảnh đại diện thành công!");
            response.put("avatarPath", avatarPath);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Error uploading avatar for user: {}", email, e);
            return createErrorResponse("Lỗi khi upload ảnh: " + e.getMessage(), 500);
        }
    }

    @PostMapping("/delete-avatar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAvatar(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!isAuthenticated(authentication)) {
            return createErrorResponse("Bạn cần đăng nhập để xóa ảnh đại diện!", 401);
        }

        String email = authentication.getName();
        
        // Tìm người dùng - kiểm tra cả SystemUserEntity và CustomersEntity
        SystemUserEntity systemUser = null;
        CustomersEntity customer = null;
        
        // Thử tìm trong SystemUserEntity trước
        systemUser = systemUserRepository.findByEmailIgnoreCase(email)
            .orElseGet(() -> systemUserRepository.findByUsernameIgnoreCase(email).orElse(null));
        
        if (systemUser != null) {
            if (systemUser.getRole() != SystemUserEntity.Role.CUSTOMER) {
                return createErrorResponse("Chỉ khách hàng có thể xóa ảnh đại diện!", 403);
            }
            customer = customersRepository.findBySystemUser(systemUser).orElse(null);
            if (customer == null) {
                customer = customersRepository.findByEmailIgnoreCase(email).orElse(null);
            }
        } else {
            // Nếu không tìm thấy trong SystemUserEntity, thử tìm trong CustomersEntity
            customer = customersRepository.findByEmailIgnoreCase(email).orElse(null);
            if (customer != null) {
                systemUser = customer.getSystemUser();
            }
        }
        
        if (customer == null) {
            return createErrorResponse("Không tìm thấy thông tin khách hàng!", 404);
        }

        try {
            if (customer.getAvatar() != null && !customer.getAvatar().equals(DEFAULT_AVATAR)) {
                File oldFile = new File(UPLOAD_DIR, customer.getAvatar());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            customer.setAvatar(DEFAULT_AVATAR);
            customersRepository.save(customer);
            
            // Cập nhật session avatarPath về default
            String avatarPath = "/img/customers/" + DEFAULT_AVATAR;
            
            // Clear cache để force refresh user data
            HttpSession session = request.getSession();
            clearUserCache(session);
            
            response.put("success", true);
            response.put("message", "Xóa ảnh đại diện thành công!");
            response.put("avatarPath", avatarPath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting avatar for user: {}", email, e);
            return createErrorResponse("Lỗi khi xóa ảnh: " + e.getMessage(), 500);
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && 
               authentication.isAuthenticated() && 
               !authentication.getName().equals("anonymousUser");
    }

    private void setupDisplayAttributes(Model model, SystemUserEntity systemUser, CustomersEntity customer) {
        String displayName = customer != null ? customer.getName() : systemUser.getUsername();
        
        // ✅ Ưu tiên email từ Customer trước, sau đó mới dùng SystemUser email
        String displayEmail = customer != null && customer.getEmail() != null 
            ? customer.getEmail() 
            : systemUser.getEmail();
            
        String displayPhone = customer != null && customer.getPhone() != null ? customer.getPhone() : "Chưa cập nhật";
        String displayAddress = customer != null && customer.getAddress() != null ? customer.getAddress() : "Chưa cập nhật";
        String displayCccd = customer != null && customer.getCccd() != null ? customer.getCccd() : "Chưa cập nhật";
        String displayRole = systemUser.getRole().toString();
        String avatarPath = getAvatarPath(systemUser, customer);

        logger.debug("📧 Display email set to: {} (Customer: {}, SystemUser: {})", 
                    displayEmail, customer != null ? customer.getEmail() : "null", systemUser.getEmail());

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

    private String validateProfileInputs(String name, String email, String phone, String address, String cccd, 
        String currentPassword, String newPassword, String confirmPassword, SystemUserEntity systemUser, CustomersEntity customer) {
        
        if (name == null || name.trim().isEmpty()) {
            return "Tên không được để trống!";
        }
        
        if (email == null || email.trim().isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "Email không hợp lệ!";
        }
        
        if (phone != null && !phone.trim().isEmpty() && !phone.matches("^[0-9]{10,11}$")) {
            return "Số điện thoại không hợp lệ! (10-11 chữ số)";
        }
        
        if (cccd != null && !cccd.trim().isEmpty() && !cccd.matches("^[0-9]{12}$")) {
            return "Số CCCD không hợp lệ! (12 chữ số)";
        }
        
        // ✅ Check email change using customer email as baseline
        String currentDisplayEmail = customer != null && customer.getEmail() != null ? customer.getEmail() : systemUser.getEmail();
        boolean isEmailChanged = email != null && !email.trim().isEmpty() && 
                               !email.trim().equalsIgnoreCase(currentDisplayEmail);
        
        if (isEmailChanged && (currentPassword == null || currentPassword.trim().isEmpty())) {
            return "Vui lòng nhập mật khẩu hiện tại để thay đổi email!";
        }
        if (currentPassword != null && !currentPassword.trim().isEmpty()) {
            // ✅ Check password against customer first, then systemUser
            String storedPassword = null;
            if (customer != null && customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
                storedPassword = customer.getPassword();
            } else {
                storedPassword = systemUser.getPassword();
            }
            
            if (!passwordEncoder.matches(currentPassword, storedPassword)) {
                return "Mật khẩu hiện tại không đúng!";
            }
        }
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return "Vui lòng nhập mật khẩu hiện tại để đổi mật khẩu!";
            }
            
            if (!confirmPassword.equals(newPassword)) {
                return "Mật khẩu xác nhận không khớp!";
            }
            
            if (newPassword.length() < 6) {
                return "Mật khẩu mới phải có ít nhất 6 ký tự!";
            }
        }
        
        return null;
    }

    private void updateUserInformation(SystemUserEntity systemUser, CustomersEntity customer,
            String name, String email, String phone, String address, String cccd, String newPassword) {
        
        try {
            logger.info("Bắt đầu cập nhật thông tin cho người dùng ID: {}", systemUser.getId());
            
            // Cập nhật thông tin cơ bản của người dùng
            systemUser.setUsername(name);
            systemUser.setEmail(email);
            
            // Cập nhật mật khẩu nếu có
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                logger.info("Đang cập nhật mật khẩu mới cho người dùng");
                systemUser.setPassword(passwordEncoder.encode(newPassword));
            }
            
            // Lưu thông tin người dùng
            systemUser = systemUserRepository.save(systemUser);
            logger.info("Đã cập nhật thông tin hệ thống cho người dùng: {}", systemUser.getEmail());

            // Nếu là khách hàng, cập nhật thông tin khách hàng
            if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                boolean isNewCustomer = false;
                
                if (customer == null) {
                    logger.info("Tạo mới thông tin khách hàng cho người dùng ID: {}", systemUser.getId());
                    customer = new CustomersEntity();
                    customer.setSystemUser(systemUser);
                    isNewCustomer = true;
                } else {
                    logger.info("Cập nhật thông tin khách hàng hiện có");
                }
                
                // Cập nhật thông tin khách hàng
                customer.setName(name);
                customer.setEmail(email);
                customer.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
                customer.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);
                customer.setCccd(cccd != null && !cccd.trim().isEmpty() ? cccd.trim() : null);
                
                // Lưu thông tin khách hàng
                customer = customersRepository.save(customer);
                
                if (isNewCustomer) {
                    logger.info("Đã tạo mới thông tin khách hàng thành công");
                } else {
                    logger.info("Đã cập nhật thông tin khách hàng thành công");
                }
            }
            
            logger.info("Hoàn thành cập nhật thông tin cho người dùng ID: {}", systemUser.getId());
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật thông tin người dùng: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể cập nhật thông tin người dùng: " + e.getMessage(), e);
        }
    }

    private void updateAuthenticationContext(String newEmail, Authentication currentAuthentication) {
        try {
            // Lấy thông tin xác thực hiện tại
            Object principal = currentAuthentication.getPrincipal();
            Object credentials = currentAuthentication.getCredentials();
            
            // Kiểm tra và cast đúng loại CustomUserDetails
            if (!(principal instanceof CustomUserDetails)) {
                logger.error("Principal is not an instance of CustomUserDetails: {}", principal.getClass().getName());
                throw new RuntimeException("Invalid principal type");
            }
            
            CustomUserDetails currentUser = (CustomUserDetails) principal;
            
            // Tạo CustomUserDetails mới với thông tin đã cập nhật từ database
            CustomUserDetails newPrincipal;
            if (currentUser.isSystemUser()) {
                // Lấy SystemUserEntity mới từ database với email đã cập nhật
                SystemUserEntity updatedSystemUser = systemUserRepository.findByEmailIgnoreCase(newEmail)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy SystemUser với email: " + newEmail));
                newPrincipal = new CustomUserDetails(updatedSystemUser);
            } else {
                // Lấy CustomersEntity mới từ database với email đã cập nhật
                CustomersEntity updatedCustomer = customersRepository.findByEmailIgnoreCase(newEmail)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Customer với email: " + newEmail));
                newPrincipal = new CustomUserDetails(updatedCustomer);
            }
            
            // Tạo authentication mới
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                newPrincipal, 
                credentials, 
                currentAuthentication.getAuthorities()
            );
            
            // Giữ nguyên thông tin chi tiết xác thực
            ((UsernamePasswordAuthenticationToken) newAuthentication).setDetails(
                currentAuthentication.getDetails()
            );
            
            // Cập nhật SecurityContext
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            
            logger.info("Đã cập nhật authentication context với email mới: {}", newEmail);
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật authentication context:", e);
            throw new RuntimeException("Không thể cập nhật thông tin xác thực", e);
        }
    }

    private Map<String, String> createUpdatedDataMap(SystemUserEntity systemUser, CustomersEntity customer) {
        Map<String, String> updatedData = new HashMap<>();
        
        // ✅ Ưu tiên email từ Customer trước, sau đó mới dùng SystemUser email
        String displayEmail = customer != null && customer.getEmail() != null 
            ? customer.getEmail() 
            : systemUser.getEmail();
            
        if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER && customer != null) {
            updatedData.put("name", customer.getName());
            updatedData.put("email", displayEmail); // ✅ Sử dụng customer email ưu tiên
            updatedData.put("phone", customer.getPhone() != null ? customer.getPhone() : "Chưa cập nhật");
            updatedData.put("address", customer.getAddress() != null ? customer.getAddress() : "Chưa cập nhật");
            updatedData.put("cccd", customer.getCccd() != null ? customer.getCccd() : "Chưa cập nhật");
        } else {
            updatedData.put("name", systemUser.getUsername());
            updatedData.put("email", displayEmail); // ✅ Consistent email logic
            updatedData.put("phone", "Chưa cập nhật");
            updatedData.put("address", "Chưa cập nhật");
            updatedData.put("cccd", "Chưa cập nhật");
        }
        
        logger.debug("📋 Created updated data map with email: {}", displayEmail);
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
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IOException("Tên file không hợp lệ");
        }
        String cleanFileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        File dest = new File(uploadPath, cleanFileName);
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