
package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;
import sd19303no1.hotel_booking_and_management_system.Repository.CustomersRepository;
import sd19303no1.hotel_booking_and_management_system.DTO.CustomerDTO;
import sd19303no1.hotel_booking_and_management_system.Repository.BookingOrderRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.ReviewRepository;
import sd19303no1.hotel_booking_and_management_system.Repository.SystemUserRepository;
import sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity;

@Service
public class CustomersService {
    private static final Logger logger = LoggerFactory.getLogger(CustomersService.class);

    @Autowired
    private CustomersRepository customerRepository;
    @Autowired
    private BookingOrderRepository bookingOrderRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SystemUserRepository systemUserRepository;

    public List<CustomersEntity> getAllCustomers() {
        logger.info("Fetching all customers");
        return customerRepository.findAll();
    }

    public CustomersEntity getCustomerById(Integer id) {
        logger.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id).orElse(null);
    }

    public void updateCustomer(CustomersEntity customer) {
        if (customer.getCustomerId() == null || !customerRepository.existsById(customer.getCustomerId())) {
            logger.error("Customer with ID {} not found for update", customer.getCustomerId());
            throw new IllegalArgumentException("Không tìm thấy khách hàng để cập nhật!");
        }

        try {
            // Lấy thông tin cũ để so sánh
            CustomersEntity oldCustomer = customerRepository.findById(customer.getCustomerId()).orElse(null);
            if (oldCustomer != null) {
                // Kiểm tra xem khách hàng có liên kết với SystemUserEntity không
                SystemUserEntity systemUser = oldCustomer.getSystemUser();
                if (systemUser != null) {
                    // Cập nhật SystemUserEntity nếu có thay đổi
                    boolean hasChanges = false;
                    
                    if (!oldCustomer.getEmail().equals(customer.getEmail())) {
                        systemUser.setEmail(customer.getEmail());
                        hasChanges = true;
                        logger.info("Updating email for SystemUser: {}", customer.getEmail());
                    }
                    
                    if (!oldCustomer.getName().equals(customer.getName())) {
                        systemUser.setUsername(customer.getName());
                        hasChanges = true;
                        logger.info("Updating username for SystemUser: {}", customer.getName());
                    }
                    
                    if (hasChanges) {
                        systemUserRepository.save(systemUser);
                        logger.info("Saved updated SystemUser for customer ID: {}", customer.getCustomerId());
                    }
                }
            }
            
            // Lưu thông tin customer
            customerRepository.save(customer);
            logger.info("Successfully updated customer with ID: {}", customer.getCustomerId());
        } catch (Exception e) {
            logger.error("Error updating customer with ID {}: {}", customer.getCustomerId(), e.getMessage());
            throw new RuntimeException("Lỗi khi cập nhật khách hàng: " + e.getMessage(), e);
        }
    }

    public List<CustomersEntity> findAll() {
        logger.info("Fetching all customers for admin");
        return customerRepository.findAll();
    }

    public List<CustomersEntity> findAllCustomersForAdmin() {
        logger.info("Fetching all customers for admin dashboard");
        return customerRepository.findAll();
    }

    public void save(CustomersEntity customer) {
        logger.info("Saving customer with email: {}", customer.getEmail());
        customerRepository.save(customer);
        logger.info("Successfully saved customer with ID: {}", customer.getCustomerId());
    }

    public CustomersEntity findBySystemUser(SystemUserEntity systemUser) {
        logger.info("Finding customer by SystemUser with email: {}", systemUser.getEmail());
        return customerRepository.findBySystemUser(systemUser).orElse(null);
    }

    public CustomersEntity findByEmail(String email) {
        logger.info("Finding customer by email: {}", email);
        CustomersEntity customer = customerRepository.findByEmailIgnoreCase(email).orElse(null);
        if (customer == null) {
            logger.warn("No customer found for email: {}", email);
        } else {
            logger.info("Found customer with ID: {} for email: {}", customer.getCustomerId(), email);
        }
        return customer;
    }

    public List<CustomerDTO> getAllCustomerDTOs() {
        logger.info("Fetching all customer DTOs");
        List<CustomersEntity> customers = customerRepository.findAll();
        List<CustomerDTO> dtos = new ArrayList<>();
        for (CustomersEntity c : customers) {
            // Chỉ lấy customers có SystemUserEntity với role CUSTOMER
            if (c.getSystemUser() != null && c.getSystemUser().getRole() == SystemUserEntity.Role.CUSTOMER) {
                CustomerDTO dto = new CustomerDTO();
                dto.setCustomerId(c.getCustomerId());
                dto.setName(c.getName());
                dto.setEmail(c.getEmail());
                dto.setPhone(c.getPhone());
                dto.setAddress(c.getAddress());
                dto.setCreatedDate(c.getCreatedDate());
                dto.setStatus(c.getStatus());
                
                // Booking count - Sử dụng customerId thay vì email
                int bookingCount = (int) bookingOrderRepository.findByCustomerIdOrderByCreatedAtDesc(c.getCustomerId()).size();
                dto.setBookingCount(bookingCount);
                logger.debug("Customer ID: {}, Booking count: {}", c.getCustomerId(), bookingCount);
                
                // Spending - Sử dụng customerId thay vì email
                double spending = bookingOrderRepository.findByCustomerIdOrderByCreatedAtDesc(c.getCustomerId())
                    .stream()
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
                    .sum();
                dto.setSpending(spending);
                logger.debug("Customer ID: {}, Total spending: {}", c.getCustomerId(), spending);
                
                // Rating (nếu có logic rating theo customer, nếu không thì để 0)
                dto.setRating(0);
                dtos.add(dto);
            }
        }
        logger.info("Returning {} customer DTOs", dtos.size());
        return dtos;
    }

    public CustomerDTO getCustomerDTOById(Integer customerId) {
        logger.info("Fetching customer DTO by ID: {}", customerId);
        CustomersEntity customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            logger.warn("No customer found for ID: {}", customerId);
            return null;
        }
        
        // Chỉ trả về customer nếu có SystemUserEntity với role CUSTOMER
        if (customer.getSystemUser() == null || customer.getSystemUser().getRole() != SystemUserEntity.Role.CUSTOMER) {
            logger.warn("Customer ID: {} is not a valid CUSTOMER role", customerId);
            return null;
        }
        
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setCreatedDate(customer.getCreatedDate());
        dto.setStatus(customer.getStatus());
        
        // Booking count - Sử dụng customerId thay vì email
        int bookingCount = (int) bookingOrderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getCustomerId()).size();
        dto.setBookingCount(bookingCount);
        logger.debug("Customer ID: {}, Booking count: {}", customerId, bookingCount);
        
        // Spending - Sử dụng customerId thay vì email
        double spending = bookingOrderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getCustomerId())
            .stream()
            .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0)
            .sum();
        dto.setSpending(spending);
        logger.debug("Customer ID: {}, Total spending: {}", customerId, spending);
        
        // Rating
        dto.setRating(0);
        
        logger.info("Returning customer DTO for ID: {}", customerId);
        return dto;
    }

    public void deleteCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            logger.error("Customer with ID {} not found for deletion", customerId);
            throw new IllegalArgumentException("Không tìm thấy khách hàng để xóa!");
        }
        logger.info("Deleting customer with ID: {}", customerId);
        customerRepository.deleteById(customerId);
        logger.info("Successfully deleted customer with ID: {}", customerId);
    }
}
