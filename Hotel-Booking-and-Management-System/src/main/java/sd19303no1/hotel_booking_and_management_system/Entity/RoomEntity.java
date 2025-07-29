package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "rooms")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_number", nullable = true, unique = false, length = 20)
    @Size(max = 10, message = "Mã phòng không được quá 10 ký tự")
    private String roomNumber; // mã phòng (VD: T110PRX, CPAWTF, ...)

    @Column(name = "name_number", columnDefinition = "TEXT")
    @NotBlank(message = "Tên phòng không được để trống")
    private String nameNumber; // Tên phòng

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    @NotNull(message = "Loại phòng không được để trống")
    private RoomType type; // Loại phòng: VIP, STANDARD, DELUXE, FAMILY...

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Giá phòng không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phòng phải lớn hơn 0")
    @DecimalMax(value = "99999999.99", message = "Giá phòng không được vượt quá 99,999,999.99")
    private BigDecimal price; // Giá phòng/đêm

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Trạng thái phòng không được để trống")
    private RoomStatus status = RoomStatus.AVAILABLE; // Trạng thái phòng

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 10000, message = "Mô tả không được quá 10000 ký tự")
    private String description; // Mô tả chi tiết phòng

    @ElementCollection
    @CollectionTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "amenity")
    private List<String> amenities; // Danh sách tiện nghi: WIFI, TV, AIR_CONDITIONER...

    @ElementCollection
    @CollectionTable(name = "room_images", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "image_url")
    private List<String> imageUrls; // Danh sách URL hình ảnh

    @Column(name = "capacity", nullable = false)
    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa phải từ 1 người trở lên")
    @Max(value = 10, message = "Sức chứa không được vượt quá 10 người")
    private Integer capacity; // Sức chứa tối đa

    @Column(name = "service", length = 10000)
    @Size(max = 10000, message = "Dịch vụ không được quá 10000 ký tự")
    private String service; // Service: Có bữa sáng tự chọn, có xe đưa đón, có bãi đậu xe, có nhân viên hỗ trợ...

    @Enumerated(EnumType.STRING)
    @Column(name = "view", length = 500)
    private ViewType view; // View: SEA_VIEW, CITY_VIEW, GARDEN_VIEW, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", length = 500)
    private BedType bedType; // Loại giường: SINGLE_BED, DOUBLE_BED, KING_BED, TWIN_BEDS

    @Enumerated(EnumType.STRING)
    @Column(name = "bathroom_type", length = 500)
    private BathroomType bathroomType; // Loại phòng tắm: SHOWER, BATHTUB, COMBINATION

    @Column(name = "total_rooms", nullable = false)
    @NotNull(message = "Tổng số phòng không được để trống")
    @Min(value = 1, message = "Tổng số phòng phải từ 1 trở lên")
    private Integer totalRooms; // Tổng số phòng của loại này

    @Column(name = "is_smoking", nullable = false)
    private Boolean isSmoking = false; // Cho phép hút thuốc

    @Column(name = "is_pet_friendly", nullable = false)
    private Boolean isPetFriendly = false; // Cho phép mang thú cưng

    @Column(name = "icon_path", length = 255)
    private String iconPath; // Đường dẫn đến icon tùy chỉnh

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // Quan hệ với đối tác (partner) nếu phòng do đối tác quản lý
    @ManyToOne
    @JoinColumn(name = "partner_id")
    private PartnerEntity partner;

    // Quan hệ 1-n với BookingOrderEntity (một phòng có nhiều booking)
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingOrderEntity> bookings;

    // Quan hệ 1-n với ReviewEntity (một phòng có nhiều review)
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    // Transient field for average rating
    @Transient
    private Double averageRating;

    // Constructors
    public RoomEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getNameNumber() {
        return nameNumber;
    }

    public void setNameNumber(String nameNumber) {
        this.nameNumber = nameNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public ViewType getView() {
        return view;
    }

    public void setView(ViewType view) {
        this.view = view;
    }

    public BedType getBedType() {
        return bedType;
    }

    public void setBedType(BedType bedType) {
        this.bedType = bedType;
    }

    public BathroomType getBathroomType() {
        return bathroomType;
    }

    public void setBathroomType(BathroomType bathroomType) {
        this.bathroomType = bathroomType;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Boolean getIsSmoking() {
        return isSmoking;
    }

    public void setIsSmoking(Boolean isSmoking) {
        this.isSmoking = isSmoking;
    }

    public Boolean getIsPetFriendly() {
        return isPetFriendly;
    }

    public void setIsPetFriendly(Boolean isPetFriendly) {
        this.isPetFriendly = isPetFriendly;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    public List<BookingOrderEntity> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingOrderEntity> bookings) {
        this.bookings = bookings;
    }

    public List<ReviewEntity> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewEntity> reviews) {
        this.reviews = reviews;
    }

    // Business methods
    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE && totalRooms > 0;
    }

    public boolean canBook(Integer quantity) {
        return isAvailable() && totalRooms >= quantity;
    }

    public void bookRoom(Integer quantity) {
        if (canBook(quantity)) {
            totalRooms -= quantity;
            if (totalRooms == 0) {
                status = RoomStatus.FULL;
            }
            updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Không đủ phòng để đặt");
        }
    }

    public void releaseRoom(Integer quantity) {
        totalRooms += quantity;
        if (status == RoomStatus.FULL && totalRooms > 0) {
            status = RoomStatus.AVAILABLE;
        }
        updatedAt = LocalDateTime.now();
    }

    // Icon utility methods
    public String getDisplayIcon() {
        // Ưu tiên icon tùy chỉnh, nếu không có thì dùng icon từ enum
        if (iconPath != null && !iconPath.trim().isEmpty()) {
            return iconPath;
        }
        return type != null ? type.getIconClass() : "fas fa-home";
    }

    public String getStatusIcon() {
        return status != null ? status.getIconClass() : "fas fa-question-circle";
    }

    public String getViewIcon() {
        return view != null ? view.getIconClass() : "fas fa-eye";
    }

    public String getBedTypeIcon() {
        return bedType != null ? bedType.getIconClass() : "fas fa-bed";
    }

    public String getBathroomTypeIcon() {
        return bathroomType != null ? bathroomType.getIconClass() : "fas fa-bath";
    }

    // Override methods
    @Override
    public String toString() {
        return "RoomEntity{" +
                "roomId=" + roomId +
                ", roomNumber='" + roomNumber + '\'' +
                ", nameNumber='" + nameNumber + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", status=" + status +
                ", capacity=" + capacity +
                ", totalRooms=" + totalRooms +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomEntity that = (RoomEntity) o;
        return Objects.equals(roomId, that.roomId) &&
                Objects.equals(roomNumber, that.roomNumber) &&
                Objects.equals(nameNumber, that.nameNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, roomNumber, nameNumber);
    }

    // Enum cho loại phòng
    public enum RoomType {
        SINGLE("Phòng đơn", "fas fa-bed"),
        DOUBLE("Phòng đôi", "fas fa-bed"),
        TRIPLE("Phòng ba", "fas fa-bed"),
        QUAD("Phòng bốn", "fas fa-bed"),
        SUITE("Phòng sang trọng", "fas fa-crown"),
        VIP("Phòng VIP", "fas fa-star"),
        STANDARD("Phòng tiêu chuẩn", "fas fa-home"),
        DELUXE("Phòng cao cấp", "fas fa-gem"),
        FAMILY("Phòng gia đình", "fas fa-users");

        private final String description;
        private final String iconClass;

        RoomType(String description, String iconClass) {
            this.description = description;
            this.iconClass = iconClass;
        }

        public String getDescription() {
            return description;
        }

        public String getIconClass() {
            return iconClass;
        }
    }

    // Enum cho trạng thái phòng
    public enum RoomStatus {
        AVAILABLE("Còn trống", "fas fa-check-circle text-success"),
        OCCUPIED("Đã có khách", "fas fa-user text-warning"),
        MAINTENANCE("Đang bảo trì", "fas fa-tools text-danger"),
        CLEANING("Đang dọn dẹp", "fas fa-broom text-info"),
        RESERVED("Đã được đặt trước", "fas fa-calendar-check text-primary"),
        FULL("Hết phòng", "fas fa-times-circle text-danger");

        private final String description;
        private final String iconClass;

        RoomStatus(String description, String iconClass) {
            this.description = description;
            this.iconClass = iconClass;
        }

        public String getDescription() {
            return description;
        }

        public String getIconClass() {
            return iconClass;
        }
    }

    // Enum cho loại giường
    public enum BedType {
        SINGLE_BED("Giường đơn", "fas fa-bed"),
        DOUBLE_BED("Giường đôi", "fas fa-bed"),
        KING_BED("Giường King", "fas fa-bed"),
        TWIN_BEDS("Giường đôi Twin", "fas fa-bed"),
        BUNK_BED("Giường tầng", "fas fa-bed");

        private final String description;
        private final String iconClass;

        BedType(String description, String iconClass) {
            this.description = description;
            this.iconClass = iconClass;
        }

        public String getDescription() {
            return description;
        }

        public String getIconClass() {
            return iconClass;
        }
    }

    // Enum cho loại phòng tắm
    public enum BathroomType {
        SHOWER("Vòi sen", "fas fa-shower"),
        BATHTUB("Bồn tắm", "fas fa-bath"),
        COMBINATION("Kết hợp", "fas fa-bath");

        private final String description;
        private final String iconClass;

        BathroomType(String description, String iconClass) {
            this.description = description;
            this.iconClass = iconClass;
        }

        public String getDescription() {
            return description;
        }

        public String getIconClass() {
            return iconClass;
        }
    }

    // Enum cho hướng nhìn
    public enum ViewType {
        SEA_VIEW("Hướng biển", "fas fa-water"),
        CITY_VIEW("Hướng thành phố", "fas fa-city"),
        GARDEN_VIEW("Hướng vườn", "fas fa-seedling"),
        POOL_VIEW("Hướng hồ bơi", "fas fa-swimming-pool"),
        MOUNTAIN_VIEW("Hướng núi", "fas fa-mountain");

        private final String description;
        private final String iconClass;

        ViewType(String description, String iconClass) {
            this.description = description;
            this.iconClass = iconClass;
        }

        public String getDescription() {
            return description;
        }

        public String getIconClass() {
            return iconClass;
        }
    }

    // Enum cho tiện nghi
    public enum Amenity {
        WIFI("WiFi miễn phí", "fas fa-wifi"),
        TV("TV màn hình phẳng", "fas fa-tv"),
        AIR_CONDITIONER("Điều hòa", "fas fa-snowflake"),
        MINIBAR("Minibar", "fas fa-glass-martini"),
        SAFE("Két an toàn", "fas fa-shield-alt"),
        HAIR_DRYER("Máy sấy tóc", "fas fa-wind"),
        WORK_DESK("Bàn làm việc", "fas fa-desktop"),
        COFFEE_MAKER("Máy pha cà phê", "fas fa-coffee"),
        REFRIGERATOR("Tủ lạnh", "fas fa-igloo"),
        BALCONY("Ban công", "fas fa-door-open"),
        ROOM_SERVICE("Dịch vụ phòng", "fas fa-concierge-bell"),
        BREAKFAST_INCLUDED("Bữa sáng miễn phí", "fas fa-utensils");

        private final String description;
        private final String iconClass;

        Amenity(String description, String iconClass) {
            this.description = description;
            this.iconClass = iconClass;
        }

        public String getDescription() {
            return description;
        }

        public String getIconClass() {
            return iconClass;
        }
    }
    
}