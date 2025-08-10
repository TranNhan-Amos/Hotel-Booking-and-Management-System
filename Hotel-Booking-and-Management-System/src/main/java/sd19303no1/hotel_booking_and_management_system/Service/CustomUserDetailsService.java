package sd19303no1.hotel_booking_and_management_system.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String lowerCaseEmail = email.toLowerCase();
        System.out.println("=== CUSTOM USER DETAILS DEBUG ===");
        System.out.println("Loading user by email: " + email + " (lowercase: " + lowerCaseEmail + ")");

        // Tìm trong SystemUserEntity (sử dụng ignore-case)
        return systemUserRepository.findByEmailIgnoreCase(lowerCaseEmail)
                .map(user -> {
                    System.out.println("Found SystemUser: " + user.getEmail() + " with role: " + user.getRole());
                    return new CustomUserDetails(user);
                })
                .orElseGet(() -> {
                    System.out.println("SystemUser not found, searching in Customers...");
                    return customersRepository.findByEmailIgnoreCase(lowerCaseEmail)
                            .map(customer -> {
                                System.out.println("Found Customer: " + customer.getEmail());
                                return new CustomUserDetails(customer);
                            })
                            .orElseThrow(() -> {
                                System.out.println("User not found in both tables");
                                return new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email);
                            });
                });
    }

   
    // Custom UserDetails implementation
    public static class CustomUserDetails implements UserDetails {
        private final SystemUserEntity systemUser;
        private final CustomersEntity customer;
        private final boolean isSystemUser;

        public CustomUserDetails(SystemUserEntity systemUser) {
            this.systemUser = systemUser;
            this.customer = null;
            this.isSystemUser = true;
            System.out.println("Created CustomUserDetails for SystemUser: " + systemUser.getEmail() + " with role: " + systemUser.getRole());
        }

        public CustomUserDetails(CustomersEntity customer) {
            this.customer = customer;
            this.systemUser = null;
            this.isSystemUser = false;
            System.out.println("Created CustomUserDetails for Customer: " + customer.getEmail());
        }

        @Override
        public String getUsername() {
            return isSystemUser ? systemUser.getEmail() : customer.getEmail();
        }

        @Override
        public String getPassword() {
            return isSystemUser ? systemUser.getPassword() : customer.getPassword();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }



        @Override
        public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
            if (isSystemUser) {
                String authority = "ROLE_" + systemUser.getRole().name();
                System.out.println("SystemUser authority: " + authority);
                return Collections.singletonList(new SimpleGrantedAuthority(authority));
            } else {
                System.out.println("Customer authority: ROLE_CUSTOMER");
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
            }
        }

        // Getter methods để truy cập thông tin người dùng
        public String getEmail() {
            return isSystemUser ? systemUser.getEmail() : customer.getEmail();
        }

        public SystemUserEntity getSystemUser() {
            return systemUser;
        }

        public CustomersEntity getCustomer() {
            return customer;
        }

        public boolean isSystemUser() {
            return isSystemUser;
        }
    }
}