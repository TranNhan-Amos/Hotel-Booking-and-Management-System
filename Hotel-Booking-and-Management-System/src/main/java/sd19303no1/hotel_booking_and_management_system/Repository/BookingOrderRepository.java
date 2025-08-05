package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingOrderRepository extends JpaRepository<BookingOrderEntity, Integer> {
    
    // PERFORMANCE OPTIMIZATION: Find booking by ID with all related entities loaded to prevent N+1 queries
    @Query("SELECT b FROM BookingOrderEntity b " +
           "LEFT JOIN FETCH b.customer " +
           "LEFT JOIN FETCH b.room r " +
           "LEFT JOIN FETCH r.partner " +
           "LEFT JOIN FETCH b.status " +
           "LEFT JOIN FETCH b.voucher " +
           "WHERE b.bookingId = :bookingId")
    Optional<BookingOrderEntity> findByIdWithDetails(@Param("bookingId") Integer bookingId);
    
    List<BookingOrderEntity> findByEmailOrderByCreatedAtDesc(String email);

    List<BookingOrderEntity> findAllByOrderByCreatedAtDesc();

    // Phương thức để lấy N booking gần nhất
    List<BookingOrderEntity> findTopNByOrderByCreatedAtDesc(Pageable pageable);

    // Thêm method để lấy booking gần đây với thông tin đầy đủ
    @Query("SELECT b FROM BookingOrderEntity b " +
           "LEFT JOIN FETCH b.customer " +
           "LEFT JOIN FETCH b.room " +
           "LEFT JOIN FETCH b.status " +
           "ORDER BY b.createdAt DESC")
    List<BookingOrderEntity> findRecentBookingsWithDetails(Pageable pageable);
    
    // Thêm method để lấy booking gần đây với thông tin đầy đủ hơn
    @Query("SELECT b FROM BookingOrderEntity b " +
           "LEFT JOIN FETCH b.customer " +
           "LEFT JOIN FETCH b.room r " +
           "LEFT JOIN FETCH r.partner " +
           "LEFT JOIN FETCH b.status " +
           "WHERE b.status.statusName IN ('CONFIRMED', 'PENDING', 'COMPLETED') " +
           "ORDER BY b.createdAt DESC")
    List<BookingOrderEntity> findRecentBookingsWithFullDetails(Pageable pageable);

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

    // Tìm booking theo booking_status field
    @Query("SELECT b FROM BookingOrderEntity b WHERE b.bookingStatus = :bookingStatus")
    List<BookingOrderEntity> findByBookingStatus(@Param("bookingStatus") String bookingStatus);

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
            WHERE (r.partner.id = :partnerId OR b.partner.id = :partnerId OR b.email = :partnerEmail)

                             """)
    List<BookingOrderEntity> findAllBookingsByPartner(@Param("partnerId") Long partnerId, @Param("partnerEmail") String partnerEmail);

    @Query("""
                SELECT FUNCTION('DATE', b.createdAt), COUNT(b), SUM(b.totalPrice)
                FROM BookingOrderEntity b
                WHERE b.room.partner.id = :partnerId
                  AND b.createdAt >= :startDate AND b.createdAt < :endDate
                GROUP BY FUNCTION('DATE', b.createdAt)
                ORDER BY FUNCTION('DATE', b.createdAt) DESC
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

    // DataReport for Partner
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

    @Query("""
                SELECT DISTINCT YEAR(b.createdAt)
                FROM BookingOrderEntity b
                WHERE b.room.partner.id = :partnerId
                ORDER BY YEAR(b.createdAt) DESC
            """)
    List<Integer> findAvailableBookingYears(@Param("partnerId") Long partnerId);
 
    //Lấy dữ liệu doanh thu hàng tháng của đối tác theo năm
    @Query("""
    SELECT MONTH(b.createdAt), SUM(b.totalPrice)
    FROM BookingOrderEntity b
    WHERE b.room.partner.id = :partnerId AND YEAR(b.createdAt) = :year
    GROUP BY MONTH(b.createdAt)
    ORDER BY MONTH(b.createdAt)
""")
List<Object[]> getMonthlyRevenueReportPartner(@Param("partnerId") Long partnerId,
                                 @Param("year") Integer year);

  // Thêm các phương thức cho báo cáo
  @Query("SELECT b FROM BookingOrderEntity b WHERE b.createdAt BETWEEN :startDate AND :endDate")
  List<BookingOrderEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  // Định nghĩa projection cho phòng
  public interface TopRoomProjection {
      Integer getRoomId();
      String getRoomName();
      Long getBookingCount();
      java.math.BigDecimal getTotalRevenue();
  }
  // Định nghĩa projection cho khách hàng
  public interface TopCustomerProjection {
      Integer getCustomerId();
      String getCustomerName();
      String getCustomerEmail();
      Long getBookingCount();
      java.math.BigDecimal getTotalSpent();
  }

@Query(value = """
      SELECT 
          r.room_id as roomId,
          r.name_number as roomName,
          COUNT(b.booking_id) as bookingCount,
          SUM(b.total_price) as totalRevenue
      FROM rooms r
      LEFT JOIN bookingorder b ON r.room_id = b.room_id
      WHERE b.status_id NOT IN (SELECT status_id FROM status WHERE status_name IN ('CANCELLED', 'REFUNDED'))
      GROUP BY r.room_id, r.name_number
      ORDER BY bookingCount DESC
      """, nativeQuery = true)
  List<Map<String, Object>> findTopRoomsByBookingCount();

   @Query(value = """
      SELECT 
          c.customer_id as customerId,
          c.name as customerName,
          c.email as customerEmail,
          COUNT(b.booking_id) as bookingCount,
          SUM(b.total_price) as totalSpent
      FROM customers c
      LEFT JOIN bookingorder b ON c.customer_id = b.customer_id
      WHERE b.status_id NOT IN (SELECT status_id FROM status WHERE status_name IN ('CANCELLED', 'REFUNDED'))
      GROUP BY c.customer_id, c.name, c.email
      ORDER BY bookingCount DESC
      """, nativeQuery = true)
  List<Map<String, Object>> findTopCustomersByBookingCount();

  // Methods for Reports
  @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM BookingOrderEntity b " +
         "WHERE DATE(b.createdAt) BETWEEN :startDate AND :endDate " +
         "AND b.status.statusName NOT IN ('CANCELLED', 'REFUNDED')")
  BigDecimal calculateTotalRevenueInDateRange(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

  @Query("SELECT COUNT(b) FROM BookingOrderEntity b " +
         "WHERE DATE(b.createdAt) BETWEEN :startDate AND :endDate " +
         "AND b.status.statusName NOT IN ('CANCELLED', 'REFUNDED')")
  Long countBookingsInDateRange(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

  @Query("SELECT COUNT(DISTINCT b.customer.customerId) FROM BookingOrderEntity b " +
         "WHERE DATE(b.createdAt) BETWEEN :startDate AND :endDate " +
         "AND b.status.statusName NOT IN ('CANCELLED', 'REFUNDED')")
  Long countNewCustomersInDateRange(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

  @Query("SELECT COALESCE(SUM(b.roomQuantity), 0) FROM BookingOrderEntity b " +
         "WHERE DATE(b.createdAt) BETWEEN :startDate AND :endDate " +
         "AND b.status.statusName NOT IN ('CANCELLED', 'REFUNDED')")
  Long countBookedRoomsInDateRangeForReports(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

  @Query(value = """
      SELECT 
          DATE(b.created_at) as date,
          COUNT(b.booking_id) as totalBookings,
          COALESCE(SUM(b.total_price), 0) as totalRevenue,
          0.0 as occupancyRate,
          COUNT(DISTINCT b.customer_id) as newCustomers
      FROM bookingorder b
      WHERE DATE(b.created_at) BETWEEN :startDate AND :endDate
        AND b.status_id NOT IN (SELECT status_id FROM status WHERE status_name IN ('CANCELLED', 'REFUNDED'))
      GROUP BY DATE(b.created_at)
      ORDER BY date
      """, nativeQuery = true)
  List<Map<String, Object>> getDailyReports(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query(value = """
       SELECT 
           r.room_id as roomId,
           r.name_number as roomName,
           COUNT(b.booking_id) as bookingCount,
           COALESCE(SUM(b.total_price), 0) as totalRevenue
       FROM rooms r
       LEFT JOIN bookingorder b ON r.room_id = b.room_id
       WHERE (b.created_at BETWEEN :startDate AND :endDate OR b.created_at IS NULL)
         AND (b.status_id NOT IN (SELECT status_id FROM status WHERE status_name IN ('CANCELLED', 'REFUNDED')) OR b.status_id IS NULL)
       GROUP BY r.room_id, r.name_number
       ORDER BY bookingCount DESC, totalRevenue DESC
       """, nativeQuery = true)
  List<Map<String, Object>> getTopRooms(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

  @Query(value = """
      SELECT 
          c.customer_id as customerId,
          c.name as customerName,
          c.email as customerEmail,
          COUNT(b.booking_id) as bookingCount,
          COALESCE(SUM(b.total_price), 0) as totalSpent
      FROM customers c
      LEFT JOIN bookingorder b ON c.customer_id = b.customer_id
      WHERE (b.created_at BETWEEN :startDate AND :endDate OR b.created_at IS NULL)
        AND (b.status_id NOT IN (SELECT status_id FROM status WHERE status_name IN ('CANCELLED', 'REFUNDED')) OR b.status_id IS NULL)
      GROUP BY c.customer_id, c.name, c.email
      ORDER BY bookingCount DESC, totalSpent DESC
      """, nativeQuery = true)
  List<Map<String, Object>> getTopCustomers(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}