package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    @Query("SELECT r FROM ReviewEntity r ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findRecentReviews(Pageable pageable);

    List<ReviewEntity> findByRoomRoomIdOrderByReviewDateDesc(Integer roomId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.room.roomId = :roomId")
    Double findAverageRatingByRoomId(@Param("roomId") Integer roomId);


}
