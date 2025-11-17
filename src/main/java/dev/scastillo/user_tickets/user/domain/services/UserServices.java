package dev.scastillo.user_tickets.user.domain.services;

import dev.scastillo.user_tickets.user.domain.model.User;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface UserServices {

    Page<User> getAllUsers(int page, int size);
    User createUser(User user);
    User getUserById(UUID id);
    void updateUser(User user);
}
