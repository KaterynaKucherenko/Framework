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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
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
        return save(userModel);

    }


    public UserDetailsService userDetailsService() {
        return this::loadUserByUsername;
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        String rawPassword = request.password();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameNotFoundException("User " + request.username() + " already exist");
        }
        var userModel = UserModel.builder()
                .username(request.username())
                .password(encodedPassword)
                .role(Role.ROLE_USER)
                .build();
        this.create(userModel);
        var jwt = jwtTokenService.generateToken(userModel);
        return new JwtAuthenticationResponse(jwt);
    }


    public JwtAuthenticationResponse signIn(SignInRequest request) {
        String rawPassword = request.password();
        System.out.println("Raw Password: " + rawPassword);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(), request.password()));
        System.out.println("Authenticating user: " + request.username());
        UserModel user = userRepository.findByUsername(request.username()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwt = jwtTokenService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}




