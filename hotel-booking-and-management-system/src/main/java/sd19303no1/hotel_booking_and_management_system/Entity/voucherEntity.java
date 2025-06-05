package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "voucher")
public class VoucherEntity {

    @Id
    @Column(name = "voucher_id")
    private Integer voucherId;

    @Column(name = "discount", precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", length = 50)
    private String status;

}
