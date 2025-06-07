package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.StatusEntity;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, Integer> {
    StatusEntity findByStatusName(String statusName);
}
