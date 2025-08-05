package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.StatusEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;
import sd19303no1.hotel_booking_and_management_system.Repository.StatusRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;

@Controller
public class BookingController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingOrderService bookingOrderService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị trang chi tiết đặt phòng
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
                Optional<RoomEntity> roomOpt = roomService.findById(roomId);
                if (roomOpt.isPresent()) {
                    RoomEntity room = roomOpt.get();
                    model.addAttribute("room", room);
                    if (checkInDate != null && checkOutDate != null) {
                        LocalDate checkIn = LocalDate.parse(checkInDate);
                        LocalDate checkOut = LocalDate.parse(checkOutDate);
                        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                        if (nights < 1) {
                            model.addAttribute("error", "Ngày trả phòng phải sau ngày nhận phòng");
                            return "Page/Booking";
                        }
                        model.addAttribute("nights", nights);
                    }
                    if (room.getTotalRooms() != null) {
                        model.addAttribute("availableRooms", room.getTotalRooms());
                        int maxRoomsToBook = Math.min(room.getTotalRooms(), 5);
                        model.addAttribute("maxRoomsToBook", maxRoomsToBook);
                    }
                } else {
                    model.addAttribute("error", "Không tìm thấy phòng");
                }
            }
            model.addAttribute("customerName", customerName != null ? customerName : "");
            model.addAttribute("email", email != null ? email : "");
            model.addAttribute("phone", phone != null ? phone : "");
            model.addAttribute("checkInDate", checkInDate);
            model.addAttribute("checkOutDate", checkOutDate);
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "Page/Booking";
    }

    // Xử lý tạo booking
    @PostMapping("/process-booking")
    public String processBooking(
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam Integer roomId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(required = false) String idNumber,
            @RequestParam(required = false) String specialRequest,
            @RequestParam(required = false) String voucherCode,
            @RequestParam(defaultValue = "1") Integer roomQuantity,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== PROCESS BOOKING DEBUG START ===");
        System.out.println("roomId: " + roomId);
        System.out.println("customerName: " + customerName);
        System.out.println("email: " + email);
        System.out.println("phone: " + phone);
        System.out.println("checkInDate: " + checkInDate);
        System.out.println("checkOutDate: " + checkOutDate);
        System.out.println("roomQuantity: " + roomQuantity);
        System.out.println("specialRequest: " + specialRequest);

        try {
            // Validate input
            if (roomId == null || checkInDate == null || checkOutDate == null) {
                redirectAttributes.addFlashAttribute("error", "Thông tin đặt phòng không hợp lệ");
                return "redirect:/bookings?roomId=" + roomId;
            }
            if (customerName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Họ tên không được để trống");
                return "redirect:/bookings?roomId=" + roomId;
            }
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                redirectAttributes.addFlashAttribute("error", "Email không hợp lệ");
                return "redirect:/bookings?roomId=" + roomId;
            }
            if (!phone.matches("^[0-9]{10,11}$")) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại không hợp lệ");
                return "redirect:/bookings?roomId=" + roomId;
            }
            if (roomQuantity < 1 || roomQuantity > 5) {
                redirectAttributes.addFlashAttribute("error", "Số lượng phòng không hợp lệ (1-5 phòng)");
                return "redirect:/bookings?roomId=" + roomId;
            }

            // Check room availability
            Optional<RoomEntity> roomOpt = roomService.findById(roomId);
            if (roomOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng");
                return "redirect:/bookings?roomId=" + roomId;
            }
            RoomEntity room = roomOpt.get();
            if (room.getTotalRooms() == null || room.getTotalRooms() < roomQuantity) {
                redirectAttributes.addFlashAttribute("error", "Không đủ phòng trống. Còn lại: " + (room.getTotalRooms() != null ? room.getTotalRooms() : 0) + " phòng");
                return "redirect:/bookings?roomId=" + roomId;
            }

            // Parse dates
            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);
            if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
                redirectAttributes.addFlashAttribute("error", "Ngày trả phòng phải sau ngày nhận phòng");
                return "redirect:/bookings?roomId=" + roomId;
            }

            // Create or update customer
            CustomersEntity customer = customersService.findByEmail(email);
            if (customer == null) {
                customer = new CustomersEntity();
                customer.setName(customerName);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setAddress(address);
                customer.setCccd(idNumber);
                customersService.save(customer);
            } else {
                customer.setName(customerName);
                customer.setPhone(phone);
                customer.setAddress(address);
                customer.setCccd(idNumber);
                customersService.save(customer);
            }

            // Create booking
            Integer voucherId = null;
            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                VoucherEntity voucher = voucherService.findByCode(voucherCode);
                if (voucher != null) {
                    voucherId = voucher.getVoucherId();
                }
            }
            BookingOrderEntity booking = bookingOrderService.createBooking(
                customerName,
                email,
                phone,
                address,
                roomId,
                checkIn,
                checkOut,
                voucherId,
                specialRequest,
                roomQuantity
            );

            // Set initial status to PENDING
            StatusEntity pendingStatus = statusRepository.findByStatusNameIgnoreCase("PENDING");
            if (pendingStatus != null) {
                booking.setStatus(pendingStatus);
                bookingOrderRepository.save(booking);
            }

            System.out.println("=== PROCESS BOOKING DEBUG: Booking created with ID: " + booking.getBookingId() + " ===");
            redirectAttributes.addFlashAttribute("success", "Đặt phòng thành công! Vui lòng tiến hành thanh toán.");
            return "redirect:/payment?bookingId=" + booking.getBookingId();

        } catch (Exception e) {
            System.out.println("=== PROCESS BOOKING DEBUG: Error occurred: " + e.getMessage() + " ===");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đặt phòng: " + e.getMessage());
            return "redirect:/bookings?roomId=" + roomId;
        }
    }

    // Tìm kiếm phòng
    @GetMapping("/search-rooms")
    public String searchRooms(@RequestParam(required = false) String roomType,
                             @RequestParam(required = false) String checkInDate,
                             @RequestParam(required = false) String checkOutDate,
                             @RequestParam(required = false, defaultValue = "1") Integer roomCount,
                             @RequestParam(required = false, defaultValue = "2") Integer adults,
                             @RequestParam(required = false, defaultValue = "0") Integer children,
                             Model model) {
        model.addAttribute("roomType", roomType);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("roomCount", roomCount);
        model.addAttribute("adults", adults);
        model.addAttribute("children", children);

        if (roomType != null && !roomType.isEmpty() &&
            checkInDate != null && !checkInDate.isEmpty() &&
            checkOutDate != null && !checkOutDate.isEmpty()) {
            try {
                LocalDate checkIn = LocalDate.parse(checkInDate);
                LocalDate checkOut = LocalDate.parse(checkOutDate);
                List<RoomEntity> availableRooms = roomService.searchRooms(roomType, checkIn, checkOut, roomCount);
                model.addAttribute("availableRooms", availableRooms);
                model.addAttribute("hasSearchResults", true);
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra khi tìm kiếm phòng: " + e.getMessage());
            }
        }
        return "Client/room-search";
    }

    // Đặt phòng qua AJAX
    @PostMapping("/book-room")
    @ResponseBody
    public Map<String, Object> bookRoom(@RequestParam Integer roomId,
                                       @RequestParam String checkInDate,
                                       @RequestParam String checkOutDate,
                                       @RequestParam Integer roomQuantity,
                                       @RequestParam(required = false) String specialRequests) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để đặt phòng");
                return response;
            }

            String email = authentication.getName();
            CustomersEntity customer = customersService.findByEmail(email);
            if (customer == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin khách hàng");
                return response;
            }

            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);
            if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
                response.put("success", false);
                response.put("message", "Ngày trả phòng phải sau ngày nhận phòng");
                return response;
            }

            Optional<RoomEntity> roomOpt = roomService.findById(roomId);
            if (roomOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy phòng");
                return response;
            }
            RoomEntity room = roomOpt.get();
            
            // Kiểm tra số phòng khả dụng sử dụng logic tính toán động
            int availableRooms = roomService.getAvailableRoomCount(roomId, checkIn, checkOut);
            if (availableRooms < roomQuantity) {
                response.put("success", false);
                response.put("message", "Không đủ phòng trống. Số phòng còn lại: " + availableRooms);
                return response;
            }

            BookingOrderEntity booking = bookingOrderService.createBooking(
                customer.getName(),
                email,
                customer.getPhone(),
                customer.getAddress(),
                roomId,
                checkIn,
                checkOut,
                null,
                specialRequests,
                roomQuantity
            );

            // Set initial status to PENDING
            StatusEntity pendingStatus = statusRepository.findByStatusNameIgnoreCase("PENDING");
            if (pendingStatus != null) {
                booking.setStatus(pendingStatus);
                bookingOrderRepository.save(booking);
            }

            response.put("success", true);
            response.put("message", "Đặt phòng thành công!");
            response.put("bookingId", booking.getBookingId());
            response.put("totalPrice", booking.getTotalPrice());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi đặt phòng: " + e.getMessage());
        }
        return response;
    }

    // Xem chi tiết đặt phòng
    @GetMapping("/booking-details")
    public String bookingDetails(@RequestParam(required = false) Integer bookingId,
                                @RequestParam(required = false) String checkInDate,
                                @RequestParam(required = false) String checkOutDate,
                                @RequestParam(required = false) String customerName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }

            if (bookingId != null) {
                Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
                if (bookingOpt.isPresent()) {
                    BookingOrderEntity booking = bookingOpt.get();
                    String currentUserEmail = authentication.getName();
                    if (currentUserEmail.equals(booking.getEmail())) {
                        model.addAttribute("booking", booking);
                        model.addAttribute("room", booking.getRoom());
                        model.addAttribute("checkInDate", booking.getCheckInDate());
                        model.addAttribute("checkOutDate", booking.getCheckOutDate());
                        model.addAttribute("email", booking.getEmail());
                        if (booking.getCustomer() != null) {
                            model.addAttribute("customerName", booking.getCustomer().getName());
                            model.addAttribute("phone", booking.getCustomer().getPhone());
                        }
                        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                                booking.getCheckInDate(), booking.getCheckOutDate());
                            model.addAttribute("nights", nights);
                        }
                    } else {
                        model.addAttribute("error", "Bạn không có quyền xem thông tin đặt phòng này");
                    }
                } else {
                    model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
                }
            } else {
                model.addAttribute("checkInDate", checkInDate);
                model.addAttribute("checkOutDate", checkOutDate);
                model.addAttribute("customerName", customerName);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
            }
            return "Page/Booking";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Xem danh sách đặt phòng của khách hàng
    @GetMapping("/my-bookings")
    public String myBookings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        List<BookingOrderEntity> bookings = bookingOrderService.getBookingsByCustomerEmailForCustomer(email);
        model.addAttribute("bookings", bookings);
        return "Client/my-bookings";
    }

    // Hủy đặt phòng
    @PostMapping("/cancel-booking")
    @ResponseBody
    public Map<String, Object> cancelBooking(@RequestParam Integer bookingId,
                                            @RequestParam(required = false) String reason) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return response;
            }
            
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                BookingOrderEntity booking = bookingOpt.get();
                String currentUserEmail = authentication.getName();
                
                // Kiểm tra quyền hủy phòng
                if (!currentUserEmail.equals(booking.getEmail())) {
                    response.put("success", false);
                    response.put("message", "Bạn không có quyền hủy đặt phòng này");
                    return response;
                }
                
                // Kiểm tra trạng thái hiện tại
                if ("CANCELLED".equals(booking.getStatus().getStatusName()) || 
                    "COMPLETED".equals(booking.getStatus().getStatusName()) ||
                    "REFUNDED".equals(booking.getStatus().getStatusName())) {
                    response.put("success", false);
                    response.put("message", "Không thể hủy đặt phòng ở trạng thái này");
                    return response;
                }
                
                // Sử dụng service để hủy phòng
                BookingOrderEntity cancelledBooking = bookingOrderService.cancelBooking(bookingId, reason);
                
                response.put("success", true);
                response.put("message", "Hủy đặt phòng thành công! Số tiền hoàn lại: " + 
                            (cancelledBooking.getRefundAmount() != null ? 
                             cancelledBooking.getRefundAmount().toString() + " ₫" : "0 ₫"));
                
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin đặt phòng");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi hủy đặt phòng: " + e.getMessage());
        }
        return response;
    }

    // Yêu cầu hoàn tiền
    @PostMapping("/request-refund")
    @ResponseBody
    public Map<String, Object> requestRefund(@RequestParam Integer bookingId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return response;
            }
            
            Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
            if (bookingOpt.isPresent()) {
                BookingOrderEntity booking = bookingOpt.get();
                String currentUserEmail = authentication.getName();
                
                // Kiểm tra quyền
                if (!currentUserEmail.equals(booking.getEmail())) {
                    response.put("success", false);
                    response.put("message", "Bạn không có quyền yêu cầu hoàn tiền cho đặt phòng này");
                    return response;
                }
                
                // Kiểm tra trạng thái
                if (!"CANCELLED".equals(booking.getStatus().getStatusName())) {
                    response.put("success", false);
                    response.put("message", "Chỉ có thể yêu cầu hoàn tiền cho đặt phòng đã hủy");
                    return response;
                }
                
                if ("COMPLETED".equals(booking.getRefundStatus())) {
                    response.put("success", false);
                    response.put("message", "Đã hoàn tiền cho đặt phòng này");
                    return response;
                }
                
                // Xử lý hoàn tiền
                BookingOrderEntity refundedBooking = bookingOrderService.processRefund(bookingId);
                
                response.put("success", true);
                response.put("message", "Hoàn tiền thành công! Số tiền: " + 
                            (refundedBooking.getRefundAmount() != null ? 
                             refundedBooking.getRefundAmount().toString() + " ₫" : "0 ₫"));
                
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin đặt phòng");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi xử lý hoàn tiền: " + e.getMessage());
        }
        return response;
    }

    // Xem chi tiết đặt phòng theo ID
    @GetMapping("/booking-detail/{bookingId}")
    public String bookingDetail(@PathVariable Integer bookingId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Optional<BookingOrderEntity> bookingOpt = bookingOrderService.findBookingByIdForAdmin(bookingId);
        if (bookingOpt.isPresent()) {
            BookingOrderEntity booking = bookingOpt.get();
            String currentUserEmail = authentication.getName();
            if (currentUserEmail.equals(booking.getEmail())) {
                model.addAttribute("booking", booking);
                model.addAttribute("room", booking.getRoom());
                // Truyền avatarPath
                String avatarPath = null;
                if (booking.getCustomer() != null && booking.getCustomer().getAvatar() != null && !booking.getCustomer().getAvatar().isEmpty()) {
                    avatarPath = "/img/customers/" + booking.getCustomer().getAvatar();
                } else {
                    avatarPath = "/img/customers/default-avatar.png";
                }
                model.addAttribute("avatarPath", avatarPath);
                if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(
                        booking.getCheckInDate(), booking.getCheckOutDate());
                    model.addAttribute("nights", nights);
                }
            } else {
                model.addAttribute("error", "Bạn không có quyền xem thông tin đặt phòng này");
            }
        } else {
            model.addAttribute("error", "Không tìm thấy thông tin đặt phòng");
        }
        return "Client/booking-detail";
    }

    // Chỉnh sửa thông tin cá nhân
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        try {
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            if (systemUser == null) {
                model.addAttribute("error", "Không tìm thấy thông tin người dùng");
                return "Client/edit-profile";
            }
            if ("CUSTOMER".equals(systemUser.getRole())) {
                CustomersEntity customer = customersService.findBySystemUser(systemUser);
                if (customer != null) {
                    model.addAttribute("customer", customer);
                }
            }
            model.addAttribute("systemUser", systemUser);
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "Client/edit-profile";
    }

    // Cập nhật thông tin cá nhân
    @PostMapping("/profile/update")
    @ResponseBody
    public Map<String, Object> updateProfile(@RequestParam(required = false) String fullName,
                                            @RequestParam(required = false) String phone,
                                            @RequestParam(required = false) String address,
                                            @RequestParam(required = false) String currentPassword,
                                            @RequestParam(required = false) String newPassword,
                                            @RequestParam(required = false) String confirmPassword) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return response;
            }
            String email = authentication.getName();
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            if (systemUser == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin người dùng");
                return response;
            }
            if ("CUSTOMER".equals(systemUser.getRole())) {
                CustomersEntity customer = customersService.findBySystemUser(systemUser);
                if (customer != null) {
                    if (fullName != null && !fullName.trim().isEmpty()) {
                        customer.setName(fullName.trim());
                    }
                    if (phone != null && !phone.trim().isEmpty()) {
                        customer.setPhone(phone.trim());
                    }
                    if (address != null && !address.trim().isEmpty()) {
                        customer.setAddress(address.trim());
                    }
                    customersService.save(customer);
                }
            }
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    response.put("success", false);
                    response.put("message", "Vui lòng nhập mật khẩu hiện tại");
                    return response;
                }
                if (!newPassword.equals(confirmPassword)) {
                    response.put("success", false);
                    response.put("message", "Mật khẩu mới không khớp");
                    return response;
                }
                // TODO: Use proper password encoder
                if (!passwordEncoder.matches(currentPassword, systemUser.getPassword())) {
                    response.put("success", false);
                    response.put("message", "Mật khẩu hiện tại không đúng");
                    return response;
                }
                systemUser.setPassword(passwordEncoder.encode(newPassword));
                systemUserService.save(systemUser);
            }
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return response;
    }
}