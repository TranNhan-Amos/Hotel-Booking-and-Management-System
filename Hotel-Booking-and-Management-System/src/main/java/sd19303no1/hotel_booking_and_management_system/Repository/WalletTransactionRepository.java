package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sd19303no1.hotel_booking_and_management_system.Entity.WalletTransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Integer> {
    
    List<WalletTransactionEntity> findByEmailOrderByCreatedAtDesc(String email);
    
    Page<WalletTransactionEntity> findByEmailOrderByCreatedAtDesc(String email, Pageable pageable);
    
    List<WalletTransactionEntity> findByEmailAndTransactionTypeOrderByCreatedAtDesc(String email, String transactionType);
    
    List<WalletTransactionEntity> findByEmailAndStatusOrderByCreatedAtDesc(String email, String status);
    
    @Query("SELECT wt FROM WalletTransactionEntity wt WHERE wt.email = :email AND wt.createdAt >= :startDate ORDER BY wt.createdAt DESC")
    List<WalletTransactionEntity> findByEmailAndCreatedAtAfter(@Param("email") String email, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(wt) FROM WalletTransactionEntity wt WHERE wt.email = :email AND wt.status = 'SUCCESS'")
    Long countSuccessfulTransactionsByEmail(@Param("email") String email);
    
    @Query("SELECT SUM(wt.amount) FROM WalletTransactionEntity wt WHERE wt.email = :email AND wt.transactionType = :type AND wt.status = 'SUCCESS' AND wt.createdAt >= :startDate")
    BigDecimal sumAmountByEmailAndTypeAndDate(@Param("email") String email, @Param("type") String type, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT wt FROM WalletTransactionEntity wt WHERE wt.email = :email AND wt.referenceId = :referenceId ORDER BY wt.createdAt DESC")
    List<WalletTransactionEntity> findByEmailAndReferenceId(@Param("email") String email, @Param("referenceId") String referenceId);
    
    @Query("SELECT wt FROM WalletTransactionEntity wt WHERE wt.email = :email AND wt.transactionType IN ('REFUND', 'WITHDRAW') AND wt.status = 'PENDING' ORDER BY wt.createdAt DESC")
    List<WalletTransactionEntity> findPendingRefundsByEmail(@Param("email") String email);
    
    @Query("SELECT wt FROM WalletTransactionEntity wt WHERE wt.email = :email AND wt.transactionType = 'REFUND' AND wt.status = 'SUCCESS' ORDER BY wt.createdAt DESC")
    List<WalletTransactionEntity> findSuccessfulRefundsByEmail(@Param("email") String email);
} 