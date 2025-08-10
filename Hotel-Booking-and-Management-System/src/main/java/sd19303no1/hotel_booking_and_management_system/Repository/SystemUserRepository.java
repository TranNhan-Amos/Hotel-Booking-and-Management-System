package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUserEntity, Long> {
    Optional<SystemUserEntity> findByEmail(String email);
    Optional<SystemUserEntity> findByEmailIgnoreCase(String email);
    Optional<SystemUserEntity> findByUsername(String username);
    Optional<SystemUserEntity> findByUsernameIgnoreCase(String username);
    boolean existsByUsername(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}
