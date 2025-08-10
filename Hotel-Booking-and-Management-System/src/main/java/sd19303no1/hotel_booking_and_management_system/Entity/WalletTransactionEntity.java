package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransactionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // TOPUP, WITHDRAW, REFUND, PAYMENT, TRANSFER
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "fee", precision = 15, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED
    
    @Column(name = "reference_id")
    private String referenceId; // Booking ID, Bank Account, etc.
    
    @Column(name = "payment_method")
    private String paymentMethod; // VISA, MOMO, BANK, etc.
    
    @Column(name = "bank_account")
    private String bankAccount;
    
    @Column(name = "account_name")
    private String accountName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Constructors
    public WalletTransactionEntity() {
        this.createdAt = LocalDateTime.now();
    }
    
    public WalletTransactionEntity(String email, String transactionType, BigDecimal amount) {
        this();
        this.email = email;
        this.transactionType = transactionType;
        this.amount = amount;
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
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getFee() {
        return fee;
    }
    
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        if ("SUCCESS".equals(status)) {
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getBankAccount() {
        return bankAccount;
    }
    
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    // Business methods
    public BigDecimal getTotalAmount() {
        return this.amount.add(this.fee);
    }
    
    public boolean isSuccessful() {
        return "SUCCESS".equals(this.status);
    }
    
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(this.status);
    }
    
    public String getTransactionTypeDisplay() {
        switch (this.transactionType) {
            case "TOPUP": return "Nạp tiền";
            case "WITHDRAW": return "Rút tiền";
            case "REFUND": return "Hoàn tiền";
            case "PAYMENT": return "Thanh toán";
            case "TRANSFER": return "Chuyển khoản";
            default: return this.transactionType;
        }
    }
    
    public String getStatusDisplay() {
        switch (this.status) {
            case "PENDING": return "Chờ xử lý";
            case "SUCCESS": return "Thành công";
            case "FAILED": return "Thất bại";
            default: return this.status;
        }
    }
} 