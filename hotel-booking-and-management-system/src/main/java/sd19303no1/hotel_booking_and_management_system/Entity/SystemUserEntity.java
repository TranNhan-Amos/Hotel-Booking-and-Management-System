package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemUserEntity {

    public enum Role {
        ADMIN, PARTNER, STAFF, CUSTOMER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
