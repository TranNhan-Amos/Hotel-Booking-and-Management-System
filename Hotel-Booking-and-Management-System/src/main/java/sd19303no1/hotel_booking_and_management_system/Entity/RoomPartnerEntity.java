package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Entity
@Table(name = "room_partners")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class RoomPartnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", unique = true, nullable = false)
    private Long  roomId;

    @Column(name = "bathroom_type")
    private String bathroomType;

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

    @ElementCollection
    @CollectionTable(name = "room_partner_images", joinColumns = @JoinColumn(name = "room_partner_id"))
    @Column(name = "image_url")
    private List<String> imageUrls; // Danh sách URL hình ảnh
}
