package sd19303no1.hotel_booking_and_management_system.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sd19303no1.hotel_booking_and_management_system.Entity.WalletEntity;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Integer> {
    
    Optional<WalletEntity> findByEmail(String email);
    Optional<WalletEntity> findByEmailIgnoreCase(String email);
    
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    
    @Query("SELECT w.balance FROM WalletEntity w WHERE w.email = :email")
    BigDecimal findBalanceByEmail(@Param("email") String email);
    
    @Query("SELECT w.totalTransactions FROM WalletEntity w WHERE w.email = :email")
    Integer findTotalTransactionsByEmail(@Param("email") String email);
    
    @Query("SELECT w.monthlySpending FROM WalletEntity w WHERE w.email = :email")
    BigDecimal findMonthlySpendingByEmail(@Param("email") String email);
    
    @Query("SELECT w.rewardsPoints FROM WalletEntity w WHERE w.email = :email")
    Integer findRewardsPointsByEmail(@Param("email") String email);
    
    @Query("SELECT w.memberTier FROM WalletEntity w WHERE w.email = :email")
    String findMemberTierByEmail(@Param("email") String email);
} 