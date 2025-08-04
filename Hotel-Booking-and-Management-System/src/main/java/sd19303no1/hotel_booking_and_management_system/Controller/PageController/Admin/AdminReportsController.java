package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sd19303no1.hotel_booking_and_management_system.Service.ReportsService;
import sd19303no1.hotel_booking_and_management_system.DTO.SummaryReportDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.DailyReportDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Controller
public class AdminReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/admin/reports")
    public String viewReportsPage(Model model) {
        // Lấy tháng và năm hiện tại
        YearMonth currentYearMonth = YearMonth.now();
        int currentMonth = currentYearMonth.getMonthValue();
        int currentYear = currentYearMonth.getYear();
        
        // Lấy dữ liệu báo cáo cho tháng hiện tại
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();
        
        SummaryReportDTO summaryReport = reportsService.getSummaryReport(startDate, endDate);
        List<DailyReportDTO> dailyReports = reportsService.getDailyReports(startDate, endDate);
        List<Map<String, Object>> topRooms = reportsService.getTopRooms(startDate, endDate);
        List<Map<String, Object>> topCustomers = reportsService.getTopCustomers(startDate, endDate);
        
        model.addAttribute("summaryReport", summaryReport);
        model.addAttribute("dailyReports", dailyReports);
        model.addAttribute("topRooms", topRooms);
        model.addAttribute("topCustomers", topCustomers);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);
        
        return "Admin/Reports-Management";
    }

    @PostMapping("/admin/reports/summary")
    @ResponseBody
    public SummaryReportDTO getSummaryReport(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return reportsService.getSummaryReport(start, end);
    }

    @PostMapping("/admin/reports/daily")
    @ResponseBody
    public List<DailyReportDTO> getDailyReports(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return reportsService.getDailyReports(start, end);
    }

    @PostMapping("/admin/reports/top-rooms")
    @ResponseBody
    public List<Map<String, Object>> getTopRooms(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return reportsService.getTopRooms(start, end);
    }

    @PostMapping("/admin/reports/top-customers")
    @ResponseBody
    public List<Map<String, Object>> getTopCustomers(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return reportsService.getTopCustomers(start, end);
    }
} 