package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name")
    private String name;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "email", length = 45, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

}
