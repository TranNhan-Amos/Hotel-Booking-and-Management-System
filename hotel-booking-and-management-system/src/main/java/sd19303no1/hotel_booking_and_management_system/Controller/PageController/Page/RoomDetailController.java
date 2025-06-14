package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.ReviewService;
import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;

@Controller
public class RoomDetailController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private VoucherService voucherService;

    @GetMapping("/room/{id}")
    public String roomDetail(@PathVariable Integer id, Model model) {
        // Lấy thông tin phòng
        RoomEntity room = roomService.findById(id);
        if (room == null) {
            return "redirect:/"; // Redirect về trang chủ nếu không tìm thấy phòng
        }
        
        // Tính rating trung bình cho phòng
        Double avgRating = reviewService.getAverageRatingByRoomId(id);
        room.setAverageRating(avgRating != null ? avgRating : 0.0);

        model.addAttribute("room", room);

        // Lấy các đánh giá cho phòng này
        List<ReviewEntity> reviews = reviewService.getReviewsByRoomId(id);
        model.addAttribute("reviews", reviews);

        // Lấy các phòng liên quan (cùng loại)
        List<RoomEntity> relatedRooms = roomService.getRelatedRooms(room.getType(), id, 4);
        // Tính rating cho các phòng liên quan
        relatedRooms.forEach(relatedRoom -> {
            Double relatedAvgRating = reviewService.getAverageRatingByRoomId(relatedRoom.getRoomId());
            relatedRoom.setAverageRating(relatedAvgRating != null ? relatedAvgRating : 0.0);
        });
        model.addAttribute("relatedRooms", relatedRooms);

        // Lấy vouchers cho form đặt phòng
        List<VoucherEntity> vouchers = voucherService.getAllActiveVouchers();
        model.addAttribute("vouchers", vouchers);

        return "Page/Details";
    }
}
