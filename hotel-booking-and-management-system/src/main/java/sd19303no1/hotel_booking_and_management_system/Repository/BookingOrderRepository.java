package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.domain.Pageable; // Thêm import này
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; // Nếu bạn dùng @Query
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import java.util.List;

public interface BookingOrderRepository extends JpaRepository<BookingOrderEntity, Integer> {
    List<BookingOrderEntity> findByEmailOrderByCreatedAtDesc(String email);
    List<BookingOrderEntity> findAllByOrderByCreatedAtDesc();

    // Phương thức để lấy N booking gần nhất
    // Spring Data JPA sẽ tự hiểu "TopN" hoặc bạn có thể dùng @Query với Pageable
    List<BookingOrderEntity> findTopNByOrderByCreatedAtDesc(Pageable pageable);

    // Nếu findTopNByOrderByCreatedAtDesc không hoạt động như mong đợi, bạn có thể dùng:
    // @Query("SELECT b FROM BookingOrderEntity b ORDER BY b.createdAt DESC")
    // List<BookingOrderEntity> findRecentBookings(Pageable pageable);
    // Và trong service sẽ gọi: bookingOrderRepository.findRecentBookings(PageRequest.of(0, limit));
}