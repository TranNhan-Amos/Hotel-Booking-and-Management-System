package sd19303no1.hotel_booking_and_management_system.DTO;

import java.time.LocalDate;

public class AdminBookingRequestDTO {

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private Integer customerId; // For linking to existing customer if admin knows ID

    private Integer roomId;
    private Integer roomQuantity;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer voucherId;
    private String bookingStatus; // Admin can set this directly

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public Integer getRoomQuantity() { return roomQuantity; }
    public void setRoomQuantity(Integer roomQuantity) { this.roomQuantity = roomQuantity; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public Integer getVoucherId() { return voucherId; }
    public void setVoucherId(Integer voucherId) { this.voucherId = voucherId; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
}