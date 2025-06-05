package sd19303no1.hotel_booking_and_management_system.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.apache.catalina.User;

@Entity
@Table(name = "Post")
public class PostEntity {

    @Id
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
