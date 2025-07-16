package sd19303no1.hotel_booking_and_management_system.Controller.ClientController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class PaymentController {

    @Autowired
    private BookingOrderService bookingOrderService;

    @GetMapping("/payment")
    public String payment(@RequestParam(required = false) Integer bookingId, Model model) {
        if (bookingId != null) {
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                BookingOrderEntity booking = bookingOpt.get();
                model.addAttribute("booking", booking);
                model.addAttribute("bookingId", bookingId);
                
                // Calculate nights
                if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(
                        booking.getCheckInDate(), booking.getCheckOutDate());
                    model.addAttribute("nights", nights);
                }
                
                // Calculate prices
                if (booking.getRoom() != null && booking.getRoom().getPrice() != null) {
                    BigDecimal basePrice = booking.getRoom().getPrice();
                    BigDecimal serviceFee = basePrice.multiply(new BigDecimal("0.1"));
                    BigDecimal vat = basePrice.add(serviceFee).multiply(new BigDecimal("0.1"));
                    BigDecimal total = basePrice.add(serviceFee).add(vat);
                    
                    model.addAttribute("basePrice", basePrice);
                    model.addAttribute("serviceFee", serviceFee);
                    model.addAttribute("vat", vat);
                    model.addAttribute("total", total);
                }
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
            }
        }
        return "Page/Payment";
    }
    
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam Integer bookingId,
                                @RequestParam String paymentMethod,
                                Model model) {
        try {
            System.out.println("=== PROCESS PAYMENT DEBUG START ===");
            System.out.println("bookingId: " + bookingId);
            System.out.println("paymentMethod: " + paymentMethod);
            
            // Get booking
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (!bookingOpt.isPresent()) {
                model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
                return "redirect:/";
            }
            
            BookingOrderEntity booking = bookingOpt.get();
            
            // Update payment method and status
            booking.setPaymentMethod(paymentMethod);
            booking.setPaymentStatus("PENDING");
            
            // Save booking using repository directly
            bookingOrderService.confirmPayment(bookingId);
            
            System.out.println("=== PROCESS PAYMENT DEBUG: Payment processed successfully ===");
            
            // Redirect to success page
            return "redirect:/payment-success?bookingId=" + bookingId;
            
        } catch (Exception e) {
            System.out.println("=== PROCESS PAYMENT DEBUG: Error occurred: " + e.getMessage() + " ===");
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi xử lý thanh toán: " + e.getMessage());
            return "redirect:/payment?bookingId=" + bookingId;
        }
    }
    
    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam Integer bookingId, Model model) {
        try {
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                model.addAttribute("booking", bookingOpt.get());
                model.addAttribute("success", "Thanh toán thành công! Vui lòng đến khách sạn để check-in.");
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "Page/PaymentSuccess";
    }
    
    @GetMapping("/Payment")
    public String Payment(Model model) {
        return "Page/Payment";
    }
}