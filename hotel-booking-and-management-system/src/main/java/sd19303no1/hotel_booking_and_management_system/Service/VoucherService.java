package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.VoucherRepository;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public List<VoucherEntity> getActiveVouchers() {
        LocalDate today = LocalDate.now();
        return voucherRepository.findActiveVouchers(today);
    }

    public List<VoucherEntity> getAllActiveVouchers() {
        LocalDate today = LocalDate.now();
        return voucherRepository.findActiveVouchers(today);
    }

    public VoucherEntity findById(Integer voucherId) {
        return voucherRepository.findById(voucherId).orElse(null);
    }

    public VoucherEntity findByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    // Kiểm tra voucher có hợp lệ không
    public boolean isVoucherValid(VoucherEntity voucher, LocalDate checkInDate) {
        if (voucher == null) return false;
        
        // Kiểm tra voucher còn hạn không
        if (voucher.getExpiryDate() != null && checkInDate.isAfter(voucher.getExpiryDate())) {
            return false;
        }
        
        // Kiểm tra voucher còn số lượng không
        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            return false;
        }
        
        // Kiểm tra trạng thái voucher
        if (!"ACTIVE".equals(voucher.getStatus())) {
            return false;
        }
        
        return true;
    }

    // Tăng số lần sử dụng voucher
    public void incrementUsageCount(Integer voucherId) {
        VoucherEntity voucher = voucherRepository.findById(voucherId).orElse(null);
        if (voucher != null) {
            voucher.setUsedCount(voucher.getUsedCount() + 1);
            voucherRepository.save(voucher);
        }
    }
}
