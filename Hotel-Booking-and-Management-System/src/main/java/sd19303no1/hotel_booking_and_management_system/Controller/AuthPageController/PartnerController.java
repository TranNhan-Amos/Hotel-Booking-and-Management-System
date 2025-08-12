package sd19303no1.hotel_booking_and_management_system.Controller.AuthPageController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;

import sd19303no1.hotel_booking_and_management_system.DTO.MonthlyRevenueReportPartnerDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.ReportsPartnerDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;

import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PartnerController {



    @Autowired
    private PartnerService partnerService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private BookingOrderService bookingOrderService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/partner")
    public String partnerDashboard(Model model) {
        try {
            System.out.println("=== DEBUG: partnerDashboard method called");

            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // Assuming email is used as username
            System.out.println("=== DEBUG: User email: " + userEmail);

            // Find system user by email
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            System.out.println("=== DEBUG: SystemUser found: " + (systemUser != null));

            if (systemUser != null && systemUser.isPartner()) {
                System.out.println("=== DEBUG: User is partner");
                // Find partner information
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                System.out.println("=== DEBUG: Partner found: " + (partner != null));

                if (partner != null) {
                    long roomCount = roomService.findByPartnerId(partner.getId()).size();
                    System.out.println("=== DEBUG: Room count: " + roomCount);
                    model.addAttribute("partner", partner);
                    model.addAttribute("systemUser", systemUser);
                    model.addAttribute("hasPartnerInfo", true);
                    model.addAttribute("userEmail", userEmail);
                    model.addAttribute("roomCount", roomCount);
                } else {
                    System.out.println("=== DEBUG: No partner info, showing registration prompt");
                    model.addAttribute("hasPartnerInfo", false);
                    model.addAttribute("userEmail", userEmail);
                }
            } else {
                System.out.println("=== DEBUG: User is not partner, redirecting to login");
                // Redirect to login if not partner
                return "redirect:/login";
            }

        } catch (Exception e) {
            System.out.println("=== DEBUG: Exception in partnerDashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi tải thông tin đối tác");
            model.addAttribute("hasPartnerInfo", false);
        }

        System.out.println("=== DEBUG: Returning DashboardPartner template");
        return "Partner/DashboardPartner";
    }

    @GetMapping("/partner/register")
    public String showPartnerRegistrationForm(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find system user by email
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                // Check if partner info already exists
                PartnerEntity existingPartner = partnerService.findBySystemUser(systemUser);
                if (existingPartner != null) {
                    return "redirect:/partner"; // Already has partner info
                }

                // Create new partner entity with pre-filled email
                PartnerEntity partner = new PartnerEntity();
                partner.setEmail(userEmail);

                model.addAttribute("partner", partner);
                model.addAttribute("systemUser", systemUser);
            } else {
                return "redirect:/login";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải form đăng ký");
            // Tạo partner object rỗng để tránh lỗi template
            PartnerEntity partner = new PartnerEntity();
            model.addAttribute("partner", partner);
        }

        return "Partner/PartnerRegister";
    }

    @PostMapping("/partner/register")
    public String processPartnerRegistration(
            @RequestParam("companyName") String companyName,
            @RequestParam("taxId") String taxId,
            @RequestParam("address") String address,
            @RequestParam("businessLicense") String businessLicense,
            @RequestParam("contactPerson") String contactPerson,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("businessmodel") String businessmodel,
            @RequestParam("hotelamenities") String hotelamenities,
            RedirectAttributes redirectAttributes) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                // Create new partner entity
                PartnerEntity partner = new PartnerEntity();
                partner.setCompanyName(companyName);
                partner.setTaxId(taxId);
                partner.setAddress(address);
                partner.setBusinessLicense(businessLicense);
                partner.setContactPerson(contactPerson);
                partner.setPhone(phone);
                partner.setEmail(userEmail); // Use logged-in user's email
                partner.setBusinessmodel(businessmodel);
                partner.setHotelamenities(hotelamenities);
                partner.setSystemUser(systemUser);

                // Save partner information
                partnerService.save(partner);

                redirectAttributes.addFlashAttribute("success", "Thông tin đối tác đã được đăng ký thành công!");
                return "redirect:/partner";
            } else {
                redirectAttributes.addFlashAttribute("error", "Không có quyền truy cập!");
                return "redirect:/login";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi đăng ký thông tin đối tác: " + e.getMessage());
            return "redirect:/partner/register";
        }
    }

    @GetMapping("/dashboard/partner")
    public String DashboardPartner(Model model) {
        return ("Partner/DashboardPartner");
    }

    @GetMapping("/partner/reports")
    public String viewPartnerReports(@RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    Long partnerId = partner.getId();

                    // ✅ Xử lý ngày trước
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                    String defaultDate = LocalDate.now().format(formatter);

                    startDate = (startDate != null && !startDate.isEmpty()) ? startDate : defaultDate;
                    endDate = (endDate != null && !endDate.isEmpty()) ? endDate : defaultDate;

                    LocalDate start = LocalDate.parse(startDate);
                    LocalDate end = LocalDate.parse(endDate);

                    // ✅ Gọi service với ngày đã xử lý
                    Map<LocalDate, ReportsPartnerDTO> DayReports = bookingOrderService.getDailyBookingCount(partnerId,
                            start, end);
                    List<Integer> availableYears = bookingOrderService.getAvailableBookingYears(partnerId);

                    BigDecimal totalRevenue = BigDecimal.ZERO;
                    Long totalBookings = 0L;

                    if (start != null && end != null) {
                        List<Object[]> reportData = bookingOrderService.getReportData(partnerId, start, end);

                        totalRevenue = (reportData != null && !reportData.isEmpty() && reportData.get(0)[0] != null)
                                ? (BigDecimal) reportData.get(0)[0]
                                : BigDecimal.ZERO;
                        totalBookings = (reportData != null && !reportData.isEmpty() && reportData.get(0)[1] != null)
                                ? ((Number) reportData.get(0)[1]).longValue()
                                : 0L;

                        System.out.println("Filtered from: " + start + " to " + end);
                        System.out.println("Total Revenue: " + totalRevenue + ", Total Bookings: " + totalBookings);
                    }

                    model.addAttribute("totalRevenue", totalRevenue);
                    model.addAttribute("totalBookings", totalBookings);
                    model.addAttribute("startDate", startDate);
                    model.addAttribute("endDate", endDate);
                    model.addAttribute("partnerId", partnerId);
                    model.addAttribute("DayReports", DayReports);
                    model.addAttribute("availableYears", availableYears);

                    return "Partner/ReportsPartner";
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "Partner/ReportsPartner";
                }
            } else {
                model.addAttribute("error", "Bạn không có quyền truy cập.");
                return "Partner/ReportsPartner";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Đã xảy ra lỗi khi tải báo cáo.");
            return "Partner/ReportsPartner";
        }
    }

    // Lấy dữ liệu doanh thu hàng tháng của đối tác theo năm
    @GetMapping("/api/partner/monthly-revenue")
    @ResponseBody
    public List<MonthlyRevenueReportPartnerDTO> getMonthlyRevenue(@RequestParam Long partnerId,
            @RequestParam Integer year) {
        return bookingOrderService.getMonthlyRevenueReportPartner(partnerId, year);
    }

    // Removed this method as it conflicts with ReviewPartnerController
    // The reviews functionality is now handled by ReviewPartnerController

    @GetMapping("/partner/payments")
    public String viewPartnerPayments() {
        // Logic to retrieve and display partner payments information can be added here
        return "Partner/PaymentsPartner"; // Path to your Thymeleaf template for partner payments
    }

    @GetMapping("/partner/settings")
    public String viewPartnerSettings(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // Assuming email is used as username

            // Find system user by email
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                // Find partner information
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    model.addAttribute("partner", partner);
                    model.addAttribute("systemUser", systemUser);
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                }
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải thông tin cài đặt đối tác");
        }

        return "Partner/SettingsPartner";
    }

    @PostMapping("/partner/settings/update")
    public String updatePartnerSettings(
            @ModelAttribute("partner") @Valid PartnerEntity partner,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "Partner/SettingsPartner"; // Return to settings page if there are validation errors
        }

        try {
            // Save updated partner information
            partnerService.save(partner);
            redirectAttributes.addFlashAttribute("success", "Cài đặt đối tác đã được cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi cập nhật cài đặt đối tác: " + e.getMessage());
        }

        return "redirect:/partner/settings";
    }

    @GetMapping("/partner/support")
    public String viewPartnerSupport() {
        // Logic to retrieve and display partner support information can be added here
        return "Partner/SupportPartner"; // Path to your Thymeleaf template for partner support
    }
    @GetMapping("/partner/profile")
    public String viewPartnerProfile(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // Assuming email is used as username

            // Find system user by email
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {
                // Find partner information
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    model.addAttribute("partner", partner);
                    model.addAttribute("systemUser", systemUser);
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                }
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải thông tin đối tác");
        }

        return "Partner/ProfilePartner"; // Path to your Thymeleaf template for partner profile
    }
}
