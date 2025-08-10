package sd19303no1.hotel_booking_and_management_system.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sd19303no1.hotel_booking_and_management_system.DTO.AdminBookingRequestDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.MonthlyRevenueReportPartnerDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.ReportsPartnerDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.PaymentMethod;
import sd19303no1.hotel_booking_and_management_system.Entity.PaymentStatus;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.StatusEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.PartnerRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.RoomRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.StatusRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.VoucherRepository;
import sd19303no1.hotel_booking_and_management_system.Service.RoomService;

@Service
public class BookingOrderService {

    @Autowired
    private BookingOrderRepository bookingOrderRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private RoomService roomService;

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
        CustomersEntity customer = customersRepository.findByEmailIgnoreCase(email.toLowerCase())
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

        // TODO: Kiểm tra phòng có sẵn trong khoảng thời gian đã chọn không? (QUAN
        // TRỌNG)
        // boolean isRoomAvailable = checkRoomAvailability(roomId, checkInDate,
        // checkOutDate);
        // if (!isRoomAvailable) {
        // throw new RuntimeException("Phòng " + roomId + " không còn trống trong khoảng
        // thời gian đã chọn.");
        // }

        // 3. Kiểm tra phòng có sẵn trong khoảng thời gian đã chọn không
        int availableRooms = roomService.getAvailableRoomCount(roomId, checkInDate, checkOutDate);
        boolean isRoomAvailable = availableRooms >= roomQuantity;
        if (!isRoomAvailable) {
            throw new RuntimeException("Không đủ phòng có sẵn để đặt. Số phòng còn lại: " + availableRooms);
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
        BigDecimal totalPrice = calculateTotalPrice(room.getPrice(), checkInDate, checkOutDate, voucherEntity, roomQuantity);

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
        booking.setBookingStatus("PENDING");
        booking.setTotalPrice(totalPrice);
        booking.setSpecialRequests(specialRequests);
        booking.setPaymentMethod("PAY_ON_ARRIVAL");
        booking.setPaymentStatus("PENDING");
        booking.setRoomQuantity(roomQuantity);

        BookingOrderEntity savedBooking = bookingOrderRepository.save(booking);

        // Không cần trừ totalRooms vì sẽ tính động theo booking
        // Số phòng có sẵn sẽ được tính trong RoomService.getAvailableRoomCount()

        return savedBooking;
    }

    // Kiểm tra phòng có sẵn không
    public boolean checkRoomAvailability(Integer roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        // Kiểm tra xem có booking nào đã tồn tại cho phòng này trong khoảng thời gian
        // này không
        List<BookingOrderEntity> conflictingBookings = bookingOrderRepository.findConflictingBookings(
                roomId, checkInDate, checkOutDate);

        return conflictingBookings.isEmpty();
    }

