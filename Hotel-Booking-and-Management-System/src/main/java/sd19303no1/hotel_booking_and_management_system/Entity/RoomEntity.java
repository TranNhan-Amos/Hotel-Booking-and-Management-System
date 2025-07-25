package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rooms")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_number", nullable = true, unique = false, length = 20)
    private String roomNumber; // Số phòng (VD: P101, P102)

    @Column(name = "type", nullable = false, length = 50)
    private String type; // Loại phòng: SINGLE, DOUBLE, TRIPLE, QUAD, SUITE

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Giá phòng/đêm

    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE"; // Trạng thái: AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết phòng

    @ElementCollection
    @CollectionTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "amenity")
    private List<String> amenities; // Danh sách tiện nghi: WIFI, TV, AIR_CONDITIONER, etc.

    @ElementCollection
    @CollectionTable(name = "room_images", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "image_url")
    private List<String> imageUrls; // Danh sách URL hình ảnh

    @Column(name = "capacity", nullable = false)
    private Integer capacity; // Sức chứa tối đa

    @Column(name = "floor", nullable = false)
    private Integer floor; // Tầng đặt phòng

    @Column(name = "view", length = 50)
    private String view; // View: SEA_VIEW, CITY_VIEW, GARDEN_VIEW, etc.

    @Column(name = "bed_type", length = 50)
    private String bedType; // Loại giường: SINGLE_BED, DOUBLE_BED, KING_BED, TWIN_BEDS

    @Column(name = "bed_count")
    private Integer bedCount; // Số lượng giường

    @Column(name = "bathroom_type", length = 50)
    private String bathroomType; // Loại phòng tắm: SHOWER, BATHTUB, COMBINATION

    @Column(name = "is_smoking", nullable = false)
    private Boolean isSmoking = false; // Cho phép hút thuốc

    @Column(name = "total_rooms", nullable = false)
    private Integer totalRooms; // Tổng số phòng của loại này

    // Quan hệ với đối tác (partner) nếu phòng do đối tác quản lý
    @ManyToOne
    @JoinColumn(name = "partner_id")
    private PartnerEntity partner;

    // Transient field for average rating
    @Transient
    private Double averageRating;

    // Constructors
    public RoomEntity() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public String getBathroomType() {
        return bathroomType;
    }

    public void setBathroomType(String bathroomType) {
        this.bathroomType = bathroomType;
    }

    public Boolean getIsSmoking() {
        return isSmoking;
    }

    public void setIsSmoking(Boolean isSmoking) {
        this.isSmoking = isSmoking;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    // Enum cho loại phòng
    public enum RoomType {
        SINGLE, // Phòng đơn
        DOUBLE, // Phòng đôi
        TRIPLE, // Phòng ba
        QUAD,   // Phòng bốn
        SUITE   // Phòng sang trọng
    }

    // Enum cho trạng thái phòng
    public enum RoomStatus {
        AVAILABLE,   // Còn trống
        OCCUPIED,    // Đã có khách
        MAINTENANCE, // Đang bảo trì
        CLEANING,    // Đang dọn dẹp
        RESERVED     // Đã được đặt trước
    }

    // Enum cho loại giường
    public enum BedType {
        SINGLE_BED,
        DOUBLE_BED,
        KING_BED,
        TWIN_BEDS,
        BUNK_BED
    }

    // Enum cho loại phòng tắm
    public enum BathroomType {
        SHOWER,     // Vòi sen
        BATHTUB,    // Bồn tắm
        COMBINATION // Kết hợp
    }

    // Enum cho hướng nhìn
    public enum ViewType {
        SEA_VIEW,
        CITY_VIEW,
        GARDEN_VIEW,
        POOL_VIEW,
        MOUNTAIN_VIEW
    }

    // Enum cho tiện nghi
    public enum Amenity {
        WIFI,
        TV,
        AIR_CONDITIONER,
        MINIBAR,
        SAFE,
        HAIR_DRYER,
        WORK_DESK,
        COFFEE_MAKER,
        REFRIGERATOR,
        BALCONY,
        ROOM_SERVICE,
        BREAKFAST_INCLUDED
    }
}