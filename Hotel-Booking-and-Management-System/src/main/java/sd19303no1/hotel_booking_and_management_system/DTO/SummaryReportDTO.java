package sd19303no1.hotel_booking_and_management_system.DTO;

import java.math.BigDecimal;

public class SummaryReportDTO {
    private BigDecimal totalRevenue;
    private Long totalBookings;
    private Long newCustomers;
    private Double occupancyRate;

    public SummaryReportDTO() {
    }

    public SummaryReportDTO(BigDecimal totalRevenue, Long totalBookings, Long newCustomers, Double occupancyRate) {
        this.totalRevenue = totalRevenue;
        this.totalBookings = totalBookings;
        this.newCustomers = newCustomers;
        this.occupancyRate = occupancyRate;
    }

    // Getters and Setters
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Long getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(Long newCustomers) {
        this.newCustomers = newCustomers;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }
} 