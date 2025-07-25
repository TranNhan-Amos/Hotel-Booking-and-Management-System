package sd19303no1.hotel_booking_and_management_system.DTO;

import java.math.BigDecimal;

public class MonthlyRevenueReportPartnerDTO {
     private int month;
    private BigDecimal totalRevenue;

    public MonthlyRevenueReportPartnerDTO(int month, BigDecimal totalRevenue) {
        this.month = month;
        this.totalRevenue = totalRevenue;
    }

    public int getMonth() {
        return month;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
}
