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
    List<RoomEntity.RoomType> findDistinctTypes();

    RoomEntity findFirstByType(RoomEntity.RoomType type);

    @Query("SELECT r FROM RoomEntity r WHERE r.status = 'AVAILABLE' " +
           "AND r.roomId NOT IN (SELECT b.room.roomId FROM BookingOrderEntity b " +
           "WHERE (b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate) " +
           "AND b.status.statusName IN ('CONFIRMED', 'PENDING'))")
    List<RoomEntity> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate, 
                                       @Param("checkOutDate") LocalDate checkOutDate, 
                                       Pageable pageable);

    @Query("SELECT r FROM RoomEntity r WHERE r.status = 'AVAILABLE' " +
           "AND (LOWER(CAST(r.type AS string)) LIKE LOWER(CONCAT('%', :location, '%')) " +
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
    List<RoomEntity> findRelatedRooms(@Param("roomType") RoomEntity.RoomType roomType, @Param("excludeRoomId") Integer excludeRoomId, Pageable pageable);

    List<RoomEntity> findByPartner_Id(Long partnerId);
    
    // Thêm method để lấy phòng với đánh giá trung bình
    @Query("SELECT r, COALESCE(AVG(rev.rating), 0.0) as avgRating FROM RoomEntity r " +
           "LEFT JOIN r.reviews rev " +
           "LEFT JOIN FETCH r.partner " +
           "LEFT JOIN FETCH r.amenities " +
           "LEFT JOIN FETCH r.imageUrls " +
           "GROUP BY r " +
           "ORDER BY r.createdAt DESC")
    List<Object[]> findRoomsWithAverageRating(Pageable pageable);
    
    // Thêm method để lấy phòng nổi bật cho trang chủ (chỉ fetch partner)
    @Query("SELECT r FROM RoomEntity r " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE r.status = 'AVAILABLE' " +
           "ORDER BY r.createdAt DESC")
    List<RoomEntity> findFeaturedRooms(Pageable pageable);
    
    // Thêm method để lấy amenities cho một phòng
    @Query("SELECT r.amenities FROM RoomEntity r WHERE r.roomId = :roomId")
    List<String> findAmenitiesByRoomId(@Param("roomId") Integer roomId);
    
    // Thêm method để lấy phòng với đánh giá trung bình được tính sẵn
    @Query("SELECT r, COALESCE(AVG(rev.rating), 0.0) as avgRating FROM RoomEntity r " +
           "LEFT JOIN r.reviews rev " +
           "LEFT JOIN FETCH r.partner " +
           "WHERE r.status = 'AVAILABLE' " +
           "GROUP BY r " +
           "ORDER BY r.createdAt DESC")
    List<Object[]> findFeaturedRoomsWithRating(Pageable pageable);
}