    // Kiểm tra voucher có hợp lệ không
    public boolean isVoucherValid(VoucherEntity voucher, LocalDate checkInDate) {
        if (voucher == null)
            return false;

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
    public BigDecimal calculateTotalPrice(BigDecimal roomPrice, LocalDate checkInDate, LocalDate checkOutDate, VoucherEntity voucher, Integer roomQuantity) {
        // Tính số đêm
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // Giá cơ bản (nhân với số lượng phòng)
        BigDecimal basePrice = roomPrice.multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(roomQuantity));
    

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
        System.out.println("=== DEBUG: cancelBooking called with bookingId: " + bookingId + ", reason: " + reason + " ===");
        
        // Kiểm tra lý do hủy phòng
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do hủy phòng.");
        }

        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));

        // Kiểm tra xem có thể hủy không
        System.out.println("=== DEBUG: Checking if booking can be cancelled ===");
        if (!canCancelBooking(booking)) {
            System.out.println("=== DEBUG: Booking cannot be cancelled ===");
            throw new RuntimeException("Không thể hủy đặt phòng này. Vui lòng kiểm tra chính sách hủy phòng.");
        }
        System.out.println("=== DEBUG: Booking can be cancelled ===");

        // Kiểm tra trạng thái thanh toán
        boolean isPaid = "PAID".equalsIgnoreCase(booking.getPaymentStatus());
        System.out.println("=== DEBUG: Payment status: " + booking.getPaymentStatus() + ", isPaid: " + isPaid + " ===");

        // Tính toán số tiền hoàn lại
        BigDecimal refundAmount = calculateRefundAmount(booking);

        // Xác định trạng thái mới dựa trên việc đã thanh toán hay chưa
        StatusEntity newStatus;
        String newBookingStatus;
        String newRefundStatus;

        if (isPaid) {
            // Đã thanh toán -> chuyển thành "Đã hoàn tiền"
            newStatus = statusRepository.findByStatusNameIgnoreCase("REFUNDED");
            if (newStatus == null) {
                newStatus = new StatusEntity();
                newStatus.setStatusName("REFUNDED");
                newStatus.setDescription("Đã hoàn tiền");
                newStatus = statusRepository.save(newStatus);
            }
            newBookingStatus = "REFUNDED";
            newRefundStatus = "COMPLETED";
            System.out.println("=== DEBUG: Booking is paid, setting status to REFUNDED ===");
        } else {
            // Chưa thanh toán -> chuyển thành "Đã hủy phòng"
            newStatus = statusRepository.findByStatusNameIgnoreCase("CANCELLED");
            if (newStatus == null) {
                newStatus = new StatusEntity();
                newStatus.setStatusName("CANCELLED");
                newStatus.setDescription("Đã hủy");
                newStatus = statusRepository.save(newStatus);
            }
            newBookingStatus = "CANCELLED";
            newRefundStatus = "NONE";
            System.out.println("=== DEBUG: Booking is not paid, setting status to CANCELLED ===");
        }

        // Cập nhật trạng thái booking
        booking.setStatus(newStatus);
        booking.setBookingStatus(newBookingStatus);
        booking.setCancellationDate(LocalDateTime.now());
        booking.setCancellationReason(reason);
        booking.setRefundAmount(refundAmount);
        booking.setRefundStatus(newRefundStatus);

        // Nếu đã thanh toán và có tiền hoàn lại, cập nhật ngày hoàn tiền
        if (isPaid && refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            booking.setRefundDate(LocalDateTime.now());
        }

        System.out.println("=== DEBUG: About to save cancelled booking ===");
        BookingOrderEntity savedBooking = bookingOrderRepository.save(booking);
        System.out.println("=== DEBUG: Booking saved successfully with ID: " + savedBooking.getBookingId() + " ===");
        System.out.println("=== DEBUG: Booking status: " + savedBooking.getBookingStatus() + " ===");
        System.out.println("=== DEBUG: Refund status: " + savedBooking.getRefundStatus() + " ===");

        // Không cần cộng lại totalRooms vì sẽ tính động theo booking
        // Số phòng có sẵn sẽ được tính trong RoomService.getAvailableRoomCount()

        return savedBooking;
    }

    // Kiểm tra xem có thể hủy đặt phòng không
    public boolean canCancelBooking(BookingOrderEntity booking) {
        LocalDate today = LocalDate.now();
        LocalDate checkInDate = booking.getCheckInDate();

        // Tính số ngày trước ngày check-in
        long daysBeforeCheckIn = ChronoUnit.DAYS.between(today, checkInDate);

        System.out.println("=== DEBUG: canCancelBooking - today: " + today + ", checkInDate: " + checkInDate + ", daysBeforeCheckIn: " + daysBeforeCheckIn + " ===");

        // Luôn cho phép hủy, chính sách hoàn tiền được xử lý trong calculateRefundAmount:
        // - Hủy trước 48h: hoàn 100% tiền
        // - Hủy từ 24h đến 48h: hoàn 50% tiền
        // - Hủy trong vòng 24h: không hoàn tiền
        System.out.println("=== DEBUG: canCancelBooking - Always allow cancellation ===");
        return true;
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
        booking.setBookingStatus("REFUNDED");

        return bookingOrderRepository.save(booking);
    }

    // Xác nhận thanh toán thực tế (cho admin xác nhận thủ công)
    @Transactional
    public BookingOrderEntity confirmPayment(Integer bookingId) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));

        // Kiểm tra xem đã có giá booking chưa
        if (booking.getTotalPrice() == null || booking.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Chưa có giá booking, không thể xác nhận thanh toán.");
        }

        // Kiểm tra xem đã thanh toán chưa
        if ("PAID".equals(booking.getPaymentStatus())) {
            throw new RuntimeException("Đặt phòng này đã được thanh toán.");
        }

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
        booking.setBookingStatus("CONFIRMED");

        return bookingOrderRepository.save(booking);
    }

    // Xác nhận thanh toán tại khách sạn (cho trường hợp thanh toán tại khách sạn)
    @Transactional
    public BookingOrderEntity confirmPaymentAtHotel(Integer bookingId) {
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
        booking.setBookingStatus("CONFIRMED");

        return bookingOrderRepository.save(booking);
    }

    /**
     * Xử lý thanh toán booking
     * @param bookingId ID của booking
     * @param paymentMethodCode Mã phương thức thanh toán
     * @return BookingOrderEntity đã được cập nhật
     */
    @Transactional
    public BookingOrderEntity processPayment(Integer bookingId, String paymentMethodCode) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));

        // Validate payment method
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.fromCode(paymentMethodCode);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ: " + paymentMethodCode);
        }

        // Set payment method
        booking.setPaymentMethod(paymentMethod.getCode());

        // Xử lý trạng thái thanh toán và booking dựa trên phương thức
        if (paymentMethod.requiresImmediatePayment()) {
            // Thanh toán ngay (thẻ, MoMo, chuyển khoản) - đã thanh toán thành công
            booking.setPaymentStatus(PaymentStatus.PAID.getCode());
            booking.setPaidDate(LocalDateTime.now());
            
            // Set booking status thành CONFIRMED ngay lập tức (đã đặt phòng thành công)
            StatusEntity confirmedStatus = statusRepository.findByStatusNameIgnoreCase("CONFIRMED");
            if (confirmedStatus == null) {
                confirmedStatus = new StatusEntity();
                confirmedStatus.setStatusName("CONFIRMED");
                confirmedStatus.setDescription("Đã xác nhận");
                confirmedStatus = statusRepository.save(confirmedStatus);
            }
            booking.setStatus(confirmedStatus);
            booking.setBookingStatus("CONFIRMED");
        } else {
            // Thanh toán tại khách sạn - vẫn đặt phòng thành công nhưng chờ thanh toán
            booking.setPaymentStatus(PaymentStatus.PENDING.getCode());
            
            // Set booking status thành CONFIRMED (đã đặt phòng thành công, chờ thanh toán tại khách sạn)
            StatusEntity confirmedStatus = statusRepository.findByStatusNameIgnoreCase("CONFIRMED");
            if (confirmedStatus == null) {
                confirmedStatus = new StatusEntity();
                confirmedStatus.setStatusName("CONFIRMED");
                confirmedStatus.setDescription("Đã xác nhận");
                confirmedStatus = statusRepository.save(confirmedStatus);
            }
            booking.setStatus(confirmedStatus);
            booking.setBookingStatus("CONFIRMED");
        }

        return bookingOrderRepository.save(booking);
    }

    // Xác nhận giữ phòng (không set PAID, chỉ CONFIRMED)
    @Transactional
    public BookingOrderEntity confirmBooking(Integer bookingId) {
        BookingOrderEntity booking = bookingOrderRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId));
        StatusEntity confirmedStatus = statusRepository.findByStatusNameIgnoreCase("CONFIRMED");
        if (confirmedStatus == null) {
            confirmedStatus = new StatusEntity();
            confirmedStatus.setStatusName("CONFIRMED");
            confirmedStatus.setDescription("Đã xác nhận");
            confirmedStatus = statusRepository.save(confirmedStatus);
        }
        booking.setStatus(confirmedStatus);
        booking.setBookingStatus("CONFIRMED");
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
        booking.setBookingStatus("CHECKED_IN");

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
        booking.setBookingStatus("CHECKED_OUT");

        BookingOrderEntity savedBooking = bookingOrderRepository.save(booking);
        System.out.println(
                "=== SERVICE DEBUG: Booking saved successfully with ID: " + savedBooking.getBookingId() + " ===");

        return savedBooking;
    }

    // --- PHƯƠNG THỨC CHO ADMIN QUẢN LÝ CRUD (Sử dụng bởi
    // AdminBookingApiController) ---

    @Transactional
    public BookingOrderEntity adminCreateOrUpdateBooking(Integer existingBookingId, AdminBookingRequestDTO dto,
            boolean isCustomerFlowTriggered) {
        System.out.println("[SERVICE DEBUG] adminCreateOrUpdateBooking - existingBookingId: " + existingBookingId);
        System.out.println("[SERVICE DEBUG] adminCreateOrUpdateBooking - DTO received: " + dto.toString());

        if (dto.getCustomerEmail() != null) {
            System.out.println(
                    "[SERVICE DEBUG] adminCreateOrUpdateBooking - DTO Customer Email: " + dto.getCustomerEmail());
            if (dto.getCustomerEmail().equalsIgnoreCase("0.email")) {
                System.err.println(
                        "[SERVICE CRITICAL DEBUG] DTO contains '0.email' for customerEmail in service method!");
            }
        }

        // Validation
        if (dto.getCheckInDate() == null || dto.getCheckOutDate() == null ||
                dto.getCheckInDate().isAfter(dto.getCheckOutDate())
                || dto.getCheckInDate().isEqual(dto.getCheckOutDate())) {
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
                if (dto.getCustomerName() != null && !dto.getCustomerName().isEmpty())
                    customer.setName(dto.getCustomerName());
                if (dto.getCustomerPhone() != null)
                    customer.setPhone(dto.getCustomerPhone());
                if (dto.getCustomerAddress() != null)
                    customer.setAddress(dto.getCustomerAddress());
                customersRepository.save(customer);
            } else if (dto.getCustomerId() != null) {
                customer = customersRepository.findById(dto.getCustomerId())
                        .orElseThrow(() -> new RuntimeException(
                                "Khách hàng với ID: " + dto.getCustomerId() + " không tồn tại."));
            }
        } else {
            booking = new BookingOrderEntity();
            booking.setBookingDate(LocalDate.now());

            if (dto.getCustomerId() != null) {
                customer = customersRepository.findById(dto.getCustomerId())
                        .orElseThrow(() -> new RuntimeException(
                                "Khách hàng với ID: " + dto.getCustomerId() + " không tồn tại."));
            } else {
                customer = customersRepository.findByEmailIgnoreCase(dto.getCustomerEmail().toLowerCase())
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
        Integer roomQuantity = dto.getRoomQuantity() != null ? dto.getRoomQuantity() : (booking.getRoomQuantity() != null ? booking.getRoomQuantity() : 1);
        BigDecimal totalPrice = calculateTotalPrice(room.getPrice(), dto.getCheckInDate(), dto.getCheckOutDate(), booking.getVoucher(), roomQuantity);
        booking.setTotalPrice(totalPrice);
        booking.setRoomQuantity(roomQuantity);

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
        booking.setBookingStatus(statusNameToSet);
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
        booking.setBookingStatus(newStatusName);
        return bookingOrderRepository.save(booking);
    }

    public List<BookingOrderEntity> getRecentBookings(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Giới hạn số lượng đặt phòng phải lớn hơn 0");
        }
        return bookingOrderRepository.findTopNByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    /**
     * Lấy danh sách booking của khách hàng theo email
     * @param email Email của khách hàng
     * @return Danh sách booking của khách hàng, sắp xếp theo thời gian tạo giảm dần
     * @throws IllegalArgumentException nếu email là null hoặc rỗng
     */
    public List<BookingOrderEntity> getBookingsByCustomerEmailForCustomer(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        List<BookingOrderEntity> bookings = bookingOrderRepository.findByEmailOrderByCreatedAtDesc(email.toLowerCase());
        return bookings != null ? bookings : new ArrayList<>();
    }

    /**
     * Lấy danh sách booking của khách hàng theo customer ID
     * @param customerId ID của khách hàng
     * @return Danh sách booking của khách hàng, sắp xếp theo thời gian tạo giảm dần
     * @throws IllegalArgumentException nếu customerId là null
     */
    public List<BookingOrderEntity> getBookingsByCustomerIdForCustomer(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được để trống.");
        }
        System.out.println("=== SERVICE DEBUG: Searching bookings for customer ID: " + customerId + " ===");
        
        // Test query để debug
        try {
            List<BookingOrderEntity> allBookings = bookingOrderRepository.findAll();
            System.out.println("=== SERVICE DEBUG: Total bookings in database: " + allBookings.size() + " ===");
            
            // Log một vài booking để xem cấu trúc
            for (int i = 0; i < Math.min(3, allBookings.size()); i++) {
                BookingOrderEntity booking = allBookings.get(i);
                System.out.println("=== BOOKING " + i + ": ID=" + booking.getBookingId() + 
                                 ", CustomerID=" + (booking.getCustomer() != null ? booking.getCustomer().getCustomerId() : "NULL") +
                                 ", Email=" + booking.getEmail() + " ===");
            }
        } catch (Exception e) {
            System.out.println("=== ERROR getting all bookings: " + e.getMessage() + " ===");
        }
        
        List<BookingOrderEntity> bookings = bookingOrderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        System.out.println("=== SERVICE DEBUG: JPA Query found " + (bookings != null ? bookings.size() : 0) + " bookings for customer ID " + customerId + " ===");
        
        // Test native query để so sánh
        try {
            List<BookingOrderEntity> nativeBookings = bookingOrderRepository.findByCustomerIdNative(customerId);
            System.out.println("=== SERVICE DEBUG: Native Query found " + (nativeBookings != null ? nativeBookings.size() : 0) + " bookings for customer ID " + customerId + " ===");
            
            if (bookings.isEmpty() && !nativeBookings.isEmpty()) {
                System.out.println("=== WARNING: JPA query returned empty but Native query found data! Using native results ===");
                bookings = nativeBookings;
            }
        } catch (Exception e) {
            System.out.println("=== ERROR in native query: " + e.getMessage() + " ===");
        }
        
        return bookings != null ? bookings : new ArrayList<>();
    }

    public List<CustomersEntity> findAllCustomersForAdmin() {
        return customersRepository.findAll();
    }

    // Tìm booking theo trạng thái
    public List<BookingOrderEntity> findByStatusName(String statusName) {
        return bookingOrderRepository.findByStatusName(statusName);
    }

    // Tìm booking theo booking_status field
    public List<BookingOrderEntity> findByBookingStatus(String bookingStatus) {
        if (bookingStatus == null || bookingStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking status không được để trống.");
        }
        return bookingOrderRepository.findByBookingStatus(bookingStatus);
    }

    // Lấy thống kê booking
    public Map<String, Object> getBookingStatistics() {
        List<BookingOrderEntity> allBookings = bookingOrderRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Thống kê theo booking_status
        long totalBookings = allBookings.size();
        long pendingBookings = allBookings.stream()
                .filter(booking -> "PENDING".equals(booking.getBookingStatus()))
                .count();
        long confirmedBookings = allBookings.stream()
                .filter(booking -> "CONFIRMED".equals(booking.getBookingStatus()))
                .count();
        long cancelledBookings = allBookings.stream()
                .filter(booking -> "CANCELLED".equals(booking.getBookingStatus()))
                .count();
        
        // Thống kê theo payment_status
        long pendingPaymentBookings = allBookings.stream()
                .filter(booking -> "PENDING".equals(booking.getPaymentStatus()))
                .count();
        long paidBookings = allBookings.stream()
                .filter(booking -> "PAID".equals(booking.getPaymentStatus()))
                .count();
        
        // Tổng doanh thu
        BigDecimal totalRevenue = allBookings.stream()
                .filter(booking -> booking.getTotalPrice() != null)
                .map(BookingOrderEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("totalBookings", totalBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("confirmedBookings", confirmedBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("pendingPaymentBookings", pendingPaymentBookings);
        stats.put("paidBookings", paidBookings);
        stats.put("totalRevenue", totalRevenue);
        
        return stats;
    }

    // Đếm phòng booking hôm nay theo partner
    public long countTodayBookingsByPartner(Long partnerId) {
        return bookingOrderRepository.countTodayBookingsByPartner(partnerId);
    }

    // Hiện tất cả booking của partner
    public List<BookingOrderEntity> findAllBookingsByPartner(Long partnerId) {
        System.out.println("=== DEBUG: findAllBookingsByPartner called with partnerId: " + partnerId + " ===");
        
        // Lấy email của partner
        PartnerEntity partner = partnerRepository.findById(partnerId).orElse(null);
        String partnerEmail = partner != null ? partner.getEmail() : null;
        
        System.out.println("=== DEBUG: Partner email: " + partnerEmail + " ===");
        
        List<BookingOrderEntity> result = bookingOrderRepository.findAllBookingsByPartner(partnerId, partnerEmail);
        
        System.out.println("=== DEBUG: Repository returned " + result.size() + " bookings ===");
        
        // Debug: Print details of first few bookings if any exist
        if (!result.isEmpty()) {
            System.out.println("=== DEBUG: First booking details from service ===");
            BookingOrderEntity firstBooking = result.get(0);
            System.out.println("=== DEBUG: Booking ID: " + firstBooking.getBookingId() + " ===");
            System.out.println("=== DEBUG: Booking Email: " + firstBooking.getEmail() + " ===");
            System.out.println("=== DEBUG: Room: " + (firstBooking.getRoom() != null ? firstBooking.getRoom().getRoomNumber() : "NULL") + " ===");
            System.out.println("=== DEBUG: Room Partner: " + (firstBooking.getRoom() != null && firstBooking.getRoom().getPartner() != null ? firstBooking.getRoom().getPartner().getId() : "NULL") + " ===");
            System.out.println("=== DEBUG: Booking Partner: " + (firstBooking.getPartner() != null ? firstBooking.getPartner().getId() : "NULL") + " ===");
        }
        
        return result;
    }

    public Map<LocalDate, ReportsPartnerDTO> getDailyBookingCount(Long partnerId, LocalDate start, LocalDate end) {
        LocalDateTime fromDate = start.atStartOfDay();
        LocalDateTime toDate = end.plusDays(1).atStartOfDay(); // để lấy trọn cả ngày cuối cùng

        List<Object[]> rawResults = bookingOrderRepository.getBookingStatsInRange(partnerId, fromDate, toDate);

        Map<LocalDate, ReportsPartnerDTO> result = new LinkedHashMap<>();

        for (Object[] row : rawResults) {
            Date date = (Date) row[0];
            Long count = (Long) row[1];
            BigDecimal total = (BigDecimal) row[2];

            result.put(date.toLocalDate(), new ReportsPartnerDTO(count, total));
        }

        return result;
    }

    // Lấy dữ liệu doanh thu hàng tháng của đối tác theo năm
    public List<MonthlyRevenueReportPartnerDTO> getMonthlyRevenueReportPartner(Long partnerId, int year) {
        List<Object[]> rows = bookingOrderRepository.getMonthlyRevenueReportPartner(partnerId, year);
        List<MonthlyRevenueReportPartnerDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            int month = (int) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            result.add(new MonthlyRevenueReportPartnerDTO(month, revenue));
        }

        return result;
    }

    // Doanh thu hôm nay theo partner
    public Integer sumDoanhThuToday(Long partnerId) {
        return bookingOrderRepository.sumDoanhThuToday(partnerId);
    }

    // DataReport for Partner
    public List<Object[]> getReportData(Long partnerId, LocalDate startDate, LocalDate endDate) {
        return bookingOrderRepository.getReportData(partnerId, startDate, endDate);
    }

    public List<Integer> getAvailableBookingYears(Long partnerId) {
        return bookingOrderRepository.findAvailableBookingYears(partnerId);
    }

    // Lấy top khách hàng cho đối tác
    public List<Map<String, Object>> getTopCustomers(Long partnerId, LocalDate startDate, LocalDate endDate) {
        // Filter top customers by partner
        List<Map<String, Object>> allCustomers = bookingOrderRepository.getTopCustomers(startDate, endDate);
        
        // Filter by partner - this is a temporary solution until we update the repository query
        // In a production environment, we should update the repository query to include partner filtering
        return allCustomers.stream()
            .filter(customer -> {
                // For now, return all customers since the repository doesn't filter by partner
                // TODO: Update repository query to include partner filtering
                return true;
            })
            .limit(10) // Limit to top 10 customers
            .toList();
    }

    // UPDATE Booking Details (by Admin) - chức năng chỉnh sửa đặt phòng
    @Transactional
    public BookingOrderEntity updateBookingDetails(Integer bookingId, Map<String, Object> updateData) {
        System.out.println("=== DEBUG: updateBookingDetails called with bookingId: " + bookingId + ", updateData: " + updateData + " ===");
        Optional<BookingOrderEntity> bookingOpt = bookingOrderRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId);
        }

        BookingOrderEntity booking = bookingOpt.get();

        // Cập nhật ngày nhận phòng
        if (updateData.containsKey("checkInDate")) {
            String checkInDateStr = (String) updateData.get("checkInDate");
            System.out.println("=== DEBUG: checkInDateStr: " + checkInDateStr + " ===");
            if (checkInDateStr != null && !checkInDateStr.trim().isEmpty()) {
                try {
                    LocalDate checkInDate = LocalDate.parse(checkInDateStr);
                    // Bỏ validation ngày trong quá khứ khi chỉnh sửa booking hiện có
                    // Chỉ kiểm tra format ngày hợp lệ
                    booking.setCheckInDate(checkInDate);
                    System.out.println("=== DEBUG: Set checkInDate to: " + checkInDate + " ===");
                } catch (Exception e) {
                    System.err.println("=== DEBUG: Error parsing checkInDate: " + e.getMessage() + " ===");
                    throw new IllegalArgumentException("Ngày nhận phòng không hợp lệ: " + checkInDateStr);
                }
            }
        }

        // Cập nhật ngày trả phòng
        if (updateData.containsKey("checkOutDate")) {
            String checkOutDateStr = (String) updateData.get("checkOutDate");
            System.out.println("=== DEBUG: checkOutDateStr: " + checkOutDateStr + " ===");
            if (checkOutDateStr != null && !checkOutDateStr.trim().isEmpty()) {
                try {
                    LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);
                    if (booking.getCheckInDate() != null && checkOutDate.isBefore(booking.getCheckInDate())) {
                        throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");
                    }
                    booking.setCheckOutDate(checkOutDate);
                    System.out.println("=== DEBUG: Set checkOutDate to: " + checkOutDate + " ===");
                } catch (Exception e) {
                    System.err.println("=== DEBUG: Error parsing checkOutDate: " + e.getMessage() + " ===");
                    throw new IllegalArgumentException("Ngày trả phòng không hợp lệ: " + checkOutDateStr);
                }
            }
        }

        // Số đêm được tính toán tự động từ checkInDate và checkOutDate
        // Không cần cập nhật numberOfNights vì nó được tính toán trong getNumberOfNights()

        // Cập nhật tổng tiền
        if (updateData.containsKey("totalPrice")) {
            Object totalPriceObj = updateData.get("totalPrice");
            System.out.println("=== DEBUG: totalPriceObj: " + totalPriceObj + " ===");
            if (totalPriceObj != null) {
                try {
                    BigDecimal totalPrice;
                    if (totalPriceObj instanceof BigDecimal) {
                        totalPrice = (BigDecimal) totalPriceObj;
                    } else if (totalPriceObj instanceof String) {
                        totalPrice = new BigDecimal((String) totalPriceObj);
                    } else if (totalPriceObj instanceof Double) {
                        totalPrice = BigDecimal.valueOf((Double) totalPriceObj);
                    } else if (totalPriceObj instanceof Integer) {
                        totalPrice = BigDecimal.valueOf((Integer) totalPriceObj);
                    } else {
                        totalPrice = new BigDecimal(totalPriceObj.toString());
                    }
                    if (totalPrice.compareTo(BigDecimal.ZERO) >= 0) {
                        booking.setTotalPrice(totalPrice);
                        System.out.println("=== DEBUG: Set totalPrice to: " + totalPrice + " ===");
                    }
                } catch (Exception e) {
                    System.err.println("=== DEBUG: Error parsing totalPrice: " + e.getMessage() + " ===");
                    throw new IllegalArgumentException("Tổng tiền không hợp lệ: " + totalPriceObj);
                }
            }
        }

        // Cập nhật trạng thái đặt phòng
        if (updateData.containsKey("bookingStatus")) {
            String bookingStatus = (String) updateData.get("bookingStatus");
            System.out.println("=== DEBUG: bookingStatus: " + bookingStatus + " ===");
            if (bookingStatus != null && !bookingStatus.trim().isEmpty()) {
                booking.setBookingStatus(bookingStatus);
                
                // Cập nhật cả StatusEntity để đồng bộ
                StatusEntity statusEntity = statusRepository.findByStatusNameIgnoreCase(bookingStatus);
                if (statusEntity == null) {
                    statusEntity = new StatusEntity();
                    statusEntity.setStatusName(bookingStatus);
                    statusEntity.setDescription("Trạng thái: " + bookingStatus);
                    statusEntity = statusRepository.save(statusEntity);
                }
                booking.setStatus(statusEntity);
                System.out.println("=== DEBUG: Set bookingStatus to: " + bookingStatus + " ===");
            }
        }

        // Cập nhật trạng thái thanh toán
        if (updateData.containsKey("paymentStatus")) {
            String paymentStatus = (String) updateData.get("paymentStatus");
            System.out.println("=== DEBUG: paymentStatus: " + paymentStatus + " ===");
            if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
                booking.setPaymentStatus(paymentStatus);
                
                // Nếu thanh toán thành công, cập nhật ngày thanh toán
                if ("PAID".equals(paymentStatus)) {
                    booking.setPaidDate(LocalDateTime.now());
                }
                System.out.println("=== DEBUG: Set paymentStatus to: " + paymentStatus + " ===");
            }
        }

        // Cập nhật phương thức thanh toán
        if (updateData.containsKey("paymentMethod")) {
            String paymentMethod = (String) updateData.get("paymentMethod");
            System.out.println("=== DEBUG: paymentMethod: " + paymentMethod + " ===");
            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                booking.setPaymentMethod(paymentMethod);
                System.out.println("=== DEBUG: Set paymentMethod to: " + paymentMethod + " ===");
            }
        }

        // Cập nhật ngày thanh toán
        if (updateData.containsKey("paidDate")) {
            Object paidDateObj = updateData.get("paidDate");
            System.out.println("=== DEBUG: paidDateObj: " + paidDateObj + " ===");
            if (paidDateObj != null && !"null".equals(paidDateObj.toString()) && !paidDateObj.toString().trim().isEmpty()) {
                try {
                    LocalDateTime paidDate = LocalDateTime.parse(paidDateObj.toString());
                    booking.setPaidDate(paidDate);
                    System.out.println("=== DEBUG: Set paidDate to: " + paidDate + " ===");
                } catch (Exception e) {
                    System.err.println("=== DEBUG: Error parsing paidDate: " + e.getMessage() + " ===");
                    // Không throw exception vì paidDate có thể null
                }
            }
        }

        // Cập nhật ghi chú
        if (updateData.containsKey("specialRequests")) {
            String specialRequests = (String) updateData.get("specialRequests");
            System.out.println("=== DEBUG: specialRequests: " + specialRequests + " ===");
            if (specialRequests != null) {
                booking.setSpecialRequests(specialRequests);
                System.out.println("=== DEBUG: Set specialRequests to: " + specialRequests + " ===");
            }
        }

        // Tính toán lại số đêm và tổng tiền nếu ngày thay đổi
        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
            long numberOfNights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            
            // Tính lại tổng tiền nếu có thay đổi ngày
            if (booking.getRoom() != null && booking.getRoom().getPrice() != null) {
                BigDecimal roomPrice = booking.getRoom().getPrice();
                BigDecimal totalPrice = roomPrice.multiply(BigDecimal.valueOf(numberOfNights));
                booking.setTotalPrice(totalPrice);
                System.out.println("=== DEBUG: Recalculated totalPrice to: " + totalPrice + " ===");
            }
        }

        System.out.println("=== DEBUG: About to save updated booking ===");
        try {
            BookingOrderEntity savedBooking = bookingOrderRepository.save(booking);
            System.out.println("=== DEBUG: Booking saved successfully with ID: " + savedBooking.getBookingId() + " ===");
            return savedBooking;
        } catch (Exception e) {
            System.err.println("=== DEBUG: Error saving booking: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw e;
        }
    }

    // SEND Notification (by Admin) - chức năng gửi thông báo
    public boolean sendNotification(Integer bookingId, String notificationType, String title, String content, 
                                   Boolean sendEmail, Boolean sendSMS) {
        Optional<BookingOrderEntity> bookingOpt = bookingOrderRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId);
        }

        BookingOrderEntity booking = bookingOpt.get();
        boolean sent = false;

        try {
            // Gửi email nếu được yêu cầu
            if (sendEmail != null && sendEmail && booking.getCustomer() != null && booking.getCustomer().getEmail() != null) {
                // TODO: Implement email sending logic
                System.out.println("Sending email to: " + booking.getCustomer().getEmail());
                System.out.println("Subject: " + title);
                System.out.println("Content: " + content);
                sent = true;
            }

            // Gửi SMS nếu được yêu cầu
            if (sendSMS != null && sendSMS && booking.getCustomer() != null && booking.getCustomer().getPhone() != null) {
                // TODO: Implement SMS sending logic
                System.out.println("Sending SMS to: " + booking.getCustomer().getPhone());
                System.out.println("Content: " + content);
                sent = true;
            }

            // Lưu thông báo vào database (nếu cần)
            // TODO: Implement notification logging

            return sent;
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            return false;
        }
    }
    public BookingOrderEntity saveBooking(BookingOrderEntity booking) {
    // bookingOrderRepository là JPA repository
    return bookingOrderRepository.save(booking);
}

    
}