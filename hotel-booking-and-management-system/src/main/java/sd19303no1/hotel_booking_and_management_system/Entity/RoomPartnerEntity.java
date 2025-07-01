package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class RoomPartnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", unique = true, nullable = false)
    private Long  roomId;

    @Column(name = "bathroom_type")
    private int bathroomType;

    @Column(name = "bed_count")
    private Integer bedCount;

    @Column(name = "bed_type")
    private String bedType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "floor")
    private int  floor;

    @Column(name = "is_smoking")
    private Boolean isSmoking;

    @Column(name = "price")
    private int price;

    @Column(name = "room_number", unique = true)
    private String roomNumber;

    @Column(name = "status")
    private String status; 

    @Column(name = "type")
    private String type; 

    @Column(name = "view")
    private String view; 

    @Column(name = "partner_id")
    private Long partnerId;
}
