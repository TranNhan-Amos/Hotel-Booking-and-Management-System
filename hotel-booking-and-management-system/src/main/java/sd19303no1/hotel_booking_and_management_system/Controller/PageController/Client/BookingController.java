package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Client;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;

@Controller
public class BookingController {

    @Autowired
    private BookingOrderService bookingOrderService;

    @PostMapping("/bookings")
    public String createBooking(
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam Integer roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam(required = false) Integer voucherId,
            RedirectAttributes redirectAttributes) {

        try {
            bookingOrderService.createBooking(customerName, email, phone, address, 
                                            roomId, checkInDate, checkOutDate, voucherId);
            redirectAttributes.addFlashAttribute("message", "Đặt phòng thành công! Chúng tôi sẽ liên hệ với bạn sớm.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/";
    }
}
