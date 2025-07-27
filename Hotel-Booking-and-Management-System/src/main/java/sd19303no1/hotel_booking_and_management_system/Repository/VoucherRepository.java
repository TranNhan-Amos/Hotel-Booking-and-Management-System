package sd19303no1.hotel_booking_and_management_system.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Integer> {

    @Query("SELECT v FROM VoucherEntity v WHERE v.startDate <= :today " +
           "AND v.endDate >= :today AND v.status = 'ACTIVE'")
    List<VoucherEntity> findActiveVouchers(@Param("today") LocalDate today);

    @Query("SELECT v FROM VoucherEntity v WHERE v.startDate <= :today " +
           "AND v.endDate >= :today AND v.status = 'ACTIVE' " +
           "ORDER BY v.discount DESC")
    List<VoucherEntity> findActiveVouchersOrderByDiscount(@Param("today") LocalDate today);

    VoucherEntity findByCode(String code);
}
