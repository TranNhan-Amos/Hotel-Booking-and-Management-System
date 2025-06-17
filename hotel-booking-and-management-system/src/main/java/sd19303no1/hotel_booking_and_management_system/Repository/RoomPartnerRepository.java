package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sd19303no1.hotel_booking_and_management_system.Entity.RoomPartnerEntity;
public interface RoomPartnerRepository extends JpaRepository<RoomPartnerEntity, Long > {
    
    List<RoomPartnerEntity> findByPartnerId(Long partnerId);
}
