package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "room")
public class RoomEntity {

    @Id
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "type")
    private String type;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "status")
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}

