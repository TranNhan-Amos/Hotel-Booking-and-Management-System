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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Service.CustomersService;


@Controller
@RequestMapping("/admin/customers")
public class AdminManagementCustomersController {

     @Autowired
    private CustomersService customersService;

     @GetMapping
    public String viewCustomesrManagementPage(Model model) {
        List<CustomersEntity> customers = customersService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "admin/management-Customers";
    }

     @PostMapping("/update")
    public String updateCustomer(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("bookingCount") Integer bookingCount,
            @RequestParam("createdDate") String createdDate,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            CustomersEntity customer = customersService.getCustomerById(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
                return "redirect:/admin/customers";
            }
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            // customer.setBookingCount(bookingCount);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = formatter.parse(createdDate);
            customer.setCreatedDate(parsedDate);
            customer.setStatus(status);

            customersService.updateCustomer(customer);
            redirectAttributes.addFlashAttribute("message", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật khách hàng thất bại: " + e.getMessage());
        }
        return "redirect:/admin/customers";
    }
}