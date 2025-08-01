package sd19303no1.hotel_booking_and_management_system.Controller.AuthPageController;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                HttpServletRequest request) {
        if (error != null) {
            model.addAttribute("error", "Email hoặc mật khẩu không hợp lệ.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Bạn đã đăng xuất thành công.");
        }
        
        // Đảm bảo session được tạo
        HttpSession session = request.getSession(true);
        
        return "Auth/login";
    }

    // Xóa @PostMapping("/login") vì Spring Security sẽ xử lý
    // @PostMapping("/login")
    // public String processLogin(@RequestParam String email,
    //                           @RequestParam String password,
    //                           Model model) {
    //     try {
    //         Authentication authentication = authenticationManager.authenticate(
    //                 new UsernamePasswordAuthenticationToken(email.toLowerCase(), password));
    //         SecurityContextHolder.getContext().setAuthentication(authentication);
    //         return getRedirectUrl(authentication);
    //     } catch (AuthenticationException e) {
    //         model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
    //         return "Auth/login";
    //     }
    // }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam(name = "confirm-password") String confirmPassword,
                                 @RequestParam(name = "terms", required = false) String terms,
                                 Model model) {
        if (terms == null || !terms.equals("on")) {
            model.addAttribute("error", "Vui lòng đồng ý với điều khoản sử dụng!");
            model.addAttribute("showRegister", true);
            return "Auth/login";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp!");
            model.addAttribute("showRegister", true);
            return "Auth/login";
        }

        String lowerCaseEmail = email.toLowerCase();
        if (systemUserRepository.existsByEmail(lowerCaseEmail) || 
            customersRepository.existsByEmail(lowerCaseEmail)) {
            model.addAttribute("error", "Email đã được sử dụng!");
            model.addAttribute("showRegister", true);
            return "Auth/login";
        }

        // Tạo SystemUserEntity
        SystemUserEntity newUser = new SystemUserEntity();
        newUser.setUsername(username);
        newUser.setEmail(lowerCaseEmail);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(SystemUserEntity.Role.CUSTOMER);
        systemUserRepository.save(newUser);

        // Tạo CustomersEntity và liên kết
        CustomersEntity customer = new CustomersEntity();
        customer.setName(username);
        customer.setEmail(lowerCaseEmail);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setSystemUser(newUser);
        customersRepository.save(customer);

        // Cập nhật liên kết ngược
        newUser.setCustomer(customer);
        systemUserRepository.save(newUser);

        // Tự động đăng nhập
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(lowerCaseEmail, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return getRedirectUrl(authentication);
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Đăng ký thành công nhưng đăng nhập tự động thất bại. Vui lòng đăng nhập.");
            return "Auth/login";
        }
    }

    private String getRedirectUrl(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(authority -> switch (authority.getAuthority()) {
                    case "ROLE_ADMIN" -> "redirect:/admin/bookings";
                    case "ROLE_PARTNER" -> "redirect:/partner/reports";
                    case "ROLE_STAFF" -> "redirect:/staff/dashboard";
                    default -> "redirect:/";
                })
                .findFirst()
                .orElse("redirect:/");
    }
}