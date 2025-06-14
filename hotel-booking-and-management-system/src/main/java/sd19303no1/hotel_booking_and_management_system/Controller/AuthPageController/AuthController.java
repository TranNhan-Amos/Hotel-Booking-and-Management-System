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
    public String showLoginPage(Model model, @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        
        if (error != null) {
            model.addAttribute("error", "Email hoặc mật khẩu không hợp lệ.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Bạn đã đăng xuất thành công.");
        }
        return "Auth/login";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(name = "confirm-password") String confirmPassword,
            @RequestParam(name = "terms", required = false) String terms,
            Model model, HttpServletRequest request) {

        if (terms == null) {
            model.addAttribute("error", "Vui lòng đồng ý với điều khoản sử dụng!");
            model.addAttribute("showRegister", true);
            return "Auth/login";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp!");
            model.addAttribute("showRegister", true);
            return "Auth/login";
        }
        if (systemUserRepository.existsByEmail(email.toLowerCase())) {
            model.addAttribute("error", "Email đã được sử dụng!");
            model.addAttribute("showRegister", true);
            return "Auth/login";
        }

        // Create and save SystemUserEntity
        SystemUserEntity newUser = new SystemUserEntity();
        newUser.setUsername(username);
        newUser.setEmail(email.toLowerCase());
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(SystemUserEntity.Role.CUSTOMER);
        systemUserRepository.save(newUser);

        // Create and save CustomersEntity
        CustomersEntity customer = new CustomersEntity();
        customer.setName(username);
        customer.setEmail(email.toLowerCase());
        customer.setPassword(passwordEncoder.encode(password));
        customersRepository.save(customer);

        // Automatically log in the new user
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email.toLowerCase(), password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute("loggedInUser", newUser);
            return getRedirectUrl(authentication);
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Đăng ký thành công nhưng đăng nhập tự động thất bại. Vui lòng đăng nhập.");
            return "Auth/login";
        }
    }

    private String getRedirectUrl(Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/dashboard";
        }
        return "redirect:/";
    }
}
