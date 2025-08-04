package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import sd19303no1.hotel_booking_and_management_system.Service.ReviewService;
import sd19303no1.hotel_booking_and_management_system.DTO.WebsiteStatsDTO;

import java.util.List;

@Controller
public class EvaluateController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/admin/Evaluate")
    public String viewEvaluatePage(Model model,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "12") int size,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) Integer rating) {
        
        // Lấy thống kê tổng quan website
        WebsiteStatsDTO websiteStats = reviewService.getWebsiteStats();
        model.addAttribute("websiteStats", websiteStats);

        // Lấy danh sách đánh giá với phân trang và filter
        List<ReviewEntity> reviews;
        int totalReviews = 0;
        int totalPages = 0;
        int startIndex = 0;
        int endIndex = 0;

        if (search != null && !search.trim().isEmpty()) {
            // Tìm kiếm theo từ khóa
            reviews = reviewService.searchReviews(search.trim(), page - 1, size);
            totalReviews = reviewService.getTotalReviewsBySearch(search.trim());
        } else if (rating != null && rating > 0) {
            // Lọc theo rating
            reviews = reviewService.getReviewsByRating(rating, page - 1, size);
            totalReviews = reviewService.getTotalReviewsByRating(rating);
        } else {
            // Lấy tất cả đánh giá
            reviews = reviewService.getAllReviewsForAdmin(page - 1, size);
            totalReviews = reviewService.getTotalReviews();
        }

        // Tính toán thông tin phân trang
        totalPages = (int) Math.ceil((double) totalReviews / size);
        startIndex = (page - 1) * size + 1;
        endIndex = Math.min(page * size, totalReviews);

        model.addAttribute("reviews", reviews);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("startIndex", startIndex);
        model.addAttribute("endIndex", endIndex);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        model.addAttribute("rating", rating);

        return "Admin/Evaluate";
    }

    @PostMapping("/admin/reviews/reply")
    @ResponseBody
    public String replyToReview(@RequestParam Integer reviewId, 
                               @RequestParam String response) {
        try {
            reviewService.replyToReview(reviewId, response);
            return "Trả lời đánh giá thành công!";
        } catch (Exception e) {
            return "Có lỗi xảy ra: " + e.getMessage();
        }
    }
}