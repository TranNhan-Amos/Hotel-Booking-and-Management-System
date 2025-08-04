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

public abstract class BaseController {

    @Autowired
    protected CustomersService customersService;

    @Autowired
    protected SystemUserService systemUserService;

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
            
            // Thử tìm trong CustomersEntity trước
            CustomersEntity customer = customersService.findByEmail(email);
            if (customer != null) {
                model.addAttribute("currentUser", customer);
            } else {
                // Nếu không tìm thấy trong CustomersEntity, thử tìm trong SystemUserEntity
                SystemUserEntity systemUser = systemUserService.findByEmail(email);
                if (systemUser != null) {
                    // Tạo một CustomersEntity tạm thời từ SystemUserEntity
                    CustomersEntity tempCustomer = new CustomersEntity();
                    tempCustomer.setName(systemUser.getUsername());
                    tempCustomer.setEmail(systemUser.getEmail());
                    tempCustomer.setPhone("Chưa cập nhật");
                    tempCustomer.setSystemUser(systemUser);
                    model.addAttribute("currentUser", tempCustomer);
                }
            }
        }
    }
} 