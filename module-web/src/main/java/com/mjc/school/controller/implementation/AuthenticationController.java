package com.mjc.school.controller.implementation;

import com.mjc.school.service.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.service.dtoForUser.SignInRequest;
import com.mjc.school.service.dtoForUser.SignUpRequest;
import com.mjc.school.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping( produces = "application/json")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;


    @Operation(summary = "User registration")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return userService.signUp(request);
    }

    @Operation(summary = "User authorisation")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return userService.signIn(request);

    }

    @GetMapping("/get-admin")
    @Operation(summary = "Get admin role")
    public void getAdmin() {
        userService.getAdmin();
    }
}
