package com.mjc.school.service.dtoForUser;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record SignInRequest(
        @NotNull
        @Min(3)
        @Max(15)
        String username,
        @NotNull
        @Min(3)
        @Max(255)
        String password) {
}


