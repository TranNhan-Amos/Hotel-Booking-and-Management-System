package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.ReviewRepository;
import sd19303no1.hotel_booking_and_management_system.DTO.WebsiteStatsDTO;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<ReviewEntity> getRecentReviews(int limit) {
        return reviewRepository.findRecentReviews(PageRequest.of(0, limit));
    }

    public List<ReviewEntity> getReviewsByRoomId(Integer roomId) {
        return reviewRepository.findByRoomRoomIdOrderByReviewDateDesc(roomId);
    }

    public Double getAverageRatingByRoomId(Integer roomId) {
        return reviewRepository.findAverageRatingByRoomId(roomId);
    }

    // Admin review management methods
    public List<ReviewEntity> getAllReviewsForAdmin(int page, int size) {
        return reviewRepository.findAllReviewsForAdmin(PageRequest.of(page, size));
    }

    public Map<String, Long> getReviewStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalReviews", reviewRepository.countTotalReviews());
        stats.put("positiveReviews", reviewRepository.countPositiveReviews());
        stats.put("negativeReviews", reviewRepository.countNegativeReviews());
        stats.put("pendingReviews", reviewRepository.countPendingReviews());
        return stats;
    }

    public WebsiteStatsDTO getWebsiteStats() {
        // Lấy điểm trung bình tổng thể
        Double overallAverageRating = reviewRepository.getOverallAverageRating();
        if (overallAverageRating == null) {
            overallAverageRating = 0.0;
        }

        // Lấy tổng số đánh giá
        Long totalReviews = reviewRepository.countTotalReviews();
        if (totalReviews == null) {
            totalReviews = 0L;
        }

        // Lấy số đánh giá tích cực (4-5 sao)
        Long positiveReviews = reviewRepository.countPositiveReviews();
        if (positiveReviews == null) {
            positiveReviews = 0L;
        }

        // Lấy số đánh giá tiêu cực (1-2 sao)
        Long negativeReviews = reviewRepository.countNegativeReviews();
        if (negativeReviews == null) {
            negativeReviews = 0L;
        }

        // Lấy số đánh giá trung bình (3 sao)
        Long neutralReviews = reviewRepository.countReviewsByRating(3);
        if (neutralReviews == null) {
            neutralReviews = 0L;
        }

        // Lấy phân bố đánh giá theo sao
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = reviewRepository.countReviewsByRating(i);
            ratingDistribution.put(i, count != null ? count : 0L);
        }

        // Tính phần trăm cho mỗi mức đánh giá
        Map<Integer, Double> ratingPercentages = new HashMap<>();
        if (totalReviews > 0) {
            for (int i = 1; i <= 5; i++) {
                Long count = ratingDistribution.get(i);
                double percentage = (double) count / totalReviews * 100;
                ratingPercentages.put(i, Math.round(percentage * 10.0) / 10.0); // Làm tròn 1 chữ số thập phân
            }
        } else {
            for (int i = 1; i <= 5; i++) {
                ratingPercentages.put(i, 0.0);
            }
        }

        return new WebsiteStatsDTO(overallAverageRating, totalReviews, positiveReviews, 
                                 negativeReviews, neutralReviews, ratingDistribution, ratingPercentages);
    }

    public List<ReviewEntity> searchReviews(String searchTerm, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllReviewsForAdmin(page, size);
        }
        return reviewRepository.findReviewsBySearchTerm(searchTerm.trim(), PageRequest.of(page, size));
    }

    public List<ReviewEntity> getReviewsByRating(Integer rating, int page, int size) {
        if (rating == null || rating == 0) {
            return getAllReviewsForAdmin(page, size);
        }
        return reviewRepository.findReviewsByRating(rating, PageRequest.of(page, size));
    }

    public List<ReviewEntity> getReviewsByStatus(String status, int page, int size) {
        switch (status.toLowerCase()) {
            case "pending":
                return reviewRepository.findPendingReviews(PageRequest.of(page, size));
            case "approved":
                return reviewRepository.findApprovedReviews(PageRequest.of(page, size));
            case "rejected":
                return reviewRepository.findRejectedReviews(PageRequest.of(page, size));
            default:
                return getAllReviewsForAdmin(page, size);
        }
    }

    public Optional<ReviewEntity> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public boolean approveReview(Integer reviewId, SystemUserEntity admin, String response) {
        Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            ReviewEntity review = reviewOpt.get();
            review.setResponse(response != null ? response : "Đánh giá đã được duyệt");
            review.setResponseDate(LocalDate.now());
            review.setResponder(admin);
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    public boolean rejectReview(Integer reviewId, SystemUserEntity admin, String response) {
        Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            ReviewEntity review = reviewOpt.get();
            review.setResponse(response != null ? response : "Đánh giá đã bị từ chối");
            review.setResponseDate(LocalDate.now());
            review.setResponder(admin);
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    public boolean replyToReview(Integer reviewId, SystemUserEntity admin, String response) {
        Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            ReviewEntity review = reviewOpt.get();
            String currentResponse = review.getResponse();
            String newResponse = currentResponse != null ? currentResponse + "\n\n" + response : response;
            review.setResponse(newResponse);
            review.setResponseDate(LocalDate.now());
            review.setResponder(admin);
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    // Thêm các method cho controller mới
    public int getTotalReviews() {
        Long count = reviewRepository.countTotalReviews();
        return count != null ? count.intValue() : 0;
    }

    public int getTotalReviewsBySearch(String searchTerm) {
        Long count = reviewRepository.countReviewsBySearchTerm(searchTerm);
        return count != null ? count.intValue() : 0;
    }

    public int getTotalReviewsByRating(Integer rating) {
        Long count = reviewRepository.countReviewsByRating(rating);
        return count != null ? count.intValue() : 0;
    }

    public void replyToReview(Integer reviewId, String response) {
        Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            ReviewEntity review = reviewOpt.get();
            review.setResponse(response);
            review.setResponseDate(LocalDate.now());
            reviewRepository.save(review);
        } else {
            throw new RuntimeException("Không tìm thấy đánh giá với ID: " + reviewId);
        }
    }

    public Long getTotalReviewCount() {
        return reviewRepository.countTotalReviews();
    }

    public Long getReviewCountBySearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getTotalReviewCount();
        }
        return (long) reviewRepository.findReviewsBySearchTerm(searchTerm.trim(), PageRequest.of(0, Integer.MAX_VALUE)).size();
    }

    public Long getReviewCountByRating(Integer rating) {
        if (rating == null || rating == 0) {
            return getTotalReviewCount();
        }
        return (long) reviewRepository.findReviewsByRating(rating, PageRequest.of(0, Integer.MAX_VALUE)).size();
    }

    public Long getReviewCountByStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return reviewRepository.countPendingReviews();
            case "approved":
                return (long) reviewRepository.findApprovedReviews(PageRequest.of(0, Integer.MAX_VALUE)).size();
            case "rejected":
                return (long) reviewRepository.findRejectedReviews(PageRequest.of(0, Integer.MAX_VALUE)).size();
            default:
                return getTotalReviewCount();
        }
    }

    // Website overall rating statistics
    public Map<String, Object> getWebsiteRatingStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Overall average rating
        Double overallAvg = reviewRepository.getOverallAverageRating();
        stats.put("overallAverageRating", overallAvg != null ? Math.round(overallAvg * 10.0) / 10.0 : 0.0);
        stats.put("averageRating", overallAvg != null ? Math.round(overallAvg * 10.0) / 10.0 : 0.0);
        
        // Approved reviews average rating
        Double approvedAvg = reviewRepository.getApprovedReviewsAverageRating();
        stats.put("approvedAverageRating", approvedAvg != null ? Math.round(approvedAvg * 10.0) / 10.0 : 0.0);
        
        // Total reviews
        Long totalReviews = reviewRepository.countTotalReviews();
        stats.put("totalReviews", totalReviews);
        
        // Approved reviews
        Long approvedReviews = reviewRepository.countApprovedReviews();
        stats.put("approvedReviews", approvedReviews);
        
        // Pending reviews
        Long pendingReviews = reviewRepository.countPendingReviews();
        stats.put("pendingReviews", pendingReviews);
        
        // Add positive and negative reviews counts (all reviews)
        stats.put("positiveReviews", reviewRepository.countPositiveReviews());
        stats.put("negativeReviews", reviewRepository.countNegativeReviews());
        
        // Rating distribution
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = reviewRepository.countReviewsByRating(i);
            ratingDistribution.put(i, count != null ? count : 0L);
        }
        stats.put("ratingDistribution", ratingDistribution);
        
        // Approved rating distribution
        Map<Integer, Long> approvedRatingDistribution = new HashMap<>();
        approvedRatingDistribution.put(5, reviewRepository.countApprovedPositiveReviews());
        approvedRatingDistribution.put(4, reviewRepository.countApprovedPositiveReviews());
        approvedRatingDistribution.put(3, reviewRepository.countApprovedNeutralReviews());
        approvedRatingDistribution.put(2, reviewRepository.countApprovedNegativeReviews());
        approvedRatingDistribution.put(1, reviewRepository.countApprovedNegativeReviews());
        stats.put("approvedRatingDistribution", approvedRatingDistribution);
        
        // Calculate percentages
        if (totalReviews > 0) {
            stats.put("approvalRate", Math.round((double) approvedReviews / totalReviews * 100.0));
            stats.put("pendingRate", Math.round((double) pendingReviews / totalReviews * 100.0));
        } else {
            stats.put("approvalRate", 0);
            stats.put("pendingRate", 0);
        }
        
        // Calculate rating percentages
        Map<Integer, Double> ratingPercentages = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = ratingDistribution.get(i);
            double percentage = totalReviews > 0 ? (double) count / totalReviews * 100.0 : 0.0;
            ratingPercentages.put(i, Math.round(percentage * 10.0) / 10.0);
        }
        stats.put("ratingPercentages", ratingPercentages);
        
        return stats;
    }

    public Map<String, Object> getDetailedRatingAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        
        // Get rating distribution from database
        List<Object[]> ratingData = reviewRepository.getRatingDistribution();
        Map<Integer, Long> distribution = new HashMap<>();
        
        for (Object[] data : ratingData) {
            Integer rating = (Integer) data[0];
            Long count = (Long) data[1];
            distribution.put(rating, count);
        }
        
        // Fill in missing ratings with 0
        for (int i = 1; i <= 5; i++) {
            if (!distribution.containsKey(i)) {
                distribution.put(i, 0L);
            }
        }
        
        analysis.put("ratingDistribution", distribution);
        
        // Calculate total approved reviews
        Long totalApproved = distribution.values().stream().mapToLong(Long::longValue).sum();
        analysis.put("totalApprovedReviews", totalApproved);
        
        // Calculate weighted average
        double weightedSum = 0;
        long totalCount = 0;
        for (Map.Entry<Integer, Long> entry : distribution.entrySet()) {
            weightedSum += entry.getKey() * entry.getValue();
            totalCount += entry.getValue();
        }
        
        double weightedAverage = totalCount > 0 ? weightedSum / totalCount : 0;
        analysis.put("weightedAverage", Math.round(weightedAverage * 10.0) / 10.0);
        
        // Calculate percentages
        Map<Integer, Double> percentages = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = distribution.getOrDefault(i, 0L);
            double percentage = totalCount > 0 ? (double) count / totalCount * 100.0 : 0.0;
            percentages.put(i, Math.round(percentage * 10.0) / 10.0);
        }
        analysis.put("ratingPercentages", percentages);
        
        return analysis;
    }
}
