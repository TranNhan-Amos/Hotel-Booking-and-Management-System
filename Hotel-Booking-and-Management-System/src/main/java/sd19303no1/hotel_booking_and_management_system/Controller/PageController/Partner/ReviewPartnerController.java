package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Partner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.PartnerRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.ReviewRepository;
import sd19303no1.hotel_booking_and_management_system.Service.ReviewService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ReviewPartnerController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PartnerRepository partnerRepository;

    @GetMapping("/partner/reviews")
    public String viewPartnerReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer rating,
            Model model) {

        // Lấy thông tin partner đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        PartnerEntity partner = partnerRepository.findByEmail(userEmail);
        if (partner == null) {
            return "redirect:/login";
        }
        Long partnerId = partner.getId();

        // Lấy danh sách đánh giá cho các phòng của partner
        List<ReviewEntity> reviews;
        long totalReviews;
        
        if (search != null && !search.trim().isEmpty()) {
            // Tìm kiếm theo từ khóa
            reviews = reviewRepository.findReviewsBySearchTermForPartner(
                search.trim(), partnerId, PageRequest.of(page, size));
            totalReviews = reviewRepository.countReviewsBySearchTermForPartner(search.trim(), partnerId);
        } else if (rating != null) {
            // Lọc theo rating
            reviews = reviewRepository.findReviewsByRatingForPartner(
                rating, partnerId, PageRequest.of(page, size));
            totalReviews = reviewRepository.countReviewsByRatingForPartner(rating, partnerId);
        } else {
            // Lấy tất cả đánh giá
            reviews = reviewRepository.findReviewsByPartnerId(
                partnerId, PageRequest.of(page, size));
            totalReviews = reviewRepository.countReviewsByPartnerId(partnerId);
        }

        // Tính toán thống kê
        Map<String, Object> stats = calculateReviewStats(partnerId);
        
        // Tính toán phân bố rating
        Map<String, Object> ratingAnalysis = calculateRatingDistribution(partnerId);

        // Pagination
        int totalPages = (int) Math.ceil((double) totalReviews / size);
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("search", search);
        model.addAttribute("selectedRating", rating);
        model.addAttribute("stats", stats);
        model.addAttribute("ratingAnalysis", ratingAnalysis);
        model.addAttribute("partner", partner);

        return "Partner/ReviewsPartner";
    }

    @PostMapping("/partner/reviews/{reviewId}/respond")
    @ResponseBody
    public Map<String, Object> respondToReview(
            @PathVariable Integer reviewId,
            @RequestParam String response) {

        Map<String, Object> result = new HashMap<>();
        
        try {
            // Lấy thông tin partner đang đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            PartnerEntity partner = partnerRepository.findByEmail(userEmail);
            if (partner == null) {
                result.put("success", false);
                result.put("message", "Không tìm thấy thông tin đối tác");
                return result;
            }

            // Tìm review
            Optional<ReviewEntity> reviewOpt = reviewRepository.findById(reviewId);
            if (reviewOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "Không tìm thấy đánh giá");
                return result;
            }

            ReviewEntity review = reviewOpt.get();
            
            // Kiểm tra xem review có thuộc về partner này không
            if (!review.getRoom().getPartner().getId().equals(partner.getId())) {
                result.put("success", false);
                result.put("message", "Bạn không có quyền phản hồi đánh giá này");
                return result;
            }

            // Cập nhật phản hồi
            review.setResponse(response);
            review.setResponseDate(LocalDate.now());
            review.setResponder(partner.getSystemUser());
            
            reviewRepository.save(review);

            result.put("success", true);
            result.put("message", "Phản hồi đã được lưu thành công");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/api/partner/reviews/stats")
    @ResponseBody
    public Map<String, Object> getReviewStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        PartnerEntity partner = partnerRepository.findByEmail(userEmail);
        if (partner == null) {
            return new HashMap<>();
        }

        Long partnerId = partner.getId();
        Map<String, Object> stats = calculateReviewStats(partnerId);
        stats.put("ratingDistribution", calculateRatingDistribution(partnerId));
        
        return stats;
    }

    private Map<String, Object> calculateReviewStats(Long partnerId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Tổng số đánh giá
        long totalReviews = reviewRepository.countReviewsByPartnerId(partnerId);
        stats.put("totalReviews", totalReviews);
        
        // Đánh giá trung bình
        Double avgRating = reviewRepository.getAverageRatingByPartnerId(partnerId);
        stats.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        
        // Đánh giá tháng này
        long thisMonthReviews = reviewRepository.countReviewsThisMonthByPartnerId(partnerId);
        stats.put("thisMonthReviews", thisMonthReviews);
        
        // Cần phản hồi
        long pendingResponses = reviewRepository.countPendingReviewsByPartnerId(partnerId);
        stats.put("pendingResponses", pendingResponses);
        
        return stats;
    }

    private Map<String, Object> calculateRatingDistribution(Long partnerId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Lấy phân bố rating từ database
        List<Object[]> ratingData = reviewRepository.getRatingDistributionByPartnerId(partnerId);
        Map<Integer, Long> distribution = new HashMap<>();
        
        for (Object[] data : ratingData) {
            Integer rating = (Integer) data[0];
            Long count = (Long) data[1];
            distribution.put(rating, count);
        }
        
        // Điền các rating thiếu với 0
        for (int i = 1; i <= 5; i++) {
            if (!distribution.containsKey(i)) {
                distribution.put(i, 0L);
            }
        }
        
        analysis.put("ratingDistribution", distribution);
        
        // Tính tổng đánh giá
        Long totalReviews = distribution.values().stream().mapToLong(Long::longValue).sum();
        analysis.put("totalReviews", totalReviews);
        
        // Tính trung bình có trọng số
        double weightedSum = 0;
        long totalCount = 0;
        for (Map.Entry<Integer, Long> entry : distribution.entrySet()) {
            weightedSum += entry.getKey() * entry.getValue();
            totalCount += entry.getValue();
        }
        
        double weightedAverage = totalCount > 0 ? weightedSum / totalCount : 0;
        analysis.put("weightedAverage", Math.round(weightedAverage * 10.0) / 10.0);
        
        // Tính phần trăm
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