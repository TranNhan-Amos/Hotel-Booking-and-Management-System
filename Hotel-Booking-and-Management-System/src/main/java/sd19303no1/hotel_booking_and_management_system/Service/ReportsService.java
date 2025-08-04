package sd19303no1.hotel_booking_and_management_system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sd19303no1.hotel_booking_and_management_system.DTO.SummaryReportDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.DailyReportDTO;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private RoomRepository roomRepository;

    public SummaryReportDTO getSummaryReport(LocalDate startDate, LocalDate endDate) {
        // Tính tổng doanh thu
        BigDecimal totalRevenue = bookingOrderRepository.calculateTotalRevenueInDateRange(startDate, endDate);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Tính tổng số đặt phòng
        Long totalBookings = bookingOrderRepository.countBookingsInDateRange(startDate, endDate);
        if (totalBookings == null) {
            totalBookings = 0L;
        }

        // Tính số khách hàng mới
        Long newCustomers = customersRepository.countByCreatedDateBetween(
            java.sql.Date.valueOf(startDate), 
            java.sql.Date.valueOf(endDate)
        );
        if (newCustomers == null) {
            newCustomers = 0L;
        }

        // Tính tỷ lệ lấp đầy
        Double occupancyRate = calculateOccupancyRate(startDate, endDate);

        return new SummaryReportDTO(totalRevenue, totalBookings, newCustomers, occupancyRate);
    }

    public List<DailyReportDTO> getDailyReports(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> dailyData = bookingOrderRepository.getDailyReports(startDate, endDate);
        
        return dailyData.stream().map(data -> {
            // Xử lý date từ java.sql.Date thành LocalDate
            LocalDate date;
            Object dateObj = data.get("date");
            if (dateObj instanceof java.sql.Date) {
                date = ((java.sql.Date) dateObj).toLocalDate();
            } else if (dateObj instanceof LocalDate) {
                date = (LocalDate) dateObj;
            } else {
                // Fallback nếu không thể parse
                date = LocalDate.now();
            }
            
            Long totalBookings = ((Number) data.get("totalBookings")).longValue();
            BigDecimal totalRevenue = (BigDecimal) data.get("totalRevenue");
            Double occupancyRate = ((Number) data.get("occupancyRate")).doubleValue();
            Long newCustomers = ((Number) data.get("newCustomers")).longValue();
            
            return new DailyReportDTO(date, totalBookings, totalRevenue, occupancyRate, newCustomers);
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopRooms(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> allRooms = bookingOrderRepository.getTopRooms(startDate, endDate);
        return allRooms.stream().limit(10).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopCustomers(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> allCustomers = bookingOrderRepository.getTopCustomers(startDate, endDate);
        return allCustomers.stream().limit(10).collect(Collectors.toList());
    }

    private Double calculateOccupancyRate(LocalDate startDate, LocalDate endDate) {
        // Lấy tổng số phòng
        Long totalRooms = roomRepository.countTotalRooms();
        if (totalRooms == null || totalRooms == 0) {
            return 0.0;
        }

        // Lấy số phòng đã đặt trong khoảng thời gian
        Long bookedRooms = bookingOrderRepository.countBookedRoomsInDateRangeForReports(startDate, endDate);
        if (bookedRooms == null) {
            bookedRooms = 0L;
        }

        // Tính tỷ lệ lấp đầy
        double occupancyRate = (double) bookedRooms / totalRooms * 100;
        return Math.round(occupancyRate * 10.0) / 10.0; // Làm tròn đến 1 chữ số thập phân
    }
} 