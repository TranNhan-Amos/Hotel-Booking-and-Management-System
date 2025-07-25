package sd19303no1.hotel_booking_and_management_system.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportDTO {
    private LocalDate date;
    private Integer totalBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenue;
    private Double occupancyRate;
    private Integer newCustomers;
    private Integer completedBookings;
    private Integer cancelledBookings;
    private BigDecimal totalDiscount;
    private BigDecimal netRevenue;
    
    // Constructor
    public ReportDTO() {}
    
    public ReportDTO(LocalDate date, Integer totalBookings, BigDecimal totalRevenue, 
                    BigDecimal averageRevenue, Double occupancyRate, Integer newCustomers,
                    Integer completedBookings, Integer cancelledBookings, 
                    BigDecimal totalDiscount, BigDecimal netRevenue) {
        this.date = date;
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
        this.averageRevenue = averageRevenue;
        this.occupancyRate = occupancyRate;
        this.newCustomers = newCustomers;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        this.totalDiscount = totalDiscount;
        this.netRevenue = netRevenue;
    }
    
    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public Integer getTotalBookings() { return totalBookings; }
    public void setTotalBookings(Integer totalBookings) { this.totalBookings = totalBookings; }
    
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public BigDecimal getAverageRevenue() { return averageRevenue; }
    public void setAverageRevenue(BigDecimal averageRevenue) { this.averageRevenue = averageRevenue; }
    
    public Double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(Double occupancyRate) { this.occupancyRate = occupancyRate; }
    
    public Integer getNewCustomers() { return newCustomers; }
    public void setNewCustomers(Integer newCustomers) { this.newCustomers = newCustomers; }
    
    public Integer getCompletedBookings() { return completedBookings; }
    public void setCompletedBookings(Integer completedBookings) { this.completedBookings = completedBookings; }
    
    public Integer getCancelledBookings() { return cancelledBookings; }
    public void setCancelledBookings(Integer cancelledBookings) { this.cancelledBookings = cancelledBookings; }
    
    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigDecimal totalDiscount) { this.totalDiscount = totalDiscount; }
    
    public BigDecimal getNetRevenue() { return netRevenue; }
    public void setNetRevenue(BigDecimal netRevenue) { this.netRevenue = netRevenue; }
} 