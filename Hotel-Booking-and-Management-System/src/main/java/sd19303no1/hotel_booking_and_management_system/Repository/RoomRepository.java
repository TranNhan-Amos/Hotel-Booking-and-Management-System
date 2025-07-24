package sd19303no1.hotel_booking_and_management_system.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {

    @Query("SELECT DISTINCT r.type FROM RoomEntity r")
    List<String> findDistinctTypes();

    RoomEntity findFirstByType(String type);

    @Query("SELECT r FROM RoomEntity r WHERE r.status = 'AVAILABLE' " +
           "AND r.roomId NOT IN (SELECT b.room.roomId FROM BookingOrderEntity b " +
           "WHERE (b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate) " +
           "AND b.status.statusName IN ('CONFIRMED', 'PENDING'))")
    List<RoomEntity> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate, 
                                       @Param("checkOutDate") LocalDate checkOutDate, 
                                       Pageable pageable);

    @Query("SELECT r FROM RoomEntity r WHERE r.status = 'AVAILABLE' " +
           "AND (LOWER(r.type) LIKE LOWER(CONCAT('%', :location, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND r.roomId NOT IN (SELECT b.room.roomId FROM BookingOrderEntity b " +
           "WHERE (b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate) " +
           "AND b.status.statusName IN ('CONFIRMED', 'PENDING'))")
    List<RoomEntity> findAvailableRoomsByLocation(@Param("location") String location,
                                                 @Param("checkInDate") LocalDate checkInDate, 
                                                 @Param("checkOutDate") LocalDate checkOutDate, 
                                                 Pageable pageable);

    // Thêm method để tìm phòng liên quan
    @Query("SELECT r FROM RoomEntity r WHERE r.type = :roomType AND r.roomId != :excludeRoomId AND r.status = 'AVAILABLE'")
    List<RoomEntity> findRelatedRooms(@Param("roomType") String roomType, @Param("excludeRoomId") Integer excludeRoomId, Pageable pageable);

    List<RoomEntity> findByPartner_Id(Long partnerId);
}