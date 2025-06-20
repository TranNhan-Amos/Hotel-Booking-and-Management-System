package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.ReviewService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;
import sd19303no1.hotel_booking_and_management_system.DTO.RoomTypeDTO;

@Controller
public class IndexController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookingOrderService bookingOrderService;

    @GetMapping("/")
    public String home(Model model) {
        // Lấy các phòng nổi bật (top 5 phòng có rating cao nhất)
        List<RoomEntity> featuredRooms = roomService.getFeaturedRooms(5);
        model.addAttribute("featuredRooms", featuredRooms);

        // Lấy các loại phòng khác nhau
        List<RoomTypeDTO> roomTypes = roomService.getRoomTypes();
        model.addAttribute("roomTypes", roomTypes);

        // Lấy các voucher đang hoạt động
        List<VoucherEntity> activeVouchers = voucherService.getActiveVouchers();
        model.addAttribute("activeVouchers", activeVouchers);

        // Lấy tất cả vouchers cho dropdown
        List<VoucherEntity> vouchers = voucherService.getAllActiveVouchers();
        model.addAttribute("vouchers", vouchers);

        // Lấy các đánh giá gần đây (top 6)
        List<ReviewEntity> recentReviews = reviewService.getRecentReviews(6);
        model.addAttribute("recentReviews", recentReviews);

        // Lấy các booking gần đây (top 6)
        List<BookingOrderEntity> recentBookings = bookingOrderService.getRecentBookings(6);
        model.addAttribute("recentBookings", recentBookings);

        return "Page/index";
    }

    @GetMapping("/search")
    public String searchRooms(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LocalDate checkInDate,
            @RequestParam(required = false) LocalDate checkOutDate,
            @RequestParam(required = false, defaultValue = "2") Integer guests,
            Model model) {

        List<RoomEntity> searchResults = roomService.searchRooms(location, checkInDate, checkOutDate, guests);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("location", location);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("guests", guests);

        return "Page/search-results";
    }
}
