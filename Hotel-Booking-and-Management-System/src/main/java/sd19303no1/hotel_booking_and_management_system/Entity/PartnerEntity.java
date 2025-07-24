package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "partners")
public class PartnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "companyName")
    private String companyName;

    @Column(name = "taxId")
    private String taxId; // Mã số thuế

    @Column(name = "address")
    private String address;

    @Column(name = "contactPerson")
    private String contactPerson; // Người liên hệ

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "businessLicense")
    private String businessLicense; // Số giấy phép kinh doanh

    @Column(name = "businessmodel")
    private String businessmodel; // mô hình kinh doanh

    @Column(name = "hotelamenities")
    private String hotelamenities; 
    // Quan hệ 1-1 với SystemUserEntity
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private SystemUserEntity systemUser;


}