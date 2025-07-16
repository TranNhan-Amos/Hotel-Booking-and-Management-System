package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, Long> {
    PartnerEntity findBySystemUser(SystemUserEntity systemUser);
    PartnerEntity findByEmail(String email);
    
    
}
