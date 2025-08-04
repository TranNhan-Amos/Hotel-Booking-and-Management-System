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

    // Admin review management methods
    @Query("SELECT r FROM ReviewEntity r ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findAllReviewsForAdmin(Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r")
    Long countTotalReviews();

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.rating >= 4")
    Long countPositiveReviews();

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.rating <= 2")
    Long countNegativeReviews();

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.response IS NULL")
    Long countPendingReviews();

    @Query("SELECT r FROM ReviewEntity r WHERE r.customer.name LIKE %:searchTerm% OR r.comment LIKE %:searchTerm% ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findReviewsBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.customer.name LIKE %:searchTerm% OR r.comment LIKE %:searchTerm%")
    Long countReviewsBySearchTerm(@Param("searchTerm") String searchTerm);

    @Query("SELECT r FROM ReviewEntity r WHERE r.rating = :rating ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findReviewsByRating(@Param("rating") Integer rating, Pageable pageable);

    @Query("SELECT r FROM ReviewEntity r WHERE r.response IS NULL ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findPendingReviews(Pageable pageable);

    @Query("SELECT r FROM ReviewEntity r WHERE r.response IS NOT NULL ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findApprovedReviews(Pageable pageable);

    @Query("SELECT r FROM ReviewEntity r WHERE r.response IS NOT NULL AND r.response LIKE '%rejected%' ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findRejectedReviews(Pageable pageable);

    // Website overall rating statistics
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r")
    Double getOverallAverageRating();

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.rating = :rating")
    Long countReviewsByRating(@Param("rating") Integer rating);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.rating >= 4 AND r.response IS NOT NULL")
    Long countApprovedPositiveReviews();

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.rating <= 2 AND r.response IS NOT NULL")
    Long countApprovedNegativeReviews();

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.rating = 3 AND r.response IS NOT NULL")
    Long countApprovedNeutralReviews();

    @Query("SELECT r.rating, COUNT(r) FROM ReviewEntity r WHERE r.response IS NOT NULL GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistribution();

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.response IS NOT NULL")
    Double getApprovedReviewsAverageRating();

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.response IS NOT NULL")
    Long countApprovedReviews();

    // Partner review management methods
    @Query("SELECT r FROM ReviewEntity r WHERE r.room.partner.id = :partnerId ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findReviewsByPartnerId(@Param("partnerId") Long partnerId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.room.partner.id = :partnerId")
    Long countReviewsByPartnerId(@Param("partnerId") Long partnerId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.room.partner.id = :partnerId")
    Double getAverageRatingByPartnerId(@Param("partnerId") Long partnerId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.room.partner.id = :partnerId AND MONTH(r.reviewDate) = MONTH(CURRENT_DATE) AND YEAR(r.reviewDate) = YEAR(CURRENT_DATE)")
    Long countReviewsThisMonthByPartnerId(@Param("partnerId") Long partnerId);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.room.partner.id = :partnerId AND r.response IS NULL")
    Long countPendingReviewsByPartnerId(@Param("partnerId") Long partnerId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.room.partner.id = :partnerId AND (r.customer.name LIKE %:searchTerm% OR r.comment LIKE %:searchTerm%) ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findReviewsBySearchTermForPartner(@Param("searchTerm") String searchTerm, @Param("partnerId") Long partnerId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.room.partner.id = :partnerId AND (r.customer.name LIKE %:searchTerm% OR r.comment LIKE %:searchTerm%)")
    Long countReviewsBySearchTermForPartner(@Param("searchTerm") String searchTerm, @Param("partnerId") Long partnerId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.room.partner.id = :partnerId AND r.rating = :rating ORDER BY r.reviewDate DESC")
    List<ReviewEntity> findReviewsByRatingForPartner(@Param("rating") Integer rating, @Param("partnerId") Long partnerId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ReviewEntity r WHERE r.room.partner.id = :partnerId AND r.rating = :rating")
    Long countReviewsByRatingForPartner(@Param("rating") Integer rating, @Param("partnerId") Long partnerId);

    @Query("SELECT r.rating, COUNT(r) FROM ReviewEntity r WHERE r.room.partner.id = :partnerId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionByPartnerId(@Param("partnerId") Long partnerId);
}
