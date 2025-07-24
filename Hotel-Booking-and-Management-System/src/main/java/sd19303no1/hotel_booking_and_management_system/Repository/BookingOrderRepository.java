package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BookingOrderRepository extends JpaRepository<BookingOrderEntity, Integer> {
  List<BookingOrderEntity> findByEmailOrderByCreatedAtDesc(String email);

  List<BookingOrderEntity> findAllByOrderByCreatedAtDesc();

  // Phương thức để lấy N booking gần nhất
  List<BookingOrderEntity> findTopNByOrderByCreatedAtDesc(Pageable pageable);

  // Tìm các booking xung đột (conflicting bookings)
  @Query("SELECT b FROM BookingOrderEntity b WHERE b.room.roomId = :roomId " +
      "AND b.status.statusName NOT IN ('CANCELLED', 'REFUNDED') " +
      "AND ((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
  List<BookingOrderEntity> findConflictingBookings(@Param("roomId") Integer roomId,
      @Param("checkInDate") LocalDate checkInDate,
      @Param("checkOutDate") LocalDate checkOutDate);

  // Tìm booking theo trạng thái
  @Query("SELECT b FROM BookingOrderEntity b WHERE b.status.statusName = :statusName")
  List<BookingOrderEntity> findByStatusName(@Param("statusName") String statusName);

  // Tìm booking theo khoảng thời gian
  @Query("SELECT b FROM BookingOrderEntity b WHERE b.checkInDate >= :startDate AND b.checkInDate <= :endDate")
  List<BookingOrderEntity> findByCheckInDateBetween(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  // Tìm booking cần thanh toán
  @Query("SELECT b FROM BookingOrderEntity b WHERE b.paymentStatus = 'PENDING'")
  List<BookingOrderEntity> findPendingPayments();

  // Tìm booking cần hoàn tiền
  @Query("SELECT b FROM BookingOrderEntity b WHERE b.refundStatus = 'PENDING'")
  List<BookingOrderEntity> findPendingRefunds();

  @Query("""
          SELECT COUNT(b)
          FROM BookingOrderEntity b
          JOIN b.room r
          WHERE DATE(b.bookingDate) = CURRENT_DATE
            AND r.partner.id = :partnerId
      """)
  long countTodayBookingsByPartner(@Param("partnerId") Long partnerId);

  @Query("""
                             SELECT b
      FROM BookingOrderEntity b
      JOIN FETCH b.room r
      JOIN FETCH b.customer c
      WHERE r.partner.id = :partnerId

                       """)
  List<BookingOrderEntity> findAllBookingsByPartner(@Param("partnerId") Long partnerId);

 @Query("""
    SELECT FUNCTION('DATE', b.createdAt), COUNT(b), SUM(b.totalPrice)
    FROM BookingOrderEntity b
    WHERE b.room.partner.id = :partnerId
      AND b.createdAt >= :startDate AND b.createdAt < :endDate
    GROUP BY FUNCTION('DATE', b.createdAt)
    ORDER BY FUNCTION('DATE', b.createdAt)
""")
List<Object[]> getBookingStatsInRange(@Param("partnerId") Long partnerId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);


  @Query("""
      SELECT SUM(b.totalPrice)
      FROM BookingOrderEntity b
          JOIN b.room r
          WHERE DATE(b.bookingDate) = CURRENT_DATE
            AND r.partner.id = :partnerId
      """)
  Integer sumDoanhThuToday(@Param("partnerId") Long partnerId);


  //DataReport for Partner
  @Query(value = """
      SELECT
          SUM(b.total_price) AS total_revenue,
          COUNT(*) AS total_bookings
      FROM
          bookingorder b
      JOIN
          rooms r ON b.room_id = r.room_id
      WHERE
          r.partner_id = :partnerId
          AND b.booking_date BETWEEN :startDate AND :endDate
      """, nativeQuery = true)
  List<Object[]> getReportData(@Param("partnerId") Long partnerId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

      

}