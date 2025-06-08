package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Sử dụng Optional để xử lý null tốt hơn

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Quan trọng cho các thao tác ghi

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.StatusEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.StatusRepository;
// Giả sử bạn có các Exception tùy chỉnh để xử lý lỗi rõ ràng hơn
// import sd19303no1.hotel_booking_and_management_system.Exception.ResourceNotFoundException;

@Service
public class BookingOrderService {

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoomService roomService; // Đảm bảo RoomService có phương thức findById trả về RoomEntity hoặc ném Exception

    @Autowired
    private VoucherService voucherService; // Đảm bảo VoucherService có phương thức findById trả về VoucherEntity hoặc ném Exception

    public List<BookingOrderEntity> getRecentBookings(int limit) {
        // Đảm bảo limit > 0
        if (limit <= 0) {
            throw new IllegalArgumentException("Giới hạn số lượng đặt phòng phải lớn hơn 0");
        }
        return bookingOrderRepository.findRecentBookings(PageRequest.of(0, limit));
    }

    // Thêm @Transactional để đảm bảo tính nhất quán dữ liệu khi có nhiều thao tác ghi
    @Transactional
    public BookingOrderEntity createBooking(String customerName, String email, String phone, String address,
                                          Integer roomId, LocalDate checkInDate, LocalDate checkOutDate,
                                          Integer voucherId) {

        // 0. Validate đầu vào (ví dụ: checkInDate phải trước checkOutDate, email hợp lệ, etc.)
        // (Phần này nên được thực hiện ở Controller hoặc bằng validation annotations trên DTO)
        if (checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("Ngày nhận phòng và trả phòng không hợp lệ.");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("ID phòng không được để trống.");
        }
        // Thêm các validation khác nếu cần

        // 1. Tìm hoặc tạo customer
        // Sử dụng Optional để xử lý null an toàn hơn và code rõ ràng hơn
        CustomersEntity customer = customersRepository.findByEmail(email.toLowerCase())
                .orElseGet(() -> {
                    CustomersEntity newCustomer = new CustomersEntity();
                    newCustomer.setName(customerName);
                    newCustomer.setEmail(email.toLowerCase()); // Lưu email dạng chữ thường để tìm kiếm nhất quán
                    newCustomer.setPhone(phone);
                    newCustomer.setAddress(address);
                    // Bạn có thể muốn đặt mật khẩu mặc định hoặc một cơ chế khác nếu khách hàng này sẽ đăng nhập
                    // newCustomer.setPassword("some_default_or_generated_password_hashed");
                    return customersRepository.save(newCustomer);
                });

        // 2. Lấy room
        // Giả sử roomService.findById trả về RoomEntity hoặc ném ResourceNotFoundException nếu không tìm thấy
        RoomEntity room = roomService.findById(roomId); // Nên ném exception nếu không tìm thấy
        if (room == null) { // Kiểm tra này có thể thừa nếu findById đã ném exception
            throw new RuntimeException("Phòng không tồn tại với ID: " + roomId); // Hoặc ResourceNotFoundException
        }

        // TODO: Kiểm tra xem phòng có sẵn trong khoảng thời gian checkInDate, checkOutDate không?
        // Đây là một bước RẤT QUAN TRỌNG. Bạn cần logic để kiểm tra tính khả dụng của phòng.
        // Ví dụ: boolean isRoomAvailable = roomService.isRoomAvailable(roomId, checkInDate, checkOutDate);
        // if (!isRoomAvailable) {
        //     throw new RuntimeException("Phòng " + roomId + " không còn trống trong khoảng thời gian đã chọn.");
        // }


        // 3. Lấy voucher nếu có
        VoucherEntity voucher = null;
        if (voucherId != null) {
            voucher = voucherService.findById(voucherId); // Nên ném exception nếu không tìm thấy hoặc voucher không hợp lệ
            if (voucher == null) { // Kiểm tra này có thể thừa nếu findById đã ném exception
                 throw new RuntimeException("Voucher không tồn tại với ID: " + voucherId); // Hoặc ResourceNotFoundException
            }
            // TODO: Kiểm tra xem voucher có hợp lệ không (còn hạn, chưa sử dụng hết, đủ điều kiện áp dụng,...)
            // if (!voucherService.isVoucherValid(voucher, /* các thông tin đơn hàng khác nếu cần */)) {
            //     throw new RuntimeException("Voucher " + voucher.getCode() + " không hợp lệ hoặc không thể áp dụng.");
            // }
        }

        // 4. Lấy status PENDING
        // Nên có một hằng số cho tên trạng thái để tránh lỗi chính tả
        final String PENDING_STATUS_NAME = "PENDING";
        StatusEntity status = statusRepository.findByStatusName(PENDING_STATUS_NAME);
        if (status == null) {
            // Chỉ tạo status nếu nó thực sự chưa tồn tại.
            // Trong thực tế, các trạng thái cơ bản nên được seed vào DB khi khởi tạo ứng dụng.
            StatusEntity newStatus = new StatusEntity();
            newStatus.setStatusName(PENDING_STATUS_NAME);
            // Nếu StatusEntity có enum Status, bạn cũng nên set nó
            // newStatus.setStatusEnumValue(StatusEntity.Status.PENDING); // Ví dụ
            status = statusRepository.save(newStatus);
        }

        // 5. Tạo đối tượng BookingOrderEntity
        BookingOrderEntity booking = new BookingOrderEntity();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setEmail(email.toLowerCase()); // Lưu email của đơn đặt hàng, có thể khác với email của customer nếu customer thay đổi email sau này
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setBookingDate(LocalDate.now()); // Thời điểm khách hàng thực hiện đặt
        // booking.setCreatedAt(LocalDateTime.now()); // @CreationTimestamp trong Entity sẽ tự động làm điều này
                                                // Nếu không có @CreationTimestamp, thì dòng này là cần thiết.
                                                // Kiểm tra lại BookingOrderEntity của bạn. Nếu có @CreationTimestamp thì không cần set ở đây.

        booking.setVoucher(voucher);
        booking.setStatus(status);

        // TODO: Tính toán tổng tiền (totalPrice) cho đơn đặt hàng
        // BigDecimal totalPrice = calculateTotalPrice(room.getPrice(), checkInDate, checkOutDate, voucher);
        // booking.setTotalPrice(totalPrice); // Giả sử bạn có trường totalPrice trong BookingOrderEntity

        // 6. Lưu đơn đặt hàng
        return bookingOrderRepository.save(booking);
    }

