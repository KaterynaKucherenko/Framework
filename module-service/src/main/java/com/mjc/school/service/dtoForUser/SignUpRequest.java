package com.mjc.school.service.dtoForUser;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SignUpRequest {
    @NotNull
    @Min(3)
    @Max(15)
    private String username;
    @NotNull
    @Min(3)
    @Max(255)
    private String password;
}
