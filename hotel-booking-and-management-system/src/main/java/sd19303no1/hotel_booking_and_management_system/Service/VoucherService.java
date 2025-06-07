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
}
