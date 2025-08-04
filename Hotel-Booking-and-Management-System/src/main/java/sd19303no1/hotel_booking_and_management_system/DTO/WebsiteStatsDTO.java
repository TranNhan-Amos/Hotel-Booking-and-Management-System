package sd19303no1.hotel_booking_and_management_system.DTO;

import java.util.Map;

public class WebsiteStatsDTO {
    private Double overallAverageRating;
    private Long totalReviews;
    private Long positiveReviews;
    private Long negativeReviews;
    private Long neutralReviews;
    private Map<Integer, Long> ratingDistribution;
    private Map<Integer, Double> ratingPercentages;

    public WebsiteStatsDTO() {}

    public WebsiteStatsDTO(Double overallAverageRating, Long totalReviews, Long positiveReviews, 
                          Long negativeReviews, Long neutralReviews, Map<Integer, Long> ratingDistribution, 
                          Map<Integer, Double> ratingPercentages) {
        this.overallAverageRating = overallAverageRating;
        this.totalReviews = totalReviews;
        this.positiveReviews = positiveReviews;
        this.negativeReviews = negativeReviews;
        this.neutralReviews = neutralReviews;
        this.ratingDistribution = ratingDistribution;
        this.ratingPercentages = ratingPercentages;
    }

    // Getters and Setters
    public Double getOverallAverageRating() {
        return overallAverageRating;
    }

    public void setOverallAverageRating(Double overallAverageRating) {
        this.overallAverageRating = overallAverageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Long getPositiveReviews() {
        return positiveReviews;
    }

    public void setPositiveReviews(Long positiveReviews) {
        this.positiveReviews = positiveReviews;
    }

    public Long getNegativeReviews() {
        return negativeReviews;
    }

    public void setNegativeReviews(Long negativeReviews) {
        this.negativeReviews = negativeReviews;
    }

    public Long getNeutralReviews() {
        return neutralReviews;
    }

    public void setNeutralReviews(Long neutralReviews) {
        this.neutralReviews = neutralReviews;
    }

    public Map<Integer, Long> getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(Map<Integer, Long> ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }

    public Map<Integer, Double> getRatingPercentages() {
        return ratingPercentages;
    }

    public void setRatingPercentages(Map<Integer, Double> ratingPercentages) {
        this.ratingPercentages = ratingPercentages;
    }
} 