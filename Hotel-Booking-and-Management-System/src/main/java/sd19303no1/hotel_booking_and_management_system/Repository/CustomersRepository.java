
package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;

@Repository
public interface CustomersRepository extends JpaRepository<CustomersEntity, Integer> {
    Optional<CustomersEntity> findByEmail(String email);
    Optional<CustomersEntity> findByEmailIgnoreCase(String email);
    Optional<CustomersEntity> findBySystemUser(sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity systemUser);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<CustomersEntity> findAll();
    
    // Thêm các phương thức cho báo cáo
    @Query("SELECT COUNT(c) FROM CustomersEntity c WHERE c.createdDate = :date")
    long countByCreatedDate(@Param("date") Date date);
    
    @Query("SELECT COUNT(c) FROM CustomersEntity c WHERE c.createdDate BETWEEN :startDate AND :endDate")
    Long countByCreatedDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
