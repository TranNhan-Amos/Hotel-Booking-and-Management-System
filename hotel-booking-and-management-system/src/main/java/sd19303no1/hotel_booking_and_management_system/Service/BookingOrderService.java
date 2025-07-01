package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sd19303no1.hotel_booking_and_management_system.DTO.AdminBookingRequestDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.StatusEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.StatusRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.VoucherRepository;


@Service
public class BookingOrderService {

    @Autowired
    private BookingOrderRepository bookingOrderRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private StatusRepository statusRepository; // Đảm bảo đã inject chính xác
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    // --- PHƯƠNG THỨC CHO KHÁCH HÀNG ĐẶT PHÒNG (Sử dụng bởi BookingController) ---
    @Transactional
    public BookingOrderEntity createBooking(String customerName, String email, String phone, String address,
                                            Integer roomId, LocalDate checkInDate, LocalDate checkOutDate,
                                            Integer voucherId) {
        System.out.println("=== SERVICE DEBUG: createBooking started ===");
        
        // Validate đầu vào cơ bản
        if (checkInDate == null || checkOutDate == null ||
            checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("Ngày nhận phòng và trả phòng không hợp lệ.");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("ID phòng không được để trống.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email khách hàng không được để trống.");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }

        System.out.println("=== SERVICE DEBUG: Validation passed ===");

        // 1. Tìm hoặc tạo customer
        CustomersEntity customer = customersRepository.findByEmail(email.toLowerCase())
                .orElseGet(() -> {
                    System.out.println("=== SERVICE DEBUG: Creating new customer ===");
                    CustomersEntity newCustomer = new CustomersEntity();
                    newCustomer.setName(customerName);
                    newCustomer.setEmail(email.toLowerCase());
                    newCustomer.setPhone(phone);
                    newCustomer.setAddress(address);
                    // Cân nhắc mật khẩu mặc định nếu khách hàng này có thể đăng nhập
                    return customersRepository.save(newCustomer);
                });

        System.out.println("=== SERVICE DEBUG: Customer found/created: " + customer.getCustomerId() + " ===");

        // 2. Lấy room
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại với ID: " + roomId));

        System.out.println("=== SERVICE DEBUG: Room found: " + room.getRoomId() + " ===");

        // TODO: Kiểm tra phòng có sẵn trong khoảng thời gian đã chọn không? (QUAN TRỌNG)
        // boolean isRoomAvailable = checkRoomAvailability(roomId, checkInDate, checkOutDate);
        // if (!isRoomAvailable) {
        //     throw new RuntimeException("Phòng " + roomId + " không còn trống trong khoảng thời gian đã chọn.");
        // }

        // 3. Lấy voucher nếu có
        VoucherEntity voucherEntity = null;
        if (voucherId != null) {
            voucherEntity = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại với ID: " + voucherId));
            // TODO: Kiểm tra voucher có hợp lệ không (còn hạn, điều kiện áp dụng,...)
            // if (!isVoucherValid(voucherEntity, ...)) {
            //     throw new RuntimeException("Voucher " + voucherEntity.getCode() + " không hợp lệ.");
            // }
        }

        // 4. Lấy status PENDING cho khách hàng đặt mới
        final String PENDING_STATUS_NAME = "PENDING";
        StatusEntity status = statusRepository.findByStatusNameIgnoreCase(PENDING_STATUS_NAME);
        if (status == null) {
            System.out.println("=== SERVICE DEBUG: Creating new PENDING status ===");
            // Nếu trạng thái PENDING chưa có trong DB (nên được seed sẵn)
            StatusEntity newStatus = new StatusEntity();
            newStatus.setStatusName(PENDING_STATUS_NAME);
            status = statusRepository.save(newStatus);
        }

        System.out.println("=== SERVICE DEBUG: Status found/created: " + status.getStatusId() + " ===");

        // 5. Tạo đối tượng BookingOrderEntity
        BookingOrderEntity booking = new BookingOrderEntity();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setEmail(email.toLowerCase()); // Email lúc đặt phòng
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setBookingDate(LocalDate.now()); // Ngày khách hàng thực hiện đặt
        // booking.setCreatedAt(LocalDateTime.now()); // @CreationTimestamp sẽ tự động xử lý

        booking.setVoucher(voucherEntity);
        booking.setStatus(status);

        System.out.println("=== SERVICE DEBUG: Booking object created, saving... ===");

        // TODO: Tính toán tổng tiền (totalPrice) cho đơn đặt hàng
        // BigDecimal totalPrice = calculateTotalPrice(room.getPrice(), checkInDate, checkOutDate, voucherEntity);
        // booking.setTotalPrice(totalPrice);

        BookingOrderEntity savedBooking = bookingOrderRepository.save(booking);
        System.out.println("=== SERVICE DEBUG: Booking saved successfully with ID: " + savedBooking.getBookingId() + " ===");
        
        return savedBooking;
    }

