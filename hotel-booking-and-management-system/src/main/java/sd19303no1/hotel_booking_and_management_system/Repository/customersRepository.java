package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;

public interface CustomersRepository extends JpaRepository<CustomersEntity, Long> {
}
