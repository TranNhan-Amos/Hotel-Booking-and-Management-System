package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.RoomEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.StatusEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.StatusRepository;

@Service
public class BookingOrderService {

    @Autowired
    private BookingOrderRepository bookingOrderRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private VoucherService voucherService;

    public List<BookingOrderEntity> getRecentBookings(int limit) {
        return bookingOrderRepository.findRecentBookings(PageRequest.of(0, limit));
    }

    public BookingOrderEntity createBooking(String customerName, String email, String phone, String address,
                                          Integer roomId, LocalDate checkInDate, LocalDate checkOutDate,
                                          Integer voucherId) {
        // Tìm hoặc tạo customer
        CustomersEntity customer = customersRepository.findByEmail(email);
        if (customer == null) {
            customer = new CustomersEntity();
            customer.setName(customerName);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer = customersRepository.save(customer);
        }

        // Lấy room
        RoomEntity room = roomService.findById(roomId);
        if (room == null) {
            throw new RuntimeException("Phòng không tồn tại");
        }

        // Lấy voucher nếu có
        VoucherEntity voucher = null;
        if (voucherId != null) {
            voucher = voucherService.findById(voucherId);
        }

        // Lấy status PENDING
        StatusEntity status = statusRepository.findByStatusName("PENDING");
        if (status == null) {
            // Tạo status mặc định nếu chưa có
            status = new StatusEntity();
            status.setStatusName("PENDING");
            status = statusRepository.save(status);
        }

        // Tạo booking
        BookingOrderEntity booking = new BookingOrderEntity();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setEmail(email);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setBookingDate(LocalDate.now());
        booking.setCreatedAt(LocalDateTime.now());
        booking.setVoucher(voucher);
        booking.setStatus(status);

        return bookingOrderRepository.save(booking);
    }

    public List<BookingOrderEntity> getBookingsByCustomerEmail(String email) {
        return bookingOrderRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    public List<BookingOrderEntity> getAllBookings() {
        return bookingOrderRepository.findAll();
    }
}
