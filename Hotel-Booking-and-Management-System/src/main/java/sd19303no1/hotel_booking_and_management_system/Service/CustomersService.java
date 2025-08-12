package sd19303no1.hotel_booking_and_management_system.Service;

import java.util.List;

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

     @Autowired
    private CustomersRepository customerRepository;
    @Autowired
    private BookingOrderRepository bookingOrderRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SystemUserRepository systemUserRepository;

    public List<CustomersEntity> getAllCustomers() {
        return customerRepository.findAll();
    }


    public CustomersEntity getCustomerById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }


    public void updateCustomer(CustomersEntity customer) {
        if (customer.getCustomerId() != null && customerRepository.existsById(customer.getCustomerId())) {
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
                        }
                        
                        if (!oldCustomer.getName().equals(customer.getName())) {
                            systemUser.setUsername(customer.getName());
                            hasChanges = true;
                        }
                        
                        if (hasChanges) {
                            systemUserRepository.save(systemUser);
                        }
                    }
                }
                
                // Lưu thông tin customer
                customerRepository.save(customer);
                
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi cập nhật khách hàng: " + e.getMessage(), e);
            }
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

    public List<CustomerDTO> getAllCustomerDTOs() {
        List<CustomersEntity> customers = customerRepository.findAll();
        List<CustomerDTO> dtos = new java.util.ArrayList<>();
        for (CustomersEntity c : customers) {
            // Chỉ lấy customers có SystemUserEntity với role CUSTOMER
            if (c.getSystemUser() != null && c.getSystemUser().getRole() == sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity.Role.CUSTOMER) {
                CustomerDTO dto = new CustomerDTO();
                dto.setCustomerId(c.getCustomerId());
                dto.setName(c.getName());
                dto.setEmail(c.getEmail());
                dto.setPhone(c.getPhone());
                dto.setAddress(c.getAddress());
                dto.setCreatedDate(c.getCreatedDate());
                dto.setStatus(c.getStatus());
                // Booking count
                int bookingCount = (int) bookingOrderRepository.findByEmailOrderByCreatedAtDesc(c.getEmail()).size();
                dto.setBookingCount(bookingCount);
                // Spending (tổng totalPrice các booking)
                double spending = bookingOrderRepository.findByEmailOrderByCreatedAtDesc(c.getEmail())
                    .stream().mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0).sum();
                dto.setSpending(spending);
                // Rating (nếu có logic rating theo customer, nếu không thì để 0)
                dto.setRating(0);
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public CustomerDTO getCustomerDTOById(Integer customerId) {
        CustomersEntity customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return null;
        
        // Chỉ trả về customer nếu có SystemUserEntity với role CUSTOMER
        if (customer.getSystemUser() == null || customer.getSystemUser().getRole() != sd19303no1.hotel_booking_and_management_system.Entity.SystemUserEntity.Role.CUSTOMER) {
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
        
        // Booking count
        int bookingCount = (int) bookingOrderRepository.findByEmailOrderByCreatedAtDesc(customer.getEmail()).size();
        dto.setBookingCount(bookingCount);
        // Spending
        double spending = bookingOrderRepository.findByEmailOrderByCreatedAtDesc(customer.getEmail())
            .stream().mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0).sum();
        dto.setSpending(spending);
        // Rating
        dto.setRating(0);
        
        return dto;
    }

    public void deleteCustomer(Integer customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
        } else {
            throw new IllegalArgumentException("Không tìm thấy khách hàng để xóa!");
        }
    }
}
