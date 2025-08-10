package sd19303no1.hotel_booking_and_management_system.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sd19303no1.hotel_booking_and_management_system.Entity.WalletEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.WalletTransactionEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.WalletRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.WalletTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    
    // Wallet management
    public WalletEntity getOrCreateWallet(String email) {
        Optional<WalletEntity> existingWallet = walletRepository.findByEmailIgnoreCase(email);
        if (existingWallet.isPresent()) {
            return existingWallet.get();
        } else {
            WalletEntity newWallet = new WalletEntity(email);
            return walletRepository.save(newWallet);
        }
    }
    
    public WalletEntity getWalletByEmail(String email) {
        return walletRepository.findByEmailIgnoreCase(email).orElse(null);
    }
    
    public BigDecimal getBalance(String email) {
        return walletRepository.findBalanceByEmail(email);
    }
    
    @Transactional
    public WalletEntity addBalance(String email, BigDecimal amount) {
        WalletEntity wallet = getOrCreateWallet(email);
        wallet.addBalance(amount);
        wallet.incrementTransactions();
        return walletRepository.save(wallet);
    }
    
    @Transactional
    public WalletEntity subtractBalance(String email, BigDecimal amount) {
        WalletEntity wallet = getOrCreateWallet(email);
        if (wallet.getBalance().compareTo(amount) >= 0) {
            wallet.subtractBalance(amount);
            wallet.incrementTransactions();
            return walletRepository.save(wallet);
        } else {
            throw new RuntimeException("Số dư không đủ để thực hiện giao dịch");
        }
    }
    
    @Transactional
    public WalletEntity addSpending(String email, BigDecimal amount) {
        WalletEntity wallet = getOrCreateWallet(email);
        wallet.addSpending(amount);
        wallet.addRewardsPoints(calculateRewardPoints(amount));
        wallet.updateMemberTier();
        return walletRepository.save(wallet);
    }
    
    private Integer calculateRewardPoints(BigDecimal amount) {
        // 1 điểm cho mỗi 10,000₫ chi tiêu
        return amount.divide(new BigDecimal("10000"), 0, BigDecimal.ROUND_DOWN).intValue();
    }
    
    // Transaction management
    public WalletTransactionEntity createTransaction(String email, String transactionType, BigDecimal amount, String description) {
        WalletTransactionEntity transaction = new WalletTransactionEntity(email, transactionType, amount);
        transaction.setDescription(description);
        return walletTransactionRepository.save(transaction);
    }
    
    public WalletTransactionEntity createRefundTransaction(String email, BigDecimal amount, String bookingId, String hotelName) {
        WalletTransactionEntity transaction = new WalletTransactionEntity(email, "REFUND", amount);
        transaction.setDescription("Hoàn tiền hủy đặt phòng - " + hotelName);
        transaction.setReferenceId(bookingId);
        transaction.setStatus("SUCCESS");
        return walletTransactionRepository.save(transaction);
    }
    
    public WalletTransactionEntity createWithdrawalTransaction(String email, BigDecimal amount, String bankAccount, String accountName) {
        WalletTransactionEntity transaction = new WalletTransactionEntity(email, "WITHDRAW", amount);
        transaction.setDescription("Rút tiền về tài khoản " + accountName);
        transaction.setBankAccount(bankAccount);
        transaction.setAccountName(accountName);
        transaction.setStatus("PENDING");
        return walletTransactionRepository.save(transaction);
    }
    
    public List<WalletTransactionEntity> getTransactionsByEmail(String email) {
        return walletTransactionRepository.findByEmailOrderByCreatedAtDesc(email);
    }
    
    public List<WalletTransactionEntity> getTransactionsByEmailAndType(String email, String transactionType) {
        return walletTransactionRepository.findByEmailAndTransactionTypeOrderByCreatedAtDesc(email, transactionType);
    }
    
    public List<WalletTransactionEntity> getPendingRefunds(String email) {
        return walletTransactionRepository.findPendingRefundsByEmail(email);
    }
    
    public List<WalletTransactionEntity> getSuccessfulRefunds(String email) {
        return walletTransactionRepository.findSuccessfulRefundsByEmail(email);
    }
    
    // Wallet statistics
    public Map<String, Object> getWalletData(String email) {
        Map<String, Object> walletData = new HashMap<>();
        
        WalletEntity wallet = getOrCreateWallet(email);
        List<WalletTransactionEntity> recentTransactions = getTransactionsByEmail(email);
        
        // Calculate monthly spending
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal monthlySpending = recentTransactions.stream()
            .filter(t -> t.getCreatedAt().isAfter(startOfMonth) && "PAYMENT".equals(t.getTransactionType()) && t.isSuccessful())
            .map(WalletTransactionEntity::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        walletData.put("balance", wallet.getBalance());
        walletData.put("totalTransactions", wallet.getTotalTransactions());
        walletData.put("monthlySpending", monthlySpending);
        walletData.put("rewardsPoints", wallet.getRewardsPoints());
        walletData.put("memberTier", wallet.getMemberTier());
        walletData.put("lastTopUp", wallet.getLastTopUp());
        walletData.put("lastTopUpAmount", wallet.getLastTopUpAmount());
        
        return walletData;
    }
    
    public Map<String, Object> getWalletStatistics(String email) {
        Map<String, Object> stats = new HashMap<>();
        
        List<WalletTransactionEntity> allTransactions = getTransactionsByEmail(email);
        List<WalletTransactionEntity> successfulRefunds = getSuccessfulRefunds(email);
        List<WalletTransactionEntity> pendingRefunds = getPendingRefunds(email);
        
        // Calculate total refunded amount
        BigDecimal totalRefunded = successfulRefunds.stream()
            .map(WalletTransactionEntity::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("totalTransactions", allTransactions.size());
        stats.put("totalRefunded", totalRefunded);
        stats.put("pendingRefunds", pendingRefunds.size());
        stats.put("successfulRefunds", successfulRefunds.size());
        
        return stats;
    }
    
    // Refund processing
    @Transactional
    public WalletTransactionEntity processRefundToWallet(String email, BigDecimal amount, String bookingId, String hotelName) {
        // Create refund transaction
        WalletTransactionEntity refundTransaction = createRefundTransaction(email, amount, bookingId, hotelName);
        
        // Add balance to wallet
        addBalance(email, amount);
        
        return refundTransaction;
    }
    
    @Transactional
    public WalletTransactionEntity processRefundToBank(String email, BigDecimal amount, String bookingId, String hotelName, String bankAccount, String accountName) {
        // Create withdrawal transaction
        WalletTransactionEntity withdrawalTransaction = createWithdrawalTransaction(email, amount, bankAccount, accountName);
        withdrawalTransaction.setDescription("Hoàn tiền hủy đặt phòng - " + hotelName);
        withdrawalTransaction.setReferenceId(bookingId);
        
        return walletTransactionRepository.save(withdrawalTransaction);
    }
} 