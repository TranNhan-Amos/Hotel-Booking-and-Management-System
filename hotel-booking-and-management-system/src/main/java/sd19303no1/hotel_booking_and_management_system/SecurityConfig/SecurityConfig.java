package sd19303no1.hotel_booking_and_management_system.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF configuration
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // Optionally exclude certain endpoints from CSRF if needed
                // .ignoringRequestMatchers("/register")
            )
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",                        // Trang chủ
                    "/login",                   // Trang đăng nhập
                    "/register",                // Trang đăng ký
                    "/css/**",                  // Tài nguyên CSS
                    "/js/**",                   // Tài nguyên JavaScript
                    "/images/**",               // Tài nguyên hình ảnh
                    "/room/**",                 // Chi tiết phòng (MỚI)
                    "/search"                   // Kết quả tìm kiếm (MỚI)
                ).permitAll()
                .requestMatchers("/dashboard").hasRole("ADMIN") // Trang dashboard cho Admin
                .anyRequest().authenticated() // Tất cả các request khác cần xác thực
            )
            // Form login configuration
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email") // Khớp với tham số 'email' của form
                .passwordParameter("password") // Khớp với tham số 'password' của form
                .defaultSuccessUrl("/", true) // Chuyển hướng đến trang chủ sau khi đăng nhập thành công
                .failureHandler((request, response, exception) -> {
                    request.setAttribute("error", exception.getMessage());
                    request.setAttribute("action", "login");
                    request.getRequestDispatcher("/login").forward(request, response);
                })
                .permitAll()
            )
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}