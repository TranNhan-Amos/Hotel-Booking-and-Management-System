package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;
import sd19303no1.hotel_booking_and_management_system.Service.PartnerService;
import sd19303no1.hotel_booking_and_management_system.Entity.PartnerEntity;
import sd19303no1.hotel_booking_and_management_system.DTO.CustomerDTO;
import sd19303no1.hotel_booking_and_management_system.DTO.PartnerDTO;


@Controller
@RequestMapping("/admin/customers")
public class AdminManagementCustomersController {

     @Autowired
    private CustomersService customersService;

     @Autowired
    private PartnerService partnerService;

     @GetMapping
    public String viewCustomesrManagementPage(Model model) {
        List<CustomerDTO> customers = customersService.getAllCustomerDTOs();
        List<PartnerDTO> partners = partnerService.getAllPartnerDTOs();
        model.addAttribute("customers", customers);
        model.addAttribute("partners", partners);
        return "admin/management-Customers";
    }

     @GetMapping("/{customerId}")
    public String viewCustomerDetail(@PathVariable Integer customerId, Model model) {
        try {
            CustomerDTO customer = customersService.getCustomerDTOById(customerId);
            if (customer == null) {
                return "redirect:/admin/customers?error=Không tìm thấy khách hàng";
            }
            model.addAttribute("customer", customer);
            return "admin/customer-detail";
        } catch (Exception e) {
            return "redirect:/admin/customers?error=Lỗi khi tải thông tin khách hàng";
        }
    }

    @PostMapping("/update/{customerId}")
    @ResponseBody
    public ResponseEntity<String> updateCustomer(@PathVariable Integer customerId, 
                                               @RequestBody String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CustomerDTO customerData = mapper.readValue(jsonData, CustomerDTO.class);
            
            CustomersEntity customer = customersService.getCustomerById(customerId);
            if (customer == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy khách hàng");
            }
            
            // Cập nhật thông tin
            customer.setName(customerData.getName());
            customer.setEmail(customerData.getEmail());
            customer.setPhone(customerData.getPhone());
            customer.setAddress(customerData.getAddress());
            customer.setStatus(customerData.getStatus());
            
            customersService.updateCustomer(customer);
            
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    @PostMapping("/delete/{customerId}")
    @ResponseBody
    public ResponseEntity<String> deleteCustomer(@PathVariable Integer customerId) {
        try {
            customersService.deleteCustomer(customerId);
            return ResponseEntity.ok("Xóa khách hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa: " + e.getMessage());
        }
    }

    @GetMapping("/partner/{partnerId}")
    public String viewPartnerDetail(@PathVariable Long partnerId, Model model) {
        try {
            PartnerDTO partner = partnerService.getPartnerDTOById(partnerId);
            if (partner == null) {
                return "redirect:/admin/customers?error=Không tìm thấy đối tác";
            }
            model.addAttribute("partner", partner);
            return "admin/partner-detail";
        } catch (Exception e) {
            return "redirect:/admin/customers?error=Lỗi khi tải thông tin đối tác";
        }
    }

    @PostMapping("/partner/update/{partnerId}")
    @ResponseBody
    public ResponseEntity<String> updatePartner(@PathVariable Long partnerId, 
                                              @RequestBody String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PartnerDTO partnerData = mapper.readValue(jsonData, PartnerDTO.class);
            
            PartnerEntity partner = partnerService.findById(partnerId);
            if (partner == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy đối tác");
            }
            
            // Cập nhật thông tin
            partner.setCompanyName(partnerData.getCompanyName());
            partner.setEmail(partnerData.getEmail());
            partner.setPhone(partnerData.getPhone());
            partner.setAddress(partnerData.getAddress());
            partner.setContactPerson(partnerData.getContactPerson());
            partner.setTaxId(partnerData.getTaxId());
            partner.setBusinessLicense(partnerData.getBusinessLicense());
            partner.setBusinessmodel(partnerData.getBusinessmodel());
            partner.setHotelamenities(partnerData.getHotelamenities());
            
            partnerService.updatePartner(partner);
            
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    @PostMapping("/partner/delete/{partnerId}")
    @ResponseBody
    public ResponseEntity<String> deletePartner(@PathVariable Long partnerId) {
        try {
            partnerService.deletePartner(partnerId);
            return ResponseEntity.ok("Xóa đối tác thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa: " + e.getMessage());
        }
    }
}