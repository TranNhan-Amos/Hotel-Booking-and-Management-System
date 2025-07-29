package sd19303no1.hotel_booking_and_management_system.Controller.ClientController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.StatusEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;
import sd19303no1.hotel_booking_and_management_system.Repository.StatusRepository;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class PaymentController {

    @Autowired
    private BookingOrderService bookingOrderService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @GetMapping("/payment")
    public String payment(@RequestParam(required = false) Integer bookingId, Model model) {
        if (bookingId == null) {
            model.addAttribute("error", "Mã đặt phòng không được cung cấp");
            return "Page/Payment";
        }

        Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
        if (bookingOpt.isPresent()) {
            BookingOrderEntity booking = bookingOpt.get();
            model.addAttribute("booking", booking);
            model.addAttribute("bookingId", bookingId);
            model.addAttribute("room", booking.getRoom());
            model.addAttribute("customerName",
                    booking.getCustomer() != null ? booking.getCustomer().getName() : booking.getEmail());
            model.addAttribute("email", booking.getEmail());
            model.addAttribute("phone",
                    booking.getCustomer() != null ? booking.getCustomer().getPhone() : booking.getEmail());
            model.addAttribute("customerCCCD", booking.getCustomer() != null ? booking.getCustomer().getCccd() : "");

            // Calculate nights
            if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                long nights = java.time.temporal.ChronoUnit.DAYS.between(
                        booking.getCheckInDate(), booking.getCheckOutDate());
                model.addAttribute("nights", nights);
            }

            // Calculate prices
            if (booking.getRoom() != null && booking.getRoom().getPrice() != null) {
                BigDecimal basePrice = booking.getRoom().getPrice()
                        .multiply(BigDecimal.valueOf(booking.getRoomQuantity()));
                BigDecimal serviceFee = basePrice.multiply(new BigDecimal("0.1"));
                BigDecimal vat = basePrice.add(serviceFee).multiply(new BigDecimal("0.1"));
                BigDecimal total = basePrice.add(serviceFee).add(vat);
                model.addAttribute("basePrice", basePrice);
                model.addAttribute("serviceFee", serviceFee);
                model.addAttribute("vat", vat);
                model.addAttribute("total", total);
            } else {
                model.addAttribute("error", "Không thể tính toán chi phí do thiếu thông tin phòng");
            }
        } else {
            model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
        }
        return "Page/Payment";
    }

    @PostMapping("/process-payment")
    public String processPayment(
            @RequestParam Integer bookingId,
            @RequestParam String customerName,
            @RequestParam String customerEmail,
            @RequestParam String customerPhone,
            @RequestParam(required = false) String customerCCCD,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String expiryDate,
            @RequestParam(required = false) String cvv,
            @RequestParam(required = false) String cardHolderName,
            @RequestParam(required = false) String momoNumber,
            Model model, RedirectAttributes redirectAttributes) {

        System.out.println("=== PROCESS PAYMENT DEBUG START ===");
        System.out.println("bookingId: " + bookingId);
        System.out.println("customerName: " + customerName);
        System.out.println("customerEmail: " + customerEmail);
        System.out.println("customerPhone: " + customerPhone);
        System.out.println("customerCCCD: " + customerCCCD);
        System.out.println("paymentMethod: " + paymentMethod);

        // Validate input
        if (bookingId == null) {
            model.addAttribute("error", "Mã đặt phòng không được cung cấp");
            return "Page/Payment";
        }
        if (customerName.trim().isEmpty()) {
            model.addAttribute("error", "Họ tên không được để trống");
            return "Page/Payment";
        }
        if (!customerEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Email không hợp lệ");
            return "Page/Payment";
        }
        if (!customerPhone.matches("^[0-9]{10,11}$")) {
            model.addAttribute("error", "Số điện thoại không hợp lệ");
            return "Page/Payment";
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            model.addAttribute("error", "Phương thức thanh toán không được để trống");
            return "Page/Payment";
        }
        if (paymentMethod.equals("creditCard")) {
            if (cardNumber == null || cardNumber.replaceAll("\\s", "").length() < 13
                    || cardNumber.replaceAll("\\s", "").length() > 19) {
                model.addAttribute("error", "Số thẻ không hợp lệ");
                return "Page/Payment";
            }
            if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
                model.addAttribute("error", "Ngày hết hạn không hợp lệ");
                return "Page/Payment";
            }
            if (cvv == null || cvv.length() < 3 || cvv.length() > 4) {
                model.addAttribute("error", "CVV không hợp lệ");
                return "Page/Payment";
            }
            if (cardHolderName == null || cardHolderName.trim().isEmpty()) {
                model.addAttribute("error", "Tên chủ thẻ không được để trống");
                return "Page/Payment";
            }
        } else if (paymentMethod.equals("momo")) {
            if (momoNumber == null || !momoNumber.matches("^[0-9]{10,11}$")) {
                model.addAttribute("error", "Số điện thoại MoMo không hợp lệ");
                return "Page/Payment";
            }
        }

        // Find booking
        Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
        if (!bookingOpt.isPresent()) {
            model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
            return "Page/Payment";
        }

        BookingOrderEntity booking = bookingOpt.get();

        // Validate booking details
        if (booking.getRoom() == null || booking.getCheckInDate() == null || booking.getCheckOutDate() == null
                || booking.getRoomQuantity() == null || booking.getRoomQuantity() <= 0) {
            model.addAttribute("error", "Thông tin đặt phòng không đầy đủ");
            return "Page/Payment";
        }

        // Save or update customer information
        CustomersEntity customer = customersService.findByEmail(customerEmail);
        if (customer == null) {
            customer = new CustomersEntity();
            customer.setEmail(customerEmail);
        }
        customer.setName(customerName);
        customer.setPhone(customerPhone);
        customer.setCccd(customerCCCD);
        customersService.save(customer);

        // Update customer information
        booking.setCustomer(customer);
        
        // Process payment using service method
        booking = bookingOrderService.processPayment(bookingId, paymentMethod);
        
        // Calculate and set total price
        if (booking.getRoom() != null && booking.getRoom().getPrice() != null && booking.getRoomQuantity() != null) {
            BigDecimal basePrice = booking.getRoom().getPrice().multiply(BigDecimal.valueOf(booking.getRoomQuantity()));
            BigDecimal serviceFee = basePrice.multiply(new BigDecimal("0.1")); // Phí dịch vụ 10%
            BigDecimal vat = basePrice.add(serviceFee).multiply(new BigDecimal("0.1")); // VAT 10%
            BigDecimal total = basePrice.add(serviceFee).add(vat);

            booking.setTotalPrice(total);
        }
        
        bookingOrderRepository.save(booking);

        // TODO: Integrate payment gateway for creditCard/momo/bankTransfer
        System.out.println("===== PROCESS PAYMENT DEBUG: Payment processed successfully ===");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Payment Status: " + booking.getPaymentStatus());
        System.out.println("Total Price: " + booking.getTotalPrice());
        
        // Set success message based on payment method
        String successMessage;
        if (paymentMethod.equalsIgnoreCase("payOnArrival")) {
            successMessage = "Đặt phòng thành công! Chờ thanh toán tại khách sạn.";
        } else {
            successMessage = "Đã thanh toán, chờ nhận phòng.";
        }
        
        redirectAttributes.addFlashAttribute("success", successMessage);
        return "redirect:/payment-success?bookingId=" + bookingId;
    }

    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam Integer bookingId, Model model) {
        try {
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                BookingOrderEntity booking = bookingOpt.get();
                model.addAttribute("booking", booking);
                            // Set success message based on payment status and method
            String successMessage;
            if (booking.getPaymentStatus().equals("PAID")) {
                successMessage = "Đã thanh toán, chờ nhận phòng.";
            } else {
                successMessage = "Đặt phòng thành công! Chờ thanh toán tại khách sạn.";
            }
            model.addAttribute("success", successMessage);
                // Calculate prices for display
                if (booking.getRoom() != null && booking.getRoom().getPrice() != null) {
                    BigDecimal basePrice = booking.getRoom().getPrice()
                            .multiply(BigDecimal.valueOf(booking.getRoomQuantity()));
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
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "Page/PaymentSuccess";
    }

    @PostMapping("/payment")
    public String handlePaymentPost(
            @RequestParam Integer roomId,
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam Integer roomQuantity,
            @RequestParam(required = false) String specialRequests,
            Model model,
            RedirectAttributes redirectAttributes) {
        RoomEntity room = roomService.findById(roomId);
        if (room == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng");
            return "redirect:/bookings?roomId=" + roomId;
        }
        // Tạo booking tạm thời (status PENDING, chưa thanh toán)
        BookingOrderEntity booking = new BookingOrderEntity();
        booking.setRoom(room);
        booking.setEmail(email);
        booking.setCheckInDate(java.time.LocalDate.parse(checkInDate));
        booking.setCheckOutDate(java.time.LocalDate.parse(checkOutDate));
        booking.setRoomQuantity(roomQuantity);
        booking.setSpecialRequests(specialRequests);
        booking.setPaymentStatus("PENDING");
        booking.setPaymentMethod("PENDING");
        booking.setBookingDate(java.time.LocalDate.now());
        // Tạo customer tạm thời
        CustomersEntity customer = customersService.findByEmail(email);
        if (customer == null) {
            customer = new CustomersEntity();
            customer.setEmail(email);
            customer.setName(customerName);
            customer.setPhone(phone);
            customersService.save(customer);
        }
        booking.setCustomer(customer);
        // Set status PENDING
        StatusEntity pendingStatus = statusRepository.findByStatusNameIgnoreCase("PENDING");
        if (pendingStatus != null) {
            booking.setStatus(pendingStatus);
        }
        bookingOrderRepository.save(booking);
        // Redirect sang GET /payment?bookingId=...
        return "redirect:/payment?bookingId=" + booking.getBookingId();
    }
}