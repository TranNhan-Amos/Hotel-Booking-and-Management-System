package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sd19303no1.hotel_booking_and_management_system.Entity.customersEntity;

public interface customersRepository extends JpaRepository<customersEntity, Long> {
}
