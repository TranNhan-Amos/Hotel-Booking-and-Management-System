package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "review")
public class ReviewEntity {

    @Id
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomersEntity customer;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "review_date")
    private LocalDate reviewDate;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    @Column(name = "response_date")
    private LocalDate responseDate;

    @ManyToOne
    @JoinColumn(name = "responder_id")
    private SystemUserEntity responder;

    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public CustomersEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomersEntity customer) {
        this.customer = customer;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDate getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDate responseDate) {
        this.responseDate = responseDate;
    }

    public SystemUserEntity getResponder() {
        return responder;
    }

    public void setResponder(SystemUserEntity responder) {
        this.responder = responder;
    }
}
