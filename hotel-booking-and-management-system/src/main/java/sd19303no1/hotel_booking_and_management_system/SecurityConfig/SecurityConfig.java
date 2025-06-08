package sd19303no1.hotel_booking_and_management_system.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Comment out or remove original
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * PasswordEncoder tùy chỉnh CHỈ DÀNH CHO MÔI TRƯỜNG PHÁT TRIỂN.
     * PasswordEncoder này sẽ cho phép bất kỳ mật khẩu nào khớp với mật khẩu đã lưu.
     * CẢNH BÁO: CỰC KỲ KÉM AN TOÀN. KHÔNG SỬ DỤNG TRONG PRODUCTION.
     */
    private static class AllowAllPasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(CharSequence rawPassword) {
            // Phương thức này có thể được gọi nếu bạn đăng ký người dùng mới
            // hoặc thay đổi mật khẩu khi encoder này đang hoạt động.
            // Để đơn giản, chúng ta trả về một chuỗi cố định hoặc dựa trên rawPassword.
            System.err.println("CẢNH BÁO: AllowAllPasswordEncoder.encode() được gọi với: " + rawPassword +
                               ". Đây là cấu hình bỏ qua đăng nhập cho development. Trả về giá trị giả.");
            // Trả về một giá trị giả, không phải là mật khẩu gốc
            return "dummy_encoded_for_dev_bypass_" + (rawPassword != null ? rawPassword.toString().hashCode() : "null_password");
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            // Đây là phần quan trọng: luôn trả về true để bỏ qua việc kiểm tra mật khẩu.
            // Điều này có nghĩa là bất kỳ mật khẩu nào được nhập vào cũng sẽ "khớp"
            // với mật khẩu đã được mã hóa và lưu trữ trong cơ sở dữ liệu.
            System.err.println("CẢNH BÁO: AllowAllPasswordEncoder.matches() được gọi. " +
                               "Mật khẩu nhập: '" + rawPassword + "', Mật khẩu lưu trữ (đã mã hóa): '" + encodedPassword +
                               "'. BỎ QUA KIỂM TRA - LUÔN TRẢ VỀ TRUE.");
            return true;
        }
    }

    // CẢNH BÁO: ĐÂY LÀ CẤU HÌNH TẠM THỜI, KÉM AN TOÀN CHỈ DÀNH CHO MÔI TRƯỜNG PHÁT TRIỂN
    // ĐỂ BỎ QUA VIỆC KIỂM TRA MẬT KHẨU VÌ BẠN ĐÃ QUÊN MẬT KHẨU.
    // HÃY KHÔI PHỤC LẠI BCryptPasswordEncoder VÀ ĐẶT LẠI MẬT KHẨU TRONG CƠ SỞ DỮ LIỆU
    // CÀNG SỚM CÀNG TỐT.
    @Bean
    public static PasswordEncoder passwordEncoder() {
        System.err.println();
        System.err.println("********************************************************************");
        System.err.println("********************************************************************");
        System.err.println("CẢNH BÁO: ĐANG SỬ DỤNG AllowAllPasswordEncoder - VIỆC KIỂM TRA MẬT KHẨU BỊ BỎ QUA!");
        System.err.println("CẤU HÌNH NÀY CHỈ DÀNH CHO MÔI TRƯỜNG PHÁT TRIỂN VÀ CỰC KỲ KÉM AN TOÀN.");
        System.err.println("BẠN CÓ THỂ ĐĂNG NHẬP VỚI BẤT KỲ MẬT KHẨU NÀO CHO MỘT USERNAME (EMAIL) HỢP LỆ.");
        System.err.println("HÃY KHÔI PHỤC LẠI BCryptPasswordEncoder CHO PRODUCTION HOẶC PHÁT TRIỂN THÔNG THƯỜNG.");
        System.err.println("CÁCH AN TOÀN HƠN ĐỂ LẤY LẠI QUYỀN TRUY CẬP LÀ ĐẶT LẠI MẬT KHẨU TRONG CSDL.");
        System.err.println("********************************************************************");
        System.err.println("********************************************************************");
        System.err.println();
        return new AllowAllPasswordEncoder(); // TẠM THỜI CHO DEVELOPMENT
        // return new BCryptPasswordEncoder(); // Encoder gốc và an toàn
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tạm thời vô hiệu hóa CSRF để dễ test
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/room/**", "/search"
                ).permitAll()
                .requestMatchers("/dashboard/**").hasRole("ADMIN") // Người dùng vẫn cần có vai trò ADMIN
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email") // Quan trọng: Spring Security sẽ tìm người dùng bằng email
                .defaultSuccessUrl("/", true)
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
}