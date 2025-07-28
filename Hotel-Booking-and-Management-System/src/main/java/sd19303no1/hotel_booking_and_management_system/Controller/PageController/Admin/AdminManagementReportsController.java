package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sd19303no1.hotel_booking_and_management_system.DTO.ReportDTO;
import sd19303no1.hotel_booking_and_management_system.Service.ReportService;

@Controller
@RequestMapping("/admin/reports")
public class AdminManagementReportsController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public String viewReportsPage(Model model) {
        // Lấy báo cáo tháng hiện tại
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        ReportDTO summaryReport;
        List<ReportDTO> dailyReports;
        List<Map<String, Object>> topRooms;
        List<Map<String, Object>> topCustomers;
        
        try {
            summaryReport = reportService.getSummaryReport(startOfMonth, endOfMonth);
            dailyReports = reportService.getDailyReports(startOfMonth, endOfMonth);
            topRooms = reportService.getTopRooms(5);
            topCustomers = reportService.getTopCustomers(5);
            
            // Nếu không có dữ liệu, sử dụng dữ liệu mẫu
            if (summaryReport.getTotalBookings() == 0) {
                summaryReport = reportService.getSampleSummaryReport();
                dailyReports = reportService.getSampleDailyReports();
                topRooms = reportService.getSampleTopRooms();
                topCustomers = reportService.getSampleTopCustomers();
            }
        } catch (Exception e) {
            // Sử dụng dữ liệu mẫu nếu có lỗi
            summaryReport = reportService.getSampleSummaryReport();
            dailyReports = reportService.getSampleDailyReports();
            topRooms = reportService.getSampleTopRooms();
            topCustomers = reportService.getSampleTopCustomers();
        }
        
        model.addAttribute("summaryReport", summaryReport);
        model.addAttribute("dailyReports", dailyReports);
        model.addAttribute("topRooms", topRooms);
        model.addAttribute("topCustomers", topCustomers);
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());
        
        return "admin/Reports-Management";
    }

    @PostMapping("/summary")
    @ResponseBody
    public ResponseEntity<ReportDTO> getSummaryReport(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            ReportDTO report = reportService.getSummaryReport(start, end);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/daily")
    @ResponseBody
    public ResponseEntity<List<ReportDTO>> getDailyReports(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<ReportDTO> reports = reportService.getDailyReports(start, end);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/weekly")
    @ResponseBody
    public ResponseEntity<List<ReportDTO>> getWeeklyReports(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<ReportDTO> reports = reportService.getWeeklyReports(start, end);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/monthly")
    @ResponseBody
    public ResponseEntity<List<ReportDTO>> getMonthlyReports(@RequestParam int year) {
        try {
            List<ReportDTO> reports = reportService.getMonthlyReports(year);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/top-rooms")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getTopRooms(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Map<String, Object>> topRooms = reportService.getTopRooms(limit);
            return ResponseEntity.ok(topRooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/top-customers")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getTopCustomers(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Map<String, Object>> topCustomers = reportService.getTopCustomers(limit);
            return ResponseEntity.ok(topCustomers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
