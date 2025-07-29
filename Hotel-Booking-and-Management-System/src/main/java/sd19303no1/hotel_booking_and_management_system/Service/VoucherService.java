package sd19303no1.hotel_booking_and_management_system.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.VoucherRepository;
import sd19303no1.hotel_booking_and_management_system.DTO.VoucherDTO;
import java.util.ArrayList;
import java.util.List;

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

    public List<VoucherDTO> getAllVoucherDTOs() {
        List<VoucherEntity> vouchers = voucherRepository.findAll();
        List<VoucherDTO> dtos = new ArrayList<>();
        for (VoucherEntity v : vouchers) {
            VoucherDTO dto = new VoucherDTO();
            dto.setVoucherId(v.getVoucherId());
            dto.setCode(v.getCode());
            dto.setDescription(v.getDescription());
            dto.setDiscount(v.getDiscount());
            dto.setDiscountAmount(v.getDiscountAmount());
            dto.setMinimumOrderAmount(v.getMinimumOrderAmount());
            dto.setStartDate(v.getStartDate());
            dto.setEndDate(v.getEndDate());
            dto.setExpiryDate(v.getExpiryDate());
            dto.setStatus(v.getStatus());
            dto.setUsageLimit(v.getUsageLimit());
            dto.setUsedCount(v.getUsedCount());
            
            // Xác định loại giảm giá
            if (v.getDiscount() != null && v.getDiscountAmount() == null) {
                dto.setDiscountType("PERCENTAGE");
            } else {
                dto.setDiscountType("FIXED");
            }
            
            // Tính phần trăm sử dụng
            if (v.getUsageLimit() != null && v.getUsageLimit() > 0) {
                dto.setUsagePercentage((double) v.getUsedCount() / v.getUsageLimit() * 100);
            } else {
                dto.setUsagePercentage(0);
            }
            
            dtos.add(dto);
        }
        return dtos;
    }

    public VoucherDTO getVoucherDTOById(Integer voucherId) {
        VoucherEntity voucher = voucherRepository.findById(voucherId).orElse(null);
        if (voucher == null) return null;
        
        VoucherDTO dto = new VoucherDTO();
        dto.setVoucherId(voucher.getVoucherId());
        dto.setCode(voucher.getCode());
        dto.setDescription(voucher.getDescription());
        dto.setDiscount(voucher.getDiscount());
        dto.setDiscountAmount(voucher.getDiscountAmount());
        dto.setMinimumOrderAmount(voucher.getMinimumOrderAmount());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        dto.setExpiryDate(voucher.getExpiryDate());
        dto.setStatus(voucher.getStatus());
        dto.setUsageLimit(voucher.getUsageLimit());
        dto.setUsedCount(voucher.getUsedCount());
        
        // Xác định loại giảm giá
        if (voucher.getDiscount() != null && voucher.getDiscountAmount() == null) {
            dto.setDiscountType("PERCENTAGE");
        } else {
            dto.setDiscountType("FIXED");
        }
        
        // Tính phần trăm sử dụng
        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0) {
            dto.setUsagePercentage((double) voucher.getUsedCount() / voucher.getUsageLimit() * 100);
        } else {
            dto.setUsagePercentage(0);
        }
        
        return dto;
    }

    public void deleteVoucher(Integer voucherId) {
        if (voucherRepository.existsById(voucherId)) {
            voucherRepository.deleteById(voucherId);
        } else {
            throw new IllegalArgumentException("Không tìm thấy voucher để xóa!");
        }
    }

    public void updateVoucher(VoucherEntity voucher) {
        if (voucher.getVoucherId() != null && voucherRepository.existsById(voucher.getVoucherId())) {
            voucherRepository.save(voucher);
        } else {
            throw new IllegalArgumentException("Không tìm thấy voucher để cập nhật!");
        }
    }

    public VoucherEntity createVoucher(VoucherEntity voucher) {
        return voucherRepository.save(voucher);
    }

    public void toggleVoucherStatus(Integer voucherId) {
        VoucherEntity voucher = voucherRepository.findById(voucherId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy voucher!"));
        
        if ("ACTIVE".equals(voucher.getStatus())) {
            voucher.setStatus("INACTIVE");
        } else {
            voucher.setStatus("ACTIVE");
        }
        
        voucherRepository.save(voucher);
    }
}
