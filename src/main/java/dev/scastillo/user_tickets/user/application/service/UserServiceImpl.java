package dev.scastillo.user_tickets.user.application.service;

import dev.scastillo.user_tickets.shared.exception.NotFoundException;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.repository.UserRepository;
import dev.scastillo.user_tickets.user.domain.services.UserServices;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserServices {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "user", key = "#result.id")
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "user", key = "#id")
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "El usuario con ID " + id + " no existe"));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "user", key = "#user.id")
    public void updateUser(User user) {
        User existingUser = getUserById(user.getId());
        existingUser.setFirsName(user.getFirsName());
        existingUser.setLastName(user.getLastName());
        userRepository.save(existingUser);
    }
}
