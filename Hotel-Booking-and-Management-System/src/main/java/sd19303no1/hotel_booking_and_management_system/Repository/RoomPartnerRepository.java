package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sd19303no1.hotel_booking_and_management_system.Entity.RoomPartnerEntity;
public interface RoomPartnerRepository extends JpaRepository<RoomPartnerEntity, Long > {
    
    List<RoomPartnerEntity> findByPartnerId(Long partnerId);

    @Query("SELECT COUNT(r.roomId) FROM RoomPartnerEntity r WHERE r.partnerId = :partnerId")
    long countRoomsByPartnerId(@Param("partnerId") Long partnerId);


    boolean existsByRoomNumberAndPartnerId(String roomNumber, Long partnerId);


}
