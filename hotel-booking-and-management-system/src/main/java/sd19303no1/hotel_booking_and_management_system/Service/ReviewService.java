package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.ReviewRepository;

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
}
