package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;

@Service
public class CustomersService {
      @Autowired
    private CustomersRepository customerRepository;
     

    public List<CustomersEntity> getAllCustomers() {
        // Logic to retrieve all customers from the database
        return customerRepository.findAll();
    }

   public void saveCustomer(CustomersEntity customer){
    customerRepository.save(customer);
   }
   
   public void deleteCustomer(Integer id) {
    customerRepository.deleteById(id);
   }

   public  CustomersEntity getCustomerById(Integer id) {
      Optional<CustomersEntity> customerfindById = customerRepository.findById(id);
      return customerfindById.orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    } 

    public CustomersEntity findNameorCustomerId(String name ,Integer customerId){
        Optional<CustomersEntity> findByNameOrCustomerId = customerRepository.findByNameOrCustomerId(name, customerId);
        return findByNameOrCustomerId.orElseThrow(() -> new RuntimeException("Customer not found with name: " + name + " or customerId: " + customerId));
    }
}
