package dev.scastillo.user_tickets.user.application.service;

import dev.scastillo.user_tickets.shared.exception.BadRequestException;
import dev.scastillo.user_tickets.shared.exception.NotFoundException;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.repository.UserRepository;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserServices {
    private final UserRepository userRepository;

    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));

        return userRepository.findAll(pageable);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "El usuario con ID " + id + " no existe"));
    }

    @Override
    public void updateUser(User user) {
        User existingUser = getUserById(user.getId());
        existingUser.setFirsName(user.getFirsName());
        existingUser.setLastName(user.getLastName());
        userRepository.save(existingUser);
    }
}