    public List<BookingOrderEntity> getBookingsByCustomerEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        return bookingOrderRepository.findByEmailOrderByCreatedAtDesc(email.toLowerCase());
    }

    // Phương thức hỗ trợ (ví dụ)
    // private BigDecimal calculateTotalPrice(BigDecimal roomPricePerNight, LocalDate checkIn, LocalDate checkOut, VoucherEntity voucher) {
    //     long numberOfNights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    //     if (numberOfNights <= 0) {
    //         throw new IllegalArgumentException("Số đêm nghỉ phải lớn hơn 0.");
    //     }
    //     BigDecimal totalRoomPrice = roomPricePerNight.multiply(BigDecimal.valueOf(numberOfNights));
    //
    //     if (voucher != null && voucher.getDiscount() != null) {
    //         // Logic áp dụng voucher (ví dụ: giảm theo % hoặc số tiền cố định)
    //         // totalRoomPrice = totalRoomPrice.subtract(voucher.getDiscount()); // Hoặc tính theo %
    //         // Đảm bảo giá không âm
    //     }
    //     return totalRoomPrice;
    // }

    // Bạn cũng nên có các phương thức khác như:
    // - findBookingById(Integer bookingId)
    // - updateBookingStatus(Integer bookingId, String newStatusName)
    // - cancelBooking(Integer bookingId)
    // - getAllBookings() (có phân trang)
}