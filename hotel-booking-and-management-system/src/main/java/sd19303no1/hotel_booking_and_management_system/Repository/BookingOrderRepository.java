package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;

@Repository
public interface BookingOrderRepository extends JpaRepository<BookingOrderEntity, Integer> {

    @Query("SELECT b FROM BookingOrderEntity b ORDER BY b.createdAt DESC")
    List<BookingOrderEntity> findRecentBookings(Pageable pageable);

    List<BookingOrderEntity> findByEmailOrderByCreatedAtDesc(String email);
}
