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
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/", "/login", "/register", "/css/**", "/Css/**", "/js/**", 
                    "/images/**", "/room/**", "/search", "/rooms", "/details/**",
                    "/bookings", "/payment", "/booking-confirmation", "/error", "/process-payment", "/test-data",
                    "/profile"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/partner/**").hasRole("PARTNER")
                .requestMatchers("/staff/**").hasRole("STAFF")
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
            );

        return http.build();
    }

    private String getRedirectUrl(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(authority -> switch (authority.getAuthority()) {
                    case "ROLE_ADMIN" -> "/admin/bookings";
                    case "ROLE_PARTNER" -> "/partner";
                    case "ROLE_STAFF" -> "/staff/dashboard";
                    default -> "/";
                })
                .findFirst()
                .orElse("/");
    }
}