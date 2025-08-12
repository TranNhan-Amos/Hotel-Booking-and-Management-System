package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;


import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;

import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;

import sd19303no1.hotel_booking_and_management_system.Service.ReviewService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;

import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;
import sd19303no1.hotel_booking_and_management_system.Controller.PageController.BaseController;

@Controller
public class RoomDetailController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(RoomDetailController.class);

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private VoucherService voucherService;

    // CustomersService và SystemUserService đã được kế thừa từ BaseController

    // Mapping cụ thể cho /room/partner để redirect đến /partner/rooms
    @GetMapping("/room/partner")
    public String roomPartnerRedirect() {
        return "redirect:/partner/rooms";
    }

    @GetMapping("/room/{id}")
    public String roomDetail(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Fetching room details for id: {}", id);

            // Lấy thông tin phòng
            Optional<RoomEntity> roomOpt = roomService.findById(id);
            if (roomOpt.isEmpty()) {
                logger.warn("Room not found for id: {}", id);
                redirectAttributes.addFlashAttribute("error", "Phòng không tồn tại.");
                return "redirect:/";
            }
            
            RoomEntity room = roomOpt.get();
            if (!room.getStatus().equals(RoomEntity.RoomStatus.AVAILABLE)) {
                logger.warn("Room not available for id: {}", id);
                redirectAttributes.addFlashAttribute("error", "Phòng không khả dụng.");
                return "redirect:/";
            }

            // Khởi tạo amenities để tránh lỗi lazy loading
            Hibernate.initialize(room.getAmenities());
            List<String> amenities = room.getAmenities() != null ? 
                room.getAmenities().stream().collect(Collectors.toList()) : List.of();
            room.setAmenities(amenities);

            // Tính rating trung bình cho phòng
            Double avgRating = reviewService.getAverageRatingByRoomId(id);
            room.setAverageRating(avgRating != null ? avgRating : 0.0);
            model.addAttribute("room", room);

            // Thêm thông tin đối tác (nếu có)
            model.addAttribute("partner", room.getPartner());

            // Lấy các đánh giá cho phòng này
            List<ReviewEntity> reviews = reviewService.getReviewsByRoomId(id);
            model.addAttribute("reviews", reviews);

            // Lấy các phòng liên quan
            Page<RoomEntity> relatedRoomsPage = roomService.findRelatedRooms(room.getType(), id, PageRequest.of(0, 4));
            List<RoomEntity> relatedRooms = relatedRoomsPage.getContent()
                    .stream()
                    .filter(r -> r.getStatus().equals(RoomEntity.RoomStatus.AVAILABLE))
                    .peek(r -> {
                        // Khởi tạo amenities cho phòng liên quan
                        Hibernate.initialize(r.getAmenities());
                        List<String> relatedAmenities = r.getAmenities() != null ? 
                            r.getAmenities().stream().collect(Collectors.toList()) : List.of();
                        r.setAmenities(relatedAmenities);
                        // Tính rating trung bình
                        Double relatedAvgRating = reviewService.getAverageRatingByRoomId(r.getRoomId());
                        r.setAverageRating(relatedAvgRating != null ? relatedAvgRating : 0.0);
                    })
                    .collect(Collectors.toList());
            model.addAttribute("relatedRooms", relatedRooms);

            // Lấy vouchers hoạt động
            List<VoucherEntity> vouchers = voucherService.getAllActiveVouchers()
                    .stream()
                    .filter(v -> v.getStatus().equals("ACTIVE") && 
                                 v.getStartDate().isBefore(java.time.LocalDate.now()) && 
                                 v.getEndDate().isAfter(java.time.LocalDate.now()))
                    .collect(Collectors.toList());
            model.addAttribute("vouchers", vouchers);

            // Tính số phòng còn trống (mặc định: hôm nay đến hôm nay+1)
            LocalDate checkIn = LocalDate.now();
            LocalDate checkOut = checkIn.plusDays(1);
            // Sử dụng logic tính toán động dựa trên booking thay vì totalRooms
            int roomAvailableCount = roomService.getAvailableRoomCount(room.getRoomId(), checkIn, checkOut);
            model.addAttribute("roomAvailableCount", roomAvailableCount);
            
            // Thêm thông tin chi tiết về tính toán số phòng
            model.addAttribute("totalRooms", room.getTotalRooms());
            model.addAttribute("checkInDate", checkIn);
            model.addAttribute("checkOutDate", checkOut);

            // Thông tin người dùng hiện tại đã được xử lý bởi BaseController

            return "Page/Details";

        } catch (Exception e) {
            logger.error("Error fetching room details for id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin phòng.");
            return "redirect:/";
        }
    }
    
    // API endpoint để kiểm tra số phòng còn trống
    @GetMapping("/api/room/{id}/availability")
    @ResponseBody
    public Map<String, Object> checkRoomAvailability(
            @PathVariable Integer id,
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Parse dates
            LocalDate checkIn = checkInDate != null ? LocalDate.parse(checkInDate) : LocalDate.now();
            LocalDate checkOut = checkOutDate != null ? LocalDate.parse(checkOutDate) : checkIn.plusDays(1);
            
            // Validate dates
            if (checkIn.isAfter(checkOut)) {
                response.put("error", "Ngày check-in không thể sau ngày check-out");
                return response;
            }
            
            // Get room info
            Optional<RoomEntity> roomOpt = roomService.findById(id);
            if (roomOpt.isEmpty()) {
                response.put("error", "Không tìm thấy phòng");
                return response;
            }
            
            RoomEntity room = roomOpt.get();
            int availableCount = roomService.getAvailableRoomCountForDateRange(id, checkIn, checkOut);
            
            response.put("roomId", id);
            response.put("roomName", room.getNameNumber());
            response.put("totalRooms", room.getTotalRooms());
            response.put("availableRooms", availableCount);
            response.put("checkInDate", checkIn.toString());
            response.put("checkOutDate", checkOut.toString());
            response.put("isAvailable", availableCount > 0);
            
        } catch (Exception e) {
            logger.error("Error checking room availability for id: {}", id, e);
            response.put("error", "Có lỗi xảy ra khi kiểm tra tính khả dụng của phòng");
        }
        
        return response;
    }
}