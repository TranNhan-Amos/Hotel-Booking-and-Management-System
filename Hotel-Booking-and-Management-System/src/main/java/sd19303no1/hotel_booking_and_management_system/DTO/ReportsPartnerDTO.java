package sd19303no1.hotel_booking_and_management_system.DTO;

import java.math.BigDecimal;

public class ReportsPartnerDTO {
     private Long count;
    private BigDecimal totalAmount;

    public ReportsPartnerDTO(Long count, BigDecimal totalAmount) {
        this.count = count;
        this.totalAmount = totalAmount;
    }

    public Long getCount() {
        return count;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
