package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;

@Repository
public interface CustomersRepository extends JpaRepository<CustomersEntity, Integer> {
    Optional<CustomersEntity> findByEmail(String email);
}
