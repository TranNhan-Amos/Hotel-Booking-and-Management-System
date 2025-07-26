package sd19303no1.hotel_booking_and_management_system.Entity;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class CustomersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "password")
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "cccd")
    private String cccd;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "created_date")
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Column(name = "status")
    private String status;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private SystemUserEntity systemUser;

    // Cập nhật getter/setter
    public SystemUserEntity getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUserEntity systemUser) {
        this.systemUser = systemUser;
    }
    // Getters and Setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        // Đồng bộ với SystemUserEntity nếu có
        if (this.systemUser != null) {
            this.systemUser.setPassword(password);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        // Đồng bộ với SystemUserEntity nếu có
        if (this.systemUser != null) {
            this.systemUser.setUsername(name);
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        // Đồng bộ với SystemUserEntity nếu có
        if (this.systemUser != null) {
            this.systemUser.setEmail(email);
        }
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}