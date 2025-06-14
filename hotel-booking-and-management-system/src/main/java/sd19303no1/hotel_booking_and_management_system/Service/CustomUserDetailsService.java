package sd19303no1.hotel_booking_and_management_system.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository; // BẠN CẦN THÊM REPOSITORY NÀY
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

// Đổi tên class từ SystemUserService thành một tên chung hơn
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SystemUserRepository systemUserRepository;

    // Thêm repository cho Customers
    @Autowired
    private CustomersRepository customersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security gọi phương thức này với "username", nhưng chúng ta đã cấu hình nó để truyền "email" vào đây.
        String lowerCaseEmail = email.toLowerCase();

        // 1. Ưu tiên tìm trong bảng SystemUser (admin, staff, etc.)
        Optional<SystemUserEntity> systemUserOpt = systemUserRepository.findByEmail(lowerCaseEmail);
        if (systemUserOpt.isPresent()) {
            SystemUserEntity user = systemUserOpt.get();
            // Tạo danh sách quyền (roles) cho người dùng hệ thống
            Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
            // Trả về một đối tượng UserDetails mà Spring Security có thể sử dụng
            return new User(user.getEmail(), user.getPassword(), authorities);
        }

        // 2. Nếu không tìm thấy, tìm trong bảng Customers
        Optional<CustomersEntity> customerOpt = customersRepository.findByEmail(lowerCaseEmail);
        if (customerOpt.isPresent()) {
            CustomersEntity customer = customerOpt.get();
            // Gán một vai trò mặc định cho khách hàng, ví dụ "ROLE_CUSTOMER"
            Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_CUSTOMER")
            );
            // Trả về một đối tượng UserDetails cho khách hàng
            return new User(customer.getEmail(), customer.getPassword(), authorities);
        }

        // 3. Nếu không tìm thấy ở cả hai nơi, ném ra exception
        throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email);
    }
}