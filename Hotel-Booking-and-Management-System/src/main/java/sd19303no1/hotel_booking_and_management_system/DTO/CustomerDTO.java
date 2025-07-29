package sd19303no1.hotel_booking_and_management_system.DTO;

import java.util.Date;

public class CustomerDTO {
    private Integer customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date createdDate;
    private String status;
    private int bookingCount;
    private double spending;
    private double rating;

    // Getters and setters
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getBookingCount() { return bookingCount; }
    public void setBookingCount(int bookingCount) { this.bookingCount = bookingCount; }
    public double getSpending() { return spending; }
    public void setSpending(double spending) { this.spending = spending; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
} 