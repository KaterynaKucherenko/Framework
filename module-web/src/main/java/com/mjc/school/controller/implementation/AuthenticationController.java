package com.mjc.school.controller.implementation;

import com.mjc.school.service.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.service.dtoForUser.SignInRequest;
import com.mjc.school.service.dtoForUser.SignUpRequest;
import com.mjc.school.service.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(produces = "application/json")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;


    @ApiOperation(value = "User registration", response = JwtAuthenticationResponse.class)
    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        JwtAuthenticationResponse response = userService.signUp(request);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "User authorisation", response = JwtAuthenticationResponse.class)
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        JwtAuthenticationResponse response = userService.signIn(request);
        System.out.println("Token in Response: " + response.getToken());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/get-admin")
    @Operation(summary = "Get admin role")
    public void getAdmin() {
        userService.getAdmin();
    }
}
