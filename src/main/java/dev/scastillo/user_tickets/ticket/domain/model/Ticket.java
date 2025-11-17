package dev.scastillo.user_tickets.ticket.domain.model;

import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets",
    indexes = {
        @Index(name = "idx_ticket_user_id", columnList = "user_id"),
        @Index(name = "idx_ticket_status", columnList = "status")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime updateAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
}
