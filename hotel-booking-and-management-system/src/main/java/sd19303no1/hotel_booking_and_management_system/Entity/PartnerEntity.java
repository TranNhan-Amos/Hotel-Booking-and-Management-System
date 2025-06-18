package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;

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
    private String hotelamenities; // mô hình kinh doanh

    // Quan hệ 1-1 với SystemUserEntity
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private SystemUserEntity systemUser;

    // Các getter và setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public SystemUserEntity getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUserEntity systemUser) {
        this.systemUser = systemUser;
    }

    public void setEmail(String userEmail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEmail'");
    }

    public void setBusinessmodel(String businessmodel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBusinessmodel'");
    }

    public void setHotelamenities(String hotelamenities2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setHotelamenities'");
    }
}