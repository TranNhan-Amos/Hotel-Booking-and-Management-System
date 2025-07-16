package sd19303no1.hotel_booking_and_management_system.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private StatusRepository statusRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    // --- PHƯƠNG THỨC CHO KHÁCH HÀNG ĐẶT PHÒNG (Sử dụng bởi BookingController) ---
    @Transactional
    public BookingOrderEntity createBooking(String customerName, String email, String phone, String address,
                                            Integer roomId, LocalDate checkInDate, LocalDate checkOutDate,
                                            Integer voucherId, String specialRequests, Integer roomQuantity) {
        System.out.println("=== SERVICE DEBUG: createBooking started ===");

        
        // Validate đầu vào cơ bản
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Ngày nhận phòng và trả phòng không được để trống.");
        }
        
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");
        }
        
        if (checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("Ngày trả phòng phải khác ngày nhận phòng (ít nhất 1 đêm).");
        }
        
        // Kiểm tra ngày nhận phòng không được trong quá khứ
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhận phòng không thể là ngày trong quá khứ.");
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

        // 3. Kiểm tra phòng có sẵn trong khoảng thời gian đã chọn không
        boolean isRoomAvailable = room.getTotalRooms() != null && room.getTotalRooms() >= roomQuantity;
        if (!isRoomAvailable) {
            throw new RuntimeException("Không đủ phòng trống để đặt. Số phòng còn lại: " + room.getTotalRooms());
        }


        // 4. Lấy voucher nếu có
        VoucherEntity voucherEntity = null;
        if (voucherId != null) {
            voucherEntity = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại với ID: " + voucherId));
            if (!isVoucherValid(voucherEntity, checkInDate)) {
                throw new RuntimeException("Voucher " + voucherEntity.getCode() + " không hợp lệ hoặc đã hết hạn.");
            }
        }

        // 5. Lấy status PENDING cho khách hàng đặt mới
        final String PENDING_STATUS_NAME = "PENDING";
        StatusEntity status = statusRepository.findByStatusNameIgnoreCase(PENDING_STATUS_NAME);
        if (status == null) {
            System.out.println("=== SERVICE DEBUG: Creating new PENDING status ===");
            // Nếu trạng thái PENDING chưa có trong DB (nên được seed sẵn)
            StatusEntity newStatus = new StatusEntity();
            newStatus.setStatusName(PENDING_STATUS_NAME);
            newStatus.setDescription("Chờ xác nhận");
            status = statusRepository.save(newStatus);
        }


        System.out.println("=== SERVICE DEBUG: Status found/created: " + status.getStatusId() + " ===");

        // 5. Tạo đối tượng BookingOrderEntity

        // 6. Tính toán tổng tiền
        BigDecimal totalPrice = calculateTotalPrice(room.getPrice(), checkInDate, checkOutDate, voucherEntity);

        // 7. Tạo đối tượng BookingOrderEntity

        BookingOrderEntity booking = new BookingOrderEntity();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setEmail(email.toLowerCase());
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setBookingDate(LocalDate.now());
        booking.setVoucher(voucherEntity);
        booking.setStatus(status);
        booking.setTotalPrice(totalPrice);
        booking.setSpecialRequests(specialRequests);
        booking.setPaymentMethod("PAY_ON_ARRIVAL");
        booking.setPaymentStatus("PENDING");
        booking.setRoomQuantity(roomQuantity);

        BookingOrderEntity savedBooking = bookingOrderRepository.save(booking);
        
        // Trừ số phòng đã đặt khỏi tổng số phòng còn trống
        room.setTotalRooms(room.getTotalRooms() - roomQuantity);
        roomRepository.save(room);
        
        return savedBooking;
    }

    // Kiểm tra phòng có sẵn không
    public boolean checkRoomAvailability(Integer roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        // Kiểm tra xem có booking nào đã tồn tại cho phòng này trong khoảng thời gian này không
        List<BookingOrderEntity> conflictingBookings = bookingOrderRepository.findConflictingBookings(
            roomId, checkInDate, checkOutDate);
        
        return conflictingBookings.isEmpty();
    }

    // Kiểm tra voucher có hợp lệ không
    public boolean isVoucherValid(VoucherEntity voucher, LocalDate checkInDate) {
        if (voucher == null) return false;
        
        // Kiểm tra voucher còn hạn không
        if (voucher.getExpiryDate() != null && checkInDate.isAfter(voucher.getExpiryDate())) {
            return false;
        }
        
        // Kiểm tra voucher còn số lượng không
        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            return false;
        }
        
        return true;
    }

    // Tính toán tổng tiền
    public BigDecimal calculateTotalPrice(BigDecimal roomPrice, LocalDate checkInDate, LocalDate checkOutDate, VoucherEntity voucher) {
        // Tính số đêm
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // Giá cơ bản
        BigDecimal basePrice = roomPrice.multiply(BigDecimal.valueOf(nights));
        
        // Phí dịch vụ (10%)
        BigDecimal serviceFee = basePrice.multiply(new BigDecimal("0.1"));
        
        // Thuế VAT (10%)
        BigDecimal vat = basePrice.add(serviceFee).multiply(new BigDecimal("0.1"));
        
        // Tổng cộng
        BigDecimal total = basePrice.add(serviceFee).add(vat);
        
        // Áp dụng voucher nếu có
        if (voucher != null && voucher.getDiscountAmount() != null) {
            BigDecimal discount = voucher.getDiscountAmount();
            total = total.subtract(discount);
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                total = BigDecimal.ZERO;
            }
        }
        
        return total;
    }

    // Hủy đặt phòng
    @Transactional
    public BookingOrderEntity cancelBooking(Integer bookingId, String reason) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));
        
        // Kiểm tra xem có thể hủy không
        if (!canCancelBooking(booking)) {
            throw new RuntimeException("Không thể hủy đặt phòng này. Vui lòng kiểm tra chính sách hủy phòng.");
        }
        
        // Tính toán số tiền hoàn lại
        BigDecimal refundAmount = calculateRefundAmount(booking);
        
        // Cập nhật trạng thái
        StatusEntity cancelledStatus = statusRepository.findByStatusNameIgnoreCase("CANCELLED");
        if (cancelledStatus == null) {
            cancelledStatus = new StatusEntity();
            cancelledStatus.setStatusName("CANCELLED");
            cancelledStatus.setDescription("Đã hủy");
            cancelledStatus = statusRepository.save(cancelledStatus);
        }
        
        booking.setStatus(cancelledStatus);
        booking.setCancellationDate(LocalDateTime.now());
        booking.setCancellationReason(reason);
        booking.setRefundAmount(refundAmount);
        booking.setRefundStatus("PENDING");
        
        // Cộng lại số phòng đã đặt vào tổng số phòng còn trống
        RoomEntity room = booking.getRoom();
        if (room != null && booking.getRoomQuantity() != null) {
            room.setTotalRooms(room.getTotalRooms() + booking.getRoomQuantity());
            roomRepository.save(room);
        }
        
        return bookingOrderRepository.save(booking);
    }

    // Kiểm tra xem có thể hủy đặt phòng không
    public boolean canCancelBooking(BookingOrderEntity booking) {
        LocalDate today = LocalDate.now();
        LocalDate checkInDate = booking.getCheckInDate();
        
        // Tính số ngày trước ngày check-in
        long daysBeforeCheckIn = ChronoUnit.DAYS.between(today, checkInDate);
        
        // Chính sách hủy phòng:
        // - Hủy trước 24h: hoàn 100%
        // - Hủy trước 48h: hoàn 50%
        // - Hủy trong vòng 24h: không hoàn tiền
        return daysBeforeCheckIn > 1; // Có thể hủy nếu còn hơn 1 ngày
    }

    // Tính toán số tiền hoàn lại
    public BigDecimal calculateRefundAmount(BookingOrderEntity booking) {
        LocalDate today = LocalDate.now();
        LocalDate checkInDate = booking.getCheckInDate();
        
        long daysBeforeCheckIn = ChronoUnit.DAYS.between(today, checkInDate);
        
        if (daysBeforeCheckIn > 2) {
            // Hủy trước 48h: hoàn 100%
            return booking.getTotalPrice();
        } else if (daysBeforeCheckIn > 1) {
            // Hủy trước 24h: hoàn 50%
            return booking.getTotalPrice().multiply(new BigDecimal("0.5"));
        } else {
            // Hủy trong vòng 24h: không hoàn tiền
            return BigDecimal.ZERO;
        }
    }

    // Xử lý hoàn tiền
    @Transactional
    public BookingOrderEntity processRefund(Integer bookingId) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));
        
        if (!"CANCELLED".equals(booking.getStatus().getStatusName())) {
            throw new RuntimeException("Chỉ có thể hoàn tiền cho đặt phòng đã hủy.");
        }
        
        if ("COMPLETED".equals(booking.getRefundStatus())) {
            throw new RuntimeException("Đã hoàn tiền cho đặt phòng này.");
        }
        
        // Cập nhật trạng thái hoàn tiền
        booking.setRefundStatus("COMPLETED");
        booking.setRefundDate(LocalDateTime.now());
        
        // Cập nhật trạng thái booking thành REFUNDED
        StatusEntity refundedStatus = statusRepository.findByStatusNameIgnoreCase("REFUNDED");
        if (refundedStatus == null) {
            refundedStatus = new StatusEntity();
            refundedStatus.setStatusName("REFUNDED");
            refundedStatus.setDescription("Đã hoàn tiền");
            refundedStatus = statusRepository.save(refundedStatus);
        }
        booking.setStatus(refundedStatus);
        
        return bookingOrderRepository.save(booking);
    }

    // Xác nhận thanh toán tại khách sạn
    @Transactional
    public BookingOrderEntity confirmPayment(Integer bookingId) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));
        
        booking.setPaymentStatus("PAID");
        booking.setPaidDate(LocalDateTime.now());
        
        // Cập nhật trạng thái thành CONFIRMED
        StatusEntity confirmedStatus = statusRepository.findByStatusNameIgnoreCase("CONFIRMED");
        if (confirmedStatus == null) {
            confirmedStatus = new StatusEntity();
            confirmedStatus.setStatusName("CONFIRMED");
            confirmedStatus.setDescription("Đã xác nhận");
            confirmedStatus = statusRepository.save(confirmedStatus);
        }
        booking.setStatus(confirmedStatus);
        
        return bookingOrderRepository.save(booking);
    }

    // Check-in
    @Transactional
    public BookingOrderEntity checkIn(Integer bookingId) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));
        
        if (!"CONFIRMED".equals(booking.getStatus().getStatusName())) {
            throw new RuntimeException("Chỉ có thể check-in cho đặt phòng đã xác nhận.");
        }
        
        StatusEntity checkedInStatus = statusRepository.findByStatusNameIgnoreCase("CHECKED_IN");
        if (checkedInStatus == null) {
            checkedInStatus = new StatusEntity();
            checkedInStatus.setStatusName("CHECKED_IN");
            checkedInStatus.setDescription("Đã check-in");
            checkedInStatus = statusRepository.save(checkedInStatus);
        }
        booking.setStatus(checkedInStatus);
        
        return bookingOrderRepository.save(booking);
    }

    // Check-out
    @Transactional
    public BookingOrderEntity checkOut(Integer bookingId) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));
        
        if (!"CHECKED_IN".equals(booking.getStatus().getStatusName())) {
            throw new RuntimeException("Chỉ có thể check-out cho đặt phòng đã check-in.");
        }
        
        StatusEntity checkedOutStatus = statusRepository.findByStatusNameIgnoreCase("CHECKED_OUT");
        if (checkedOutStatus == null) {
            checkedOutStatus = new StatusEntity();
            checkedOutStatus.setStatusName("CHECKED_OUT");
            checkedOutStatus.setDescription("Đã check-out");
            checkedOutStatus = statusRepository.save(checkedOutStatus);
        }
        booking.setStatus(checkedOutStatus);


        BookingOrderEntity savedBooking = bookingOrderRepository.save(booking);
        System.out.println("=== SERVICE DEBUG: Booking saved successfully with ID: " + savedBooking.getBookingId() + " ===");
        
        return savedBooking;
    }

    // --- PHƯƠNG THỨC CHO ADMIN QUẢN LÝ CRUD (Sử dụng bởi AdminBookingApiController) ---

    @Transactional
    public BookingOrderEntity adminCreateOrUpdateBooking(Integer existingBookingId, AdminBookingRequestDTO dto, boolean isCustomerFlowTriggered) {
        System.out.println("[SERVICE DEBUG] adminCreateOrUpdateBooking - existingBookingId: " + existingBookingId);
        System.out.println("[SERVICE DEBUG] adminCreateOrUpdateBooking - DTO received: " + dto.toString());

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

        if (existingBookingId != null) {
            booking = bookingOrderRepository.findById(existingBookingId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + existingBookingId));
            customer = booking.getCustomer();

            if (dto.getCustomerId() != null && dto.getCustomerId().equals(customer.getCustomerId())) {
                 if(dto.getCustomerName() != null && !dto.getCustomerName().isEmpty()) customer.setName(dto.getCustomerName());
                 if(dto.getCustomerPhone() != null) customer.setPhone(dto.getCustomerPhone());
                 if(dto.getCustomerAddress() != null) customer.setAddress(dto.getCustomerAddress());
                 customersRepository.save(customer);
            } else if (dto.getCustomerId() != null) {
                 customer = customersRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Khách hàng với ID: " + dto.getCustomerId() + " không tồn tại."));
            }
        } else {
            booking = new BookingOrderEntity();
            booking.setBookingDate(LocalDate.now());

            if (dto.getCustomerId() != null) {
                customer = customersRepository.findById(dto.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Khách hàng với ID: " + dto.getCustomerId() + " không tồn tại."));
            } else {
                customer = customersRepository.findByEmail(dto.getCustomerEmail().toLowerCase())
                        .orElseGet(() -> {
                            CustomersEntity newCust = new CustomersEntity();
                            newCust.setName(dto.getCustomerName());
                            newCust.setEmail(dto.getCustomerEmail().toLowerCase());
                            newCust.setPhone(dto.getCustomerPhone());
                            newCust.setAddress(dto.getCustomerAddress());
                            return customersRepository.save(newCust);
                        });
            }
        }

        booking.setCustomer(customer);
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

        // Tính toán lại tổng tiền
        BigDecimal totalPrice = calculateTotalPrice(room.getPrice(), dto.getCheckInDate(), dto.getCheckOutDate(), booking.getVoucher());
        booking.setTotalPrice(totalPrice);

        String statusNameToSet;
        if (existingBookingId == null && isCustomerFlowTriggered) {
            statusNameToSet = "PENDING";
        } else if (dto.getBookingStatus() != null && !dto.getBookingStatus().trim().isEmpty()) {
            statusNameToSet = dto.getBookingStatus();
        } else if (existingBookingId != null) {
            statusNameToSet = booking.getStatus().getStatusName();
        } else {
            statusNameToSet = "PENDING";
        }

        StatusEntity statusEntity = statusRepository.findByStatusNameIgnoreCase(statusNameToSet);
        if (statusEntity == null) {
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

    public List<BookingOrderEntity> getRecentBookings(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Giới hạn số lượng đặt phòng phải lớn hơn 0");
        }
        return bookingOrderRepository.findTopNByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    public List<BookingOrderEntity> getBookingsByCustomerEmailForCustomer(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        return bookingOrderRepository.findByEmailOrderByCreatedAtDesc(email.toLowerCase());
    }

    public List<CustomersEntity> findAllCustomersForAdmin() {
        return customersRepository.findAll();
    }

    // Tìm booking theo trạng thái
    public List<BookingOrderEntity> findByStatusName(String statusName) {
        return bookingOrderRepository.findByStatusName(statusName);
    }

    //Đếm phòng booking hôm nay theo partner
    public long countTodayBookingsByPartner(Long partnerId) {
        return bookingOrderRepository.countTodayBookingsByPartner(partnerId);
    }

    //Hiện tất cả booking của partner
    public List<BookingOrderEntity> findAllBookingsByPartner(Long partnerId) {
        return bookingOrderRepository.findAllBookingsByPartner(partnerId);
    }

    //Doanh thu hôm nay theo partner
    public Integer sumDoanhThuToday(Long partnerId) {
        return bookingOrderRepository.sumDoanhThuToday(partnerId);
    }

    //DataReport for Partner
    public List<Object[]> getReportData(Long partnerId ,LocalDate startDate, LocalDate endDate) {
        return bookingOrderRepository.getReportData(partnerId ,startDate, endDate);
    }
}