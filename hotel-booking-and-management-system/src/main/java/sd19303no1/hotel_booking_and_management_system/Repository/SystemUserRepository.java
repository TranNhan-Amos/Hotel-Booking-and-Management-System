package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;

public interface SystemUserRepository extends JpaRepository<SystemUserEntity, Integer> {
    Optional<SystemUserEntity> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
