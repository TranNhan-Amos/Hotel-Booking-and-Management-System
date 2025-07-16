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
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

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
            // TODO: Implement booking processing logic
            redirectAttributes.addFlashAttribute("success", "Đặt phòng thành công!");
            return "redirect:/payment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/bookings";
        }
    }

    @GetMapping("/search-rooms")
    public String searchRooms(@RequestParam(required = false) String roomType,
                             @RequestParam(required = false) String checkInDate,
                             @RequestParam(required = false) String checkOutDate,
                             @RequestParam(required = false, defaultValue = "1") Integer roomCount,
                             @RequestParam(required = false, defaultValue = "2") Integer adults,
                             @RequestParam(required = false, defaultValue = "0") Integer children,
                             Model model) {
        
        // Add search parameters to model
        model.addAttribute("roomType", roomType);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("roomCount", roomCount);
        model.addAttribute("adults", adults);
        model.addAttribute("children", children);

        // If search parameters are provided, search for available rooms
        if (roomType != null && !roomType.isEmpty() && 
            checkInDate != null && !checkInDate.isEmpty() && 
            checkOutDate != null && !checkOutDate.isEmpty()) {
            
            try {
                LocalDate checkIn = LocalDate.parse(checkInDate);
                LocalDate checkOut = LocalDate.parse(checkOutDate);
                
                // Get all rooms for now (simplified)
                List<RoomEntity> allRooms = roomService.getAllRooms();
                model.addAttribute("availableRooms", allRooms);
                model.addAttribute("hasSearchResults", true);
                
            } catch (Exception e) {
                model.addAttribute("error", "Có lỗi xảy ra khi tìm kiếm phòng: " + e.getMessage());
            }
        }

        return "Client/room-search";
    }

    @PostMapping("/book-room")
    @ResponseBody
    public Map<String, Object> bookRoom(@RequestParam Integer roomId,
                                       @RequestParam String checkInDate,
                                       @RequestParam String checkOutDate,
                                       @RequestParam Integer roomQuantity,
                                       @RequestParam(required = false) String specialRequests) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== CONTROLLER DEBUG: bookRoom started ===");
            System.out.println("roomId: " + roomId);
            System.out.println("checkInDate: " + checkInDate);
            System.out.println("checkOutDate: " + checkOutDate);
            System.out.println("roomQuantity: " + roomQuantity);
            System.out.println("specialRequests: " + specialRequests);
            
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập để đặt phòng");
                return response;
            }

            String email = authentication.getName();
            System.out.println("=== CONTROLLER DEBUG: User email: " + email + " ===");
            
            // Get customer info
            CustomersEntity customer = customersService.findByEmail(email);
            if (customer == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin khách hàng");
                return response;
            }

            // Parse dates
            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);

            // Call service to create booking
            BookingOrderEntity booking = bookingOrderService.createBooking(
                customer.getName(),
                email,
                customer.getPhone(),
                customer.getAddress(),
                roomId,
                checkIn,
                checkOut,
                null, // voucherId - null for now
                specialRequests,
                roomQuantity
            );

            System.out.println("=== CONTROLLER DEBUG: Booking created successfully with ID: " + booking.getBookingId() + " ===");

            response.put("success", true);
            response.put("message", "Đặt phòng thành công!");
            response.put("bookingId", booking.getBookingId());
            response.put("totalPrice", booking.getTotalPrice());

        } catch (Exception e) {
            System.out.println("=== CONTROLLER DEBUG: Error occurred: " + e.getMessage() + " ===");
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi đặt phòng: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/process-booking")
    public String processBooking(@RequestParam Integer roomId,
                                @RequestParam String customerName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam String checkInDate,
                                @RequestParam String checkOutDate,
                                @RequestParam(required = false) String specialRequests,
                                @RequestParam(defaultValue = "1") Integer roomQuantity,
                                Model model) {
        
        // Debug log for troubleshooting
        System.out.println("=== PROCESS BOOKING DEBUG START ===");
        System.out.println("roomId: " + roomId);
        System.out.println("customerName: " + customerName);
        System.out.println("email: " + email);
        System.out.println("phone: " + phone);
        System.out.println("checkInDate: " + checkInDate);
        System.out.println("checkOutDate: " + checkOutDate);
        System.out.println("specialRequests: " + specialRequests);
        System.out.println("roomQuantity: " + roomQuantity);
        
        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("=== PROCESS BOOKING DEBUG: User not authenticated ===");
            return "redirect:/login";
        }
        
        try {
            // Validate input
            if (roomId == null || checkInDate == null || checkOutDate == null) {
                System.out.println("=== PROCESS BOOKING DEBUG: Invalid input ===");
                model.addAttribute("error", "Thông tin đặt phòng không hợp lệ");
                return "redirect:/room/" + roomId;
            }

            // Parse dates
            LocalDate checkIn = LocalDate.parse(checkInDate);
            LocalDate checkOut = LocalDate.parse(checkOutDate);
            System.out.println("=== PROCESS BOOKING DEBUG: Parsed dates - checkIn: " + checkIn + ", checkOut: " + checkOut + " ===");

            // Create booking
            System.out.println("=== PROCESS BOOKING DEBUG: Calling createBooking service ===");
            BookingOrderEntity booking = bookingOrderService.createBooking(
                customerName,
                email,
                phone,
                null, // address - null for now
                roomId,
                checkIn,
                checkOut,
                null, // voucherId - null for now
                specialRequests,
                roomQuantity
            );

            System.out.println("=== PROCESS BOOKING DEBUG: Booking created successfully with ID: " + booking.getBookingId() + " ===");

            // Redirect to payment page with booking info
            return "redirect:/payment?bookingId=" + booking.getBookingId();

        } catch (Exception e) {
            System.out.println("=== PROCESS BOOKING DEBUG: Error occurred: " + e.getMessage() + " ===");
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi đặt phòng: " + e.getMessage());
            return "redirect:/room/" + roomId;
        }
    }



    @GetMapping("/booking-details")
    public String bookingDetails(@RequestParam(required = false) Integer bookingId,
                                @RequestParam(required = false) String checkInDate,
                                @RequestParam(required = false) String checkOutDate,
                                @RequestParam(required = false) String customerName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                Model model) {
        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }

            // If bookingId is provided, get booking details
            if (bookingId != null) {
                BookingOrderEntity booking = bookingOrderService.findBookingByIdForAdmin(bookingId)
                    .orElse(null);
                
                if (booking != null) {
                    // Verify that the booking belongs to the current user
                    String currentUserEmail = authentication.getName();
                    if (currentUserEmail.equals(booking.getEmail())) {
                        model.addAttribute("booking", booking);
                        model.addAttribute("room", booking.getRoom());
                        model.addAttribute("checkInDate", booking.getCheckInDate());
                        model.addAttribute("checkOutDate", booking.getCheckOutDate());
                        model.addAttribute("email", booking.getEmail());
                        
                        // Get customer info from related entity
                        if (booking.getCustomer() != null) {
                            model.addAttribute("customerName", booking.getCustomer().getName());
                            model.addAttribute("phone", booking.getCustomer().getPhone());
                        }
                        
                        // Calculate nights
                        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                                booking.getCheckInDate(), booking.getCheckOutDate());
                            model.addAttribute("nights", nights);
                        }
                    }
                }
            } else {
                // If no bookingId, use parameters from form
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

    @GetMapping("/my-bookings")
    public String myBookings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // For now, just show a message that bookings will be implemented
        model.addAttribute("message", "Chức năng xem lịch sử đặt phòng sẽ được cập nhật sớm!");
        
        return "Client/my-bookings";
    }

    @PostMapping("/cancel-booking")
    @ResponseBody
    public Map<String, Object> cancelBooking(@RequestParam Integer bookingId,
                                            @RequestParam(required = false) String reason) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Vui lòng đăng nhập");
                return response;
            }

            // For now, just return success
            response.put("success", true);
            response.put("message", "Hủy đặt phòng thành công!");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi hủy đặt phòng: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/booking-detail/{bookingId}")
    public String bookingDetail(@PathVariable Integer bookingId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // For now, just show a message
        model.addAttribute("message", "Chi tiết đặt phòng sẽ được cập nhật sớm!");
        model.addAttribute("bookingId", bookingId);
        
        return "Client/booking-detail";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        
        try {
            // Get user from system_users table
            SystemUserEntity systemUser = systemUserService.findByEmail(email);
            if (systemUser == null) {
                model.addAttribute("error", "Không tìm thấy thông tin người dùng");
                return "Client/edit-profile";
            }

            // If user is CUSTOMER, get detailed info from customers table
            if ("CUSTOMER".equals(systemUser.getRole())) {
                CustomersEntity customer = customersService.findBySystemUser(systemUser);
                if (customer != null) {
                    model.addAttribute("customer", customer);
                }
                model.addAttribute("systemUser", systemUser);
            } else {
                model.addAttribute("systemUser", systemUser);
            }

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "Client/edit-profile";
    }

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

            // Update customer information if provided
            if (fullName != null || phone != null || address != null) {
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
            }

            // Update password if provided
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

                // Verify current password (simplified - you should use proper password encoder)
                if (!systemUser.getPassword().equals(currentPassword)) {
                    response.put("success", false);
                    response.put("message", "Mật khẩu hiện tại không đúng");
                    return response;
                }

                // Update password
                systemUser.setPassword(newPassword);
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
