package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class CustomersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Thêm auto-increment
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "password")
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", unique = true)
    private String email;

}
