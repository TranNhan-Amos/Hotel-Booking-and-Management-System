package sd19303no1.hotel_booking_and_management_system.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sd19303no1.hotel_booking_and_management_system.Entity.CustomersEntity;

@Repository
public interface CustomersRepository extends JpaRepository<CustomersEntity, Integer> {
    Optional<CustomersEntity> findByEmail(String email);

    static List<CustomersEntity> findAllCustomersForAdmin() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllCustomersForAdmin'");
    }
}