    // --- PHƯƠNG THỨC CHO ADMIN QUẢN LÝ CRUD (Sử dụng bởi AdminBookingApiController) ---

    @Transactional // Đảm bảo adminCreateOrUpdateBooking là một giao dịch nguyên tử
    public BookingOrderEntity adminCreateOrUpdateBooking(Integer existingBookingId, AdminBookingRequestDTO dto, boolean isCustomerFlowTriggered) {
        // (Logic của phương thức này giữ nguyên như ở phản hồi trước,
        // đảm bảo nó sử dụng statusRepository.findByStatusNameIgnoreCase(dto.getBookingStatus())
        // hoặc statusRepository.findByStatusNameIgnoreCase("PENDING") một cách chính xác)

        System.out.println("[SERVICE DEBUG] adminCreateOrUpdateBooking - existingBookingId: " + existingBookingId);
    System.out.println("[SERVICE DEBUG] adminCreateOrUpdateBooking - DTO received: " + dto.toString());
    // logger.info("[SERVICE DEBUG] adminCreateOrUpdateBooking - DTO: {}", dto);

    if (dto.getCustomerEmail() != null) {
        System.out.println("[SERVICE DEBUG] adminCreateOrUpdateBooking - DTO Customer Email: " + dto.getCustomerEmail());
        if (dto.getCustomerEmail().equalsIgnoreCase("0.email")) {
            System.err.println("[SERVICE CRITICAL DEBUG] DTO contains '0.email' for customerEmail in service method!");
        }
    }

        // Validation
        if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null ||
            dto.getCheckInDate().isAfter(dto.getCheckOutDate()) || dto.getCheckInDate().isEqual(dto.getCheckOutDate())) {
            throw new IllegalArgumentException("Ngày nhận phòng và trả phòng không hợp lệ.");
        }
        if (dto.getRoomId() == null) {
            throw new IllegalArgumentException("ID phòng không được để trống.");
        }
        if (dto.getCustomerEmail() == null || dto.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email khách hàng không được để trống.");
        }

        BookingOrderEntity booking;
        CustomersEntity customer;

        if (existingBookingId != null) { // Cập nhật booking hiện có
            booking = bookingOrderRepository.findById(existingBookingId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + existingBookingId));
            customer = booking.getCustomer();

