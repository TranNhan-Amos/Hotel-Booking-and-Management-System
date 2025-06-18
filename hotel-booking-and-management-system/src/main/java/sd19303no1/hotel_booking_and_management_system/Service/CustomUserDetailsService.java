package sd19303no1.hotel_booking_and_management_system.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String lowerCaseEmail = email.toLowerCase();

        // Tìm trong SystemUserEntity
        return systemUserRepository.findByEmail(lowerCaseEmail)
                .map(user -> new User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                ))
                .orElseGet(() -> customersRepository.findByEmail(lowerCaseEmail)
                        .map(customer -> new User(
                                customer.getEmail(),
                                customer.getPassword(),
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                        ))
                        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email)));
    }
}