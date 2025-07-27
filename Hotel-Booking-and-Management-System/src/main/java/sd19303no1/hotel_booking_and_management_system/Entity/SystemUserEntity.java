package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_users")
public class SystemUserEntity {

    public enum Role {
        ADMIN, PARTNER, STAFF, CUSTOMER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "img", length = 255)
    private String img;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)   
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Quan hệ 1-1 với PartnerEntity (mappedBy trong PartnerEntity)
    @OneToOne(mappedBy = "systemUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PartnerEntity partner;

    // Quan hệ 1-1 với CustomersEntity (mappedBy trong CustomersEntity)
    @OneToOne(mappedBy = "systemUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CustomersEntity customer;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

    public CustomersEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomersEntity customer) {
        this.customer = customer;
    }

    // Helper methods
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
    
    public boolean isPartner() {
        return this.role == Role.PARTNER;
    }
    
    public boolean isStaff() {
        return this.role == Role.STAFF;
    }
    
    public boolean isCustomer() {
        return this.role == Role.CUSTOMER;
    }
}
