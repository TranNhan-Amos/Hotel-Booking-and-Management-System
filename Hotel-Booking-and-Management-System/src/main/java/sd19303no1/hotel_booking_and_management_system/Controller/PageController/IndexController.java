package sd19303no1.hotel_booking_and_management_system.Controller.PageController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.VoucherRepository;
import sd19303no1.hotel_booking_and_management_system.DTO.RoomTypeDTO;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class IndexController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    private static final String DEFAULT_AVATAR = "/img/customers/default-avatar.svg";

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private RoomService roomService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            
            // Tính số thông báo
            int notificationCount = calculateNotificationCount(authentication);
            model.addAttribute("notificationCount", notificationCount);

            // Lấy avatarPath
            String email = authentication.getName();
            String avatarPath = null;
            try {
                SystemUserEntity systemUser = systemUserRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new IllegalStateException("User not found for email: " + email));
                
                logger.debug("Found systemUser: {} with role: {}", systemUser.getEmail(), systemUser.getRole());
                
                // Ưu tiên lấy avatar từ CustomersEntity nếu là CUSTOMER
                if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                    CustomersEntity customer = customersRepository.findBySystemUser(systemUser)
                        .orElseGet(() -> customersRepository.findByEmailIgnoreCase(email).orElse(null));
                    logger.debug("Found customer: {}", customer != null ? customer.getEmail() : "null");
                    if (customer != null && customer.getAvatar() != null && !customer.getAvatar().isEmpty()) {
                        avatarPath = "/img/customers/" + customer.getAvatar();
                        logger.debug("Using customer avatar: {}", avatarPath);
                    }
                }
                
                // Fallback lấy từ SystemUserEntity
                if (avatarPath == null && systemUser.getImg() != null && !systemUser.getImg().isEmpty()) {
                    avatarPath = "/img/customers/" + systemUser.getImg();
                    logger.debug("Using systemUser avatar: {}", avatarPath);
                }
                
                // Nếu không có avatar, sử dụng ảnh mặc định
                if (avatarPath == null) {
                    avatarPath = DEFAULT_AVATAR;
                    logger.debug("Using default avatar: {}", avatarPath);
                }
                
                logger.debug("Avatar path for user {}: {}", email, avatarPath);
                session.setAttribute("avatarPath", avatarPath);
                logger.debug("Session avatarPath set to: {}", session.getAttribute("avatarPath"));
                
            } catch (Exception e) {
                logger.error("Error fetching avatar for user {}: {}", email, e.getMessage());
                session.setAttribute("avatarPath", DEFAULT_AVATAR);
            }
        } else {
            // Đặt avatar mặc định cho người dùng chưa đăng nhập
            session.setAttribute("avatarPath", DEFAULT_AVATAR);
        }

        // Tính toán thống kê phòng
        long totalAvailableRooms = calculateTotalAvailableRooms();
        long totalRooms = calculateTotalRooms();
        long totalHotels = calculateTotalHotels();
        double websiteAverageRating = calculateAverageRating();
        long totalCustomers = calculateTotalCustomers();
        
        model.addAttribute("totalAvailableRooms", totalAvailableRooms);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalHotels", totalHotels);
        model.addAttribute("averageRating", websiteAverageRating);
        model.addAttribute("totalCustomers", totalCustomers);

        // Lấy danh sách loại phòng
        List<RoomTypeDTO> roomTypes = getRoomTypes();
        model.addAttribute("roomTypes", roomTypes);

        // Lấy danh sách phòng nổi bật (6 phòng mới nhất)
        Page<RoomEntity> featuredRoomsPage = roomRepository.findFeaturedRooms(PageRequest.of(0, 6));
        List<RoomEntity> featuredRooms = featuredRoomsPage.getContent();
        
        // Tính đánh giá trung bình và load amenities cho từng phòng
        for (RoomEntity room : featuredRooms) {
            // Tính đánh giá trung bình
            if (room.getReviews() != null && !room.getReviews().isEmpty()) {
                double averageRating = room.getReviews().stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
                room.setAverageRating(averageRating);
            } else {
                room.setAverageRating(0.0);
            }
            
            // Load amenities
            List<String> amenities = roomRepository.findAmenitiesByRoomId(room.getRoomId());
            room.setAmenities(amenities);
        }
        
        model.addAttribute("featuredRooms", featuredRooms);

        // Lấy danh sách voucher đang hoạt động
        List<VoucherEntity> activeVouchers = voucherRepository.findActiveVouchersOrderByDiscount(LocalDate.now());
        model.addAttribute("activeVouchers", activeVouchers);

        // Lấy danh sách đặt phòng gần đây (3 booking mới nhất)
        List<BookingOrderEntity> recentBookings = bookingOrderRepository.findRecentBookingsWithFullDetails(PageRequest.of(0, 3));
        model.addAttribute("recentBookings", recentBookings);

        // Lấy danh sách đánh giá gần đây (cho demo)
        model.addAttribute("recentReviews", null);

        return "Page/Index";
    }

    private List<RoomTypeDTO> getRoomTypes() {
        List<RoomEntity> rooms = roomRepository.findAll();
        
        return rooms.stream()
            .map(room -> {
                RoomTypeDTO dto = new RoomTypeDTO();
                dto.setName(room.getType().name());
                dto.setDescription("Khám phá " + room.getType().name() + " với tiện nghi hiện đại");
                dto.setImageUrl(room.getImageUrls() != null && !room.getImageUrls().isEmpty() 
                    ? room.getImageUrls().get(0) 
                    : "https://images.unsplash.com/photo-1596436889106-be35e843f974?q=80&w=2070&auto=format&fit=crop");
                return dto;
            })
            .distinct()
            .limit(4)
            .collect(Collectors.toList());
    }

    private int calculateNotificationCount(Authentication authentication) {
        // Demo logic - có thể thay bằng logic thực tế
        String username = authentication.getName();
        
        // Kiểm tra nếu là admin
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            // Admin có thể có thông báo về đặt phòng mới, báo cáo, etc.
            return (int) (Math.random() * 5) + 1;
        } else {
            // Khách hàng có thể có thông báo về đặt phòng, khuyến mãi, etc.
            return (int) (Math.random() * 3);
        }
    }

    // Tính tổng số phòng khả dụng
    private long calculateTotalAvailableRooms() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate nextMonth = today.plusMonths(1);
            
            return roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == RoomEntity.RoomStatus.AVAILABLE)
                .mapToLong(room -> {
                    // Sử dụng RoomService để tính toán chính xác số phòng khả dụng
                    return roomService.getAvailableRoomCount(room.getRoomId(), today, nextMonth);
                })
                .sum();
        } catch (Exception e) {
            logger.error("Error calculating total available rooms: {}", e.getMessage());
            return 0;
        }
    }

    // Tính tổng số phòng
    private long calculateTotalRooms() {
        try {
            return roomRepository.findAll().stream()
                .mapToLong(RoomEntity::getTotalRooms)
                .sum();
        } catch (Exception e) {
            logger.error("Error calculating total rooms: {}", e.getMessage());
            return 0;
        }
    }

    // Tính tổng số khách sạn (partners)
    private long calculateTotalHotels() {
        try {
            return roomRepository.findAll().stream()
                .filter(room -> room.getPartner() != null)
                .map(room -> room.getPartner().getId())
                .distinct()
                .count();
        } catch (Exception e) {
            logger.error("Error calculating total hotels: {}", e.getMessage());
            return 0;
        }
    }

    // Tính đánh giá trung bình
    private double calculateAverageRating() {
        try {
            return roomRepository.findAll().stream()
                .flatMap(room -> room.getReviews() != null ? room.getReviews().stream() : java.util.stream.Stream.empty())
                .mapToDouble(review -> review.getRating())
                .average()
                .orElse(0.0);
        } catch (Exception e) {
            logger.error("Error calculating average rating: {}", e.getMessage());
            return 0.0;
        }
    }

    // Tính tổng số khách hàng
    private long calculateTotalCustomers() {
        try {
            return customersRepository.findAll().stream()
                .filter(customer -> customer.getSystemUser() != null)
                .count();
        } catch (Exception e) {
            logger.error("Error calculating total customers: {}", e.getMessage());
            return 0;
        }
    }

    @GetMapping("/test-payment")
    public String testPayment() {
        return "Page/test-payment";
    }
    
    @GetMapping("/test-csrf")
    public String testCsrf() {
        return "test-csrf";
    }
    
    @PostMapping("/test-csrf")
    public String testCsrfPost() {
        return "redirect:/?csrf-success=true";
    }

    @GetMapping("/test-logout")
    public String testLogout() {
        return "test-logout";
    }
    
    @GetMapping("/test")
    public String testCancelBooking() {
        return "Page/test";
    }
}