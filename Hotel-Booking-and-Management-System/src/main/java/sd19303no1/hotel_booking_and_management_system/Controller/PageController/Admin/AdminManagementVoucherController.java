package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



import sd19303no1.hotel_booking_and_management_system.DTO.VoucherDTO;
import sd19303no1.hotel_booking_and_management_system.Entity.VoucherEntity;
import sd19303no1.hotel_booking_and_management_system.Service.VoucherService;

@Controller
@RequestMapping("/admin/vouchers")
public class AdminManagementVoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public String viewVoucherManagementPage(Model model) {
        List<VoucherDTO> vouchers = voucherService.getAllVoucherDTOs();
        model.addAttribute("vouchers", vouchers);
        return "Admin/Management-Voucher";
    }

    @GetMapping("/{voucherId}")
    public String viewVoucherDetail(@PathVariable Integer voucherId, Model model) {
        try {
            VoucherDTO voucher = voucherService.getVoucherDTOById(voucherId);
            if (voucher == null) {
                return "redirect:/admin/vouchers?error=Không tìm thấy voucher";
            }
            model.addAttribute("voucher", voucher);
            return "Admin/voucher-detail";
        } catch (Exception e) {
            return "redirect:/admin/vouchers?error=Lỗi khi tải thông tin voucher";
        }
    }

    @GetMapping("/create")
    public String showCreateVoucherPage() {
        return "Admin/voucher-create";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createVoucher(@RequestBody VoucherDTO voucherData) {
        try {
            // Validation
            if (voucherData.getCode() == null || voucherData.getCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã voucher không được để trống");
            }
            
            if (voucherData.getDescription() == null || voucherData.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mô tả voucher không được để trống");
            }
            
            // Kiểm tra mã voucher đã tồn tại
            if (voucherService.findByCode(voucherData.getCode().trim().toUpperCase()) != null) {
                return ResponseEntity.badRequest().body("Mã voucher đã tồn tại");
            }
            
            if (voucherData.getStartDate() == null || voucherData.getEndDate() == null) {
                return ResponseEntity.badRequest().body("Ngày bắt đầu và kết thúc không được để trống");
            }
            
            if (voucherData.getStartDate().isAfter(voucherData.getEndDate())) {
                return ResponseEntity.badRequest().body("Ngày bắt đầu phải trước ngày kết thúc");
            }
            
            if (voucherData.getUsageLimit() <= 0) {
                return ResponseEntity.badRequest().body("Giới hạn sử dụng phải lớn hơn 0");
            }
            
            VoucherEntity voucher = new VoucherEntity();
            voucher.setCode(voucherData.getCode().trim().toUpperCase());
            voucher.setDescription(voucherData.getDescription().trim());
            voucher.setDiscount(voucherData.getDiscount());
            voucher.setDiscountAmount(voucherData.getDiscountAmount());
            voucher.setMinimumOrderAmount(voucherData.getMinimumOrderAmount());
            voucher.setStartDate(voucherData.getStartDate());
            voucher.setEndDate(voucherData.getEndDate());
            voucher.setExpiryDate(voucherData.getEndDate());
            voucher.setStatus("ACTIVE");
            voucher.setUsageLimit(voucherData.getUsageLimit());
            voucher.setUsedCount(0);
            
            voucherService.createVoucher(voucher);
            
            return ResponseEntity.ok("Tạo voucher thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi tạo voucher: " + e.getMessage());
        }
    }

    @PostMapping("/update/{voucherId}")
    @ResponseBody
    public ResponseEntity<String> updateVoucher(@PathVariable Integer voucherId, 
                                              @RequestBody VoucherDTO voucherData) {
        try {
            // Validation
            if (voucherData.getCode() == null || voucherData.getCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã voucher không được để trống");
            }
            
            if (voucherData.getDescription() == null || voucherData.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mô tả voucher không được để trống");
            }
            
            VoucherEntity existingVoucher = voucherService.findById(voucherId);
            if (existingVoucher == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy voucher");
            }
            
            // Kiểm tra mã voucher đã tồn tại (trừ voucher hiện tại)
            VoucherEntity duplicateVoucher = voucherService.findByCode(voucherData.getCode().trim().toUpperCase());
            if (duplicateVoucher != null && !duplicateVoucher.getVoucherId().equals(voucherId)) {
                return ResponseEntity.badRequest().body("Mã voucher đã tồn tại");
            }
            
            if (voucherData.getStartDate() == null || voucherData.getEndDate() == null) {
                return ResponseEntity.badRequest().body("Ngày bắt đầu và kết thúc không được để trống");
            }
            
            if (voucherData.getStartDate().isAfter(voucherData.getEndDate())) {
                return ResponseEntity.badRequest().body("Ngày bắt đầu phải trước ngày kết thúc");
            }
            
            if (voucherData.getUsageLimit() <= 0) {
                return ResponseEntity.badRequest().body("Giới hạn sử dụng phải lớn hơn 0");
            }
            
            // Cập nhật thông tin
            existingVoucher.setCode(voucherData.getCode().trim().toUpperCase());
            existingVoucher.setDescription(voucherData.getDescription().trim());
            existingVoucher.setDiscount(voucherData.getDiscount());
            existingVoucher.setDiscountAmount(voucherData.getDiscountAmount());
            existingVoucher.setMinimumOrderAmount(voucherData.getMinimumOrderAmount());
            existingVoucher.setStartDate(voucherData.getStartDate());
            existingVoucher.setEndDate(voucherData.getEndDate());
            existingVoucher.setExpiryDate(voucherData.getEndDate());
            existingVoucher.setUsageLimit(voucherData.getUsageLimit());
            
            voucherService.updateVoucher(existingVoucher);
            
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    @PostMapping("/delete/{voucherId}")
    @ResponseBody
    public ResponseEntity<String> deleteVoucher(@PathVariable Integer voucherId) {
        try {
            voucherService.deleteVoucher(voucherId);
            return ResponseEntity.ok("Xóa voucher thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa: " + e.getMessage());
        }
    }

    @PostMapping("/toggle/{voucherId}")
    @ResponseBody
    public ResponseEntity<String> toggleVoucherStatus(@PathVariable Integer voucherId) {
        try {
            voucherService.toggleVoucherStatus(voucherId);
            return ResponseEntity.ok("Thay đổi trạng thái thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi thay đổi trạng thái: " + e.getMessage());
        }
    }
}
