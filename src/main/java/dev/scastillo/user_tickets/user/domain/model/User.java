package dev.scastillo.user_tickets.user.domain.model;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firsName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

}
