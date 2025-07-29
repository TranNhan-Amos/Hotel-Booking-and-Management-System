package sd19303no1.hotel_booking_and_management_system.DTO;

import java.util.Date;

public class PartnerDTO {
    private Long partnerId;
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private String contactPerson;
    private String taxId;
    private String businessLicense;
    private String businessmodel;
    private String hotelamenities;
    private String status;
    private Date createdDate;
    private int roomCount;
    private double totalRevenue;
    private double averageRating;

    // Getters and setters
    public Long getPartnerId() { return partnerId; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getBusinessLicense() { return businessLicense; }
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }
    public String getBusinessmodel() { return businessmodel; }
    public void setBusinessmodel(String businessmodel) { this.businessmodel = businessmodel; }
    public String getHotelamenities() { return hotelamenities; }
    public void setHotelamenities(String hotelamenities) { this.hotelamenities = hotelamenities; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public int getRoomCount() { return roomCount; }
    public void setRoomCount(int roomCount) { this.roomCount = roomCount; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
} 