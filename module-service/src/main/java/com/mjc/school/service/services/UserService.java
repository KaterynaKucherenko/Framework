package com.mjc.school.service.services;

import com.mjc.school.repository.implementation.UserRepository;
import com.mjc.school.repository.model.Role;
import com.mjc.school.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with the same name already exists");
        }
        return save(user);
    }

    public User getByUsername(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        return userRepository.findByUsername(username);
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
//    public void getAdmin() {
//        var user = getCurrentUser();
//        user.setRole(String.valueOf(Role.ROLE_ADMIN));
//        save(user);
//    }

}
