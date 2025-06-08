package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Admin;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


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
    

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute  CustomersEntity customer) {
        customersService.saveCustomer(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@ModelAttribute("id") Integer id) {
        customersService.deleteCustomer(id);
        return "redirect:/admin/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@ModelAttribute("id") Integer id, Model model) {
        CustomersEntity customer = customersService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "admin/management-Customers";
    }

    @GetMapping("/search")
    public String searchCustomer(@ModelAttribute("name") String name, @ModelAttribute("customerId") Integer customerId, Model model) {
        CustomersEntity findByNameOrCustomerId = customersService.findNameorCustomerId(name, customerId);
        model.addAttribute("findByNameOrCustomerId", findByNameOrCustomerId);
        return "admin/management-Customers";
    }
    
}
