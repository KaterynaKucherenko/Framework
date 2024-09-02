package com.mjc.school.controller.implementation;

import com.mjc.school.service.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.service.dtoForUser.SignInRequest;
import com.mjc.school.service.dtoForUser.SignUpRequest;
import com.mjc.school.service.services.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Component
@RestController
@RequestMapping(produces = "application/json")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;


    @ApiOperation(value = "User registration", response = JwtAuthenticationResponse.class)
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return userService.signUp(request);
    }

    @ApiOperation(value = "User authorisation", response = JwtAuthenticationResponse.class)
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return userService.signIn(request);
    }
}
