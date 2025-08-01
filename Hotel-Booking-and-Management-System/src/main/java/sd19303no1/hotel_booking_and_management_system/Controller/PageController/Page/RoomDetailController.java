package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.ReviewEntity;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;
import sd19303no1.hotel_booking_and_management_system.Service.ReviewService;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;
import sd19303no1.hotel_booking_and_management_system.Service.SystemUserService;
import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;

@Controller
public class RoomDetailController {

    private static final Logger logger = LoggerFactory.getLogger(RoomDetailController.class);

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private SystemUserService systemUserService;

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
            // Tạm thời set số phòng còn trống là 1 (có thể implement logic phức tạp hơn sau)
            int roomAvailableCount = 1;
            model.addAttribute("roomAvailableCount", roomAvailableCount);

            // Thêm thông tin người dùng đã đăng nhập nếu có
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Authentication: {}", authentication);
            
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                String email = authentication.getName();
                logger.info("User email: {}", email);
                
                // Thử tìm trong CustomersEntity trước
                CustomersEntity customer = customersService.findByEmail(email);
                if (customer != null) {
                    logger.info("Found customer: {}", customer.getName());
                    model.addAttribute("currentUser", customer);
                } else {
                    // Nếu không tìm thấy trong CustomersEntity, thử tìm trong SystemUserEntity
                    SystemUserEntity systemUser = systemUserService.findByEmail(email);
                    if (systemUser != null) {
                        logger.info("Found system user: {}", systemUser.getUsername());
                        // Tạo một CustomersEntity tạm thời từ SystemUserEntity
                        CustomersEntity tempCustomer = new CustomersEntity();
                        tempCustomer.setName(systemUser.getUsername());
                        tempCustomer.setEmail(systemUser.getEmail());
                        tempCustomer.setPhone("Chưa cập nhật"); // SystemUserEntity không có phone
                        tempCustomer.setSystemUser(systemUser);
                        model.addAttribute("currentUser", tempCustomer);
                    } else {
                        logger.warn("No user found for email: {}", email);
                    }
                }
            } else {
                logger.info("No authenticated user or anonymous user");
            }

            return "Page/Details";

        } catch (Exception e) {
            logger.error("Error fetching room details for id: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin phòng.");
            return "redirect:/";
        }
    }
}