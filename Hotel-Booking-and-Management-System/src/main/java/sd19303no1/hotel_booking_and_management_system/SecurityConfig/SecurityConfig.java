package sd19303no1.hotel_booking_and_management_system.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints - không cần xác thực
                .requestMatchers(
                    "/", "/login", "/register", "/logout",
                    "/css/**", "/Css/**", "/js/**", "/images/**", "/img/**", "/room-images/**",
                    "/room/**", "/rooms", "/room-detail", "/details/**",
                    "/search-rooms", "/search", "/ListRoom", "/favoriteroom",
                    "/About", "/Contact", "/Feedback", "/Notification",
                    "/error", "/test-payment", "/test-data", "/booking-confirmation",
                    "/partner/register", "/partner", "/api/admin/bookings/**",
                    "/favicon.ico", "/robots.txt", "/test-csrf", "/test-logout",
                    "/test-csrf", "/test/**"
                ).permitAll()
                
                // Customer authenticated endpoints
                .requestMatchers(
                    "/bookings", "/booking-details", "/my-bookings", 
                    "/booking-detail/**", "/profile", "/profile/**",
                    "/upload-avatar", "/upload-room-images", "/update-profile",
                    "/payment", "/process-payment", "/payment-success",
                    "/historybooking", "/feedbackuser", "/Waller",
                    "/book-room", "/cancel-booking", "/delete-avatar", 
                    "/delete-room-image"
                ).hasRole("CUSTOMER")
                
                // Admin endpoints
                .requestMatchers(
                    "/admin/**", "/admin/rooms/**", "/admin/bookings/**",
                    "/admin/customers/**", "/admin/vouchers/**", 
                    "/admin/reports/**", "/admin/settings/**",
                    "/admin/profile", "/admin/Evaluate"
                ).hasRole("ADMIN")
                
                // Partner endpoints
                .requestMatchers(
                    "/partner/**", "/partner/rooms/**", "/partner/bookings/**",
                    "/partner/reports", "/partner/reviews", "/partner/payments",
                    "/partner/settings/**", "/partner/support", "/partner/profile",
                    "/dashboard/partner", "/api/partner/**", "/api/partner/reports/**"
                ).hasRole("PARTNER")
                
                // Staff endpoints
                .requestMatchers("/staff/**").hasRole("STAFF")
                
                // Default - require authentication for everything else
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .successHandler((request, response, authentication) -> {
                    response.sendRedirect(getRedirectUrl(authentication));
                })
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // CSRF protection với cấu hình an toàn
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                    "/api/**", "/process-payment", 
                    "/css/**", "/Css/**", "/js/**", "/images/**", 
                    "/img/**", "/room-images/**", "/favicon.ico",
                    "/login", "/register", "/partner/register",
                    "/admin/customers/update/**", "/admin/customers/delete/**",
                    "/admin/customers/partner/update/**", "/admin/customers/partner/delete/**",
                    "/admin/reviews/approve", "/admin/reviews/reject", "/admin/reviews/reply",
                    "/admin/vouchers/create", "/admin/vouchers/update/**", "/admin/vouchers/delete/**", "/admin/vouchers/toggle/**",
                    "/admin/rooms/save", "/admin/rooms/update-status",
                    "/admin/reports/summary", "/admin/reports/daily", "/admin/reports/weekly", "/admin/reports/monthly", "/admin/reports/top-rooms", "/admin/reports/top-customers",
                    "/upload-avatar", "/upload-room-images", "/update-profile", "/profile/upload-avatar", "/profile/update",
                    "/book-room", "/cancel-booking", "/process-booking",
                    "/partner/rooms/add", "/partner/rooms/edit",
                    "/partner/settings/update",
                    "/payment", "/process-payment",
                    "/api/admin/bookings/**",
                    "/api/admin/bookings/*/cancel", "/api/admin/bookings/*/refund", 
                    "/api/admin/bookings/*/confirm-payment", "/api/admin/bookings/*/confirm-payment-hotel",
                    "/api/admin/bookings/*/send-notification", "/api/admin/bookings/*/check-in", "/api/admin/bookings/*/check-out",
                    "/api/partner/reports/summary", "/api/partner/reports/daily", "/api/partner/reports/top-customers", "/api/partner/reports/monthly-revenue"
                )
            )
            // Session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            )
            // Exception handling
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error?code=403")
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login?unauthorized");
                })
            );

        return http.build();
    }

    private String getRedirectUrl(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(authority -> switch (authority.getAuthority()) {
                    case "ROLE_ADMIN" -> "/admin/bookings";
                    case "ROLE_PARTNER" -> "/partner/reports";
                    case "ROLE_STAFF" -> "/staff/dashboard";
                    case "ROLE_CUSTOMER" -> "/";
                    default -> "/";
                })
                .findFirst()
                .orElse("/");
    }
}