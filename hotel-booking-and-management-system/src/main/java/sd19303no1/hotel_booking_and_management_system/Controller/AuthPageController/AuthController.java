package sd19303no1.hotel_booking_and_management_system.Controller.AuthPageController;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String showLoginPage() {
        return "Auth/login"; // Maps to your provided HTML (login.html)
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username, // Maps to login-username in HTML
            @RequestParam String password, // Maps to login-password in HTML
            Model model,
            HttpSession session) {
        try {
            // Authenticate using username or email
            SystemUserEntity user = systemUserRepository.findByUsernameOrEmail(username, username)
                    .orElse(null);
            if (user == null) {
                model.addAttribute("error", "Tên người dùng hoặc email không tồn tại!");
                return "Auth/login";
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);
            session.setAttribute("loggedInUser", user);

            return getRedirectUrl(authentication);
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Tên người dùng/email hoặc mật khẩu không đúng!");
            return "Auth/login";
        }
    }

    // @GetMapping("/register")
    // public String showRegisterPage() {
    //     return "login"; // Same HTML file, toggled to register tab via JavaScript
    // }

    // @PostMapping("/register")
    // public String processRegister(
    //         @RequestParam String username, // Maps to register-username
    //         @RequestParam String email, // Maps to register-email
    //         @RequestParam String password, // Maps to register-password
    //         @RequestParam(name = "confirm-password") String confirmPassword, // Maps to register-confirm-password
    //         @RequestParam boolean terms, // Maps to terms checkbox
    //         Model model,
    //         HttpSession session) {
    //     // Validate terms acceptance
    //     if (!terms) {
    //         model.addAttribute("error", "Vui lòng đồng ý với điều khoản sử dụng!");
    //         model.addAttribute("username", username);
    //         model.addAttribute("email", email);
    //         return "login";
    //     }

    //     // Validate passwords match
    //     if (!password.equals(confirmPassword)) {
    //         model.addAttribute("error", "Mật khẩu không khớp!");
    //         model.addAttribute("username", username);
    //         model.addAttribute("email", email);
    //         return "login";
    //     }

    //     // Validate password length
    //     if (password.length() < 6) {
    //         model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
    //         model.addAttribute("username", username);
    //         model.addAttribute("email", email);
    //         return "login";
    //     }

    //     // Validate username length
    //     if (username.length() < 3) {
    //         model.addAttribute("error", "Tên người dùng phải có ít nhất 3 ký tự!");
    //         model.addAttribute("username", username);
    //         model.addAttribute("email", email);
    //         return "login";
    //     }

    //     // Check if username exists
    //     if (systemUserRepository.existsByUsername(username)) {
    //         model.addAttribute("error", "Tên người dùng đã được sử dụng!");
    //         model.addAttribute("username", username);
    //         model.addAttribute("email", email);
    //         return "login";
    //     }

    //     // Check if email exists
    //     if (systemUserRepository.existsByEmail(email)) {
    //         model.addAttribute("error", "Email đã được sử dụng!");
    //         model.addAttribute("username", username);
    //         model.addAttribute("email", email);
    //         return "login";
    //     }

    //     // Create and save SystemUserEntity
    //     SystemUserEntity newUser = SystemUserEntity.builder()
    //             .username(username)
    //             .email(email)
    //             .password(passwordEncoder.encode(password))
    //             .role(SystemUserEntity.Role.CUSTOMER)
    //             .build();
    //     systemUserRepository.save(newUser);

    //     // Create and save CustomersEntity
    //     CustomersEntity customer = new CustomersEntity();
    //     customer.setName(username);
    //     customer.setEmail(email);
    //     customer.setPassword(passwordEncoder.encode(password));
    //     customer.setAddress("");
    //     customer.setPhone("");
    //     customersRepository.save(customer);

    //     // Authenticate the new user
    //     try {
    //         Authentication authentication = authenticationManager.authenticate(
    //                 new UsernamePasswordAuthenticationToken(username, password));
    //         SecurityContext context = SecurityContextHolder.createEmptyContext();
    //         context.setAuthentication(authentication);
    //         SecurityContextHolder.setContext(context);
    //         session.setAttribute("SPRING_SECURITY_CONTEXT", context);
    //         session.setAttribute("loggedInUser", newUser);

    //         return getRedirectUrl(authentication);
    //     } catch (AuthenticationException e) {
    //         model.addAttribute("error", "Đăng ký thành công nhưng đăng nhập thất bại. Vui lòng thử đăng nhập lại.");
    //         return "login";
    //     }
    // }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    private String getRedirectUrl(Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }
}