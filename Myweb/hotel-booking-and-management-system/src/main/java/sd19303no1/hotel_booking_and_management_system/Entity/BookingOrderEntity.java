package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "bookingorder")
public class BookingOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private VoucherEntity voucher;

    @CreationTimestamp
    private LocalDateTime createdAt;
    // Sửa thành RoomEntity thay vì String
    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room; // Đã sửa kiểu dữ liệu ✅

    @Column(name = "email")
    private String email;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomersEntity customer;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusEntity status;

    // Thêm getters/setters và constructor nếu cần
}