package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;

@Controller
public class BookingController {

    @Autowired
    private BookingOrderService bookingOrderService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private VoucherService voucherService;

    // Bước 2: Hiển thị trang chi tiết đặt phòng
    @GetMapping("/bookings")
    public String showBookingPage(
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            Model model) {

        try {
            if (roomId != null) {
                // Lấy thông tin phòng
                RoomEntity room = roomService.findById(roomId);
                if (room != null) {
                    model.addAttribute("room", room);
                    
                    // Tính toán số đêm
                    if (checkInDate != null && checkOutDate != null) {
                        LocalDate checkIn = LocalDate.parse(checkInDate);
                        LocalDate checkOut = LocalDate.parse(checkOutDate);
                        int nights = checkIn.until(checkOut).getDays();
                        model.addAttribute("nights", nights);
                        
                        // Tính toán giá
                        double basePrice = room.getPrice().doubleValue() * nights;
                        double serviceFee = basePrice * 0.1;
                        double vat = (basePrice + serviceFee) * 0.1;
                        double total = basePrice + serviceFee + vat;
                        
                        model.addAttribute("basePrice", basePrice);
                        model.addAttribute("serviceFee", serviceFee);
                        model.addAttribute("vat", vat);
                        model.addAttribute("total", total);
                    }
                }
            }
            
            // Thêm thông tin khách hàng nếu có
            if (customerName != null) model.addAttribute("customerName", customerName);
            if (email != null) model.addAttribute("email", email);
            if (phone != null) model.addAttribute("phone", phone);
            if (checkInDate != null) model.addAttribute("checkInDate", checkInDate);
            if (checkOutDate != null) model.addAttribute("checkOutDate", checkOutDate);
            
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "Page/Booking";
    }

    // Bước 3: Xử lý thanh toán và tạo booking
    @PostMapping("/bookings")
    public String processBooking(
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam Integer roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam(required = false) String idNumber,
            @RequestParam(required = false) String specialRequest,
            @RequestParam(required = false) String voucherCode,
            @RequestParam(name = "roomQuantity", required = false, defaultValue = "1") Integer roomQuantity,
            RedirectAttributes redirectAttributes) {

        try {
            System.out.println("=== DEBUG: roomQuantity nhận từ form: " + roomQuantity);
            System.out.println("=== DEBUG: Processing booking ===");
            System.out.println("Customer: " + customerName + ", Email: " + email);
            System.out.println("Room ID: " + roomId + ", Check-in: " + checkInDate + ", Check-out: " + checkOutDate);
            
            // Validate voucher nếu có
            Integer voucherId = null;
            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                VoucherEntity voucher = voucherService.findByCode(voucherCode);
                if (voucher != null && voucherService.isVoucherValid(voucher, checkInDate)) {
                    voucherId = voucher.getVoucherId();
                } else {
                    redirectAttributes.addFlashAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
                    return "redirect:/bookings?roomId=" + roomId;
                }
            }
            
            // Tạo booking
            BookingOrderEntity booking = bookingOrderService.createBooking(
                customerName, email, phone, address, 
                roomId, checkInDate, checkOutDate, voucherId, specialRequest, roomQuantity
            );
            
            System.out.println("=== DEBUG: Booking created successfully ===");
            System.out.println("Booking ID: " + booking.getBookingId());
            
            // Chuyển hướng đến trang thanh toán
            redirectAttributes.addFlashAttribute("bookingId", booking.getBookingId());
            redirectAttributes.addFlashAttribute("success", "Thông tin đặt phòng đã được lưu!");
            
            return "redirect:/payment?bookingId=" + booking.getBookingId();
            
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error creating booking ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/bookings?roomId=" + roomId;
        }
    }

    // Trang xác nhận cuối cùng
    @GetMapping("/booking-confirmation")
    public String showConfirmation(@RequestParam Integer bookingId, Model model) {
        try {
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                BookingOrderEntity booking = bookingOpt.get();
                model.addAttribute("booking", booking);
                model.addAttribute("bookingId", bookingId);
                return "Page/BookingConfirmation";
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
                return "redirect:/";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
            return "redirect:/";
        }
    }

    // Hiển thị trang thanh toán
    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam(required = false) Integer bookingId, Model model) {
        try {
            if (bookingId == null) {
                model.addAttribute("error", "Không tìm thấy thông tin đặt phòng. Vui lòng thực hiện đặt phòng trước.");
                return "redirect:/";
            }
            
            // Lấy thông tin booking
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                BookingOrderEntity booking = bookingOpt.get();
                model.addAttribute("bookingId", bookingId);
                model.addAttribute("booking", booking);
                
                // Tính toán giá
                RoomEntity room = booking.getRoom();
                if (room != null) {
                    int nights = booking.getCheckInDate().until(booking.getCheckOutDate()).getDays();
                    double basePrice = room.getPrice().doubleValue() * nights;
                    double serviceFee = basePrice * 0.1;
                    double vat = (basePrice + serviceFee) * 0.1;
                    double total = basePrice + serviceFee + vat;
                    
                    model.addAttribute("basePrice", basePrice);
                    model.addAttribute("serviceFee", serviceFee);
                    model.addAttribute("vat", vat);
                    model.addAttribute("total", total);
                    model.addAttribute("nights", nights);
                }
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin đặt phòng với mã: " + bookingId);
                return "redirect:/";
            }
            return "Page/Payment";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Xử lý thanh toán tại khách sạn
    @PostMapping("/process-payment")
    public String processPayment(
            @RequestParam Integer bookingId,
            @RequestParam String paymentMethod,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Xác nhận thanh toán
            BookingOrderEntity booking = bookingOrderService.confirmPayment(bookingId);
            
            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công! Vui lòng đến khách sạn để check-in.");
            return "redirect:/booking-confirmation?bookingId=" + bookingId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/payment?bookingId=" + bookingId;
        }
    }

    // Hủy đặt phòng
    @PostMapping("/cancel-booking")
    public String cancelBooking(
            @RequestParam Integer bookingId,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            BookingOrderEntity booking = bookingOrderService.cancelBooking(bookingId, reason);
            
            if (booking.getRefundAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    "Đã hủy đặt phòng thành công. Số tiền hoàn lại: " + 
                    booking.getRefundAmount().toString() + " VNĐ");
            } else {
                redirectAttributes.addFlashAttribute("success", 
                    "Đã hủy đặt phòng thành công. Không có tiền hoàn lại theo chính sách.");
            }
            
            return "redirect:/booking-confirmation?bookingId=" + bookingId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/booking-confirmation?bookingId=" + bookingId;
        }
    }

    // Xử lý hoàn tiền
    @PostMapping("/process-refund")
    public String processRefund(
            @RequestParam Integer bookingId,
            RedirectAttributes redirectAttributes) {
        
        try {
            BookingOrderEntity booking = bookingOrderService.processRefund(bookingId);
            
            redirectAttributes.addFlashAttribute("success", 
                "Đã xử lý hoàn tiền thành công cho đặt phòng #" + bookingId);
            
            return "redirect:/booking-confirmation?bookingId=" + bookingId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/booking-confirmation?bookingId=" + bookingId;
        }
    }

    // Test data page
    @GetMapping("/test-data")
    public String testData(Model model) {
        return "test-data";
    }
}
