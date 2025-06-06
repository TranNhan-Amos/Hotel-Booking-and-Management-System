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
    private RoomEntity room; // ✅ Sửa thành RoomEntity

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
    private SystemUserEntity responder; // ✅ Sửa thành SystemUserEntity
}
