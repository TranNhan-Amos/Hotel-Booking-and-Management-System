package sd19303no1.hotel_booking_and_management_system.Controller.PageController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpSession;

public abstract class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
    private static final String SESSION_USER_KEY = "currentUserData";
    private static final String SESSION_AVATAR_KEY = "avatarPath";

    @Autowired
    protected CustomersService customersService;

    @Autowired
    protected SystemUserService systemUserService;

    /**
     * Thêm thông tin người dùng hiện tại vào model với session caching
     * Chỉ thực hiện database query khi cần thiết
     */
    @ModelAttribute
    public void addCurrentUserToModel(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            String authEmail = authentication.getName();
            
            // Kiểm tra session cache trước để tránh redundant queries
            CustomersEntity cachedUser = (CustomersEntity) session.getAttribute(SESSION_USER_KEY);
            String cachedAvatar = (String) session.getAttribute(SESSION_AVATAR_KEY);
            String cachedEmail = (String) session.getAttribute("cachedUserEmail");
            
            if (cachedUser != null && cachedAvatar != null && authEmail.equals(cachedEmail)) {
                // Sử dụng cached data - không cần database query
                model.addAttribute("currentUser", cachedUser);
                model.addAttribute("avatarPath", cachedAvatar);
                // ✅ Set displayEmail từ cached customer data
                model.addAttribute("displayEmail", cachedUser.getEmail());
                logger.debug("✅ Using cached data for user: {} with displayEmail: {}", authEmail, cachedUser.getEmail());
                return;
            }
            
            logger.info("🔄 Cache miss for user: {}, fetching from database", authEmail);
            
            // Thử tìm Customer trước (ưu tiên Customer data)
            CustomersEntity customer = customersService.findByEmail(authEmail);
            if (customer != null) {
                // Sử dụng Customer data từ database (chính xác nhất)
                model.addAttribute("currentUser", customer);
                
                // ✅ Set displayEmail cho navbar consistency
                model.addAttribute("displayEmail", customer.getEmail());
                
                // Avatar từ customer
                final String avatarPath = customer.getAvatar() != null && !customer.getAvatar().trim().isEmpty() 
                    ? "/img/customers/" + customer.getAvatar() 
                    : "/img/customers/default-avatar.png";
                    
                model.addAttribute("avatarPath", avatarPath);
                
                // Cache user data in session
                session.setAttribute(SESSION_USER_KEY, customer);
                session.setAttribute(SESSION_AVATAR_KEY, avatarPath);
                session.setAttribute("cachedUserEmail", authEmail);
                logger.debug("✅ Cached user data for: {} with displayEmail: {}", authEmail, customer.getEmail());
            } else {
                // Nếu không tìm thấy Customer, thử tìm SystemUser
                SystemUserEntity systemUser = systemUserService.findByEmail(authEmail);
                if (systemUser != null) {
                    // Tìm customer liên kết với systemUser
                    CustomersEntity linkedCustomer = null;
                    if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                        linkedCustomer = customersService.findBySystemUser(systemUser);
                    }
                    
                    if (linkedCustomer != null) {
                        // Sử dụng linked customer data
                        model.addAttribute("currentUser", linkedCustomer);
                        
                        // ✅ Set displayEmail cho navbar consistency
                        model.addAttribute("displayEmail", linkedCustomer.getEmail());
                        
                        final String avatarPath = linkedCustomer.getAvatar() != null && !linkedCustomer.getAvatar().trim().isEmpty()
                            ? "/img/customers/" + linkedCustomer.getAvatar()
                            : (systemUser.getImg() != null && !systemUser.getImg().trim().isEmpty()
                                ? "/img/customers/" + systemUser.getImg()
                                : "/img/customers/default-avatar.png");
                                
                        model.addAttribute("avatarPath", avatarPath);
                        
                        // Cache user data in session
                        session.setAttribute(SESSION_USER_KEY, linkedCustomer);
                        session.setAttribute(SESSION_AVATAR_KEY, avatarPath);
                        session.setAttribute("cachedUserEmail", authEmail);
                    } else {
                        // Tạo customer từ systemUser với email từ authentication
                        CustomersEntity tempCustomer = new CustomersEntity();
                        tempCustomer.setName(systemUser.getUsername());
                        tempCustomer.setEmail(authEmail); // Sử dụng email đăng nhập
                        tempCustomer.setPhone("Chưa cập nhật");
                        tempCustomer.setSystemUser(systemUser);
                        
                        model.addAttribute("currentUser", tempCustomer);
                        
                        // ✅ Set displayEmail cho navbar consistency - sử dụng auth email
                        model.addAttribute("displayEmail", authEmail);
                        
                        final String avatarPath = systemUser.getImg() != null && !systemUser.getImg().trim().isEmpty()
                            ? "/img/customers/" + systemUser.getImg()
                            : "/img/customers/default-avatar.png";
                            
                        model.addAttribute("avatarPath", avatarPath);
                        
                        // Cache user data in session
                        session.setAttribute(SESSION_USER_KEY, tempCustomer);
                        session.setAttribute(SESSION_AVATAR_KEY, avatarPath);
                        session.setAttribute("cachedUserEmail", authEmail);
                    }
                }
            }
        }
    }
    
    /**
     * Xóa cache session khi user data được cập nhật
     */
    protected void clearUserCache(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY);
        session.removeAttribute(SESSION_AVATAR_KEY);
        session.removeAttribute("cachedUserEmail");
        logger.info("🗑️ User cache cleared from session");
    }
    
    /**
     * Force refresh user data từ database và cập nhật cache
     */
    protected void refreshUserData(Model model, HttpSession session) {
        clearUserCache(session);
        addCurrentUserToModel(model, session);
    }
} 