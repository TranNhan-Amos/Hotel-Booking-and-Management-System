package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sd19303no1.hotel_booking_and_management_system.Entity.BookingOrderEntity;
import sd19303no1.hotel_booking_and_management_system.Entity.WalletTransactionEntity;
import sd19303no1.hotel_booking_and_management_system.Service.BookingOrderService;
import sd19303no1.hotel_booking_and_management_system.Service.CustomUserDetailsService;
import sd19303no1.hotel_booking_and_management_system.Service.WalletService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WallerController {
    
    @Autowired
    private BookingOrderService bookingOrderService;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private WalletService walletService;
    
    @GetMapping("Waller")
    public String Waller(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        if (email != null && !email.equals("anonymousUser")) {
            // Lấy thông tin ví và giao dịch của user
            Map<String, Object> walletData = walletService.getWalletData(email);
            model.addAttribute("walletData", walletData);
            
            // Lấy danh sách đặt phòng gần đây
            List<BookingOrderEntity> recentBookings = bookingOrderService.getBookingsByCustomerEmailForCustomer(email);
            model.addAttribute("recentBookings", recentBookings.subList(0, Math.min(recentBookings.size(), 5)));
            
            // Lấy giao dịch ví
            List<WalletTransactionEntity> walletTransactions = walletService.getTransactionsByEmail(email);
            model.addAttribute("walletTransactions", walletTransactions.subList(0, Math.min(walletTransactions.size(), 10)));
            
            // Tính toán thống kê
            BigDecimal totalSpent = recentBookings.stream()
                .filter(booking -> booking.getTotalPrice() != null)
                .map(BookingOrderEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalRefunded = recentBookings.stream()
                .filter(booking -> booking.getRefundAmount() != null && "COMPLETED".equals(booking.getRefundStatus()))
                .map(BookingOrderEntity::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("totalRefunded", totalRefunded);
            model.addAttribute("bookingCount", recentBookings.size());
            model.addAttribute("pendingRefunds", recentBookings.stream()
                .filter(booking -> "PENDING".equals(booking.getRefundStatus())).count());
        }
        
        return "Page/Wallet";
    }
    
    @PostMapping("/wallet/process-refund")
    @ResponseBody
    public Map<String, Object> processRefundToWallet(@RequestParam Integer bookingId, 
                                                    @RequestParam(required = false) String _csrf) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            if (email == null || email.equals("anonymousUser")) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
                return response;
            }
            
            // Xử lý hoàn tiền vào ví
            BookingOrderEntity booking = bookingOrderService.processRefund(bookingId);
            
            if (booking != null && "COMPLETED".equals(booking.getRefundStatus())) {
                // Lấy tên khách sạn
                String hotelName = "Khách sạn";
                if (booking.getRoom() != null && booking.getRoom().getPartner() != null) {
                    hotelName = booking.getRoom().getPartner().getCompanyName();
                }
                
                // Tạo giao dịch hoàn tiền vào ví
                WalletTransactionEntity refundTransaction = walletService.processRefundToWallet(
                    email, 
                    booking.getRefundAmount(), 
                    bookingId.toString(), 
                    hotelName
                );
                
                response.put("success", true);
                response.put("message", "Hoàn tiền thành công! Số tiền " + 
                    booking.getRefundAmount().toString() + "₫ đã được chuyển vào ví của bạn");
                response.put("refundAmount", booking.getRefundAmount());
                response.put("newBalance", walletService.getBalance(email));
                response.put("transactionId", refundTransaction.getId());
            } else {
                response.put("success", false);
                response.put("message", "Không thể xử lý hoàn tiền. Vui lòng thử lại sau");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/wallet/withdraw-refund")
    @ResponseBody
    public Map<String, Object> withdrawRefundToBank(@RequestParam Integer bookingId, 
                                                   @RequestParam String bankAccount,
                                                   @RequestParam String accountName,
                                                   @RequestParam(required = false) String _csrf) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            if (email == null || email.equals("anonymousUser")) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
                return response;
            }
            
            // Xử lý hoàn tiền và chuyển về tài khoản ngân hàng
            BookingOrderEntity booking = bookingOrderService.processRefund(bookingId);
            
            if (booking != null && "COMPLETED".equals(booking.getRefundStatus())) {
                // Lấy tên khách sạn
                String hotelName = "Khách sạn";
                if (booking.getRoom() != null && booking.getRoom().getPartner() != null) {
                    hotelName = booking.getRoom().getPartner().getCompanyName();
                }
                
                // Tạo giao dịch rút tiền về ngân hàng
                WalletTransactionEntity withdrawalTransaction = walletService.processRefundToBank(
                    email, 
                    booking.getRefundAmount(), 
                    bookingId.toString(), 
                    hotelName,
                    bankAccount,
                    accountName
                );
                
                response.put("success", true);
                response.put("message", "Yêu cầu rút tiền thành công! Số tiền " + 
                    booking.getRefundAmount().toString() + "₫ sẽ được chuyển về tài khoản " + 
                    accountName + " trong 1-3 ngày làm việc");
                response.put("refundAmount", booking.getRefundAmount());
                response.put("bankAccount", bankAccount);
                response.put("accountName", accountName);
                response.put("transactionId", withdrawalTransaction.getId());
            } else {
                response.put("success", false);
                response.put("message", "Không thể xử lý hoàn tiền. Vui lòng thử lại sau");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/wallet/topup")
    @ResponseBody
    public Map<String, Object> topUpWallet(@RequestParam BigDecimal amount, 
                                          @RequestParam String paymentMethod,
                                          @RequestParam(required = false) String _csrf) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            if (email == null || email.equals("anonymousUser")) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
                return response;
            }
            
            // Tạo giao dịch nạp tiền
            WalletTransactionEntity topUpTransaction = walletService.createTransaction(
                email, 
                "TOPUP", 
                amount, 
                "Nạp tiền từ " + paymentMethod
            );
            topUpTransaction.setPaymentMethod(paymentMethod);
            topUpTransaction.setStatus("SUCCESS");
            
            // Cập nhật số dư ví
            walletService.addBalance(email, amount);
            
            response.put("success", true);
            response.put("message", "Nạp tiền thành công! Số tiền " + 
                amount.toString() + "₫ đã được thêm vào ví của bạn");
            response.put("amount", amount);
            response.put("newBalance", walletService.getBalance(email));
            response.put("transactionId", topUpTransaction.getId());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return response;
    }
}
