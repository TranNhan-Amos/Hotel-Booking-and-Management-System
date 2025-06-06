package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status")
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Thêm auto-increment
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "status_name")
    private String statusName;

    public enum Status {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
}
