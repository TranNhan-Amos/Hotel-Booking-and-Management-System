package sd19303no1.hotel_booking_and_management_system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;

@Service
public class SystemUserService {
    
    @Autowired
    private SystemUserRepository systemUserRepository;
    
    public SystemUserEntity findByEmail(String email) {
        return systemUserRepository.findByEmail(email).orElse(null);
    }
    
    public SystemUserEntity findByUsername(String username) {
        return systemUserRepository.findByUsername(username).orElse(null);
    }
    
    public boolean existsByEmail(String email) {
        return systemUserRepository.existsByEmail(email);
    }
    
    public boolean existsByUsername(String username) {
        return systemUserRepository.existsByUsername(username);
    }
    
    public SystemUserEntity save(SystemUserEntity user) {
        return systemUserRepository.save(user);
    }
}
