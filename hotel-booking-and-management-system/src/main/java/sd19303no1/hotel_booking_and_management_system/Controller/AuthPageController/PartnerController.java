package sd19303no1.hotel_booking_and_management_system.Controller.AuthPageController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomPartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomPartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PartnerController {

    @Autowired
    private RoomPartnerService roomPartnerService;
    
    @Autowired
    private PartnerService partnerService;
    
    @Autowired
    private SystemUserService systemUserService;
    
    @GetMapping("/partner")
    public String partnerDashboard(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // Assuming email is used as username
            
            // Find system user by email
            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
            
            if (systemUser != null && systemUser.isPartner()) {
                // Find partner information
                PartnerEntity partner = partnerService.findBySystemUser(systemUser);
                long roomCount = roomPartnerService.countRoomsByPartnerId(partner.getId());
                
                if (partner != null) {
                    
                    model.addAttribute("partner", partner);
                    model.addAttribute("systemUser", systemUser);
                    model.addAttribute("hasPartnerInfo", true);
                    model.addAttribute("userEmail", userEmail);
                    model.addAttribute("roomCount", roomCount);
                } else {
                    model.addAttribute("hasPartnerInfo", false);
                    model.addAttribute("userEmail", userEmail);
                }
            } else {
                // Redirect to login if not partner
                return "redirect:/login";
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải thông tin đối tác");
            model.addAttribute("hasPartnerInfo", false);
        }
        
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
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng ký thông tin đối tác: " + e.getMessage());
            return "redirect:/partner/register";
        }
    }

    @GetMapping("/dashboard/partner")
    public String DashboardPartner(Model model ) {
        return ("Partner/DashboardPartner");
    }
  @GetMapping("/room/partner")
    public String roomPartner(Model model) {
    try {
        // Lấy thông tin người dùng đã đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Lấy email từ Authentication


        SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
        
        if (systemUser != null && systemUser.isPartner()) {
           
            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            
            if (partner != null) {
                Long partnerId = partner.getId();
                
                List<RoomPartnerEntity> roomPartners = roomPartnerService.findByPartnerId(partnerId);
                
               
                RoomPartnerEntity roomPartner = new RoomPartnerEntity();
                roomPartner.setPartnerId(partnerId);

                
                model.addAttribute("roomPartners", roomPartners);
                model.addAttribute("roomPartner", roomPartner);
                
                return "Partner/RoomPartner"; 
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                return "Partner/RoomPartner";
            }
        } else {
            return "redirect:/login"; 
        }
    } catch (Exception e) {
        model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách phòng: " + e.getMessage());
        return "Partner/RoomPartner";
    }
}

     @PostMapping("/room/partner/add")
        public String addRoomPartner(
        @ModelAttribute("roomPartner") @Valid RoomPartnerEntity roomPartner,
        BindingResult result,
        RedirectAttributes redirectAttributes) {
    try {
    
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

       
        SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);
        
        if (systemUser != null && systemUser.isPartner()) {
            
            PartnerEntity partner = partnerService.findBySystemUser(systemUser);
            
            if (partner != null) {
                if (result.hasErrors()) {
                    return "Partner/AddRoomPartner"; 
                }
                
                roomPartner.setPartnerId(partner.getId());
                roomPartnerService.save(roomPartner);
                redirectAttributes.addFlashAttribute("success", "Thêm phòng mới thành công!");
                return "redirect:/room/partner"; 
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đối tác.");
                return "redirect:/room/partner";
            }
        } else {
            return "redirect:/login";
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm phòng: " + e.getMessage());
        return "redirect:/room/partner";
    }
}
    

    @GetMapping("/partner/bookings")
    public String viewPartnerBookings() {
        // Logic to retrieve and display partner bookings information can be added here
        return "Partner/BookingsPartner"; // Path to your Thymeleaf template for partner bookings
    }
    

    @GetMapping("/partner/reports")
    public String viewPartnerReports() {
        // Logic to retrieve and display partner reports information can be added here
        return "Partner/ReportsPartner"; // Path to your Thymeleaf template for partner reports
    }

    @GetMapping("/partner/reviews")
    public String viewPartnerReviews() {
        // Logic to retrieve and display partner reviews information can be added here
        return "Partner/ReviewsPartner"; // Path to your Thymeleaf template for partner reviews
    }

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
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật cài đặt đối tác: " + e.getMessage());
        }
        
        return "redirect:/partner/settings";
    }

    @GetMapping("/partner/support")
    public String viewPartnerSupport() {
        // Logic to retrieve and display partner support information can be added here
        return "Partner/SupportPartner"; // Path to your Thymeleaf template for partner support
    }
}
