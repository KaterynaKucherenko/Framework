package com.mjc.school.service.services;

import com.mjc.school.repository.implementation.UserRepository;
import com.mjc.school.repository.model.Role;
import com.mjc.school.repository.model.UserModel;
import com.mjc.school.service.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.service.dtoForUser.SignInRequest;
import com.mjc.school.service.dtoForUser.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
    }

    @Transactional
    public UserModel create(UserModel userModel) {
        if (userRepository.existsByUsername(userModel.getUsername())) {
            throw new IllegalArgumentException("User with the same name already exists");
        }
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        return save(userModel);

    }


    public UserDetailsService userDetailsService() {
        return (UserDetailsService) this;
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        var userModel = UserModel.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();
        this.create(userModel);
        var jwt = jwtTokenService.generateToken(userModel);
        return new JwtAuthenticationResponse(jwt);
    }


    public JwtAuthenticationResponse signIn(SignInRequest request) {
        System.out.println("Authenticating user: " + request.username());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        ));

        var user = this.userDetailsService().loadUserByUsername(request.username());
        var jwt = jwtTokenService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }


    public UserModel getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByUsername(username);
    }

    private UserModel getUserByUsername(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        return userRepository.findByUsername(username);
    }

    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ROLE_ADMIN);
        save(user);
    }
}






