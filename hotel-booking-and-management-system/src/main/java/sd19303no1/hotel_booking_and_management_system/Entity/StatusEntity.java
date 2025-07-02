package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status")
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "status_name")
    private String statusName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public enum Status {
        PENDING,        // Chờ xác nhận
        CONFIRMED,      // Đã xác nhận
        CANCELLED,      // Đã hủy
        COMPLETED,      // Hoàn thành
        CHECKED_IN,     // Đã check-in
        CHECKED_OUT,    // Đã check-out
        NO_SHOW,        // Không đến
        REFUNDED        // Đã hoàn tiền
    }

    // Getters and Setters
    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
