package sd19303no1.hotel_booking_and_management_system.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {

    @Query("SELECT DISTINCT r.type FROM RoomEntity r")
    List<RoomEntity.RoomType> findDistinctTypes();

    RoomEntity findFirstByType(RoomEntity.RoomType type);

    @Query("SELECT r FROM RoomEntity r " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE r.status = 'AVAILABLE' " +
           "AND r.roomId NOT IN (SELECT b.room.roomId FROM BookingOrderEntity b " +
           "WHERE b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate " +
           "AND b.status.statusName = 'CONFIRMED')")
    Page<RoomEntity> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate, 
                                       @Param("checkOutDate") LocalDate checkOutDate, 
                                       Pageable pageable);

    @Query("SELECT r FROM RoomEntity r " +
           "LEFT JOIN FETCH r.partner p " +
           "WHERE r.status = 'AVAILABLE' " +
           "AND (LOWER(p.address) LIKE LOWER(CONCAT('%', :location, '%')) " +
           "OR LOWER(p.companyName) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND r.roomId NOT IN (SELECT b.room.roomId FROM BookingOrderEntity b " +
           "WHERE b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate " +
           "AND b.status.statusName = 'CONFIRMED')")
    Page<RoomEntity> findAvailableRoomsByLocation(@Param("location") String location,
                                                 @Param("checkInDate") LocalDate checkInDate, 
                                                 @Param("checkOutDate") LocalDate checkOutDate, 
                                                 Pageable pageable);

    @Query("SELECT r FROM RoomEntity r " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE r.type = :roomType AND r.roomId != :excludeRoomId AND r.status = 'AVAILABLE'")
    Page<RoomEntity> findRelatedRooms(@Param("roomType") RoomEntity.RoomType roomType, 
                                     @Param("excludeRoomId") Integer excludeRoomId, 
                                     Pageable pageable);

    @Query("SELECT r FROM RoomEntity r " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE r.partner.id = :partnerId")
    List<RoomEntity> findByPartnerId(@Param("partnerId") Long partnerId);

    @Query("SELECT r FROM RoomEntity r " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE (:partnerId IS NULL OR r.partner.id = :partnerId) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:type IS NULL OR r.type = :type) " +
           "AND (:search IS NULL OR " +
           "(LOWER(r.nameNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<RoomEntity> findRoomsWithFilters(@Param("partnerId") Long partnerId,
                                         @Param("status") RoomEntity.RoomStatus status,
                                         @Param("type") RoomEntity.RoomType type,
                                         @Param("search") String search,
                                         Pageable pageable);

    @Query("SELECT r, COALESCE(AVG(rev.rating), 0.0) as avgRating FROM RoomEntity r " +
           "LEFT JOIN r.reviews rev " +
           "LEFT JOIN FETCH r.partner " +
           "LEFT JOIN FETCH r.amenities " +
           "GROUP BY r " +
           "ORDER BY r.createdAt DESC")
    Page<Object[]> findRoomsWithAverageRating(Pageable pageable);

    @Query("SELECT r FROM RoomEntity r " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE r.status = 'AVAILABLE' " +
           "ORDER BY r.createdAt DESC")
    Page<RoomEntity> findFeaturedRooms(Pageable pageable);

    @Query("SELECT r.amenities FROM RoomEntity r WHERE r.roomId = :roomId")
    List<String> findAmenitiesByRoomId(@Param("roomId") Integer roomId);

    @Query("SELECT r, COALESCE(AVG(rev.rating), 0.0) as avgRating FROM RoomEntity r " +
           "LEFT JOIN r.reviews rev " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE r.status = 'AVAILABLE' " +
           "GROUP BY r " +
           "ORDER BY avgRating DESC, r.createdAt DESC")
    Page<Object[]> findFeaturedRoomsWithRating(Pageable pageable);

    // Đếm số phòng đã được đặt trong khoảng thời gian
    @Query("SELECT COALESCE(SUM(b.roomQuantity), 0) FROM BookingOrderEntity b " +
           "WHERE b.room.roomId = :roomId " +
           "AND b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate " +
           "AND b.status.statusName NOT IN ('CANCELLED', 'REFUNDED')")
    long countBookedRoomsInDateRange(@Param("roomId") Integer roomId,
                                    @Param("checkInDate") LocalDate checkInDate,
                                    @Param("checkOutDate") LocalDate checkOutDate);

    // Tìm phòng theo partner ID
    @Query("SELECT r FROM RoomEntity r WHERE r.partner.id = :partnerId")
    List<RoomEntity> findByPartner_Id(@Param("partnerId") Long partnerId);
    
    // Đếm tổng số phòng
    @Query("SELECT COUNT(r) FROM RoomEntity r")
    Long countTotalRooms();
}