package sd19303no1.hotel_booking_and_management_system.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.DTO.ReportDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;

@Service
public class ReportService {

    @Autowired
    private BookingOrderRepository bookingOrderRepository;
    
    @Autowired
    private CustomersRepository customersRepository;
    
    @Autowired
    private RoomRepository roomRepository;

    // Báo cáo tổng quan theo khoảng thời gian
    public ReportDTO getSummaryReport(LocalDate startDate, LocalDate endDate) {
        List<BookingOrderEntity> bookings = bookingOrderRepository.findByCreatedAtBetween(
            startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        if (bookings.isEmpty()) {
            return new ReportDTO(startDate, 0, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        BigDecimal totalRevenue = bookings.stream()
            .map(BookingOrderEntity::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageRevenue = totalRevenue.divide(BigDecimal.valueOf(bookings.size()), 2, RoundingMode.HALF_UP);
        
        long completedBookings = bookings.stream()
            .filter(b -> "COMPLETED".equals(b.getStatus().getStatusName()))
            .count();
        
        long cancelledBookings = bookings.stream()
            .filter(b -> "CANCELLED".equals(b.getStatus().getStatusName()))
            .count();
        
        // Tính tỷ lệ lấp đầy (giả định có 50 phòng)
        int totalRooms = 50;
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double occupancyRate = (double) completedBookings / (totalRooms * totalDays) * 100;
        
        // Khách hàng mới trong khoảng thời gian
        Date startDateConverted = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateConverted = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
        long newCustomers = customersRepository.countByCreatedDateBetween(startDateConverted, endDateConverted);
        
        return new ReportDTO(
            startDate,
            bookings.size(),
            totalRevenue,
            averageRevenue,
            occupancyRate,
            (int) newCustomers,
            (int) completedBookings,
            (int) cancelledBookings,
            BigDecimal.ZERO, // totalDiscount - cần tính từ voucher
            totalRevenue // netRevenue - tạm thời bằng totalRevenue
        );
    }

    // Báo cáo theo ngày
    public List<ReportDTO> getDailyReports(LocalDate startDate, LocalDate endDate) {
        List<ReportDTO> dailyReports = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            List<BookingOrderEntity> dayBookings = bookingOrderRepository.findByCreatedAtBetween(
                currentDate.atStartOfDay(), currentDate.atTime(23, 59, 59));
            
            BigDecimal dayRevenue = dayBookings.stream()
                .map(BookingOrderEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgRevenue = dayBookings.isEmpty() ? BigDecimal.ZERO :
                dayRevenue.divide(BigDecimal.valueOf(dayBookings.size()), 2, RoundingMode.HALF_UP);
            
            long completed = dayBookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus().getStatusName()))
                .count();
            
            long cancelled = dayBookings.stream()
                .filter(b -> "CANCELLED".equals(b.getStatus().getStatusName()))
                .count();
            
            // Tỷ lệ lấp đầy theo ngày
            double occupancyRate = (double) completed / 50 * 100; // Giả định 50 phòng
            
            Date currentDateConverted = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            long newCustomers = customersRepository.countByCreatedDate(currentDateConverted);
            
            dailyReports.add(new ReportDTO(
                currentDate,
                dayBookings.size(),
                dayRevenue,
                avgRevenue,
                occupancyRate,
                (int) newCustomers,
                (int) completed,
                (int) cancelled,
                BigDecimal.ZERO,
                dayRevenue
            ));
            
            currentDate = currentDate.plusDays(1);
        }
        
        return dailyReports;
    }

    // Báo cáo theo tuần
    public List<ReportDTO> getWeeklyReports(LocalDate startDate, LocalDate endDate) {
        List<ReportDTO> weeklyReports = new ArrayList<>();
        
        LocalDate weekStart = startDate;
        while (!weekStart.isAfter(endDate)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }
            
            List<BookingOrderEntity> weekBookings = bookingOrderRepository.findByCreatedAtBetween(
                weekStart.atStartOfDay(), weekEnd.atTime(23, 59, 59));
            
            BigDecimal weekRevenue = weekBookings.stream()
                .map(BookingOrderEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgRevenue = weekBookings.isEmpty() ? BigDecimal.ZERO :
                weekRevenue.divide(BigDecimal.valueOf(weekBookings.size()), 2, RoundingMode.HALF_UP);
            
            long completed = weekBookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus().getStatusName()))
                .count();
            
            long cancelled = weekBookings.stream()
                .filter(b -> "CANCELLED".equals(b.getStatus().getStatusName()))
                .count();
            
            // Tỷ lệ lấp đầy theo tuần
            long totalDays = ChronoUnit.DAYS.between(weekStart, weekEnd) + 1;
            double occupancyRate = (double) completed / (50 * totalDays) * 100;
            
            Date weekStartConverted = Date.from(weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date weekEndConverted = Date.from(weekEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
            long newCustomers = customersRepository.countByCreatedDateBetween(weekStartConverted, weekEndConverted);
            
            weeklyReports.add(new ReportDTO(
                weekStart,
                weekBookings.size(),
                weekRevenue,
                avgRevenue,
                occupancyRate,
                (int) newCustomers,
                (int) completed,
                (int) cancelled,
                BigDecimal.ZERO,
                weekRevenue
            ));
            
            weekStart = weekStart.plusWeeks(1);
        }
        
        return weeklyReports;
    }

    // Báo cáo theo tháng
    public List<ReportDTO> getMonthlyReports(int year) {
        List<ReportDTO> monthlyReports = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            LocalDate monthStart = LocalDate.of(year, month, 1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            
            List<BookingOrderEntity> monthBookings = bookingOrderRepository.findByCreatedAtBetween(
                monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59));
            
            BigDecimal monthRevenue = monthBookings.stream()
                .map(BookingOrderEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgRevenue = monthBookings.isEmpty() ? BigDecimal.ZERO :
                monthRevenue.divide(BigDecimal.valueOf(monthBookings.size()), 2, RoundingMode.HALF_UP);
            
            long completed = monthBookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus().getStatusName()))
                .count();
            
            long cancelled = monthBookings.stream()
                .filter(b -> "CANCELLED".equals(b.getStatus().getStatusName()))
                .count();
            
            // Tỷ lệ lấp đầy theo tháng
            long totalDays = ChronoUnit.DAYS.between(monthStart, monthEnd) + 1;
            double occupancyRate = (double) completed / (50 * totalDays) * 100;
            
            Date monthStartConverted = Date.from(monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date monthEndConverted = Date.from(monthEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
            long newCustomers = customersRepository.countByCreatedDateBetween(monthStartConverted, monthEndConverted);
            
            monthlyReports.add(new ReportDTO(
                monthStart,
                monthBookings.size(),
                monthRevenue,
                avgRevenue,
                occupancyRate,
                (int) newCustomers,
                (int) completed,
                (int) cancelled,
                BigDecimal.ZERO,
                monthRevenue
            ));
        }
        
        return monthlyReports;
    }

    // Thống kê top phòng được đặt nhiều nhất
    public List<Map<String, Object>> getTopRooms(int limit) {
        return bookingOrderRepository.findTopRoomsByBookingCount(limit);
    }

    // Thống kê khách hàng VIP (đặt nhiều nhất)
    public List<Map<String, Object>> getTopCustomers(int limit) {
        return bookingOrderRepository.findTopCustomersByBookingCount(limit);
    }

    // Tạo dữ liệu mẫu cho testing
    public ReportDTO getSampleSummaryReport() {
        return new ReportDTO(
            LocalDate.now(),
            156,
            new BigDecimal("2450000"),
            new BigDecimal("15705"),
            78.5,
            89,
            120,
            15,
            new BigDecimal("150000"),
            new BigDecimal("2300000")
        );
    }

    public List<ReportDTO> getSampleDailyReports() {
        List<ReportDTO> reports = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            reports.add(new ReportDTO(
                date,
                8 + i,
                new BigDecimal("1200000").add(new BigDecimal(i * 50000)),
                new BigDecimal("150000"),
                75.0 + i * 2,
                5 + i,
                6 + i,
                1,
                new BigDecimal("10000"),
                new BigDecimal("1190000").add(new BigDecimal(i * 50000))
            ));
        }
        
        return reports;
    }

    public List<Map<String, Object>> getSampleTopRooms() {
        List<Map<String, Object>> rooms = new ArrayList<>();
        
        Map<String, Object> room1 = new java.util.HashMap<>();
        room1.put("roomId", 1);
        room1.put("roomName", "Phòng Deluxe");
        room1.put("bookingCount", 25);
        room1.put("totalRevenue", new BigDecimal("5000000"));
        
        Map<String, Object> room2 = new java.util.HashMap<>();
        room2.put("roomId", 2);
        room2.put("roomName", "Phòng Suite");
        room2.put("bookingCount", 20);
        room2.put("totalRevenue", new BigDecimal("8000000"));
        
        Map<String, Object> room3 = new java.util.HashMap<>();
        room3.put("roomId", 3);
        room3.put("roomName", "Phòng Standard");
        room3.put("bookingCount", 18);
        room3.put("totalRevenue", new BigDecimal("3000000"));
        
        rooms.add(room1);
        rooms.add(room2);
        rooms.add(room3);
        
        return rooms;
    }

    public List<Map<String, Object>> getSampleTopCustomers() {
        List<Map<String, Object>> customers = new ArrayList<>();
        
        Map<String, Object> customer1 = new java.util.HashMap<>();
        customer1.put("customerId", 1);
        customer1.put("customerName", "Nguyễn Văn A");
        customer1.put("customerEmail", "nguyenvana@email.com");
        customer1.put("bookingCount", 15);
        customer1.put("totalSpent", new BigDecimal("3000000"));
        
        Map<String, Object> customer2 = new java.util.HashMap<>();
        customer2.put("customerId", 2);
        customer2.put("customerName", "Trần Thị B");
        customer2.put("customerEmail", "tranthib@email.com");
        customer2.put("bookingCount", 12);
        customer2.put("totalSpent", new BigDecimal("2500000"));
        
        Map<String, Object> customer3 = new java.util.HashMap<>();
        customer3.put("customerId", 3);
        customer3.put("customerName", "Lê Văn C");
        customer3.put("customerEmail", "levanc@email.com");
        customer3.put("bookingCount", 10);
        customer3.put("totalSpent", new BigDecimal("2000000"));
        
        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        
        return customers;
    }
} 