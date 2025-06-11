package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;

@Service
public class CustomersService {

      @Autowired
    private CustomersRepository customerRepository;

    public CustomersEntity getCustomerById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }
     

    public List<CustomersEntity> getAllCustomers() {
        // Logic to retrieve all customers from the database
        return customerRepository.findAll();
    }

    public void deleteCustomer(Integer id) {
    customerRepository.deleteById(id);
   }

   public void updateCustomer(CustomersEntity customer) {
        if (customer.getCustomerId() != null && customerRepository.existsById(customer.getCustomerId())) {
            customerRepository.save(customer); // Cập nhật nếu ID tồn tại
        } else {
            throw new IllegalArgumentException("Không tìm thấy khách hàng để cập nhật!");
        }
    }
}