            // Admin có thể cập nhật thông tin khách hàng liên quan đến booking này
            if (dto.getCustomerId() != null && dto.getCustomerId().equals(customer.getCustomerId())) {
                 if(dto.getCustomerName() != null && !dto.getCustomerName().isEmpty()) customer.setName(dto.getCustomerName());
                 // Cẩn thận khi cho admin đổi email của customer, có thể ảnh hưởng đăng nhập
                 // if(dto.getCustomerEmail() != null && !dto.getCustomerEmail().isEmpty()) customer.setEmail(dto.getCustomerEmail().toLowerCase());
                 if(dto.getCustomerPhone() != null) customer.setPhone(dto.getCustomerPhone());
                 if(dto.getCustomerAddress() != null) customer.setAddress(dto.getCustomerAddress());
                 customersRepository.save(customer);
            } else if (dto.getCustomerId() != null) { // Admin gán booking cho một customer khác
                 customer = customersRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Khách hàng với ID: " + dto.getCustomerId() + " không tồn tại."));
            }
            // Nếu customerId là null khi cập nhật, nhưng email trong DTO thay đổi,
            // nó sẽ cập nhật trường email trực tiếp của booking.
        } else { // Tạo booking mới bởi admin (hoặc được trigger bởi customer flow)
            booking = new BookingOrderEntity();
            booking.setBookingDate(LocalDate.now());

            if (dto.getCustomerId() != null) { // Admin chọn một khách hàng đã có
                customer = customersRepository.findById(dto.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Khách hàng với ID: " + dto.getCustomerId() + " không tồn tại."));
            } else { // Admin tạo khách hàng mới hoặc tìm theo email
                customer = customersRepository.findByEmail(dto.getCustomerEmail().toLowerCase())
                        .orElseGet(() -> {
                            CustomersEntity newCust = new CustomersEntity();
                            newCust.setName(dto.getCustomerName()); // Bắt buộc phải có tên nếu tạo mới
                            newCust.setEmail(dto.getCustomerEmail().toLowerCase());
                            newCust.setPhone(dto.getCustomerPhone());
                            newCust.setAddress(dto.getCustomerAddress());
                            return customersRepository.save(newCust);
                        });
            }
        }

        booking.setCustomer(customer);
        // Email này là email liên hệ cho đơn đặt phòng, có thể khác với email tài khoản của customer
        booking.setEmail(dto.getCustomerEmail().toLowerCase());

        RoomEntity room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại với ID: " + dto.getRoomId()));
        booking.setRoom(room);

        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());

        if (dto.getVoucherId() != null) {
            VoucherEntity voucherEntity = voucherRepository.findById(dto.getVoucherId())
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại với ID: " + dto.getVoucherId()));
            booking.setVoucher(voucherEntity);
        } else {
            booking.setVoucher(null);
        }

        // Xác định trạng thái
        String statusNameToSet;
        if (existingBookingId == null && isCustomerFlowTriggered) { // Đặt phòng mới từ khách hàng
            statusNameToSet = "PENDING";
        } else if (dto.getBookingStatus() != null && !dto.getBookingStatus().trim().isEmpty()) { // Admin đặt trạng thái cụ thể
            statusNameToSet = dto.getBookingStatus();
        } else if (existingBookingId != null) { // Cập nhật, giữ trạng thái cũ nếu không có trạng thái mới từ DTO
            statusNameToSet = booking.getStatus().getStatusName();
        } else { // Tạo mới bởi admin, không có trạng thái cụ thể -> PENDING
            statusNameToSet = "PENDING";
        }

        StatusEntity statusEntity = statusRepository.findByStatusNameIgnoreCase(statusNameToSet);
        if (statusEntity == null) {
            // Nếu trạng thái không tồn tại, có thể tạo mới hoặc báo lỗi.
            // Ví dụ, nếu admin nhập một trạng thái tùy ý không có trong DB.
            // Tạm thời báo lỗi để đảm bảo tính nhất quán.
            throw new IllegalArgumentException("Trạng thái '" + statusNameToSet + "' không hợp lệ hoặc không tồn tại.");
        }
        booking.setStatus(statusEntity);
        return bookingOrderRepository.save(booking);
    }


    public List<BookingOrderEntity> findAllBookingsForAdmin() {
        return bookingOrderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<BookingOrderEntity> findBookingByIdForAdmin(Integer bookingId) {
        return bookingOrderRepository.findById(bookingId);
    }

    @Transactional
    public void adminDeleteBookingById(Integer bookingId) {
        if (!bookingOrderRepository.existsById(bookingId)) {
            throw new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId);
        }
        bookingOrderRepository.deleteById(bookingId);
    }

    @Transactional
    public BookingOrderEntity adminUpdateBookingStatus(Integer bookingId, String newStatusName) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));

        if (newStatusName == null || newStatusName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên trạng thái mới không được để trống.");
        }

        StatusEntity newStatus = statusRepository.findByStatusNameIgnoreCase(newStatusName);
        if (newStatus == null) {
            throw new IllegalArgumentException("Trạng thái '" + newStatusName + "' không hợp lệ hoặc không tồn tại.");
        }
        booking.setStatus(newStatus);
        return bookingOrderRepository.save(booking);
    }

    // --- PHƯƠNG THỨC CHUNG (Sử dụng bởi IndexController, etc.) ---
    public List<BookingOrderEntity> getRecentBookings(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Giới hạn số lượng đặt phòng phải lớn hơn 0");
        }
        // Giả sử bạn có phương thức này trong repository, nếu không hãy tạo nó
        // Ví dụ: @Query("SELECT b FROM BookingOrderEntity b ORDER BY b.createdAt DESC")
        // List<BookingOrderEntity> findRecentBookings(Pageable pageable);
        return bookingOrderRepository.findTopNByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }
    // Bạn cần thêm phương thức findTopNByOrderByCreatedAtDesc vào BookingOrderRepository:
    // List<BookingOrderEntity> findTopNByOrderByCreatedAtDesc(Pageable pageable);


    // Phương thức cho khách hàng xem lịch sử đặt phòng của họ (ví dụ)
    public List<BookingOrderEntity> getBookingsByCustomerEmailForCustomer(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        return bookingOrderRepository.findByEmailOrderByCreatedAtDesc(email.toLowerCase());
    }

    public List<CustomersEntity> findAllCustomersForAdmin() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllCustomersForAdmin'");
    }
}