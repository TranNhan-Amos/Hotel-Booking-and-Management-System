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

import jakarta.servlet.http.HttpSession;

public abstract class BaseController {

    @Autowired
    protected CustomersService customersService;

    @Autowired
    protected SystemUserService systemUserService;

    /**
     * Refresh avatar path in session
     */
    protected void refreshSessionAvatar(HttpSession session, String email) {
        try {
            // Tìm customer trước
            CustomersEntity customer = customersService.findByEmail(email);
            if (customer != null && customer.getAvatar() != null && !customer.getAvatar().isEmpty()) {
                String avatarPath = "/img/customers/" + customer.getAvatar();
                session.setAttribute("avatarPath", avatarPath);
                return;
            }
            
            // Nếu không tìm thấy customer, tìm systemUser
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            if (systemUser != null && systemUser.getImg() != null && !systemUser.getImg().isEmpty()) {
                String avatarPath = "/img/customers/" + systemUser.getImg();
                session.setAttribute("avatarPath", avatarPath);
                return;
            }
            
            // Nếu không có avatar, set default
            session.setAttribute("avatarPath", "/img/customers/default-avatar.svg");
        } catch (Exception e) {
            // Log error but don't throw
            System.err.println("Error refreshing session avatar: " + e.getMessage());
            session.setAttribute("avatarPath", "/img/customers/default-avatar.svg");
        }
    }

    /**
     * Thêm thông tin người dùng hiện tại vào model
     * Được gọi tự động cho tất cả các request
     */
    @ModelAttribute
    public void addCurrentUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            String email = authentication.getName();
            
            // Thử tìm trong CustomersEntity trước (sử dụng ignore-case)
            CustomersEntity customer = customersService.findByEmail(email);
            if (customer != null) {
                model.addAttribute("currentUser", customer);
                // Set avatar path in session if not already set
                if (customer.getAvatar() != null && !customer.getAvatar().isEmpty()) {
                    String avatarPath = "/img/customers/" + customer.getAvatar();
                    model.addAttribute("avatarPath", avatarPath);
                }
            } else {
                // Nếu không tìm thấy trong CustomersEntity, thử tìm trong SystemUserEntity (sử dụng ignore-case)
                SystemUserEntity systemUser = systemUserService.findByEmail(email);
                if (systemUser != null) {
                    // Tạo một CustomersEntity tạm thời từ SystemUserEntity
                    CustomersEntity tempCustomer = new CustomersEntity();
                    tempCustomer.setName(systemUser.getUsername());
                    tempCustomer.setEmail(systemUser.getEmail());
                    tempCustomer.setPhone("Chưa cập nhật");
                    tempCustomer.setSystemUser(systemUser);
                    model.addAttribute("currentUser", tempCustomer);
                    
                    // Set avatar path in session if not already set
                    if (systemUser.getImg() != null && !systemUser.getImg().isEmpty()) {
                        String avatarPath = "/img/customers/" + systemUser.getImg();
                        model.addAttribute("avatarPath", avatarPath);
                    }
                }
            }
        }
    }
} 