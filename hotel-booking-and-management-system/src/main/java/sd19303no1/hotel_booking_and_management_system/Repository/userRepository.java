package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sd19303no1.hotel_booking_and_management_system.Entity.userEntity;

public interface userRepository extends JpaRepository<userEntity, Long> {
}
