package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "room")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Thêm auto-increment
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

    @ElementCollection
    @CollectionTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    @ElementCollection
    @CollectionTable(name = "room_images", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
}

