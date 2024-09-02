package com.mjc.school.service.services;

import com.mjc.school.repository.implementation.UserRepository;
import com.mjc.school.repository.model.Role;
import com.mjc.school.repository.model.UserModel;
import com.mjc.school.service.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.service.dtoForUser.SignInRequest;
import com.mjc.school.service.dtoForUser.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
    }
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

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
        return  this::loadUserByUsername;
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        String rawPassword = request.password();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameNotFoundException("User " + request.username() + " already exist");}
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
//        if (userRepository.findByUsername(request.username())==null){
//            throw new UsernameNotFoundException("User " + request.username() + " not found");
//        }
        String rawPassword = request.password();
        Optional<UserModel> users = userRepository.findByUsername(request.username());
        if (!passwordEncoder.matches(request.password(), users.get().getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        System.out.println("Raw Password during login: " + rawPassword);
        System.out.println("Encoded Password in DB: " + users.get().getPassword());
//        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.username(),
                    rawPassword));

                    System.out.println("Authenticating user: " + request.username());

//        } catch (Exception e) {
//            System.out.println("Authentication failed: " + e.getMessage());
//            throw e;}

        System.out.println("Before user=");
        UserModel user = userRepository.findByUsername(request.username()) .orElseThrow(() -> new UsernameNotFoundException("User not found"));;
        System.out.println("Before jvt" );
        var jwt = jwtTokenService.generateToken(user);
        System.out.println("Token: " + jwt);
        return new JwtAuthenticationResponse(jwt);
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}


//    public UserModel getCurrentUser() {
//        var username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return getUserByUsername(username);
//    }
//
//    private UserModel getUserByUsername(String username) {
//        if (!userRepository.existsByUsername(username)) {
//            throw new UsernameNotFoundException("User " + username + " not found");
//        }
//        return userRepository.findByUsername(username);
//    }
//
//    public void getAdmin() {
//        var user = getCurrentUser();
//        user.setRole(Role.ROLE_ADMIN);
//        save(user);
//    }



