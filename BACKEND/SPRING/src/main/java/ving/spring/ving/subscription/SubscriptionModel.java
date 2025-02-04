package ving.spring.ving.subscription;

import jakarta.persistence.*;
import lombok.*;
import ving.spring.ving.user.UserModel;

import java.time.LocalDateTime;

@Data
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "follow")
public class SubscriptionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer followId;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private UserModel streamer;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private UserModel follower;

    private Integer notification;

    private Integer donation;

    @Column(nullable = true, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
