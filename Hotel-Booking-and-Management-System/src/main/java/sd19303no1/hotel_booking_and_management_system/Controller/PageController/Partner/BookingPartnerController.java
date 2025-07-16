package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Partner;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;

@Controller
public class BookingPartnerController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private BookingOrderService bookingOrderService;

    @GetMapping("/partner/bookings")
    public String viewPartnerBookings(Model model) {
        try {
            // Lấy thông tin người dùng đã đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName(); // Lấy email từ Authentication

            SystemUserEntity systemUser = systemUserService.findByEmail(userEmail);

            if (systemUser != null && systemUser.isPartner()) {

                PartnerEntity partner = partnerService.findBySystemUser(systemUser);

                if (partner != null) {
                    Long partnerId = partner.getId();

                    long bookingshomNay = bookingOrderService.countTodayBookingsByPartner(partnerId);
                    List<BookingOrderEntity> RoomBookingsPartner = bookingOrderService
                            .findAllBookingsByPartner(partnerId); // Lấy 10 booking gần nhất
                    long sumDoanhThuToday = bookingOrderService.sumDoanhThuToday(partnerId);

                    model.addAttribute("bookingshomNay", bookingshomNay);
                    model.addAttribute("RoomBookingsPartner", RoomBookingsPartner);
                    model.addAttribute("sumDoanhThuToday", sumDoanhThuToday);
                    return "Partner/BookingsPartner";
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đối tác.");
                    return "Partner/BookingsPartner";
                }
            } else {

            }

        } catch (Exception e) {
            
        }
        return "Partner/BookingsPartner"; 
    }
}
