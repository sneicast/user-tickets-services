package dev.scastillo.user_tickets.user.domain.services;

import dev.scastillo.user_tickets.user.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UserServices {

    List<User> getAllUsers();
    User createUser(User user);
    User getUserById(UUID id);
    void updateUser(User user);
}
