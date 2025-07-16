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

    public List<CustomersEntity> getAllCustomers() {
        return customerRepository.findAll();
    }


    public CustomersEntity getCustomerById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }


    public void updateCustomer(CustomersEntity customer) {
        if (customer.getCustomerId() != null && customerRepository.existsById(customer.getCustomerId())) {
            customerRepository.save(customer); 
        } else {
            throw new IllegalArgumentException("Không tìm thấy khách hàng để cập nhật!");
        }
    }

    public List<CustomersEntity> findAll() {
        return customerRepository.findAll();
    }

    public List<CustomersEntity> findAllCustomersForAdmin() {
        return customerRepository.findAll();
    }

    public void save(CustomersEntity customer) {
        customerRepository.save(customer);
    }

    public CustomersEntity findBySystemUser(sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity systemUser) {
        return customerRepository.findBySystemUser(systemUser).orElse(null);
    }

    public CustomersEntity findByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }
}
