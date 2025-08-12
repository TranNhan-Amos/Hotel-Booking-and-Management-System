package sd19303no1.hotel_booking_and_management_system.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyReportDTO {
    private LocalDate date;
    private Long totalBookings;
    private BigDecimal totalRevenue;
    private Double occupancyRate;
    private Long newCustomers;

    public DailyReportDTO() {
    }

    public DailyReportDTO(LocalDate date, Long totalBookings, BigDecimal totalRevenue, Double occupancyRate, Long newCustomers) {
        this.date = date;
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
        this.occupancyRate = occupancyRate;
        this.newCustomers = newCustomers;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public Long getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(Long newCustomers) {
        this.newCustomers = newCustomers;
    }
} 