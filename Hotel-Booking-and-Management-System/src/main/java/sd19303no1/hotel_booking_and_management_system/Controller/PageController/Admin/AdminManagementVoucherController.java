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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
        return "admin/Management-Voucher";
    }

    @GetMapping("/{voucherId}")
    public String viewVoucherDetail(@PathVariable Integer voucherId, Model model) {
        try {
            VoucherDTO voucher = voucherService.getVoucherDTOById(voucherId);
            if (voucher == null) {
                return "redirect:/admin/vouchers?error=Không tìm thấy voucher";
            }
            model.addAttribute("voucher", voucher);
            return "admin/voucher-detail";
        } catch (Exception e) {
            return "redirect:/admin/vouchers?error=Lỗi khi tải thông tin voucher";
        }
    }

    @GetMapping("/create")
    public String showCreateVoucherPage() {
        return "admin/voucher-create";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createVoucher(@RequestBody String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            
            VoucherDTO voucherData = mapper.readValue(jsonData, VoucherDTO.class);
            
            VoucherEntity voucher = new VoucherEntity();
            voucher.setCode(voucherData.getCode());
            voucher.setDescription(voucherData.getDescription());
            voucher.setDiscount(voucherData.getDiscount());
            voucher.setDiscountAmount(voucherData.getDiscountAmount());
            voucher.setMinimumOrderAmount(voucherData.getMinimumOrderAmount());
            voucher.setStartDate(voucherData.getStartDate());
            voucher.setEndDate(voucherData.getEndDate());
            voucher.setExpiryDate(voucherData.getEndDate()); // Sử dụng endDate làm expiryDate
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
                                              @RequestBody String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            
            VoucherDTO voucherData = mapper.readValue(jsonData, VoucherDTO.class);
            
            VoucherEntity voucher = voucherService.findById(voucherId);
            if (voucher == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy voucher");
            }
            
            // Cập nhật thông tin
            voucher.setCode(voucherData.getCode());
            voucher.setDescription(voucherData.getDescription());
            voucher.setDiscount(voucherData.getDiscount());
            voucher.setDiscountAmount(voucherData.getDiscountAmount());
            voucher.setMinimumOrderAmount(voucherData.getMinimumOrderAmount());
            voucher.setStartDate(voucherData.getStartDate());
            voucher.setEndDate(voucherData.getEndDate());
            voucher.setExpiryDate(voucherData.getEndDate());
            voucher.setUsageLimit(voucherData.getUsageLimit());
            
            voucherService.updateVoucher(voucher);
            
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
