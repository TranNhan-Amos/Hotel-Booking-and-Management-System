package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class WalletEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "total_transactions")
    private Integer totalTransactions = 0;
    
    @Column(name = "monthly_spending", precision = 15, scale = 2)
    private BigDecimal monthlySpending = BigDecimal.ZERO;
    
    @Column(name = "rewards_points")
    private Integer rewardsPoints = 0;
    
    @Column(name = "member_tier")
    private String memberTier = "Basic";
    
    @Column(name = "last_top_up")
    private LocalDateTime lastTopUp;
    
    @Column(name = "last_top_up_amount", precision = 15, scale = 2)
    private BigDecimal lastTopUpAmount = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public WalletEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public WalletEntity(String email) {
        this();
        this.email = email;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public Integer getTotalTransactions() {
        return totalTransactions;
    }
    
    public void setTotalTransactions(Integer totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
    
    public BigDecimal getMonthlySpending() {
        return monthlySpending;
    }
    
    public void setMonthlySpending(BigDecimal monthlySpending) {
        this.monthlySpending = monthlySpending;
    }
    
    public Integer getRewardsPoints() {
        return rewardsPoints;
    }
    
    public void setRewardsPoints(Integer rewardsPoints) {
        this.rewardsPoints = rewardsPoints;
    }
    
    public String getMemberTier() {
        return memberTier;
    }
    
    public void setMemberTier(String memberTier) {
        this.memberTier = memberTier;
    }
    
    public LocalDateTime getLastTopUp() {
        return lastTopUp;
    }
    
    public void setLastTopUp(LocalDateTime lastTopUp) {
        this.lastTopUp = lastTopUp;
    }
    
    public BigDecimal getLastTopUpAmount() {
        return lastTopUpAmount;
    }
    
    public void setLastTopUpAmount(BigDecimal lastTopUpAmount) {
        this.lastTopUpAmount = lastTopUpAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void subtractBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementTransactions() {
        this.totalTransactions++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addSpending(BigDecimal amount) {
        this.monthlySpending = this.monthlySpending.add(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addRewardsPoints(Integer points) {
        this.rewardsPoints += points;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateMemberTier() {
        if (this.rewardsPoints >= 5000) {
            this.memberTier = "VIP";
        } else if (this.rewardsPoints >= 2500) {
            this.memberTier = "Premium";
        } else if (this.rewardsPoints >= 1000) {
            this.memberTier = "Gold";
        } else {
            this.memberTier = "Basic";
        }
        this.updatedAt = LocalDateTime.now();
    }
} 