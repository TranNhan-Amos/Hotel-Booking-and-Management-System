package sd19303no1.hotel_booking_and_management_system.Controller.PageController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomPartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomPartnerRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.VoucherRepository;
import sd19303no1.hotel_booking_and_management_system.DTO.RoomTypeDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    private static final String DEFAULT_AVATAR = "/img/customers/default-avatar.png";

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomPartnerRepository roomPartnerRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            
            // Thêm thông tin người dùng vào model
            model.addAttribute("currentUser", authentication.getPrincipal());
            
            // Tính số thông báo
            int notificationCount = calculateNotificationCount(authentication);
            model.addAttribute("notificationCount", notificationCount);

            // Lấy avatarPath
            String email = authentication.getName();
            String avatarPath = null;
            try {
                SystemUserEntity systemUser = systemUserRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found for email: " + email));
                
                // Ưu tiên lấy avatar từ CustomersEntity nếu là CUSTOMER
                if (systemUser.getRole() == SystemUserEntity.Role.CUSTOMER) {
                    CustomersEntity customer = customersRepository.findBySystemUser(systemUser)
                        .orElseGet(() -> customersRepository.findByEmail(email).orElse(null));
                    if (customer != null && customer.getAvatar() != null && !customer.getAvatar().isEmpty()) {
                        avatarPath = "/img/customers/" + customer.getAvatar();
                    }
                }
                
                // Fallback lấy từ SystemUserEntity
                if (avatarPath == null && systemUser.getImg() != null && !systemUser.getImg().isEmpty()) {
                    avatarPath = "/img/customers/" + systemUser.getImg();
                }
                
                // Nếu không có avatar, sử dụng ảnh mặc định
                if (avatarPath == null) {
                    avatarPath = DEFAULT_AVATAR;
                }
                
                logger.debug("Avatar path for user {}: {}", email, avatarPath);
                session.setAttribute("avatarPath", avatarPath);
                
            } catch (Exception e) {
                logger.error("Error fetching avatar for user {}: {}", email, e.getMessage());
                session.setAttribute("avatarPath", DEFAULT_AVATAR);
            }
        } else {
            // Đặt avatar mặc định cho người dùng chưa đăng nhập
            session.setAttribute("avatarPath", DEFAULT_AVATAR);
        }

        // Lấy danh sách loại phòng
        List<RoomTypeDTO> roomTypes = getRoomTypes();
        model.addAttribute("roomTypes", roomTypes);

        // Lấy danh sách phòng nổi bật (6 phòng mới nhất)
        List<RoomEntity> featuredRooms = roomRepository.findAll(PageRequest.of(0, 6)).getContent();
        model.addAttribute("featuredRooms", featuredRooms);

        // Lấy danh sách voucher đang hoạt động
        List<VoucherEntity> activeVouchers = voucherRepository.findActiveVouchers(LocalDate.now());
        model.addAttribute("activeVouchers", activeVouchers);

        // Lấy danh sách đặt phòng gần đây (3 booking mới nhất)
        List<BookingOrderEntity> recentBookings = bookingOrderRepository.findTopNByOrderByCreatedAtDesc(PageRequest.of(0, 3));
        model.addAttribute("recentBookings", recentBookings);

        // Lấy danh sách đánh giá gần đây (cho demo)
        model.addAttribute("recentReviews", null);

        return "Page/Index";
    }

    private List<RoomTypeDTO> getRoomTypes() {
        List<RoomPartnerEntity> roomPartners = roomPartnerRepository.findAll();
        
        return roomPartners.stream()
            .map(roomPartner -> {
                RoomTypeDTO dto = new RoomTypeDTO();
                dto.setName(roomPartner.getType());
                dto.setDescription("Khám phá " + roomPartner.getType() + " với tiện nghi hiện đại");
                dto.setImageUrl("https://images.unsplash.com/photo-1596436889106-be35e843f974?q=80&w=2070&auto=format&fit=crop");
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
}