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
            RedirectAttributes redirectAttributes) {

        try {
            System.out.println("=== DEBUG: Processing booking ===");
            System.out.println("Customer: " + customerName + ", Email: " + email);
            System.out.println("Room ID: " + roomId + ", Check-in: " + checkInDate + ", Check-out: " + checkOutDate);
            
            // Validate voucher nếu có
            Integer voucherId = null;
            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                // TODO: Implement voucher validation logic
                // VoucherEntity voucher = voucherService.findByCode(voucherCode);
                // if (voucher != null && voucher.isValid()) {
                //     voucherId = voucher.getVoucherId();
                // }
            }
            
            // Tạo booking
            BookingOrderEntity booking = bookingOrderService.createBooking(
                customerName, email, phone, address, 
                roomId, checkInDate, checkOutDate, voucherId
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
            // Lấy thông tin booking để hiển thị xác nhận
            // TODO: Implement booking confirmation service
            model.addAttribute("bookingId", bookingId);
            return "Page/BookingConfirmation";
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

    // Xử lý thanh toán
    @PostMapping("/process-payment")
    public String processPayment(
            @RequestParam Integer bookingId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String expiryDate,
            @RequestParam(required = false) String cvv,
            @RequestParam(required = false) String cardHolderName,
            @RequestParam(required = false) String momoNumber,
            RedirectAttributes redirectAttributes) {
        
        try {
            // TODO: Implement payment processing logic
            // 1. Validate payment information
            // 2. Process payment with payment gateway
            // 3. Update booking status
            // 4. Send confirmation email
            
            // Simulate payment processing
            boolean paymentSuccess = true; // TODO: Replace with actual payment processing
            
            if (paymentSuccess) {
                // Update booking status to paid
                bookingOrderService.adminUpdateBookingStatus(bookingId, "PAID");
                
                redirectAttributes.addFlashAttribute("success", 
                    "Thanh toán thành công! Mã đặt phòng: " + bookingId + 
                    ". Chúng tôi đã gửi email xác nhận.");
                
                return "redirect:/booking-confirmation?bookingId=" + bookingId;
            } else {
                redirectAttributes.addFlashAttribute("error", "Thanh toán thất bại. Vui lòng thử lại.");
                return "redirect:/payment?bookingId=" + bookingId;
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/payment?bookingId=" + bookingId;
        }
    }

    // Test endpoint để kiểm tra dữ liệu
    @GetMapping("/test-data")
    public String testData(Model model) {
        try {
            // Kiểm tra rooms
            List<RoomEntity> rooms = roomService.findAll();
            model.addAttribute("rooms", rooms);
            
            // Kiểm tra status
            // TODO: Add status service
            
            return "test-data";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "error";
        }
    }
}
