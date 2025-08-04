package sd19303no1.hotel_booking_and_management_system.API;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import sd19303no1.hotel_booking_and_management_system.DTO.MonthlyRevenueReportPartnerDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.ReportsPartnerDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;

@RestController
@RequestMapping("/api/partner/reports")
public class PartnerReportsApiController {

    @Autowired
    private BookingOrderService bookingOrderService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private SystemUserService systemUserService;

    // Lấy tổng quan báo cáo cho đối tác (commission 80%)
    @PostMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPartnerSummary(@RequestParam String startDate, 
                                                                 @RequestParam String endDate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser == null || !systemUser.isPartner()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không có quyền truy cập"));
            }

            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            if (partner == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin đối tác"));
            }

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // Lấy dữ liệu tổng quan
            List<Object[]> reportData = bookingOrderService.getReportData(partner.getId(), start, end);
            
            BigDecimal totalRevenue = BigDecimal.ZERO;
            Long totalBookings = 0L;
            Long newCustomers = 0L;
            Double occupancyRate = 0.0;

            if (reportData != null && !reportData.isEmpty() && reportData.get(0)[0] != null) {
                totalRevenue = (BigDecimal) reportData.get(0)[0];
                totalBookings = ((Number) reportData.get(0)[1]).longValue();
            }

            // Tính commission 80% cho đối tác
            BigDecimal commission = totalRevenue.multiply(new BigDecimal("0.8"));

            Map<String, Object> summary = Map.of(
                "totalRevenue", totalRevenue,
                "commission", commission,
                "totalBookings", totalBookings,
                "newCustomers", newCustomers,
                "occupancyRate", occupancyRate
            );

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi khi tải dữ liệu tổng quan"));
        }
    }

    // Lấy báo cáo chi tiết theo ngày
    @PostMapping("/daily")
    public ResponseEntity<List<Map<String, Object>>> getDailyReports(@RequestParam String startDate, 
                                                                     @RequestParam String endDate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser == null || !systemUser.isPartner()) {
                return ResponseEntity.badRequest().body(null);
            }

            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            if (partner == null) {
                return ResponseEntity.badRequest().body(null);
            }

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // Lấy dữ liệu chi tiết theo ngày
            Map<LocalDate, ReportsPartnerDTO> dailyReports = bookingOrderService.getDailyBookingCount(partner.getId(), start, end);
            
            List<Map<String, Object>> result = dailyReports.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    ReportsPartnerDTO data = entry.getValue();
                    
                    // Tính commission 80%
                    BigDecimal commission = data.getTotalAmount().multiply(new BigDecimal("0.8"));
                    BigDecimal avgCommission = data.getCount() > 0 ? 
                        commission.divide(new BigDecimal(data.getCount()), 2, BigDecimal.ROUND_HALF_UP) : 
                        BigDecimal.ZERO;

                    return Map.<String, Object>of(
                        "date", date.toString(),
                        "totalBookings", data.getCount(),
                        "bookingValue", data.getTotalAmount(),
                        "commission", commission,
                        "avgCommission", avgCommission,
                        "newCustomers", 0,
                        "occupancyRate", 0.0
                    );
                })
                .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Lấy top khách hàng
    @PostMapping("/top-customers")
    public ResponseEntity<List<Map<String, Object>>> getTopCustomers(@RequestParam String startDate, 
                                                                     @RequestParam String endDate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser == null || !systemUser.isPartner()) {
                return ResponseEntity.badRequest().body(null);
            }

            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            if (partner == null) {
                return ResponseEntity.badRequest().body(null);
            }

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // Lấy top khách hàng từ repository
            List<Map<String, Object>> topCustomers = bookingOrderService.getTopCustomers(partner.getId(), start, end);
            
            // Tính commission 80% cho mỗi khách hàng
            List<Map<String, Object>> result = topCustomers.stream()
                .map(customer -> {
                    BigDecimal totalSpent = (BigDecimal) customer.get("totalSpent");
                    BigDecimal commission = totalSpent.multiply(new BigDecimal("0.8"));
                    BigDecimal avgCommission = ((Number) customer.get("bookingCount")).longValue() > 0 ? 
                        commission.divide(new BigDecimal(((Number) customer.get("bookingCount")).longValue()), 2, BigDecimal.ROUND_HALF_UP) : 
                        BigDecimal.ZERO;

                    return Map.<String, Object>of(
                        "customerName", customer.get("customerName"),
                        "customerEmail", customer.get("customerEmail"),
                        "customerPhone", "N/A",
                        "bookingCount", customer.get("bookingCount"),
                        "totalValue", totalSpent,
                        "commission", commission,
                        "avgCommission", avgCommission,
                        "lastBookingDate", null
                    );
                })
                .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Lấy doanh thu theo tháng (commission 80%)
    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueReportPartnerDTO>> getMonthlyRevenue(@RequestParam Integer year) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser == null || !systemUser.isPartner()) {
                return ResponseEntity.badRequest().body(null);
            }

            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            if (partner == null) {
                return ResponseEntity.badRequest().body(null);
            }

            List<MonthlyRevenueReportPartnerDTO> monthlyRevenue = bookingOrderService.getMonthlyRevenueReportPartner(partner.getId(), year);
            
            // Tính commission 80% cho mỗi tháng
            List<MonthlyRevenueReportPartnerDTO> result = monthlyRevenue.stream()
                .map(data -> {
                    BigDecimal commission = data.getTotalRevenue().multiply(new BigDecimal("0.8"));
                    return new MonthlyRevenueReportPartnerDTO(data.getMonth(), commission);
                })
                .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
} 